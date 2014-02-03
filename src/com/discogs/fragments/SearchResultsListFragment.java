package com.discogs.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.discogs.R;
import com.discogs.adapters.SearchResultsAdapter;
import com.discogs.json.JSONHelper;
import com.discogs.model.Result;
import com.discogs.network.NetworkHelper;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsListFragment extends ListFragment {
    private List<Result> results = new ArrayList<Result>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setListAdapter(new SearchResultsAdapter(getActivity(), results));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void search(final String query, final String type) {
        if (!TextUtils.isEmpty(type)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONHelper jsonHelper = new JSONHelper();
                    String json = null;
                    if (type.equalsIgnoreCase("all")) {
                        json = NetworkHelper.doHTTPGet("http://api.discogs.com/database/search?q=" + query);
                        results = jsonHelper.getResults(json);
                    } else {
                        json = NetworkHelper.doHTTPGet("http://api.discogs.com/database/search?q=" + query + "&type=" + StringUtils.uncapitalize(type));
                        results = jsonHelper.getResults(json);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().findViewById(R.id.textView).setVisibility(View.GONE);
                            SearchResultsAdapter searchResultsAdapter = (SearchResultsAdapter) getListAdapter();
                            searchResultsAdapter.setResults(results);
                            searchResultsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            thread.start();
        }
    }
}