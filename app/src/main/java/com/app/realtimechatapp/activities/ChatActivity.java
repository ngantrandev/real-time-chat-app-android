package com.app.realtimechatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.realtimechatapp.adapters.ChatMessageAdapter;
import com.app.realtimechatapp.databinding.ActivityChatBinding;
import com.app.realtimechatapp.models.ChatMessage;
import com.app.realtimechatapp.models.User;
import com.app.realtimechatapp.ultilities.Constants;
import com.app.realtimechatapp.ultilities.PreferenceManager;
import com.app.realtimechatapp.ultilities.Ultil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessageList;
    private ChatMessageAdapter chatMessageAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    private String convertionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setControl();
        setEvent();
        listenMessage();
    }

    private void setEvent() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sentMessage());
    }

    private void setControl() {
        loadReceiverUserInfo();

        db = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessageList = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(
                chatMessageList,
                Ultil.getBitmapFromBase64Text(receiverUser.getImage()),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        binding.chatRecyclerView.setAdapter(chatMessageAdapter);

    }

    private void sentMessage() {
        if(binding.inputMessage.getText().toString().isEmpty()) return;

        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.getId());
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());

        db.collection(Constants.KEY_COLLECTION_CHAT).add(message);

        if (convertionId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversions = new HashMap<>();
            conversions.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversions.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversions.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversions.put(Constants.KEY_RECEIVER_ID, receiverUser.getId());
            conversions.put(Constants.KEY_RECEIVER_NAME, receiverUser.getName());
            conversions.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.getImage());
            conversions.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversions.put(Constants.KEY_TIMESTAMP, new Date());

            addConversion(conversions);
        }
        binding.inputMessage.setText("");
    }

    private void listenMessage() {
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.getId())
                .addSnapshotListener(eventListener);
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.getId())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }

        if (value != null) {
            int count = chatMessageList.size();

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceivedId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.setDateTime(Ultil.getReadableDateTime(chatMessage.getDateObject()));
                    chatMessageList.add(chatMessage);
                }
            }

            Collections.sort(chatMessageList, Comparator.comparing(ChatMessage::getDateObject));

            if (count == 0) {
                chatMessageAdapter.notifyDataSetChanged();
            } else {
                chatMessageAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
            }

            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);

        if (convertionId == null) {
            checkForConversion();
        }
    };

    void loadReceiverUserInfo() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        if (receiverUser != null) {
            binding.textName.setText(receiverUser.getName());
        }
    }

    private void addConversion(HashMap<String, Object> conversion) {
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> convertionId = documentReference.getId());
    }

    private void updateConversion(String message) {
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(convertionId);
        documentReference.update(Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date());
    }

    private void checkForConversion() {
        if (!chatMessageList.isEmpty()) {
            checkForConvertionRemoteLy(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.getId()
            );
            checkForConvertionRemoteLy(
                    receiverUser.getId(),
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConvertionRemoteLy(String senderId, String receiverId) {
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(convertionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> convertionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            convertionId = documentSnapshot.getId();
        }
    };
}