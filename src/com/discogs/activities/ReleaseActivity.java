package com.discogs.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.discogs.R;
import com.discogs.json.JSONHelper;
import com.discogs.model.*;
import com.discogs.network.VolleySingleton;
import com.discogs.utils.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.List;

public class ReleaseActivity extends ActionBarActivity {
    private StringBuffer stringBuffer = new StringBuffer();
    private LayoutInflater layoutInflater;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        this.layoutInflater = LayoutInflater.from(this);
        String url = getIntent().getExtras().getString("url");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONHelper jsonHelper = new JSONHelper();
                Release release = jsonHelper.getRelease(response);
                showUI(release);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReleaseActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void showUI(Release release) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        View content = findViewById(R.id.content);
        content.setVisibility(View.VISIBLE);
        if (release != null) {
            // Artists
            TextView artistsTextView = (TextView) findViewById(R.id.artistsTextView);
            List<Artist> artists = release.getArtists();
            String artistName = null;

            if (artists != null && artists.size() > 0) {
                stringBuffer.setLength(0);

                if (artists.size() == 1) {
                    Artist artist = artists.get(0);
                    stringBuffer.append(artist.getName());
                    artistName = stringBuffer.toString();
                    artistsTextView.setText(artistName);
                } else {
                    for (Artist artist : artists) {
                        stringBuffer.append(artist.getName());
                        stringBuffer.append(", ");
                    }

                    artistName = StringUtils.strip(stringBuffer.toString(), ", ");
                    artistsTextView.setText(artistName);
                }
            }

            // Title
            TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
            titleTextView.setText(release.getTitle());

            // Labels
            TextView labelTextView = (TextView) findViewById(R.id.labelTextView);

            if (release.getLabels() != null) {
                if (release.getLabels().size() > 1) {
                    stringBuffer.setLength(0);

                    for (Label label : release.getLabels()) {
                        stringBuffer.append(label.getName());
                        stringBuffer.append(" - ");
                        stringBuffer.append(label.getCatNo());
                        stringBuffer.append(", ");
                    }
                } else {
                    stringBuffer.setLength(0);

                    Label label = release.getLabels().get(0);
                    stringBuffer.append(label.getName());
                    stringBuffer.append(" - ");
                    stringBuffer.append(label.getCatNo());
                }

                labelTextView.setText(stringBuffer.toString());
            } else {
                findViewById(R.id.labelTableRow).setVisibility(View.GONE);
            }

            TextView countryTextView = (TextView) findViewById(R.id.countryTextView);

            if (release.getCountry() == null) {
                findViewById(R.id.countryTableRow).setVisibility(View.GONE);
            } else {
                countryTextView.setText(release.getCountry());
            }

            TextView releasedTextView = (TextView) findViewById(R.id.releasedTextView);

            if (release.getReleased() == null) {
                findViewById(R.id.releasedTableRow).setVisibility(View.GONE);
            } else {
                releasedTextView.setText(release.getReleased());
            }

            // Genres
            TextView genreTextView = (TextView) findViewById(R.id.genreTextView);

            if (release.getGenres() != null) {
                if (release.getGenres().size() > 1) {
                    stringBuffer.setLength(0);

                    for (String genre : release.getGenres()) {
                        stringBuffer.append(genre);
                        stringBuffer.append(", ");
                    }

                    genreTextView.setText(StringUtils.strip(stringBuffer.toString(), ", "));
                } else {
                    genreTextView.setText(release.getGenres().get(0));
                }
            } else {
                findViewById(R.id.genreTableRow).setVisibility(View.GONE);
            }

            // Styles
            TextView styleTextView = (TextView) findViewById(R.id.styleTextView);
            List<String> styles = release.getStyles();

            if (styles != null && styles.size() > 0) {
                if (styles.size() > 1) {
                    stringBuffer.setLength(0);

                    for (String style : styles) {
                        stringBuffer.append(style);
                        stringBuffer.append(", ");
                    }

                    String labels = stringBuffer.toString();
                    styleTextView.setText(TextUtils.substring(labels, 0, labels.length() - 2));
                } else {
                    styleTextView.setText(styles.get(0));
                }
            } else {
                findViewById(R.id.styleTableRow).setVisibility(View.GONE);
            }

            // Gallery
            Gallery gallery = (Gallery) findViewById(R.id.gallery);
            NetworkImageView imageView = (NetworkImageView) findViewById(R.id.imageView);
            imageView.setDefaultImageResId(R.drawable.ic_release);
            imageView.setImageUrl(release.getImages().get(0).getUri(), VolleySingleton.getInstance(this).getImageLoader());

            TextView notesTextView = (TextView) findViewById(R.id.notesTextView);

            if (release.getNotes() == null) {
                notesTextView.setVisibility(View.GONE);

                TextView notesHeader = (TextView) findViewById(R.id.notesHeader);
                notesHeader.setVisibility(View.GONE);
            } else {
                notesTextView.setText(Html.fromHtml(release.getNotes()));
            }

            // Tracks
            TableLayout tracklistLayout = (TableLayout) findViewById(R.id.tracklistLayout);
            tracklistLayout.setColumnShrinkable(2, true);
            tracklistLayout.setColumnStretchable(2, true);

            for (Track track : release.getTracks()) {
                TableRow tableRow = new TableRow(this);

                TextView textView = new TextView(this);
                textView.setText(track.getPosition());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                tableRow.addView(textView);

                textView = new TextView(this);

                if (!StringUtils.isEmpty(track.getPosition())) {
                    textView.setPadding(10, 0, 0, 0);
                }

                if (CollectionUtils.isEmpty(track.getArtists())) {
                    textView.setText(artistName);
                } else {
                    stringBuffer.setLength(0);

                    for (Artist artist : track.getArtists()) {
                        stringBuffer.append(artist.getName());
                        stringBuffer.append("\n");
                    }

                    textView.setText(StringUtils.strip(stringBuffer.toString(), "\n"));
                }

                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                tableRow.addView(textView);

                textView = new TextView(this);
                textView.setPadding(10, 0, 0, 0);
                textView.setText(Html.fromHtml(track.getTitle()));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                tableRow.addView(textView);

                textView = new TextView(this);

                if (StringUtils.isEmpty(track.getDuration())) {
                } else {
                    textView.setText(track.getDuration());
                    textView.setPadding(10, 0, 0, 0);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                }

                tableRow.addView(textView);

                tracklistLayout.addView(tableRow);
            }

            // Formats
            TextView formatTextView = (TextView) findViewById(R.id.formatTextView);

            if (release.getFormats() != null) {
                if (release.getFormats().size() > 1) {
                    stringBuffer.setLength(0);

                    for (Format format : release.getFormats()) {
                        stringBuffer.append(format.getName());
                        stringBuffer.append(", ");
                    }

                    String labels = stringBuffer.toString();
                    formatTextView.setText(TextUtils.substring(labels, 0, labels.length() - 2));
                } else {
                    formatTextView.setText(release.getFormats().get(0).getName());
                }
            } else {
                findViewById(R.id.formatTableRow).setVisibility(View.GONE);
            }

            // Companies
            TextView companiesTextView = (TextView) findViewById(R.id.companiesTextView);

            if (release.getCompanies() != null && release.getCompanies().size() > 0) {
                if (release.getCompanies().size() > 1) {
                    stringBuffer.setLength(0);

                    for (Label label : release.getCompanies()) {
                        stringBuffer.append(label.getEntityTypeName());
                        stringBuffer.append(" - ");
                        stringBuffer.append(label.getName());
                        stringBuffer.append("\n");
                    }

                    String labels = stringBuffer.toString();
                    companiesTextView.setText(TextUtils.substring(labels, 0, labels.length() - 2));
                } else {
                    Label company = release.getCompanies().get(0);
                    stringBuffer.setLength(0);
                    stringBuffer.append(company.getEntityTypeName());
                    stringBuffer.append(", ");
                    stringBuffer.append(company.getName());
                    companiesTextView.setText(stringBuffer.toString());
                }
            } else {
                companiesTextView.setVisibility(View.GONE);
                findViewById(R.id.companiesHeader).setVisibility(View.GONE);
            }

            // Videos
            TextView videosHeader = (TextView) findViewById(R.id.videosHeader);
            LinearLayout videosLayout = (LinearLayout) findViewById(R.id.videosLayout);
            List<Video> videos = release.getVideos();

            if (videos != null && videos.size() > 0) {
                for (final Video video : videos) {
                    LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.layout_video, null);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUri())));
                        }
                    });

                    TextView mTitleTextView = (TextView) linearLayout.findViewById(R.id.titleTextView);
                    mTitleTextView.setText(video.getTitle());

                    // TextView mDescriptionTextView = (TextView) linearLayout.findViewById(R.id.descriptionTextView);
                    // mDescriptionTextView.setText(video.getDescription());

                    TextView mDurationTextView = (TextView) linearLayout.findViewById(R.id.durationTextView);

                    if (video.getDuration() > 0) {
                        mDurationTextView.setText(Utils.splitToComponentTimes(Long.valueOf(video.getDuration())));
                    } else {
                        mDurationTextView.setVisibility(View.GONE);
                    }

                    videosLayout.addView(linearLayout);
                }
            } else {
                videosHeader.setVisibility(View.GONE);
                videosLayout.setVisibility(View.GONE);
            }
        }
    }
}