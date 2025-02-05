package com.example.realestatehub;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.realestatehub.Utils.Database;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LogInTest {

    private Context appContext;
    private Database database;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = new Database(appContext);
    }

    @Test
    public void testLoginSuccess() throws InterruptedException {
        String testEmail = "malak.qeedan@hotmail.com";
        String testPassword = "123456";

        CountDownLatch latch = new CountDownLatch(1);

        database.login(testEmail, testPassword, new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
                latch.countDown();
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                fail("Login should succeed but failed with: " + errorMessage);
                latch.countDown();
            }
        });

        boolean awaitResult = latch.await(10, TimeUnit.SECONDS);

        if (!awaitResult) {
            fail("Test timed out before login completed");
        }
    }
}