package com.example.mobileapp.ui.chatbot;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.ChatMessage;

import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<ChatMessage> messages;
    private final Context context;

    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_BOT = 1;

    public MessageAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        // Check sender field. Assuming "user" means the user sent it.
        return "user".equalsIgnoreCase(message.getSender()) ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        final TextView messageText;
        final TextView timestamp;
        final LinearLayout messageContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestampText);
            // Assuming the root layout of item_message is a LinearLayout
            messageContainer = (LinearLayout) itemView;
        }

        public void bind(ChatMessage message) {
            // Handle timestamp which is now a String
            String rawTime = message.getTimestamp();
            String displayTime = rawTime;
            
            // Simple parsing to extract HH:mm from ISO format like "2025-11-28T12:26:13.543245"
            if (rawTime != null && rawTime.contains("T") && rawTime.length() > 16) {
                try {
                    int tIndex = rawTime.indexOf("T");
                    // Extract 5 chars starting after T: 12:26
                    displayTime = rawTime.substring(tIndex + 1, tIndex + 6);
                } catch (Exception e) {
                    // Fallback to raw string if parsing fails
                    displayTime = rawTime;
                }
            } else if (rawTime == null) {
                displayTime = "";
            }

            // Use getText() instead of getMessage()
            messageText.setText(message.getText());

            boolean isUser = "user".equalsIgnoreCase(message.getSender());
            
            int backgroundRes;
            int textColor;
            int gravity;
            String senderLabel;

            if (isUser) {
                backgroundRes = R.drawable.background_message_user;
                textColor = ContextCompat.getColor(context, android.R.color.white);
                gravity = Gravity.END;
                senderLabel = "Сіз";
            } else {
                backgroundRes = R.drawable.background_message_bot;
                textColor = ContextCompat.getColor(context, android.R.color.black);
                gravity = Gravity.START;
                senderLabel = "Gemini";
            }

            messageText.setBackgroundResource(backgroundRes);
            messageText.setTextColor(textColor);
            messageContainer.setGravity(gravity);

            timestamp.setText(String.format(Locale.getDefault(), "%s | %s", displayTime, senderLabel));
        }
    }
}