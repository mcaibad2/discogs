package com.discogs.utils;

import com.discogs.Constants;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class OAuthHelper {
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "http://api.discogs.com/oauth/request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "http://api.discogs.com/oauth/access_token";
    private static final String AUTHORIZATION_WEBSITE_URL = "http://www.discogs.com/oauth/authorize";
    public final static String CALLBACK_URL = "callback://discogs";

    private OAuthConsumer mOAuthConsumer;
    private OAuthProvider mOAuthProvider;

    public OAuthHelper() {
        this.mOAuthConsumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        this.mOAuthProvider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL, AUTHORIZATION_WEBSITE_URL);
        this.mOAuthProvider.setOAuth10a(true);
    }

    public String retrieveRequestToken() throws OAuthCommunicationException, OAuthExpectationFailedException
            , OAuthNotAuthorizedException, OAuthMessageSignerException {
        String authUrl = mOAuthProvider.retrieveRequestToken(mOAuthConsumer, CALLBACK_URL);
        return authUrl;
    }

    public String[] retrieveAccessToken(String verifier) throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {
        mOAuthProvider.retrieveAccessToken(mOAuthConsumer, verifier);
        return new String[] {
            mOAuthConsumer.getToken(), mOAuthConsumer.getTokenSecret()
        };
    }
}
