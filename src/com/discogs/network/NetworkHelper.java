package com.discogs.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.discogs.Constants;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class NetworkHelper {
    private DefaultHttpClient httpClient = new DefaultHttpClient();
    private CommonsHttpOAuthConsumer mOAuthConsumer;

    public NetworkHelper(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = preferences.getString("token", null);
        String tokenSecret = preferences.getString("token_secret", null);
        if (token != null && tokenSecret != null) {
            CommonsHttpOAuthConsumer mOAuthConsumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
            mOAuthConsumer.setTokenWithSecret(token, tokenSecret);
            this.mOAuthConsumer = mOAuthConsumer;
        }
    }

    public String doHTTPGet(String url) {
        String response = null;
        HttpGet hTTPGet = new HttpGet(url);
        try {
            if (mOAuthConsumer != null) {
                mOAuthConsumer.sign(hTTPGet);
            }
            HttpResponse hTTPResponse = httpClient.execute(hTTPGet);
            response = EntityUtils.toString(hTTPResponse.getEntity());
            Log.d("Discogs", "Response: " + response);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Discogs", "Response: " + response);
        return response;
    }
}
