package com.example.bakingtime;


import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.bakingtime.ui.MainActivity;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;


@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule
            = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void listItemClick_opensDetailActivity() {

        onView(Matchers.allOf(ViewMatchers.withId(R.id.recipe_card_recycler_view), ViewMatchers.isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(ViewMatchers.withId(R.id.recipe_ingredients_label)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

}
