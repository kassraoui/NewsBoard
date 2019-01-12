package com.mkorp.newsboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.mkorp.newsboard.adapters.FlagsAdapter;
import com.mkorp.newsboard.customTabs.CustomTabActivityHelper;
import com.mkorp.newsboard.model.Article;
import com.mkorp.newsboard.model.Country;

public class MainActivity extends AppCompatActivity implements ArticlesFragment.OnArticleClickedListener {


    private ProgressBar progressBar;
    private ArticlesFragment articlesFragment;
    private boolean isUserInteracting;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentManager manager = getSupportFragmentManager();
                    ArticlesFragment homeFragment = (ArticlesFragment) manager.findFragmentByTag(ArticlesFragment.TAG);
                    if (homeFragment != null && homeFragment.isVisible())
                        return true;
                    manager.beginTransaction().replace(R.id.frame, articlesFragment, ArticlesFragment.TAG).commit();
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress);
        RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                progressBar.setVisibility(View.VISIBLE);
            }
        };
        articlesFragment = ArticlesFragment.newInstance(adapterDataObserver);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame, articlesFragment, ArticlesFragment.TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();


        final Country[] countries = {Country.ma, Country.fr, Country.us, Country.gb};
        FlagsAdapter flagsAdapter = new FlagsAdapter(this, countries);
        flagsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(flagsAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setPopupBackgroundResource(R.color.backgroundAliceBlue);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                if (isUserInteracting)
                    articlesFragment.setCountry(countries[i]);
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
}
