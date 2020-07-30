package com.example.bakingtime.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.utils.AppExecutors;
import com.example.bakingtime.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class MasterListFragment extends Fragment {

    public static final String SHARED_PREFERENCES_NAME = "myprefs";
    private final String DATABASE_EMPTY_KEY = "database_key";

    private MasterListAdapter mAdapter;
    private Context activityContext;

    public MasterListFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container);

        RecyclerView recyclerView = rootView.findViewById(R.id.recipe_card_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        mAdapter = new MasterListAdapter(activityContext);

        AppDatabase mDb = AppDatabase.getInstance(activityContext);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isDbEmpty = sharedPreferences.getBoolean(DATABASE_EMPTY_KEY, true);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);

        if(isDbEmpty) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DATABASE_EMPTY_KEY, false);
            editor.apply();

            Call call = NetworkUtils.initializeRetrofit();
            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ArrayList<Recipe> recipes = (ArrayList<Recipe>) call.execute().body();

                        if (recipes != null) {
                            mDb.recipeDao().insertRecipes(recipes);
                            Log.i("DBTAG", "recipes inserted in DB");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("DBTAG", "recipes populated in fragment UI");
                                    mAdapter.setRecipeData(recipes);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("Network error", "Unable to talk to server");
                    }
                }
            });
        } else {
            Log.i("DBTAG", "recipes loaded from db");

            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Recipe> recipes = (ArrayList<Recipe>) AppDatabase.getInstance(getActivity())
                            .recipeDao().getRecipes();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setRecipeData(recipes);
                        }
                    });
                }
            });
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
