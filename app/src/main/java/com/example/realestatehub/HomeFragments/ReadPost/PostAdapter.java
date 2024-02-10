package com.example.realestatehub.HomeFragments.ReadPost;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;

// Define the PostAdapter class that extends RecyclerView.Adapter for custom ViewHolder.
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    // Declare a List to hold PostDetails objects.
    private List<PostDetails> postList;
    // Declare an OnItemClickListener interface to handle item clicks.
    private OnItemClickListener mListener;

    // Define the OnItemClickListener interface.
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set the listener for item clicks.
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // Constructor to initialize the PostAdapter with a list of PostDetails.
    public PostAdapter(List<PostDetails> postList) {
        this.postList = postList;
    }

    // Method to inflate the item layout and create ViewHolder.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Method to bind data to the ViewHolder elements based on the position of the item in the list.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostDetails post = postList.get(position);
        holder.nameTextView.setText(getSafeValue(post.getName()));
        if (post.userName != null)
            holder.userTextView.setText(getSafeValue("by " + post.userName));

        // Use Glide library to load an image into the ImageView, or set a default image if URL is null or empty.
        if (post.getPhotoUrl() != null && !post.getPhotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.getPhotoUrl())
                    .into(holder.imageView);
        } else {
             holder.imageView.setImageResource(R.drawable.picture_img);
        }

        // Set an OnClickListener to the "view details" button to start a new activity showing post details.
        holder.viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("postDetails", post);
                context.startActivity(intent);
            }
        });
    }

    // Method to return the total number of items in the list.
    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Define the ViewHolder class that holds references to the UI components within the RecyclerView item.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView userTextView;
        public ImageView imageView;
        public Button viewDetailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userTextView = itemView.findViewById(R.id.userTextView);
            imageView = itemView.findViewById(R.id.imageView);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
        }
    }

    // Helper method to return a non-null String value.
    private String getSafeValue(String value) {
        return value != null ? value : "";
    }
}
