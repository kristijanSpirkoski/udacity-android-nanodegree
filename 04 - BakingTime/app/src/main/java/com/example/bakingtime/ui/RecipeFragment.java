package com.example.bakingtime.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Ingredient;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.models.Step;
import com.example.bakingtime.utils.AppExecutors;

import java.util.ArrayList;

public class RecipeFragment extends Fragment {

    private TextView ingredientView;
    private RecyclerView recyclerView;

    private Recipe activityRecipe;
    private int activityRecipeId;

    private DetailListAdapter mAdapter;
    private Context activityContext;

    public RecipeFragment(int id) {
        this.activityRecipeId = id;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_list, container);

        recyclerView = view.findViewById(R.id.recipe_steps_recycler_view);
        ingredientView = view.findViewById(R.id.recipe_ingredients);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new DetailListAdapter(activityContext);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);

        AppDatabase mDb = AppDatabase.getInstance(getActivity());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                activityRecipe = mDb.recipeDao().getRecipeById(activityRecipeId);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder ingredientText = new StringBuilder("");
                        for(Ingredient i : activityRecipe.getIngredients()) {
                            ingredientText.append(i.getQuantity() +
                                    ( !i.getMeasure().equals("UNIT") ? i.getMeasure() : "" ) +
                                    " " + i.getIngredient() + "\n");
                        }
                        ingredientView.setText(ingredientText);

                        mAdapter.setStepData(activityRecipe.getSteps());
                    }
                });
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
