package com.mkorp.newsboard;

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
import android.widget.Toast;

import com.mkorp.newsboard.adapters.ArticlesAdapter;
import com.mkorp.newsboard.model.ApiResponse;
import com.mkorp.newsboard.model.Article;
import com.mkorp.newsboard.model.Category;
import com.mkorp.newsboard.model.Country;
import com.mkorp.newsboard.model.NewsApiService;
import com.mkorp.newsboard.model.Status;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArticleClickedListener}
 * interface.
 */
public class ArticlesFragment extends Fragment {

    private OnArticleClickedListener onArticleClickedListener;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private Retrofit retrofit;
    private int page;
    private ArticlesAdapter adapter;
    public static final String TAG = "HomeFragment";
    private String API_KEY;
    private Country country;
    private Category category;

    public void setCountry(Country country) {
        adapter.clearArticles();
        page = 0;
        this.country = country;
        loadNextHeadlines();
    }

    public void setCategory(Category category) {
        adapter.clearArticles();
        page = 0;
        this.category = category;
        loadNextHeadlines();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticlesFragment() {
        category = Category.general;
        country = Country.ma;
        page = 0;
    }

    public static ArticlesFragment newInstance(RecyclerView.AdapterDataObserver adapterDataObserver) {
        ArticlesFragment homeFragment = new ArticlesFragment();
        homeFragment.adapterDataObserver = adapterDataObserver;
        return homeFragment;
    }

    public void loadNextHeadlines() {
        NewsApiService newsService = retrofit.create(NewsApiService.class);
        Call<ApiResponse> call = newsService.getArticles(country, category, ++page, API_KEY);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (apiResponse != null && apiResponse.getStatus() == Status.ok) {
                    List<Article> articles = apiResponse.getArticles();
                    if (articles.size() == 0)
                        Toast.makeText(getContext(), "No more articles found", Toast.LENGTH_LONG).show();
                    else
                        adapter.loadNextArticles(articles);
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

    public void connectAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.base_url))
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectAndGetApiData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        API_KEY = getResources().getString(R.string.api_key);

        Context context = view.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new ArticlesAdapter(new ArrayList<Article>(), onArticleClickedListener);
        adapter.registerAdapterDataObserver(adapterDataObserver);
        final OnBottomReachedListener onBottomReachedListener = new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                loadNextHeadlines();
            }
        };
        loadNextHeadlines();
        adapter.setOnBottomReachedListener(onBottomReachedListener);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clearArticles();
                page = 0;
                loadNextHeadlines();
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
}
