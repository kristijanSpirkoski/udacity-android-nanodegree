package com.example.movies_stage1.utilities;

import com.example.movies_stage1.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

    private static final String RESULTS_KEY = "results";
    private static final String TITLE_KEY = "original_title";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String IMAGE_PATH_KEY = "poster_path";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String OVERVIEW_KEY = "overview";


    public static Movie[] parseJsonData(String jsonString) {

        Movie[] movies = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(RESULTS_KEY);
            int numberOfMovies = jsonArray.length();
            movies = new Movie[numberOfMovies];
            for(int i=0; i<numberOfMovies; i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);
                movies[i] = new Movie(movieObject.getString(TITLE_KEY),
                        movieObject.getString(RELEASE_DATE_KEY),
                        movieObject.getString(IMAGE_PATH_KEY),
                        movieObject.getInt(VOTE_AVERAGE_KEY),
                        movieObject.getString(OVERVIEW_KEY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
