package com.discogs.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkHelper {
    public static String doHTTPGet(String spec) {
        String response = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(spec);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Discogs/1.0 +http://daskasworld.appspot.com/");
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            int status = urlConnection.getResponseCode();
            Log.d("Discogs", String.valueOf(status));
            response = readStream(inputStream);
            Log.d("Discogs", response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }

    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
