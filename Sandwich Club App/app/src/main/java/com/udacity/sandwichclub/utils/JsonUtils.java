package com.udacity.sandwichclub.utils;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) {

        String mainName = null;
        List<String> alsoKnownAs = new ArrayList<>();
        String origin = null;
        String description = null;
        String image = null;
        List<String> ingredients = new ArrayList<>();

        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            JSONObject sandwichJsonObject = new JSONObject(json);

            JSONObject nameJsonObject = sandwichJsonObject.getJSONObject("name");
            mainName = nameJsonObject.getString("mainName");
            JSONArray alsoKnownAsJsonArray = nameJsonObject.getJSONArray("alsoKnownAs");
            if (alsoKnownAsJsonArray != null) {
                for (int i = 0; i < alsoKnownAsJsonArray.length(); i++) {
                    alsoKnownAs.add(alsoKnownAsJsonArray.getString(i));
                }
            }

            origin = sandwichJsonObject.getString("placeOfOrigin");
            description = sandwichJsonObject.getString("description");
            image = sandwichJsonObject.getString("image");
            JSONArray ingredientsJsonArray = sandwichJsonObject.getJSONArray("ingredients");
            if (ingredientsJsonArray != null) {
                for (int i = 0; i < ingredientsJsonArray.length(); i++) {
                    ingredients.add(ingredientsJsonArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Sandwich(mainName, alsoKnownAs, origin, description, image, ingredients);
    }
}
