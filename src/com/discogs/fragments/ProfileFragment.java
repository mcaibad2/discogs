package com.discogs.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.discogs.DiscogsApplication;
import com.discogs.R;
import com.discogs.activities.DashboardActivity;
import com.discogs.json.JSONHelper;
import com.discogs.model.Profile;
import com.discogs.network.NetworkHelper;

public class ProfileFragment extends Fragment {
    private DashboardActivity mActivity;
    private DiscogsApplication mApplication;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (DashboardActivity) getActivity();
    }

    public void getProfile() {
        Log.d("Discogs", "Get identity information");
        mActivity.setSupportProgressBarIndeterminateVisibility(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHelper networkHelper = new NetworkHelper(getActivity());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String userName = preferences.getString("userName", null);
                String response = networkHelper.doHTTPGet("http://api.discogs.com//users/" + userName);
                JSONHelper jsonHelper = new JSONHelper();
                final Profile profile = jsonHelper.getProfile(response);
                mApplication.setProfile(profile);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProfile(profile);
                        mActivity.setSupportProgressBarIndeterminateVisibility(false);
                    }
                });
            }
        });
        thread.start();
    }

    private void showProfile(Profile profile) {
        TextView usernameTextView = (TextView) getView().findViewById(R.id.usernameTextView);
        usernameTextView.setText(profile.getUsername());
        TextView profileTextView = (TextView) getView().findViewById(R.id.profileTextView);
        profileTextView.setText(profile.getProfile());
        TextView homepageTextView = (TextView) getView().findViewById(R.id.homepageTextView);
        homepageTextView.setText(profile.getHomePage());
        TextView locationTextView = (TextView) getView().findViewById(R.id.locationTextView);
        locationTextView.setText(profile.getLocation());
        TextView registeredTextView = (TextView) getView().findViewById(R.id.registeredTextView);
        registeredTextView.setText(profile.getRegistered());
        TextView nameTextView = (TextView) getView().findViewById(R.id.nameTextView);
        nameTextView.setText(profile.getName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = preferences.getString("token", null);
        String tokenSecret = preferences.getString("token_secret", null);
        if (token != null && tokenSecret != null) {
            mApplication = ((DiscogsApplication) getActivity().getApplication());
            Profile profile = mApplication.getProfile();
            if (profile != null) {
                mActivity.setSupportProgressBarIndeterminateVisibility(true);
                showProfile(profile);
                mActivity.setSupportProgressBarIndeterminateVisibility(false);
            } else {
                getProfile();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}