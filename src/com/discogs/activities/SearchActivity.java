package com.discogs.activities;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import com.discogs.R;
import com.discogs.fragments.SearchResultsListFragment;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, ActionBar.TabListener {
    private SearchView mSearchView;
    private MenuItem searchItem;
    private String query;
    private String type = "all";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getActionBar().addTab(getActionBar().newTab().setText("All").setTabListener(this));
        getActionBar().addTab(getActionBar().newTab().setText("Release").setTabListener(this));
        getActionBar().addTab(getActionBar().newTab().setText("Master").setTabListener(this));
        getActionBar().addTab(getActionBar().newTab().setText("Artist").setTabListener(this));
        getActionBar().addTab(getActionBar().newTab().setText("Label").setTabListener(this));
        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mSearchView.setIconified(true);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(query) && query.length() >= 2) {
                SearchResultsListFragment searchResultsListFragment = (SearchResultsListFragment) getSupportFragmentManager().findFragmentById(R.id.searchResultsListFragment);
                searchResultsListFragment.search(query, type);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        mSearchView.setIconified(true);
        MenuItemCompat.collapseActionView(searchItem);
        setTitle("Search: " + s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        type = (String) tab.getText();
        if (!TextUtils.isEmpty(query)) {
            SearchResultsListFragment searchResultsListFragment = (SearchResultsListFragment) getSupportFragmentManager().findFragmentById(R.id.searchResultsListFragment);
            searchResultsListFragment.search(query, type);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }
}