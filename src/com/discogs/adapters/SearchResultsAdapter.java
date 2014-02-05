package com.discogs.adapters;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.discogs.R;
import com.discogs.model.Result;
import org.apache.commons.lang.StringUtils;

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
            viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.labelTextView);
            viewHolder.countryTextView = (TextView) convertView.findViewById(R.id.countryTextView);
            viewHolder.yearTextView = (TextView) convertView.findViewById(R.id.yearTextView);
            viewHolder.catTextView = (TextView) convertView.findViewById(R.id.catTextView);
            viewHolder.formatTextView = (TextView) convertView.findViewById(R.id.formatTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.titleTextView.setText(Html.fromHtml(result.getTitle()));
        String type = result.getType();
        if (type.equalsIgnoreCase("release") || type.equalsIgnoreCase("master")) {
            viewHolder.labelTextView.setText(StringUtils.remove(StringUtils.remove(result.getLabel(), "["), "]"));
            viewHolder.labelTextView.setVisibility(View.VISIBLE);
            viewHolder.countryTextView.setText(result.getCountry());
            viewHolder.yearTextView.setText(result.getYear());
            viewHolder.catTextView.setText(result.getCatno());
            viewHolder.formatTextView.setText(TextUtils.join(", ", result.getFormats()));
        } else {
            viewHolder.labelTextView.setVisibility(View.GONE);
            viewHolder.labelTextView.setVisibility(View.GONE);
            viewHolder.countryTextView.setVisibility(View.GONE);
            viewHolder.yearTextView.setVisibility(View.GONE);
            viewHolder.catTextView.setVisibility(View.GONE);
            viewHolder.formatTextView.setVisibility(View.GONE);
        }
        return convertView;
    }

    static private class ViewHolder {
        TextView titleTextView;
        TextView labelTextView;
        TextView countryTextView;
        TextView yearTextView;
        TextView catTextView;
        TextView formatTextView;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}