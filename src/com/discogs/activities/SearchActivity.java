package com.discogs.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.discogs.R;
import com.discogs.fragments.ResultFragment;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, ActionBar.TabListener {
    private SearchView mSearchView;
    private MenuItem searchItem;
    private String query;
    private ResultFragment allResultFragment;
    private ResultFragment releaseResultFragment;
    private ResultFragment masterResultFragment;
    private ResultFragment artistResultFragment;
    private ResultFragment labelResultFragment;
    private boolean wasCreated;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("All").setTabListener(this));
        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("Release").setTabListener(this));
        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("Master").setTabListener(this));
        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("Artist").setTabListener(this));
        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("Label").setTabListener(this));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        allResultFragment = new ResultFragment();
        transaction.add(R.id.mainLayout, allResultFragment, "all");

        releaseResultFragment = new ResultFragment();
        transaction.add(R.id.mainLayout, releaseResultFragment, "release");
        transaction.hide(releaseResultFragment);

        masterResultFragment = new ResultFragment();
        transaction.add(R.id.mainLayout, masterResultFragment, "master");
        transaction.hide(masterResultFragment);

        artistResultFragment = new ResultFragment();
        transaction.add(R.id.mainLayout, artistResultFragment, "artist");
        transaction.hide(artistResultFragment);

        labelResultFragment = new ResultFragment();
        transaction.add(R.id.mainLayout, labelResultFragment, "label");
        transaction.hide(labelResultFragment);

        transaction.commit();

        wasCreated = true;
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
            if (!TextUtils.isEmpty(query) && query.length() > 2) {
                allResultFragment.search("http://api.discogs.com/database/search?q=" + query);
                releaseResultFragment.search("http://api.discogs.com/database/search?q=" + query + "&type=release");
                masterResultFragment.search("http://api.discogs.com/database/search?q=" + query + "&type=master");
                artistResultFragment.search("http://api.discogs.com/database/search?q=" + query + "&type=artist");
                labelResultFragment.search("http://api.discogs.com/database/search?q=" + query + "&type=label");
            } else {
                Toast.makeText(this, "Query is empty", Toast.LENGTH_SHORT).show();
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

    public void showProgress() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    public void hideProgress() {
        setSupportProgressBarIndeterminateVisibility(false);
    }

    public void showTabs() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    public void hideTabs() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        mSearchView.setIconified(true);
        MenuItemCompat.collapseActionView(searchItem);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    /**********************
     * Tab Change Listener
     **********************/

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        if (wasCreated == false) {
            return;
        }

        if (tab.getText().equals("All")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.show(allResultFragment);
            transaction.hide(releaseResultFragment);
            transaction.hide(masterResultFragment);
            transaction.hide(artistResultFragment);
            transaction.hide(labelResultFragment);
            transaction.commit();
        } else if (tab.getText().equals("Release")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(allResultFragment);
            transaction.show(releaseResultFragment);
            transaction.hide(masterResultFragment);
            transaction.hide(artistResultFragment);
            transaction.hide(labelResultFragment);
            transaction.commit();
        } else if (tab.getText().equals("Master")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(allResultFragment);
            transaction.hide(releaseResultFragment);
            transaction.show(masterResultFragment);
            transaction.hide(artistResultFragment);
            transaction.hide(labelResultFragment);
            transaction.commit();
        } else if (tab.getText().equals("Artist")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(allResultFragment);
            transaction.hide(releaseResultFragment);
            transaction.hide(masterResultFragment);
            transaction.show(artistResultFragment);
            transaction.hide(labelResultFragment);
            transaction.commit();
        } else if (tab.getText().equals("Label")) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(allResultFragment);
            transaction.hide(releaseResultFragment);
            transaction.hide(masterResultFragment);
            transaction.hide(artistResultFragment);
            transaction.show(labelResultFragment);
            transaction.commit();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}