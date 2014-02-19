package com.discogs.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.discogs.Constants;
import com.discogs.R;
import com.discogs.fragments.CollectionFragment;
import com.discogs.fragments.LoginDialogFragment;
import com.discogs.fragments.ProfileFragment;
import com.discogs.fragments.WantlistFragment;
import com.discogs.network.NetworkHelper;
import com.discogs.utils.OAuthHelper;
import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DashboardActivity extends ActionBarActivity implements LoginDialogFragment.LoginDialogListener{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationItems;
    private OAuthHelper mOAuthHelper = new OAuthHelper();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_USER_LOGIN) {
            if (resultCode == RESULT_OK) {
                final String verifier = getVerifier(data);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String accessToken[] = mOAuthHelper.retrieveAccessToken(verifier);
                            saveTokens(accessToken);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getIdentity();
                                }
                            });
                        } catch (OAuthMessageSignerException e) {
                            e.printStackTrace();
                        } catch (OAuthNotAuthorizedException e) {
                            e.printStackTrace();
                        } catch (OAuthExpectationFailedException e) {
                            e.printStackTrace();
                        } catch (OAuthCommunicationException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
    }

    private String getVerifier(Intent data) {
        String verifier = null;
        Uri uri = data.getData();
        if (uri != null && uri.toString().contains(OAuthHelper.CALLBACK_URL)) {
            Uri mUri = null;
            try {
                mUri = Uri.parse(URLDecoder.decode(uri.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            verifier = mUri.getQueryParameter(OAuth.OAUTH_VERIFIER);
        }
        return verifier;
    }

    private void saveTokens(String[] accessToken) {
        SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("token", accessToken[0]);
        editor.putString("token_secret", accessToken[1]);
        editor.commit();
    }

    private void getIdentity() {
        Log.d("Discogs", "Get identity information");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHelper networkHelper = new NetworkHelper(DashboardActivity.this);
                final String response = networkHelper.doHTTPGet("http://api.discogs.com/oauth/identity");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String userName = jsonObject.getString("username");
                            SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this).edit();
                            editor.putString("userName", userName);
                            editor.commit();

                            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
                            profileFragment.getProfile();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString("token", null);
        String tokenSecret = preferences.getString("token_secret", null);
        if (token == null || tokenSecret == null) {
            LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(getSupportFragmentManager(), "loginDialogFragment");
        }

        mTitle = mDrawerTitle = getTitle();
        mNavigationItems = getResources().getStringArray(R.array.nav_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavigationItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web menu_search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSearchRequested() {
        // startActivity(new Intent(this, SearchActivity.class));
        return super.onSearchRequested();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String authUrl = mOAuthHelper.retrieveRequestToken();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DashboardActivity.this, WebActivity.class);
                            intent.putExtra("authUrl", authUrl);
                            startActivityForResult(intent, Constants.REQUEST_CODE_USER_LOGIN);
                        }
                    });
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    e.printStackTrace();
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        if (position == 0) {
            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
            if (profileFragment == null) {
                profileFragment = new ProfileFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, profileFragment, "ProfileFragment").commit();
        } else if (position == 1) {
            CollectionFragment collectionFragment = new CollectionFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, collectionFragment, "CollectionFragment").commit();
        } else if (position == 2) {
            WantlistFragment wantlistFragment = new WantlistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, wantlistFragment, "WantlistFragment").commit();
        } else if (position == 3) {
            Toast.makeText(this, "Scan intent", Toast.LENGTH_SHORT).show();
        } else if (position == 4) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavigationItems[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}