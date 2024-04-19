package com.example.a5_sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SelectedPersonalityAdapter extends RecyclerView.Adapter<SelectedPersonalityAdapter.PersonalityViewHolder> {
    private List<String> personalities;

    public SelectedPersonalityAdapter(List<String> personalities) {
        this.personalities = personalities;
    }

    @NonNull
    @Override
    public PersonalityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personality, parent, false);
        return new PersonalityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalityViewHolder holder, int position) {
        String personality = personalities.get(position);
        holder.bind(personality);

        // Set click listener for the delete icon
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Remove the persona from the list
                    personalities.remove(currentPosition);
                    // Notify the adapter about the removal
                    notifyItemRemoved(currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return personalities.size();
    }

    public void setPersonalities(List<String> personalities) {
        this.personalities = personalities;
        notifyDataSetChanged();
    }

    static class PersonalityViewHolder extends RecyclerView.ViewHolder {
        private TextView personalityTextView;
        private ImageView deleteIcon;

        public PersonalityViewHolder(@NonNull View itemView) {
            super(itemView);
            personalityTextView = itemView.findViewById(R.id.personalityTextView);
            deleteIcon = itemView.findViewById(R.id.removeIcon);
        }

        public void bind(String personality) {
            personalityTextView.setText(personality);
        }
    }
}
