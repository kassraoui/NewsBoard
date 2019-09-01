package com.mkorp.newsboard.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.mkorp.newsboard.R;
import com.mkorp.newsboard.newsApi.Country;

public class SearchArticleFragment extends Fragment {

    static final int TAG = 2;

    private ArticlesFragment articlesFragment;

    public SearchArticleFragment() {
    }

    static SearchArticleFragment newInstance() {
        return new SearchArticleFragment();
    }

    void searchArticles(String keywords) {
        articlesFragment.clearAllArticles();
        articlesFragment.setSearchKeyword(keywords);
        articlesFragment.loadNextArticles();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_article, container, false);
        SearchView searchView = view.findViewById(R.id.searchView);
        SearchManager searchManager = (SearchManager) view.getContext().getSystemService(Context.SEARCH_SERVICE);
        Activity activity = getActivity();
        if (searchManager == null || activity == null)
            return view;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setQueryRefinementEnabled(true);
        searchView.clearFocus();
        articlesFragment = ArticlesFragment.newInstance(Country.ma, true);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.childArticlesFragment, articlesFragment).commit();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
