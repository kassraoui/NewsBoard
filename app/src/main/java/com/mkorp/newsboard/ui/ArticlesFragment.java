package com.mkorp.newsboard.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.mkorp.newsboard.R;
import com.mkorp.newsboard.adapters.ArticlesAdapter;
import com.mkorp.newsboard.newsApi.Api;
import com.mkorp.newsboard.newsApi.ApiResponse;
import com.mkorp.newsboard.newsApi.Article;
import com.mkorp.newsboard.newsApi.Category;
import com.mkorp.newsboard.newsApi.Country;
import com.mkorp.newsboard.newsApi.NewsApiService;
import com.mkorp.newsboard.newsApi.RetrofitFactory;
import com.mkorp.newsboard.newsApi.Status;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/**
 * A fragment representing a list of Articles.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArticleClickedListener}
 * interface.
 */
public class ArticlesFragment extends Fragment {

    private static final String ARG_FOR_SEARCH = "for-search";
    private static final String ARG_DEFAULT_COUNTRY = "defaultCountry";
    private static final String TAG = "ArticlesFragment";
    private final Retrofit retrofit;
    private final ArticlesAdapter adapter;
    private final RetrofitFactory retrofitFactory;

    private int page;
    private Country country;
    private Category category;
    private String searchKeywords;
    private boolean forSearch;
    private OnArticleClickedListener onArticleClickedListener;
    private OnArticlesChangedListener onArticlesChangedListener;

    public static final int HOME_TAG = 0;
    public static final int CATEGORY_TAG = 3;
    private boolean lastPageReached;

    public void setCountry(Country country) {
        page = 0;
        lastPageReached = false;
        this.country = country;
    }

    public void setCategory(Category category) {
        page = 0;
        lastPageReached = false;
        this.category = category;
    }

    public void setSearchKeyword(String searchKeywords) {
        page = 0;
        lastPageReached = false;
        category = Category.Search;
        this.searchKeywords = searchKeywords;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticlesFragment() {
        category = Category.General;
        page = 0;
        lastPageReached = false;
        adapter = new ArticlesAdapter();
        retrofitFactory = new RetrofitFactory(Api.NewsApi);
        retrofit = retrofitFactory.create();
    }

    public static ArticlesFragment newInstance(Country defaultCountry, boolean forSearch) {
        ArticlesFragment fragment = new ArticlesFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_FOR_SEARCH, forSearch);
        args.putSerializable(ARG_DEFAULT_COUNTRY, defaultCountry);
        fragment.setArguments(args);
        return fragment;
    }

    public void clearAllArticles() {
        adapter.clearArticles();
    }

    private Call<ApiResponse> callApiService() {
        NewsApiService newsService = retrofit.create(NewsApiService.class);
        if (!forSearch)
            return newsService.getArticles(country, category, ++page, retrofitFactory.getApiKey());
        return newsService.searchArticles(searchKeywords, ++page, retrofitFactory.getApiKey());
    }

    public void loadNextArticles() {
        if (lastPageReached)
            return;
        if ((searchKeywords == null && forSearch)) {
            adapter.loadNextArticles(new ArrayList<Article>(), category);
            return;
        }
        Call<ApiResponse> call = callApiService();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (apiResponse != null && apiResponse.getStatus() == Status.ok) {
                    List<Article> articles = apiResponse.getArticles();
                    if (articles.size() == 0) {
                        lastPageReached = true;
                        adapter.notifyLastPageReached();
                    } else
                        adapter.loadNextArticles(articles, category);
                } else {
                    Log.e(TAG, String.format("Failed to request NewsApi : %s", apiResponse != null ? apiResponse.getMessage() : ""));
                    Toast.makeText(getContext(), "Failed to request NewsApi", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString());
                Toast.makeText(getContext(), "Failed to request NewsApi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            forSearch = args.getBoolean(ARG_FOR_SEARCH);
            country = country != null ? country : (Country) args.getSerializable(ARG_DEFAULT_COUNTRY);
            if (forSearch)
                category = Category.Search;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        final FloatingTextButton goToTopButton = view.findViewById(R.id.goToTop);

        Context context = view.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        final OnBottomReachedListener onBottomReachedListener = new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                loadNextArticles();
            }
        };

        goToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        loadNextArticles();
        adapter.setOnBottomReachedListener(onBottomReachedListener);
        adapter.setOnArticleClickedListener(onArticleClickedListener);
        adapter.setOnArticlesChangedListener(onArticlesChangedListener);

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int buttonVisibility = goToTopButton.getVisibility();
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null)
                    return;
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if ((firstVisibleItemPosition == 0 && buttonVisibility == View.VISIBLE)
                        || (dy > 0 && buttonVisibility == View.VISIBLE)) {
                    goToTopButton.setVisibility(View.GONE);
                    goToTopButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                    return;
                }
                if (firstVisibleItemPosition != 0 && dy < -20 && buttonVisibility == View.GONE) {
                    goToTopButton.setVisibility(View.VISIBLE);
                    goToTopButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clearArticles();
                page = 0;
                lastPageReached = false;
                loadNextArticles();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleClickedListener) {
            onArticleClickedListener = (OnArticleClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArticleClickedListener");
        }
        if (context instanceof OnArticlesChangedListener) {
            onArticlesChangedListener = (OnArticlesChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArticlesChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onArticleClickedListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnArticleClickedListener {
        void onArticleClicked(Article article);
    }

    public interface OnBottomReachedListener {
        void onBottomReached(int position);
    }

    public interface OnArticlesChangedListener {
        void onArticleRangeInserted(int positionStart, int itemCount);

        void onArticleRangeRemoved(int positionStart, int itemCount);
    }
}
