package com.example.bakingtime.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingtime.R;
import com.example.bakingtime.models.Recipe;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class MasterListAdapter extends RecyclerView.Adapter<MasterListAdapter.RecipeViewHolder> {

    ArrayList<Recipe> recipes;
    OnRecipeCardClickListener mListener;


    public interface OnRecipeCardClickListener {
        void onRecipeCardClick(int id);
    }

    public MasterListAdapter(Context context) {
        mListener = (OnRecipeCardClickListener) context;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_item, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.recipeName.setText(recipe.getName());
        holder.servingsView.append(" " + recipe.getServings());
        String difficulty = null;
        int numberOfSteps = recipe.getSteps().size();
        if(numberOfSteps < 4) {
            difficulty = "Easy";
        } else if(numberOfSteps >=4 && numberOfSteps < 8) {
            difficulty = "Medium";
        } else {
            difficulty = "Hard";
        }
        holder.difficultyView.append(" " + difficulty);

        String imageURL = recipe.getImage();
        try {
            new URL(imageURL).toURI();
            Picasso.get().load(imageURL).into(holder.recipeImage);
        } catch(MalformedURLException | URISyntaxException e) {
            holder.errorView.setVisibility(View.VISIBLE);
            Log.i("Exception: ", "ImageUrl invalid");
        }

    }

    @Override
    public int getItemCount() {
        if (recipes == null) {
            return 0;
        } else {
            return recipes.size();
        }
    }

    public void setRecipeData(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView recipeImage;
        private TextView recipeName;
        private TextView servingsView;
        private TextView difficultyView;
        private TextView errorView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeImage = itemView.findViewById(R.id.recipe_item_image);
            recipeName = itemView.findViewById(R.id.recipe_item_name);
            servingsView = itemView.findViewById(R.id.recipe_item_servings_label);
            difficultyView = itemView.findViewById(R.id.recipe_item_difficulty_label);
            errorView = itemView.findViewById(R.id.image_unavailable_label);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            int id = recipes.get(clickedPosition).getId();
            mListener.onRecipeCardClick(id);
        }
    }

}
