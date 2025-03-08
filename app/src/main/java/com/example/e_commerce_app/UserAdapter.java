package com.example.e_commerce_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<AdminView.User> userList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;

    public UserAdapter(List<AdminView.User> userList, OnDeleteClickListener onDeleteClickListener) {
        this.userList = userList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        AdminView.User user = userList.get(position);
        holder.nameTextView.setText(user.name);
        holder.emailTextView.setText(user.email);

        // Set up the delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder for user items
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        Button deleteButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userName);
            emailTextView = itemView.findViewById(R.id.userEmail);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Interface to handle delete button clicks
    public interface OnDeleteClickListener {
        void onDeleteClick(AdminView.User user);
    }
}
