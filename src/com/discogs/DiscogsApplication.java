package com.discogs;

import android.app.Application;
import com.discogs.model.Profile;

public class DiscogsApplication extends Application {
    private Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
