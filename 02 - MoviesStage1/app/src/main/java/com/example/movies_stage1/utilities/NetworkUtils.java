package com.example.movies_stage1.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    public static String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie";
    public static String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/original";
    public static String TOP_RATED_SEARCH_TYPE = "/top_rated";
    public static String POPULAR_SEARCH_TYPE = "/popular";


    private static final String API_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String PAGE_PARAM = "page";

    private static String key = "d952b43015b0a1ca4ae0cbe90960cc98";
    private static String languageChoice = "en-US";
    private static int pageNumber = 1;

    public static URL buildUrl(String searchType) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + searchType).buildUpon()
                .appendQueryParameter(API_PARAM, key)
                .appendQueryParameter(LANGUAGE_PARAM, languageChoice)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(pageNumber))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static String getResultHTTP(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }  catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return null;
    }
}
