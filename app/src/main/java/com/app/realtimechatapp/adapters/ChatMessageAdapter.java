package com.app.realtimechatapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.realtimechatapp.databinding.ItemContainerReveivedMessageBinding;
import com.app.realtimechatapp.databinding.ItemContainerSentMessageBinding;
import com.app.realtimechatapp.models.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessageList;
    private final Bitmap receivedProfileImage;
    private final String senderId;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatMessageAdapter(List<ChatMessage> chatMessageList, Bitmap receivedProfileImage, String senderId) {
        this.chatMessageList = chatMessageList;
        this.receivedProfileImage = receivedProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_SENT)
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ));

        else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReveivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessageList.get(position));
        } else
            ((ReceivedMessageViewHolder) holder).setData(chatMessageList.get(position), receivedProfileImage);
    }

    @Override
    public int getItemCount() {
        return chatMessageList == null ? 0 : chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageList.get(position).getSenderId().equals(senderId)) return VIEW_TYPE_SENT;
        else return VIEW_TYPE_RECEIVED;
    }

    protected class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        public SentMessageViewHolder(@NonNull ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        public void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
        }
    }

    protected class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReveivedMessageBinding binding;

        protected ReceivedMessageViewHolder(@NonNull ItemContainerReveivedMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(ChatMessage chatMessage, Bitmap receivedProfileBitmap) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.imageProfile.setImageBitmap(receivedProfileBitmap);
        }
    }
}
