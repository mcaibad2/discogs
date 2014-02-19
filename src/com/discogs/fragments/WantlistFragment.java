package com.discogs.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class WantlistFragment extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        /*
        String url = "http://api.discogs.com/users/mcaibad2/wants";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONHelper jsonHelper = new JSONHelper();
                List<Want> wants = jsonHelper.listReleasesInWantlist(response);
                setListAdapter(new WantAdapter(getActivity(), wants));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(request);
        */
    }
}