package com.example.a5_sample.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final Map<String, Integer> moodData;
    private final OnItemListener onItemListener;
    private final LocalDate selectedDate;

    public CalendarAdapter(ArrayList<String> daysOfMonth, Map<String, Integer> moodData, LocalDate selectedDate, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.moodData = moodData;
        this.selectedDate = selectedDate;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() / 7.0);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dayString = daysOfMonth.get(position);
        holder.dayOfMonth.setText(dayString);

        if (!dayString.isEmpty()) {
            int day = Integer.parseInt(dayString);  // Convert the day string to an integer
            LocalDate date = selectedDate.withDayOfMonth(day);  // Set the correct day of the month
            String dateKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));  // Format the date to create a key

            if (moodData.containsKey(dateKey)) {
                Integer mood = moodData.get(dateKey);
                int backgroundResource = getBackgroundResourceForMood(mood);  // Get the appropriate drawable based on mood
                holder.itemView.setBackgroundResource(backgroundResource);
            } else {
                // Reset to default background if no mood data
                holder.itemView.setBackgroundResource(R.color.white);
            }
        } else {
            // Handle empty days (outside current month)
            holder.itemView.setBackgroundResource(R.color.white);
        }
    }

    private int getBackgroundResourceForMood(Integer mood) {
        switch (mood) {
            case 1: return R.drawable.mood_bad;
            case 2: return R.drawable.mood_okay;
            case 3: return R.drawable.mood_good;
            case 4: return R.drawable.mood_great;
            case 5: return R.drawable.mood_excellent;
            default: return R.color.white;  // Default background if mood is out of expected range
        }
    }



    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView dayOfMonth;
        private final OnItemListener onItemListener;

        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.dayOfMonth = itemView.findViewById(R.id.cellDayText);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemListener != null) {
                onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
            }
        }
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
