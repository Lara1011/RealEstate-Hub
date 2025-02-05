package com.example.realestatehub.HomeFragments;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.realestatehub.Utils.Database;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

public class SearchFragmentTest {

    private SearchFragment searchFragment;

    @Mock
    private Database mockDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        searchFragment = new SearchFragment();
        searchFragment.setDatabase(mockDatabase);
        searchFragment.setPostList(new HashMap<>());
        searchFragment.setFilteredList(new HashMap<>());
    }

    @Test
    public void testFindMinMaxWithMockedFirebaseData() {
        doAnswer(invocation -> {
            Database.PostsCallback callback = invocation.getArgument(0);

            HashMap<String, HashMap<String, String>> mockPosts = new HashMap<>();

            HashMap<String, String> post1 = new HashMap<>();
            post1.put("Post Id", "post1");
            post1.put("Price", "50000");
            post1.put("Number Of Rooms", "3");

            HashMap<String, String> post2 = new HashMap<>();
            post2.put("Post Id", "post2");
            post2.put("Price", "1500000");
            post2.put("Number Of Rooms", "5");

            HashMap<String, String> post3 = new HashMap<>();
            post3.put("Post Id", "post3");
            post3.put("Price", "3000000");
            post3.put("Number Of Rooms", "2");

            mockPosts.put("post1", post1);
            mockPosts.put("post2", post2);
            mockPosts.put("post3", post3);

            callback.onSuccess(mockPosts);
            return null;
        }).when(mockDatabase).readPostsData(any(Database.PostsCallback.class));

        mockDatabase.readPostsData(new Database.PostsCallback() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, String>> postList) {
                searchFragment.setPostList(postList);

                String minPrice = "400000";
                String maxPrice = "2000000";

                searchFragment.findMinMax("Price", minPrice, maxPrice);

                assertNotNull(searchFragment.getFilteredList());
                assertFalse(searchFragment.getFilteredList().isEmpty());

                for (HashMap<String, String> post : searchFragment.getFilteredList().values()) {
                    int price = Integer.parseInt(post.get("Price"));
                    assertTrue(price >= Integer.parseInt(minPrice));
                    assertTrue(price <= Integer.parseInt(maxPrice));
                    assertTrue(post.get("Post Id").equals("post2"));
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                fail("Failed to fetch posts: " + errorMessage);
            }
        });

        verify(mockDatabase, times(1)).readPostsData(any(Database.PostsCallback.class));
    }
}
