package com.example.a5_sample.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.R;
import com.example.a5_sample.ui.oldJournalEntry.OldJournalEntryFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Button currentSelectedButton;
    private TextView streakCountText;
    private boolean isJournalEntryOpen = false;
    private Map<Integer, Button> monthButtons;
    private DatabaseReference userRef;
    private Map<String, Integer> moodData = new HashMap<>();
    private Map<Integer, ImageView> streakCircles = new HashMap<>();
    private int cur_month, cur_year;
    private LineChart moodLineChart;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_home, container, false);

        // Get views of components on home fragment
        calendarRecyclerView = myview.findViewById(R.id.calendarRecyclerView);
        monthYearText = myview.findViewById(R.id.monthYearTV);
        streakCountText = myview.findViewById(R.id.StreakText);
        moodLineChart = (LineChart) myview.findViewById(R.id.lineChart);
        ImageButton nextMonth = myview.findViewById(R.id.nextMonthButton);

        // Use selectedDate to keep track of the date we are looking at and update calendar view
        selectedDate = LocalDate.now();

        // Update streaks based on database
        updateStreakCount();
        createStreaks(myview);
        updateStreakCircles();

        //initialize firebase for retrieving mood data
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        retrieveMoodDataForMonth();

        // Initial error handling to prevent users from selecting future months by turning next month arrow gray
        if (isMonthInFuture(selectedDate.getMonthValue() + 1, selectedDate.getYear())) {
            nextMonth.setImageResource(R.drawable.date_arrow_gray);
        }

        // Control month arrow selector actions and calendar icon button
        myview.findViewById(R.id.prevMonthButton).setOnClickListener(v -> previousMonthAction(nextMonth)); //top left button
        nextMonth.setOnClickListener(v -> nextMonthAction(nextMonth));
        myview.findViewById(R.id.calendarButton).setOnClickListener(v -> dateSelectionAction()); // pop up button

        // Set correct calendar view based on month/year supplied
        setMonthView();

        // Firebase database integration based on user
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Update streaks
                updateStreakCount();
                updateStreakCircles();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        return myview;
    }

    private void createStreaks(View myview){
        // Map to keep track of streak circles
        streakCircles.put(0, myview.findViewById(R.id.sunFilledCircle));
        streakCircles.put(1, myview.findViewById(R.id.monFilledCircle));
        streakCircles.put(2, myview.findViewById(R.id.tuesFilledCircle));
        streakCircles.put(3, myview.findViewById(R.id.wedFilledCircle));
        streakCircles.put(4, myview.findViewById(R.id.thurFilledCircle));
        streakCircles.put(5, myview.findViewById(R.id.friFilledCircle));
        streakCircles.put(6, myview.findViewById(R.id.satFilledCircle));
    }

    public void retrieveMoodDataForMonth() {
        // Get relevant date stats
        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int[] completedCount = new int[1]; // to count if all data is retrieved
        Map<String, Integer> localMoodData = new HashMap<>(); // temporary storage

        // Integration of firebase database, go through the days
        for (int day = 1; day <= daysInMonth; day++) {
            String dateKey = String.format("%d-%02d-%02d", year, month, day);
            DatabaseReference dateRef = userRef.child(dateKey);
            dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer mood = dataSnapshot.child("mood").getValue(Integer.class);
                    if (mood != null) {
                        localMoodData.put(dateKey, mood);
                    } else {
                        localMoodData.put(dateKey, 0); // default mood value is 0 if data is missing
                    }
                    completedCount[0]++;
                    if (completedCount[0] == daysInMonth) {
                        moodData.clear();
                        moodData.putAll(localMoodData); // copy all fetched data to the main map

                        //update calendar and graphs
                        createLineChart();
                        createPieChart();
                        calendarRecyclerView.getAdapter().notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }
    }

    private void updateStreakCount() {
        // Go through the days, going backwards from current day, to calculate streak
        int streakCount = 0;
        LocalDate today = LocalDate.now();
        while (true) {
            String dateKey = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (moodData.containsKey(dateKey) && moodData.get(dateKey) != 0) {
                streakCount++; // Found data for day and increase streak
            } else {
                break; // Streak ended, stop counting
            }
            today = today.minusDays(1);
        }

        // Update the streak count text at top of home fragment
        StringBuilder streakText = new StringBuilder();
        streakText.append("You've been on a ");
        streakText.append(streakCount);
        streakText.append("-day streak of journaling.");
        Log.d("streak", streakText.toString());

        streakCountText.setText(streakText.toString());
    }

    private void updateStreakCircles() {
        // Only look at the current week
        LocalDate today = LocalDate.now();
        LocalDate streakStart = today.minusDays(today.getDayOfWeek().getValue() % 7);
        HashMap<ImageView, LocalDate> dayMap = new HashMap<>();

        // Go through this week up to current day, updating streak circles if data is found
        int streakIndex = 0;
        while (streakStart.isBefore(today) || streakStart.isEqual(today)) {
            String dateKey = streakStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (moodData.containsKey(dateKey) && moodData.get(dateKey) != 0) {
                streakCircles.get(streakIndex).setVisibility(View.VISIBLE);
            } else {
                streakCircles.get(streakIndex).setVisibility(View.INVISIBLE);
            }
            streakIndex++;
            streakStart = streakStart.plusDays(1);
        }
    }

    private void createLineChart() {
        // Get relevant date data and mood data
        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();
        Calendar calendar = Calendar.getInstance();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<Entry> entries = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            String dateKey = String.format("%d-%02d-%02d", year, month, day);
            if (moodData.containsKey(dateKey)) {
                int mood = moodData.get(dateKey);
                entries.add(new Entry(day, mood));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setColor(getContext().getResources().getColor(R.color.black));
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(1.5f);

        // Update mood visualization settings
        LineData lineData = new LineData(dataSet);
        moodLineChart.setData(lineData);
        moodLineChart.getAxisLeft().setEnabled(false);
        moodLineChart.getDescription().setEnabled(false);
        moodLineChart.getXAxis().setDrawGridLines(false);
        moodLineChart.getXAxis().setTextSize(14f);
        moodLineChart.getLegend().setEnabled(false);
        moodLineChart.getAxisRight().setEnabled(false);

        moodLineChart.getXAxis().setEnabled(true);
        moodLineChart.getXAxis().setGranularity(1f);
        moodLineChart.getXAxis().setGranularityEnabled(true);
        moodLineChart.getXAxis().setAxisMinimum(1);

        moodLineChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        moodLineChart.getXAxis().setValueFormatter(new DateValueFormatter(year, month, 7));
        moodLineChart.getXAxis().setLabelCount(daysInMonth / 7 + 1, true);

        moodLineChart.invalidate();

        moodLineChart.post(new Runnable() {
            @Override
            public void run() {
                addImagesToYAxis();
            }
        });
    }

    private void addImagesToYAxis() {
        int[] imageRes = {R.drawable.mood_bad, R.drawable.mood_okay, R.drawable.mood_good, R.drawable.mood_great, R.drawable.mood_excellent};
        float yAxisHeight = moodLineChart.getHeight(); // Total height of the chart
        float yAxisMax = moodLineChart.getAxisLeft().getAxisMaximum(); // Max value of y-axis
        float yAxisMin = moodLineChart.getAxisLeft().getAxisMinimum(); // Min value of y-axis

        // Define a standard size for the images
        int imageSize = Math.min(moodLineChart.getWidth() / 7, moodLineChart.getHeight() / 14);

        for (int i = 0; i < 5; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(imageRes[i]);

            // Set the size of the image
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(imageSize, imageSize);

            float compressedYPosition = (i + 1) * 0.9f; // Apply scaling factor
            float normalizedPosition = (compressedYPosition - yAxisMin) / (yAxisMax - yAxisMin); // Normalize the position
            int topMargin = (int) (yAxisHeight * (1 - normalizedPosition)) - imageSize / 2 - 28; // Adjust to center the image vertically

            params.topMargin = topMargin;
            params.gravity = Gravity.START | Gravity.TOP; // Adjust this based on your layout direction

            ((FrameLayout) moodLineChart.getParent()).addView(imageView, params);
        }
    }

    private void createPieChart() {
        Map<Integer, Integer> moodCounts = new HashMap<>();
        // Calculate the count of each mood
        for (Integer mood : moodData.values()) {
            if (mood != 0) {
                if (!moodCounts.containsKey(mood)) {
                    moodCounts.put(mood, 0);
                }
                moodCounts.put(mood, moodCounts.get(mood) + 1);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : moodCounts.entrySet()) {
            int key = entry.getKey();
            String label;
            int color;
            switch (key) {
                case 1:
                    label = getContext().getResources().getString(R.string.mood1);
                    color = getContext().getResources().getColor(R.color.mood1);
                    break;
                case 2:
                    label = getContext().getResources().getString(R.string.mood2);
                    color = getContext().getResources().getColor(R.color.mood2);
                    break;
                case 3:
                    label = getContext().getResources().getString(R.string.mood3);
                    color = getContext().getResources().getColor(R.color.mood3);
                    break;
                case 4:
                    label = getContext().getResources().getString(R.string.mood4);
                    color = getContext().getResources().getColor(R.color.mood4);
                    break;
                case 5:
                    label = getContext().getResources().getString(R.string.mood5);
                    color = getContext().getResources().getColor(R.color.mood5);
                    break;
                default:
                    label = "Unknown Mood";
                    color = getContext().getResources().getColor(R.color.black);
            }

            entries.add(new PieEntry(entry.getValue(), label));
            colors.add(color);
        }


        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData pieData = new PieData(dataSet);
        PieChart pieChart = getView().findViewById(R.id.pieChart);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setTextSize(14f);
        pieChart.setDrawEntryLabels(false);
        pieChart.animateY(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad);

        pieChart.invalidate(); // refresh the chart
    }

    private class DateValueFormatter extends ValueFormatter {
        private final Calendar calendar;
        private final SimpleDateFormat dateFormat;
        private final int interval;

        public DateValueFormatter(int year, int month, int interval) {
            this.interval = interval;
            calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1); // Month is zero-indexed in Calendar
            dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        }

        @Override
        public String getFormattedValue(float value) {
            calendar.set(Calendar.DAY_OF_MONTH, (int) value);
            return dateFormat.format(calendar.getTime());
        }
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        retrieveMoodDataForMonth();
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, moodData, selectedDate, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private boolean isMonthInFuture(int month, int year) {
        LocalDate currDay = LocalDate.now();
        LocalDate targetDate = LocalDate.of(year, month, 1);
        return targetDate.isAfter(currDay.withDayOfMonth(currDay.lengthOfMonth()));
    }

    private void initializeButtons(View dialogView) {
        // Create month buttons for calendar selector, putting in map for easier access
        monthButtons = new HashMap<>();
        monthButtons.put(1, dialogView.findViewById(R.id.button_jan));
        monthButtons.put(2, dialogView.findViewById(R.id.button_feb));
        monthButtons.put(3, dialogView.findViewById(R.id.button_mar));
        monthButtons.put(4, dialogView.findViewById(R.id.button_apr));
        monthButtons.put(5, dialogView.findViewById(R.id.button_may));
        monthButtons.put(6, dialogView.findViewById(R.id.button_jun));
        monthButtons.put(7, dialogView.findViewById(R.id.button_jul));
        monthButtons.put(8, dialogView.findViewById(R.id.button_aug));
        monthButtons.put(9, dialogView.findViewById(R.id.button_sep));
        monthButtons.put(10, dialogView.findViewById(R.id.button_oct));
        monthButtons.put(11, dialogView.findViewById(R.id.button_nov));
        monthButtons.put(12, dialogView.findViewById(R.id.button_dec));
    }

    private void setMonthTextColor() {
        // Handle month button color logic
        for (Map.Entry<Integer, Button> entry : monthButtons.entrySet()) {
            Button button = entry.getValue();

            if (isMonthInFuture(entry.getKey(), selectedDate.getYear())) {
                // User handling, ensuring future months are not selected
                button.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                if (button.equals(currentSelectedButton)) {
                    currentSelectedButton.setSelected(false);
                }
            } else {
                // Handle month button selection
                if (button.equals(currentSelectedButton)) {
                    currentSelectedButton.setSelected(true);
                }

                button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.month_selection_circles));
                button.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        }
    }

    private void previousMonthAction(ImageButton nextMonth) {
        // Go to previous month on calendar
        selectedDate = selectedDate.minusMonths(1);
        nextMonth.setImageResource(R.drawable.date_arrow);
        setMonthView();
    }

    private void nextMonthAction(ImageButton nextMonth) {
        if (isMonthInFuture(selectedDate.getMonthValue() + 1, selectedDate.getYear())) {
            // Gray out arrow if month in future for error handling
            nextMonth.setImageResource(R.drawable.date_arrow_gray);
        } else {
            // Go to next month on calendar
            selectedDate = selectedDate.plusMonths(1);
            nextMonth.setImageResource(R.drawable.date_arrow);
            if (isMonthInFuture(selectedDate.getMonthValue() + 1, selectedDate.getYear())){
                nextMonth.setImageResource(R.drawable.date_arrow_gray);
            }
        }
        setMonthView();
    }

    private void dateSelectionAction() {
        // Handle logic for dialog open, allowing them to easily jump to dates
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_month_picker, null);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView yearTextView = dialogView.findViewById(R.id.textview_year);
        yearTextView.setText(String.valueOf(selectedDate.getYear()));

        initializeButtons(dialogView);
        setMonthTextColor();

        ImageButton previousYearButton = dialogView.findViewById(R.id.button_previous_year);
        ImageButton nextYearButton = dialogView.findViewById(R.id.button_next_year);

        // Handle logic of previous year button in dialog selector
        previousYearButton.setOnClickListener(v -> {
            selectedDate = selectedDate.minusYears(1);
            yearTextView.setText(String.valueOf(selectedDate.getYear()));
            nextYearButton.setImageResource(R.drawable.date_arrow);
            setMonthTextColor();
        });

        // Initial handling to prevent user to go to future years
        if (selectedDate.plusYears(1).getYear() > LocalDate.now().getYear()){
            nextYearButton.setImageResource(R.drawable.date_arrow_gray);
        }

        // Handle logic of next year button in dialog selector
        nextYearButton.setOnClickListener(v -> {
            if (selectedDate.plusYears(1).getYear() <= LocalDate.now().getYear()){
                selectedDate = selectedDate.plusYears(1);
                yearTextView.setText(String.valueOf(selectedDate.getYear()));
                nextYearButton.setImageResource(R.drawable.date_arrow);
                if (selectedDate.getYear() == (LocalDate.now().getYear())){
                    nextYearButton.setImageResource(R.drawable.date_arrow_gray);
                }
            } else {
                // Error prevention to prevent going into future years
                nextYearButton.setImageResource(R.drawable.date_arrow_gray);
            }
            setMonthTextColor();
        });

        // Month button logic handling
        for (Map.Entry<Integer, Button> entry : monthButtons.entrySet()) {
            Button button = entry.getValue();
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.month_selection_circles));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int month = entry.getKey();
                    // Error handling to ensure user does not select future months
                    if(!selectedDate.withMonth(month).isAfter(LocalDate.now())) {
                        // Select the month to be set
                        selectedDate = selectedDate.withMonth(month);

                        if (currentSelectedButton != null) {
                            currentSelectedButton.setSelected(false);
                        }
                        button.setSelected(true);
                        currentSelectedButton = button;
                    }
                }
            });
        }


        // OK button logic action
        Button setButton = dialogView.findViewById(R.id.button_set);
        setButton.setOnClickListener(v -> {
            if (currentSelectedButton != null) {
                setMonthView();
                dialog.dismiss();
                currentSelectedButton = null;
            } else {
                // Error handling if user does not specify the month
                Toast.makeText(getContext(), "Please select a month first", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button logic action
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            currentSelectedButton = null;
        });
    }


    private ArrayList<String> daysInMonthArray(LocalDate date) {
        // Place dates in calendar dependent of month
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        // Format date correctly
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @Override
    public void onItemClick(int position, String dayText) {
        // Handle clicking on dates in calendar to open journal entries
        if (!dayText.equals("") && !isJournalEntryOpen) {
            LocalDate date = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), Integer.parseInt(dayText));
            Fragment fragment = new OldJournalEntryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("date", date.toString());
            fragment.setArguments(bundle);
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragment_home, fragment);
            isJournalEntryOpen = true;
            transaction.commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isJournalEntryOpen = false;
    }
}
