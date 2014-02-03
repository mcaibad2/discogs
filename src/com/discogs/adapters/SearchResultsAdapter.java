package com.discogs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.discogs.R;
import com.discogs.model.Result;

import java.util.List;

public class SearchResultsAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Result> results;

    public SearchResultsAdapter(Context context, List<Result> results) {
        super();
        this.results = results;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return results.size();
    }

    public Result getItem(int item) {
        return results.get(item);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Result result = results.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_search_result, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.titleTextView.setText(result.getTitle());
        return convertView;
    }

    static private class ViewHolder {
        TextView titleTextView;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}