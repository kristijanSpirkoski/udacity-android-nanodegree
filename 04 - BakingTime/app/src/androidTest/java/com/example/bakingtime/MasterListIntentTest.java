package com.example.bakingtime;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.runner.RunWith;
import com.example.bakingtime.ui.MainActivity;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MasterListIntentTest {


    private static final String EXTRA_RECIPE_ID_KEY = "extra_id";

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule
            = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void listItemClick_createsRecipeDetailIntent() {


        onView(Matchers.allOf(ViewMatchers.withId(R.id.recipe_card_recycler_view), ViewMatchers.isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Intents.intended(IntentMatchers.hasExtra(EXTRA_RECIPE_ID_KEY, 1));

    }
}
