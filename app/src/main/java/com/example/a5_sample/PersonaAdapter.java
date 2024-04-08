package com.example.a5_sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonaAdapter extends RecyclerView.Adapter<PersonaAdapter.ViewHolder> {

    private List<String> personasList;

    public PersonaAdapter(List<String> personasList) {
        this.personasList = personasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String persona = personasList.get(position);
        holder.personaTextView.setText(persona);
    }

    @Override
    public int getItemCount() {
        return personasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView personaTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            personaTextView = itemView.findViewById(R.id.persona_text_view);
        }
    }
}
