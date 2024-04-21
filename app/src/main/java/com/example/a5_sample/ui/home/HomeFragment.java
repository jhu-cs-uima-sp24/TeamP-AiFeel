package com.example.a5_sample.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Button currentSelectedButton;
    private boolean isJournalEntryOpen = false;
    private Map<Integer, Button> monthButtons;
    private DatabaseReference userRef;
    private Map<String, Integer> moodData = new HashMap<>();
//    private LineChart moodLineChart;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_home, container, false);

        calendarRecyclerView = myview.findViewById(R.id.calendarRecyclerView);
        monthYearText = myview.findViewById(R.id.monthYearTV);
        selectedDate = LocalDate.now();
//        moodLineChart = (LineChart) myview.findViewById(R.id.lineChart);
        ImageButton nextMonth = myview.findViewById(R.id.nextMonthButton);

        //initialize firebase for retrieving mood data
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        retrieveMoodDataForMonth(selectedDate.getYear(), selectedDate.getMonthValue() );
        if (isMonthInFuture(selectedDate.getMonthValue() + 1, selectedDate.getYear())) {
            nextMonth.setImageResource(R.drawable.date_arrow_gray);
        }

        myview.findViewById(R.id.prevMonthButton).setOnClickListener(v -> previousMonthAction(nextMonth)); //top left button
        nextMonth.setOnClickListener(v -> nextMonthAction(nextMonth));
        myview.findViewById(R.id.calendarButton).setOnClickListener(v -> dateSelectionAction()); // pop up button

        setMonthView();
        return myview;
    }


//    private ArrayList<Entry> retrieveMoodData(){
//        int year = selectedDate.getYear();
//        int month = selectedDate.getMonthValue();
//        String date_format = year+"-"+month;
//        ArrayList<Entry> entries = new ArrayList<Entry>();
//
//
//    }
    public void retrieveMoodDataForMonth(int year, int month) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int[] completedCount = new int[1]; // to count if all data is retrieved
        Map<String, Integer> localMoodData = new HashMap<>(); // temporary storage

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
                        //update your calendar here
                        //update the graph here
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Error fetching data: " + databaseError.getMessage());
                }
            });
        }
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
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
        for (Map.Entry<Integer, Button> entry : monthButtons.entrySet()) {
            Button button = entry.getValue();

            if (isMonthInFuture(entry.getKey(), selectedDate.getYear())) {
                button.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                if (button.equals(currentSelectedButton)) {
                    currentSelectedButton.setSelected(false);
                }
            } else {
                if (button.equals(currentSelectedButton)) {
                    currentSelectedButton.setSelected(true);
                }
                button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.month_selection_circles));
                button.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        }
    }

    private void previousMonthAction(ImageButton nextMonth) {
        selectedDate = selectedDate.minusMonths(1);
        nextMonth.setImageResource(R.drawable.date_arrow);
        setMonthView();
    }

    private void nextMonthAction(ImageButton nextMonth) {
        if (isMonthInFuture(selectedDate.getMonthValue() + 1, selectedDate.getYear())) {
            nextMonth.setImageResource(R.drawable.date_arrow_gray);
        } else {
            selectedDate = selectedDate.plusMonths(1);
            nextMonth.setImageResource(R.drawable.date_arrow);
            if (isMonthInFuture(selectedDate.getMonthValue() + 1, selectedDate.getYear())){
                nextMonth.setImageResource(R.drawable.date_arrow_gray);
            }
        }
        setMonthView();
    }

    private void dateSelectionAction() {
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

        previousYearButton.setOnClickListener(v -> {
            selectedDate = selectedDate.minusYears(1);
            yearTextView.setText(String.valueOf(selectedDate.getYear()));
            nextYearButton.setImageResource(R.drawable.date_arrow);
            setMonthTextColor();
        });

        if (selectedDate.plusYears(1).getYear() > LocalDate.now().getYear()){
            nextYearButton.setImageResource(R.drawable.date_arrow_gray);
        }
        nextYearButton.setOnClickListener(v -> {
            if (selectedDate.plusYears(1).getYear() <= LocalDate.now().getYear()){
                selectedDate = selectedDate.plusYears(1);
                yearTextView.setText(String.valueOf(selectedDate.getYear()));
                nextYearButton.setImageResource(R.drawable.date_arrow);
                if (selectedDate.getYear() == (LocalDate.now().getYear())){
                    nextYearButton.setImageResource(R.drawable.date_arrow_gray);
                }
            } else {
                nextYearButton.setImageResource(R.drawable.date_arrow_gray);
            }
            setMonthTextColor();
        });

        for (Map.Entry<Integer, Button> entry : monthButtons.entrySet()) {
            Button button = entry.getValue();
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.month_selection_circles));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int month = entry.getKey();
                    if(!selectedDate.withMonth(month).isAfter(LocalDate.now())) {
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


        Button setButton = dialogView.findViewById(R.id.button_set);
        setButton.setOnClickListener(v -> {
            if (currentSelectedButton != null) {
                setMonthView();
                dialog.dismiss();
                currentSelectedButton = null;
            } else {
                Toast.makeText(getContext(), "Please select a month first", Toast.LENGTH_SHORT).show();
            }
        });

        Button cancelButton = dialogView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            currentSelectedButton = null;
        });
    }


    private ArrayList<String> daysInMonthArray(LocalDate date) {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @Override
    public void onItemClick(int position, String dayText) {
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
