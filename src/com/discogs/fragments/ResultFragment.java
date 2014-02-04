package com.discogs.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.discogs.R;
import com.discogs.activities.SearchActivity;
import com.discogs.adapters.SearchResultsAdapter;
import com.discogs.json.JSONHelper;
import com.discogs.model.Result;
import com.discogs.network.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ResultFragment extends Fragment implements View.OnClickListener {
    private String query;
    private String prev;
    private String next;
    private View buttonsView;
    private Button previousButton;
    private Button nextButton;
    private SearchActivity mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (SearchActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        previousButton = (Button) view.findViewById(R.id.previousButton);
        previousButton.setOnClickListener(this);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        buttonsView = view.findViewById(R.id.buttonsView);
        return view;
    }

    public void search(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("pagination")) {
                    try {
                        JSONObject pagination = response.getJSONObject("pagination");
                        Log.d("Discogs", pagination.toString());
                        JSONObject urls = pagination.getJSONObject("urls");
                        if (urls.has("prev")) {
                            prev = urls.getString("prev");
                            previousButton.setEnabled(true);
                        } else {
                            prev = null;
                            previousButton.setEnabled(false);
                        }
                        if (urls.has("next")) {
                            next = urls.getString("next");
                            nextButton.setEnabled(true);
                        } else {
                            next = null;
                            nextButton.setEnabled(false);
                        }
                        if (TextUtils.isEmpty(next) && TextUtils.isEmpty(prev)) {
                            buttonsView.setVisibility(View.GONE);
                        } else {
                            buttonsView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSONHelper jsonHelper = new JSONHelper();
                List<Result> results = jsonHelper.getResults(response);
                ListView listView = (ListView) getView().findViewById(R.id.listView);
                listView.setAdapter(new SearchResultsAdapter(getActivity(), results));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        VolleySingleton.getInstance(mCallback).getRequestQueue().add(request);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.nextButton) {
            if (!TextUtils.isEmpty(next)) {
                search(next);
            }
        } else if (id == R.id.previousButton) {
            if (!TextUtils.isEmpty(prev)) {
                search(prev);
            }
        }
    }
}