package com.example.realestatehub;

import junit.framework.TestCase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.example.realestatehub.LogIn.ConnectingActivity;

@RunWith(AndroidJUnit4.class)
public class InsideProfileFragmentUITest extends TestCase {

    @Rule
    public ActivityScenarioRule<ConnectingActivity> activityRule =
            new ActivityScenarioRule<>(ConnectingActivity.class);

    @Test
    public void EditProfileTest() throws InterruptedException {
        onView(withId(R.id.emailEditText))
                .perform(typeText("malak.qeedan@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.signInButton))
                .perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.profile))
                .perform(click());
        Thread.sleep(500);
        onView(withId(R.id.InsideProfileLayout)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.saveOrEditButton)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.firstNameEditText))
                .perform(clearText()).perform(typeText("M"), closeSoftKeyboard());
        Thread.sleep(500);
        onView(withId(R.id.lastNameEditText))
                .perform(clearText()).perform(typeText("Q"), closeSoftKeyboard());
        Thread.sleep(500);
        onView(withId(R.id.saveOrEditButton))
                .perform(click());
        Thread.sleep(500);
        onView(withId(R.id.backButton))
                .perform(click());
        Thread.sleep(500);
        onView(withId(R.id.UserFullNameTextView)).check(matches(withText("Welcome\n"+ "M Q")));

    }

}