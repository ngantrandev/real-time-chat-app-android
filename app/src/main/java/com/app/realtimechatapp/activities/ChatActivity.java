package com.app.realtimechatapp.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.realtimechatapp.R;
import com.app.realtimechatapp.databinding.ActivityChatBinding;
import com.app.realtimechatapp.models.User;
import com.app.realtimechatapp.ultilities.Constants;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setControl();
        setEvent();
    }

    private void setEvent() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void setControl() {
        loadReceiverUserInfo();

    }

    void loadReceiverUserInfo() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.getName());
    }
}