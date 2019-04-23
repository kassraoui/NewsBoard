package com.mkorp.newsboard.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.mkorp.newsboard.CountryLocator;
import com.mkorp.newsboard.R;
import com.mkorp.newsboard.SuggestionsProvider;
import com.mkorp.newsboard.adapters.BottomBarAdapter;
import com.mkorp.newsboard.adapters.FlagsAdapter;
import com.mkorp.newsboard.customTabs.CustomTabActivityHelper;
import com.mkorp.newsboard.newsApi.Article;
import com.mkorp.newsboard.newsApi.Category;
import com.mkorp.newsboard.newsApi.Country;

public class MainActivity extends AppCompatActivity implements ArticlesFragment.OnArticleClickedListener,
        CategoryFragment.OnCategoryClickedListener, ArticlesFragment.OnArticlesChangedListener {

    private ProgressBar progressBar;
    private boolean isUserInteracting;
    private Spinner countrySpinner;
    private final Country[] countries = {Country.ma, Country.fr, Country.us, Country.gb};
    private Country currentCountry;
    private CustomPager viewPager;
    private BottomBarAdapter bottomBarAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    countrySpinner.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(ArticlesFragment.HOME_TAG);
                    return true;
                case R.id.navigation_categories:
                    countrySpinner.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(CategoryFragment.TAG);
                    return true;
                case R.id.navigation_search:
                    countrySpinner.setVisibility(View.GONE);
                    viewPager.setCurrentItem(SearchArticleFragment.TAG);
                    return true;
            }
            return false;
        }
    };

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchArticleFragment searchFragment = (SearchArticleFragment) bottomBarAdapter.getItem(SearchArticleFragment.TAG);
            if (searchFragment == null) {
                Log.e("MainActivity", "Search Fragment not instantiated");
                return;
            }
            searchFragment.searchArticles(query);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setPagingEnabled(false);

        CountryLocator cl = new CountryLocator(getApplicationContext());
        currentCountry = cl.getCountry();

        bottomBarAdapter = new BottomBarAdapter(getSupportFragmentManager());
        bottomBarAdapter.addFragment(ArticlesFragment.HOME_TAG, ArticlesFragment.newInstance(currentCountry, false));
        bottomBarAdapter.addFragment(CategoryFragment.TAG, CategoryFragment.newInstance(1));
        bottomBarAdapter.addFragment(SearchArticleFragment.TAG, SearchArticleFragment.newInstance());
        bottomBarAdapter.addFragment(ArticlesFragment.CATEGORY_TAG, ArticlesFragment.newInstance(currentCountry, false));

        viewPager.setAdapter(bottomBarAdapter);

        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        countrySpinner = (Spinner) item.getActionView();


        FlagsAdapter flagsAdapter = new FlagsAdapter(this, countries);
        flagsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        countrySpinner.setAdapter(flagsAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            countrySpinner.setPopupBackgroundResource(R.color.backgroundAliceBlue);
        }
        int position = flagsAdapter.getPosition(currentCountry);
        countrySpinner.setSelection(position);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                if (isUserInteracting) {
                    currentCountry = countries[i];
                    Fragment fragment = bottomBarAdapter.getItem(viewPager.getCurrentItem());
                    if(!(fragment instanceof ArticlesFragment))
                        return;
                    ArticlesFragment currentArticleFragments = (ArticlesFragment) fragment;
                    currentArticleFragments.setCountry(countries[i]);
                    currentArticleFragments.clearAllArticles();
                    currentArticleFragments.loadNextArticles();
                }
            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == ArticlesFragment.CATEGORY_TAG) {
            viewPager.setCurrentItem(CategoryFragment.TAG, true);
        } else {
            finish();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteracting = true;
    }

    @Override
    public void onArticleClicked(Article article) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        CustomTabsIntent customTabsIntent = builder.build();

        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, Uri.parse(article.getUrl()),
                new CustomTabActivityHelper.CustomTabFallback() {
                    @Override
                    public void openUri(Activity activity, Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                    }
                });
    }

    @Override
    public void onCategoryClicked(Category category) {
        ArticlesFragment articlesFragment = (ArticlesFragment) bottomBarAdapter.getItem(ArticlesFragment.CATEGORY_TAG);
        articlesFragment.setCategory(category);
        articlesFragment.setCountry(currentCountry);
        viewPager.setCurrentItem(ArticlesFragment.CATEGORY_TAG);
        articlesFragment.clearAllArticles();
        articlesFragment.loadNextArticles();
    }

    @Override
    public void onArticleRangeInserted(int positionStart, int itemCount) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onArticleRangeRemoved(int positionStart, int itemCount) {
        progressBar.setVisibility(View.VISIBLE);
    }
}
