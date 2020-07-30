package com.example.bakingtime.utils;

import com.example.bakingtime.models.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtils {

    public static Call<ArrayList<Recipe>> initializeRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(JsonBakingApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonBakingApi jsonBakingApi = retrofit.create(JsonBakingApi.class);

        Call<ArrayList<Recipe>> call = jsonBakingApi.getRecipesFromNetwork();
        return call;
    }


}
