package com.bookkeep;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import org.joda.time.LocalDate;

import java.util.Calendar;

/**
 * Created by Abdullah on 28/06/2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    int num;
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static DatePickerFragment newInstance(int num) {
        DatePickerFragment f = new DatePickerFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        num = getArguments().getInt("num");
        Log.d("DatePicker","numIndex: " + num);
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        MainActivity.myLibrary.getLibrary().get(num).setDueDate(new LocalDate(year, month + 1, day));
        MainActivity.myLibrary.sortLibrary();
        MainActivity.adapter.notifyDataSetChanged();
    }

}
