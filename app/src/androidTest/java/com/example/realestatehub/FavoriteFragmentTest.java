package com.example.realestatehub;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.realestatehub.Utils.Database;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class FavoriteFragmentTest {

    private Database database;
    private Context context;

    @Before
    public void setUp() throws InterruptedException {
        context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        database = new Database(context);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        CountDownLatch latch = new CountDownLatch(1);

        auth.signInWithEmailAndPassword("laraabuhamad@gmail.com", "l1234567890")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        database.setFirebaseUser(auth.getCurrentUser());
                    } else {
                        throw new RuntimeException("Failed to log in user for test.");
                    }
                    latch.countDown();
                });

        latch.await(10, TimeUnit.SECONDS);

    }

    @Test
    public void testUpdateFavoriteData() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        database.updateFavoriteData(new Database.PostsCallback() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, String>> postList) {
                assertNotNull(postList);
                assertTrue(postList.containsKey("369bc269-1fc5-4572-99a4-0228f76148f8"));
                latch.countDown();
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                fail("Test failed with error: " + errorMessage);
                latch.countDown();
            }
        });

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue("Test timed out", completed);
    }

}