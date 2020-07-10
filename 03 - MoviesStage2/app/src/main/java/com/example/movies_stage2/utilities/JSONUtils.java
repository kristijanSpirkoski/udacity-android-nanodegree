package com.example.movies_stage2.utilities;

import com.example.movies_stage2.models.Movie;
import com.example.movies_stage2.models.Review;
import com.example.movies_stage2.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    private static final String RESULTS_KEY = "results";
    private static final String TITLE_KEY = "original_title";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String IMAGE_PATH_KEY = "poster_path";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String OVERVIEW_KEY = "overview";
    private static final String ID_KEY = "id";

    private static final String AUTHOR_KEY = "author";
    private static final String CONTENT_KEY = "content";

    private static final String TYPE_KEY = "type";
    private static final String TRAILER_NAME_KEY = "name";
    private static final String LINK_KEY = "key";


    public static Movie[] parseJsonMovieData(String jsonString) {

        Movie[] movies = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(RESULTS_KEY);
            int numberOfMovies = jsonArray.length();
            movies = new Movie[numberOfMovies];
            for(int i=0; i<numberOfMovies; i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);
                movies[i] = new Movie(movieObject.getInt(ID_KEY),
                        movieObject.getString(TITLE_KEY),
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
    public static Review[] parseJsonReviewData(String jsonString){

        Review[] reviews = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(RESULTS_KEY);
            int numberOfReviews = jsonArray.length();
            reviews = new Review[numberOfReviews];

            for(int i=0; i<numberOfReviews; i++) {
                JSONObject reviewObject = jsonArray.getJSONObject(i);
                reviews[i] = new Review(reviewObject.getString(AUTHOR_KEY), reviewObject.getString(CONTENT_KEY));
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
    public static ArrayList<Trailer> parseJsonTrailerData(String jsonString) {

        ArrayList<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(RESULTS_KEY);
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject trailerObject = jsonArray.getJSONObject(i);
                if(trailerObject.getString(TYPE_KEY).equals("Trailer")) {
                    trailers.add(new Trailer(trailerObject.getString(TRAILER_NAME_KEY), trailerObject.getString(LINK_KEY)));
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }
}
