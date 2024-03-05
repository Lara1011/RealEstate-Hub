package com.example.realestatehub.HomeFragments.ReadPostAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestatehub.R;

import java.util.HashMap;

// Adapter class for handling post data in a RecyclerView
public class ReachAdapter extends RecyclerView.Adapter<ReachAdapter.ViewHolder> {
    // HashMap to store post details with postId as key
    private HashMap<String, HashMap<String, String>> postList;
    Context context;
    // Listener interface for click events
    private OnItemClickListener mListener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // Constructor for initializing the adapter with post data
    public ReachAdapter(HashMap<String, HashMap<String, String>> postList) {
        this.postList = postList;
    }

    // Method to create new ViewHolder for each item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reached_item_layout, parent, false);
        return new ViewHolder(view, mListener);
    }

    // Method to bind data to each ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Extract postId and corresponding post details
        String postId = (String) postList.keySet().toArray()[position];
        HashMap<String, String> post = postList.get(postId);

        // Set post name and username, if available
        holder.nameTextView.setText(getSafeValue(post.get("name")));
        if (post.get("userName") != null)
            holder.userTextView.setText(getSafeValue("by " + post.get("userName")));
        if (post.get("userName") != null)
            holder.userTextView.setText(getSafeValue("by " + post.get("userName")));
        if (post.get("phoneNumber") != null)
            holder.phoneNumberTextView.setText("Phone Number: " + getSafeValue(post.get("phoneNumber")));
        // Load the post image using Glide, if available, else set a default image
        if (post.get("photoUrl") != null && !post.get("photoUrl").isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.get("photoUrl"))
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.picture_img); // Set default or placeholder image
        }

        // Set an OnClickListener to the button for viewing post details
        holder.phoneNumberButton.setOnClickListener(view -> {
            String phoneNumber = getSafeValue(post.get("phoneNumber"));
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));

            context.startActivity(dialIntent);
        });
        holder.whatsAppButton.setOnClickListener(view -> {
            String phoneNumber = getSafeValue(post.get("phoneNumber"));
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?phone=" + phoneNumber));
            context.startActivity(whatsappIntent);
        });
    }

    // Method to get the count of items in the adapter
    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder class for holding view references
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView userTextView;
        public TextView phoneNumberTextView;
        public ImageView imageView;
        public Button phoneNumberButton;
        public Button whatsAppButton;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            // Initialize view references
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userTextView = itemView.findViewById(R.id.userTextView);
            phoneNumberTextView= itemView.findViewById(R.id.phoneNumberTextView);
            imageView = itemView.findViewById(R.id.imageView);
            phoneNumberButton = itemView.findViewById(R.id.callButton);
            whatsAppButton = itemView.findViewById(R.id.whatsappButton);
            // Set an OnClickListener to handle click events
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    // Helper method to safely handle null strings
    private String getSafeValue(String value) {
        return value != null ? value : "";
    }
}
