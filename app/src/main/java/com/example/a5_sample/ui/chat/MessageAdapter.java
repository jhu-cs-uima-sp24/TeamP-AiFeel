package com.example.a5_sample.ui.chat;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Message> messageList;
    private Uri uploadedImageUri; // Add a variable to store the uploaded image URI
    private Uri uploadedPalUri; // Add a variable to store the uploaded image URI

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new MyViewHolder(chatView);
    }


    public void setImage(Uri image) {
        uploadedImageUri = image;
    }
    public void setPalImage(Uri image) {
        uploadedPalUri = image;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSentBy().equals(Message.SENT_BY_ME)) {
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getMessage());
            // Set the uploaded image URI to the right profile image
            if (uploadedImageUri != null) {
                holder.rightProfileImage.setImageURI(uploadedImageUri);
            } else {
                holder.rightProfileImage.setImageResource(R.drawable.profile_default); // Set default image if uploadedImageUri is null
            }
        } else {
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftTextView.setText(message.getMessage());
        
            if (uploadedPalUri != null) {
                holder.leftProfileImage.setImageURI(uploadedPalUri);
            } else {
                holder.leftProfileImage.setImageResource(R.drawable.ai_default);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Method to set the uploaded image URI
    public void setUploadedImageUri(Uri uploadedImageUri) {
        this.uploadedImageUri = uploadedImageUri;
        notifyDataSetChanged(); // Notify adapter to update the UI
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftTextView, rightTextView;
        ImageView rightProfileImage;
        ImageView leftProfileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
            rightProfileImage = itemView.findViewById(R.id.right_profile_image); // Find the right_profile_image ImageView
            leftProfileImage = itemView.findViewById(R.id.left_profile_image);
        }
    }
}
