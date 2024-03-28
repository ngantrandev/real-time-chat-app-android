package com.app.realtimechatapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.realtimechatapp.databinding.ItemContainerUserBinding;
import com.app.realtimechatapp.listeners.UserListener;
import com.app.realtimechatapp.models.User;
import com.app.realtimechatapp.ultilities.Ultil;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final List<User> users;
    private final UserListener userListener;

    public UserAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;

        public ViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user) {
            binding.textName.setText(user.getName());
            binding.textEmail.setText(user.getEmail());
            binding.imageProfile.setImageBitmap(Ultil.getBitmapFromBase64Text(user.getImage()));
            binding.getRoot().setOnClickListener(v -> userListener.userOnClicked(user));
        }
    }
}
