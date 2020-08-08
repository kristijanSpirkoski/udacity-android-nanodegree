package com.example.bakingtime.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bakingtime.models.Recipe;

import java.util.ArrayList;
import java.util.List;

import static android.icu.text.MessagePattern.ArgType.SELECT;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id")
    List<Recipe> getRecipes();

    @Query("SELECT * FROM recipes WHERE id=:id")
    Recipe getRecipeById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipes(ArrayList<Recipe> recipes);

}
