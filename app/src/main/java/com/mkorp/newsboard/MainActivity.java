package com.mkorp.newsboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.mkorp.newsboard.adapters.FlagsAdapter;
import com.mkorp.newsboard.customTabs.CustomTabActivityHelper;
import com.mkorp.newsboard.model.Article;
import com.mkorp.newsboard.model.Category;
import com.mkorp.newsboard.model.Country;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ArticlesFragment.OnArticleClickedListener, CategoryFragment.OnCategoryClickedListener, ArticlesFragment.OnArticlesChangedListener {


    private ProgressBar progressBar;
    private boolean isUserInteracting;
    private Map<String, Fragment> fragmentsByTag;
    private String currentArticlesFragmentTag = ArticlesFragment.HOME_TAG;
    private Spinner countrySpinner;
    private final Country[] countries = {Country.ma, Country.fr, Country.us, Country.gb};
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    currentArticlesFragmentTag = ArticlesFragment.HOME_TAG;
                    return replaceFragment(ArticlesFragment.HOME_TAG, false);
                case R.id.navigation_categories:
                    currentArticlesFragmentTag = ArticlesFragment.CATEGORY_TAG;
                    return replaceFragment(CategoryFragment.TAG, false);
                case R.id.navigation_search:
                    currentArticlesFragmentTag = ArticlesFragment.SEARCH_TAG;
                    return true;
            }
            return false;
        }
    };

    private void commit(Fragment fragment, String tag, FragmentManager manager, boolean addToBackStack) {
        if (addToBackStack)
            manager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.frame, fragment, tag).addToBackStack(null).commit();
        else
            manager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.frame, fragment, tag).commit();
    }

    private boolean replaceFragment(String fragmentTag, boolean addToBackStack) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(fragmentTag);
        if (fragment != null && fragment.isVisible())
            return true;
        if (fragment != null && !fragment.isVisible()) {
            commit(fragment, fragmentTag, manager, addToBackStack);
            return true;
        }
        fragment = fragmentsByTag.get(fragmentTag);
        if (fragment == null) {
            Log.e("MainActivity", "Fragment " + fragmentTag + " is not instantiated");
            return false;
        }
        commit(fragment, fragmentTag, manager, addToBackStack);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress);

        fragmentsByTag = new HashMap<>();
        fragmentsByTag.put(ArticlesFragment.HOME_TAG, ArticlesFragment.newInstance());
        fragmentsByTag.put(CategoryFragment.TAG, CategoryFragment.newInstance(1));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        replaceFragment(ArticlesFragment.HOME_TAG, false);
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

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                if (isUserInteracting) {
                    ArticlesFragment currentArticleFragments = (ArticlesFragment) fragmentsByTag.get(currentArticlesFragmentTag);
                    if (currentArticleFragments == null)
                        return;
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
        ArticlesFragment articlesCategoryFragment;
        if (!fragmentsByTag.containsKey(ArticlesFragment.CATEGORY_TAG)) {
            articlesCategoryFragment = ArticlesFragment.newInstance();
            fragmentsByTag.put(ArticlesFragment.CATEGORY_TAG, articlesCategoryFragment);
        } else
            articlesCategoryFragment = (ArticlesFragment) fragmentsByTag.get(ArticlesFragment.CATEGORY_TAG);
        assert articlesCategoryFragment != null;
        articlesCategoryFragment.setCategory(category);
        articlesCategoryFragment.setCountry(countries[countrySpinner.getSelectedItemPosition()]);
        if(replaceFragment(ArticlesFragment.CATEGORY_TAG, true)){
            articlesCategoryFragment.clearAllArticles();
            articlesCategoryFragment.loadNextArticles();
        }
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
