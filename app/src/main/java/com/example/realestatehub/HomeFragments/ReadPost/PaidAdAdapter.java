package com.example.realestatehub.HomeFragments.ReadPost;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostDetailsActivity;
import com.example.realestatehub.R;

import java.util.HashMap;

public class PaidAdAdapter extends RecyclerView.Adapter<PaidAdAdapter.ViewHolder> {
    private HashMap<String, HashMap<String, String>> postList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PaidAdAdapter(HashMap<String, HashMap<String, String>> postList) {
        this.postList = filterAdPosts(postList);
    }

    private HashMap<String, HashMap<String, String>> filterAdPosts(HashMap<String, HashMap<String, String>> postList) {
        HashMap<String, HashMap<String, String>> filteredMap = new HashMap<>();
        for (String postId : postList.keySet()) {
            HashMap<String, String> post = postList.get(postId);
            if ("Paid".equals(post.get("Ad Type"))) {
                filteredMap.put(postId, post);
            }
        }
        return filteredMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String postId = (String) postList.keySet().toArray()[position];
        HashMap<String, String> post = postList.get(postId);

        holder.nameTextView.setText(getSafeValue(post.get("Name")));
        if (post.get("userName") != null)
            holder.userTextView.setText(getSafeValue("by " + post.get("userName")));

        if (post.get("photoUrl") != null && !post.get("photoUrl").isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.get("photoUrl"))
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.picture_img);
        }

        holder.imageView.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("Post Details", post);
            intent.putExtra("Post Id", postId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView userTextView;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userTextView = itemView.findViewById(R.id.userTextView);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setClickable(true);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    private String getSafeValue(String value) {
        return value != null ? value : "";
    }
}
