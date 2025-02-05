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
public class SignInActivityUITest extends TestCase {

    @Rule
    public ActivityScenarioRule<ConnectingActivity> activityRule =
            new ActivityScenarioRule<>(ConnectingActivity.class);

    @Test
    public void testSuccessfulLogin() throws InterruptedException {
        onView(withId(R.id.emailEditText))
                .perform(typeText("malak.qeedan@hotmail.com"), closeSoftKeyboard());

        onView(withId(R.id.passwordEditText))
                .perform(typeText("123456"), closeSoftKeyboard());

        onView(withId(R.id.signInButton))
                .perform(click());

        Thread.sleep(2000);

        onView(withId(R.id.bottomAppBar))
                .check(matches(isDisplayed()));
    }
}