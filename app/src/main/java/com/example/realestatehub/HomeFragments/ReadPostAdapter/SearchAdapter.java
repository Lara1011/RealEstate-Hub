package com.example.realestatehub.HomeFragments.ReadPostAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestatehub.R;

import java.util.HashMap;

// Adapter class for handling post data in a RecyclerView
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    // HashMap to store post details with postId as key
    private HashMap<String, HashMap<String, String>> postList;
    Context context;

    // Constructor for initializing the adapter with post data
    public SearchAdapter(HashMap<String, HashMap<String, String>> postList) {
        this.postList = postList;
    }

    // Method to create new ViewHolder for each item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to each ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Extract postId and corresponding post details
        String postId = (String) postList.keySet().toArray()[position];
        HashMap<String, String> post = postList.get(postId);

        if (post.get("keyword") != null)
            holder.keywordTextView.setText(getSafeValue(post.get("keyword")));
        if (post.get("filter") != null)
            holder.filterTextView.setText(getSafeValue(post.get("filter")));


    }

    // Method to get the count of items in the adapter
    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder class for holding view references
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView keywordTextView;
        public TextView filterTextView;


        public ViewHolder(@NonNull View itemView ) {
            super(itemView);
            // Initialize view references
            keywordTextView = itemView.findViewById(R.id.searchTextView);
            filterTextView = itemView.findViewById(R.id.filterTextView);

        }
    }

    // Helper method to safely handle null strings
    private String getSafeValue(String value) {
        return value != null ? value : "";
    }
}
