package com.example.realestatehub.HomeFragments.ReadPostAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestatehub.R;

import java.util.HashMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private HashMap<String, HashMap<String, String>> postList;
    private HashMap<String, HashMap<String, String>> freePostList;
    private OnItemClickListener mListener;

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        return 1;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PostAdapter(HashMap<String, HashMap<String, String>> postList) {
        HashMap<String, HashMap<String, String>> filteredMap = new HashMap<>();
        int count = 0;
        for (String postId : postList.keySet()) {
            if (count == 0) {
                filteredMap.put(postId + " ", postList.get(postId));
                count++;
            }
            filteredMap.put(postId, postList.get(postId));
        }
        this.postList = filteredMap;
        this.freePostList = filterAdPosts(postList);
    }

    private HashMap<String, HashMap<String, String>> filterAdPosts(HashMap<String, HashMap<String, String>> postList) {
        HashMap<String, HashMap<String, String>> filteredMap = new HashMap<>();
        for (String postId : postList.keySet()) {
            HashMap<String, String> post = postList.get(postId);
            if ("Free".equals(post.get("Ad Type"))) {
                filteredMap.put(postId, post);
            }
        }
        HashMap<String, HashMap<String, String>> filteredMap1 = new HashMap<>();
        int count = 0;
        for (String postId : filteredMap.keySet()) {
            if (count == 0) {
                filteredMap1.put(postId + " ", postList.get(postId));
                count++;
            }
            filteredMap1.put(postId, postList.get(postId));
        }
        return filteredMap1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout_vertical, parent, false);
            return new ViewHolder(view, mListener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout, parent, false);
            return new ViewHolder(view, mListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            PaidAdAdapter innerAdapter = new PaidAdAdapter(postList); // Provide context and data list
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerView.getContext(), RecyclerView.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setAdapter(innerAdapter);
        } else {
            String postId = (String) freePostList.keySet().toArray()[position];
            HashMap<String, String> post = freePostList.get(postId);

            holder.nameTextView.setText(getSafeValue(post.get("Name")));
            if (post.get("userName") != null)
                holder.userTextView.setText(getSafeValue("by " + post.get("userName")));
            if (post.get("Post Type") != null)
                holder.rentOrSellTextView.setText(getSafeValue(post.get("Post Type")));
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
                intent.putExtra("User Id", post.get("Post Owner Id"));

                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return freePostList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView rentOrSellTextView;
        public TextView userTextView;
        public ImageView imageView;
        private RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            rentOrSellTextView = itemView.findViewById(R.id.rentOrSellTextView);
            userTextView = itemView.findViewById(R.id.userTextView);
            imageView = itemView.findViewById(R.id.imageView);
            //imageView.setClickable(true);
            recyclerView = itemView.findViewById(R.id.recyclerViewVert);

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
