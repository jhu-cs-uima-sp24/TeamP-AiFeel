package com.example.a5_sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonalityAdapter extends RecyclerView.Adapter<PersonalityAdapter.PersonalityViewHolder> {

    private List<String> personalities;
    private OnItemClickListener clickListener;

    public PersonalityAdapter(List<String> personalities, OnItemClickListener listener) {
        this.personalities = personalities;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public PersonalityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.personality_item, parent, false);
        return new PersonalityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalityViewHolder holder, int position) {
        String personality = personalities.get(position);
        holder.bind(personality);
    }

    @Override
    public int getItemCount() {
        return personalities.size();
    }

    public void setPersonalities(List<String> personalities) {
        this.personalities = personalities;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String personality);
    }

    class PersonalityViewHolder extends RecyclerView.ViewHolder {

        private TextView personalityTextView;

        public PersonalityViewHolder(@NonNull View itemView) {
            super(itemView);
            personalityTextView = itemView.findViewById(R.id.personality_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String clickedPersonality = personalities.get(position);
                        clickListener.onItemClick(clickedPersonality);
                    }
                }
            });
        }

        public void bind(String personality) {
            personalityTextView.setText(personality);
        }
    }
}
