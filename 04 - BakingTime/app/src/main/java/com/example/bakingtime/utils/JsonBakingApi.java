package com.example.bakingtime.utils;

import com.example.bakingtime.models.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonBakingApi {

    String RELATIVE_URL = "topher/2017/May/59121517_baking/baking.json";
    String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    @GET(RELATIVE_URL)
    Call<ArrayList<Recipe>> getRecipesFromNetwork();
}
