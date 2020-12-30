package br.scaylart.malbile.views.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.controllers.networks.SearchReceiver;
import br.scaylart.malbile.views.adapters.pager.SearchPagerAdapter;
import br.scaylart.malbile.views.listeners.SearchMalView;
import br.scaylart.malbile.views.widget.SlidingTabLayout;

public class SearchActivity extends BaseActivity implements SearchMalView {
    public static final String TAG = SearchActivity.class.getSimpleName();

    public static final String SEARCH_ARGUMENT_KEY = TAG + ":" + "SearchArgumentKey";

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;
    SearchView searchView;

    public String query;

    SearchPagerAdapter mSearchPagerAdapter;

    public static Intent constructSearchActivityIntent(Context context, String username) {
        Intent argumentIntent = new Intent(context, SearchActivity.class);
        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);

        initializeToolbar();

        if (savedInstanceState != null)
            restoreState(savedInstanceState);

        mSearchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager(), MalbileManager.TaskJob.SEARCH, AccountService.getUsername());

        mViewPager.setAdapter(mSearchPagerAdapter);

        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accentPinkA200);
            }
        });
        mTabs.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQuery(query, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            if (searchView != null) {
                searchView.setQuery(query, false);
            }
            searchRecords(BaseService.ListType.ANIME, query);
            searchRecords(BaseService.ListType.MANGA, query);
        }
    }

    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(SEARCH_ARGUMENT_KEY)) {
            query = savedState.getString(SEARCH_ARGUMENT_KEY);

            savedState.remove(SEARCH_ARGUMENT_KEY);
        }
    }

    private void searchRecords(BaseService.ListType listType, String query) {
        Intent i = new Intent();
        i.setAction(SearchReceiver.RECEIVER);
        i.putExtra("type", listType);
        i.putExtra("query", query);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(i);
    }

    @Override
    protected void onResume() {
        if (getIntent() != null)
            handleIntent(getIntent());
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (query != null) {
            outState.putString(SEARCH_ARGUMENT_KEY, query);
        }
    }

    @Override
    public void initializeToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.fragment_search);
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primaryBlue500));

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
