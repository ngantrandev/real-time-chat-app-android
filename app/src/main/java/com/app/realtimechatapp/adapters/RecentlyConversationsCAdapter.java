package com.app.realtimechatapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.realtimechatapp.databinding.ItemContainerRecentlyConversationBinding;
import com.app.realtimechatapp.listeners.ConversionListener;
import com.app.realtimechatapp.models.ChatMessage;
import com.app.realtimechatapp.models.User;
import com.app.realtimechatapp.ultilities.Ultil;

import java.util.List;

public class RecentlyConversationsCAdapter extends RecyclerView.Adapter<RecentlyConversationsCAdapter.ConversationViewHolder> {
    private final List<ChatMessage> chatMessageList;
    private final ConversionListener conversionListener;

    public RecentlyConversationsCAdapter(List<ChatMessage> chatMessageList, ConversionListener conversionListener) {
        this.chatMessageList = chatMessageList;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                ItemContainerRecentlyConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMessageList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessageList == null ? 0 : chatMessageList.size();
    }

    protected class ConversationViewHolder extends RecyclerView.ViewHolder {
        private ItemContainerRecentlyConversationBinding binding;

        ConversationViewHolder(ItemContainerRecentlyConversationBinding itemContainerRecentlyConversationBinding) {
            super(itemContainerRecentlyConversationBinding.getRoot());
            binding = itemContainerRecentlyConversationBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textName.setText(chatMessage.getConversionName());
            binding.textRecentMessage.setText(chatMessage.getMessage());
            binding.imageProfile.setImageBitmap(Ultil.getBitmapFromBase64Text(chatMessage.getConversionImage()));

            binding.getRoot().setOnClickListener(v->{
                User user = new User();
                user.setId(chatMessage.getConversionId());
                user.setName(chatMessage.getConversionName());
                user.setImage(chatMessage.getConversionImage());

                conversionListener.onConversionClicked(user);
            });
        }
    }
}
