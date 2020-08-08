package com.example.bakingtime.database;

import androidx.room.TypeConverter;

import com.example.bakingtime.models.Ingredient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class IngredientListConverter {
    @TypeConverter
    public static ArrayList<Ingredient> fromString(String string) {
        Type listType = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        return new Gson().fromJson(string, listType);
    }
    @TypeConverter
    public static String fromList(ArrayList<Ingredient> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
