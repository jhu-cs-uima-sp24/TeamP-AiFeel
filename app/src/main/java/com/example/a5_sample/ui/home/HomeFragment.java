package com.example.a5_sample.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.R;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_home, container, false);

        calendarRecyclerView = myview.findViewById(R.id.calendarRecyclerView);
        monthYearText = myview.findViewById(R.id.monthYearTV);
        selectedDate = LocalDate.now();

        // Set up buttons for navigating months
        myview.findViewById(R.id.prevMonthButton).setOnClickListener(v -> previousMonthAction());
        myview.findViewById(R.id.nextMonthButton).setOnClickListener(v -> nextMonthAction());
        myview.findViewById(R.id.calendarButton).setOnClickListener(v -> dateSelectionAction());

        setMonthView();
        return myview;
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    private void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    private void dateSelectionAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_month_picker, null);

        TextView yearTextView = dialogView.findViewById(R.id.textview_year);
        yearTextView.setText(String.valueOf(selectedDate.getYear()));



        // Set up button listeners
        Button previousYearButton = dialogView.findViewById(R.id.button_previous_year);
        previousYearButton.setOnClickListener(v -> {
            selectedDate = selectedDate.minusYears(1);
            yearTextView.setText(String.valueOf(selectedDate.getYear()));
        });

        Button nextYearButton = dialogView.findViewById(R.id.button_next_year);
        nextYearButton.setOnClickListener(v -> {
            selectedDate = selectedDate.plusYears(1);
            yearTextView.setText(String.valueOf(selectedDate.getYear()));
        });


        Button setButton = dialogView.findViewById(R.id.button_set);
        setButton.setOnClickListener(v -> {
            setMonthView();
            // Close dialog
        });

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
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
        if (!dayText.equals("")) {
            LocalDate date = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), Integer.parseInt(dayText));
            Toast.makeText(getContext(), "Selected Date: " + date.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
