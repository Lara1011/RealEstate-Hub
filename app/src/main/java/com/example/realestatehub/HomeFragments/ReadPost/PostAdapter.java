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
import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<HashMap<String, Object>> postList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PostAdapter(List<HashMap<String, Object>> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, Object> post = postList.get(position);
        holder.nameTextView.setText(getSafeValue((String) post.get("name")));
        if (post.get("userName") != null)
            holder.userTextView.setText(getSafeValue("by " + (String) post.get("userName")));

        if (post.get("photoUrl") != null && !((String) post.get("photoUrl")).isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load((String) post.get("photoUrl"))
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.picture_img);
            // holder.imageView.setVisibility(View.GONE);
        }

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

    @Override
    public int getItemCount() {
        return postList.size();
    }

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

    private String getSafeValue(String value) {
        return value != null ? value : "";
    }
}
