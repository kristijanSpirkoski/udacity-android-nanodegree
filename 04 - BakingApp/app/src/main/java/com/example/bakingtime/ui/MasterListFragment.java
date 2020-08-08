package com.example.bakingtime.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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


    private MasterListAdapter mAdapter;
    private Context activityContext;


    private ArrayList<Recipe> recipes;

    public MasterListFragment(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
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


        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        RecyclerView recyclerView = rootView.findViewById(R.id.recipe_card_recycler_view);
        mAdapter = new MasterListAdapter(activityContext);


        recyclerView.setAdapter(mAdapter);

        if(isTablet) {
            GridLayoutManager layoutManager = new GridLayoutManager(activityContext, 2);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
            recyclerView.setLayoutManager(layoutManager);
        }
         mAdapter.setRecipeData(recipes);

         return super.onCreateView(inflater, container, savedInstanceState);
    }
}
