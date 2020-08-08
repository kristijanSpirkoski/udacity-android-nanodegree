package com.example.bakingtime;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.bakingtime.ui.MainActivity;
import com.example.bakingtime.ui.RecipeDetailActivity;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4.class)
public class NavigateBetweenRecipesTest {
    @Rule
    public ActivityTestRule<RecipeDetailActivity> activityTestRule
            = new IntentsTestRule<RecipeDetailActivity>(RecipeDetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            ArrayList<Integer> recipeIds = new ArrayList<>();
            recipeIds.add(1);
            recipeIds.add(2);
            recipeIds.add(3);
            recipeIds.add(4);
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.EXTRA_RECIPE_ID_KEY, 1);
            bundle.putIntegerArrayList(MainActivity.EXTRA_RECIPE_IDS_KEY, recipeIds);
            intent.putExtras(bundle);
            return intent;
        }
    };


    @Test
    public void clickForward_opensNewRecipe() {


        onView(ViewMatchers.withId(R.id.forward_recipe_button))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.recipe_ingredients_label))
                .check(ViewAssertions.matches(ViewMatchers.withText("Brownies Ingredients")));
    }

    public void backClickEdgeCase() {


        onView(ViewMatchers.withId(R.id.back_recipe_button))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.recipe_ingredients))
                .check(ViewAssertions.matches(ViewMatchers.withText("Nutella Pie")));

    }

}
