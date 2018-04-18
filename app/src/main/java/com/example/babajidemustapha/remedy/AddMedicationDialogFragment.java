package com.example.babajidemustapha.remedy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Jide Mustapha on 4/9/2018.
 */

public class AddMedicationDialogFragment extends DialogFragment {
    View view;
    EditText startDate;
    EditText endDate;
    EditText name;
    EditText desc;
    EditText startTime;
    EditText interval;
    Button add_med;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    SimpleDateFormat fullDateFormat;
    Calendar calendar;
    Bundle bundle;
    Medication medication;
    MedicationDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bundle = getArguments();
        String dialog_type = bundle.getString("type");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH:ss");
        fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss");
        calendar = Calendar.getInstance();
        view = getActivity().getLayoutInflater().inflate(R.layout.add_med_dialog_layout, null);
        startDate = (EditText) view.findViewById(R.id.start_date);
        endDate = (EditText) view.findViewById(R.id.end_date);
        name = (EditText) view.findViewById(R.id.med_name);
        desc = (EditText) view.findViewById(R.id.med_desc);
        startTime = (EditText) view.findViewById(R.id.start_time);
        interval = (EditText) view.findViewById(R.id.interval);
        add_med = (Button) view.findViewById(R.id.add_med);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTimeDialog();
            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDateDialog();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDateDialog();
            }
        });
        startTime.setInputType(InputType.TYPE_NULL);
        startDate.setInputType(InputType.TYPE_NULL);
        endDate.setInputType(InputType.TYPE_NULL);
        if (dialog_type.equalsIgnoreCase("update")) {
            medication = mListener.getMedication(bundle.getInt("id"));
            Calendar calendar1 = Calendar.getInstance();
            String date = "";
            String time = "";
//            dateFormat.format(new Date(medication.startDate));
            try {
                date = dateFormat.format(fullDateFormat.parse(medication.startDate));
                time = timeFormat.format(fullDateFormat.parse(medication.startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            name.setText(medication.name);
            desc.setText(medication.description);
            startTime.setText(time);
            startDate.setText(date);
            endDate.setText(medication.endDate);
            interval.setText(medication.interval + "");
            add_med.setText("UPDATE");
        }
        add_med.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (((Button) v).getText().toString()) {
                    case "ADD":
                    case "add":
                        if (validate()) {
                            //Toast.makeText(getActivity(),"Ooops! Empty",Toast.LENGTH_SHORT).show();
                            mListener.saveMedication(new Medication(name.getText().toString().trim(),
                                    desc.getText().toString().trim(),
                                    startDate.getText().toString().trim() + " " + startTime.getText().toString().trim(),
                                    endDate.getText().toString().trim(),
                                    Integer.parseInt(interval.getText().toString().trim())));
                            dismiss();
                        }
                        break;
                    case "UPDATE":
                        if (validate()) {
                            //Toast.makeText(getActivity(),"Ooops! Empty",Toast.LENGTH_SHORT).show();
                            mListener.updateMedication(new Medication(bundle.getInt("id"), name.getText().toString().trim(),
                                    desc.getText().toString().trim(),
                                    startDate.getText().toString().trim() + " " + startTime.getText().toString().trim(),
                                    endDate.getText().toString().trim(),
                                    Integer.parseInt(interval.getText().toString().trim())));
                            add_med.setText("ADD");
                            dismiss();
                        }

                        break;
                }

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    private boolean validate() {
        if (startDate.getText().toString().trim().isEmpty()) {
            startDate.setError("Please fill field");
            return false;
        } else if (endDate.getText().toString().trim().isEmpty()) {
            endDate.setError("Please fill field");
            return false;
        } else if (name.getText().toString().trim().isEmpty()) {
            name.setError("Please fill field");
            return false;
        } else if (desc.getText().toString().trim().isEmpty()) {
            desc.setError("Please fill field");
            return false;
        } else if (interval.getText().toString().trim().isEmpty()) {
            interval.setError("Please fill field");
            return false;
        } else if (startTime.getText().toString().trim().isEmpty()) {
            startTime.setError("Please fill field");
            return false;
        }
        return true;
    }

    private void showEndDateDialog() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                endDate.setText(dateFormat.format(calendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showStartDateDialog() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                startDate.setText(dateFormat.format(calendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showStartTimeDialog() {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                startTime.setText(timeFormat.format(calendar.getTime()));
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity())).show();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (MedicationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MedicationDialogListener");
        }
    }

    public interface MedicationDialogListener {
        void saveMedication(Medication medication);

        void updateMedication(Medication medication);

        Medication getMedication(int id);
    }
}
