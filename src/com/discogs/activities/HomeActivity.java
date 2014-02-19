package com.discogs.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import com.discogs.Constants;
import com.discogs.R;
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

public class HomeActivity extends Activity implements View.OnClickListener {
    private OAuthHelper mOAuthHelper = new OAuthHelper();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.continueButton).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);
    }

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
                NetworkHelper networkHelper = new NetworkHelper(HomeActivity.this);
                final String response = networkHelper.doHTTPGet("http://api.discogs.com/oauth/identity");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String userName = jsonObject.getString("username");
                            SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit();
                            editor.putString("userName", userName);
                            editor.commit();
                            startActivity(new Intent(HomeActivity.this, DashboardActivity.class));
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.loginButton) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String authUrl = mOAuthHelper.retrieveRequestToken();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(HomeActivity.this, WebActivity.class);
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
        } else if (id == R.id.continueButton) {
            startActivity(new Intent(HomeActivity.this, DashboardActivity.class));
        }
    }
}