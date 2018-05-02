package com.example.n0490426.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.n0490426.myapplication.utils.Bill;
import com.example.n0490426.myapplication.utils.EditedBill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_AMOUNT;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_DAYSBEFOREREMINDER;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_DUEDAYOFMONTH;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_SPLITBILL;


public class EditBills extends Fragment {
    private Button UpdateBtn;
    private EditText InputAmount, InputDueDate, InputDaysBefore, InputReminderTime, InputBillName;
    private CheckBox CheckBoxSplitEvenly;

    private String theBillName;
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = crntUser.getUid();
    private DatabaseReference db_reference = FirebaseDatabase.getInstance().getReference("Users/" + userID);
    EditedBill theEditedBill = new EditedBill();

    public EditBills() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_bills, container, false);
        InputAmount = (EditText) view.findViewById(R.id.inputAmount);
        InputDueDate = (EditText) view.findViewById(R.id.inputDueDate);
        InputDaysBefore = (EditText) view.findViewById(R.id.inputDaysBefore);
        InputBillName= (EditText) view.findViewById(R.id.inputBillName);
        UpdateBtn = (Button) view.findViewById(R.id.updateBtn);
        CheckBoxSplitEvenly = (CheckBox) view.findViewById(R.id.checkBoxSplitEvenly);
        InputReminderTime = (EditText) view.findViewById(R.id.inputReminderTime);
        final Calendar myCalendar = Calendar.getInstance();
        final int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = myCalendar.get(Calendar.MINUTE);
        theBillName = getArguments().getString("editedBill");
        Log.d("messages", theBillName);

        if(!theBillName.isEmpty()) {
            prepopulateBillFromEdit();
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                InputDueDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        InputDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                new TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
                InputReminderTime.setText(hourOfDay + ":" + minute);
            }
        };

        InputReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), time, hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity())).show();
            }
        });

        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean sendRequest = true;
                if(InputAmount.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please enter a bill amount" , Toast.LENGTH_SHORT).show();
                    sendRequest = false;
                }
                if(InputDueDate.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please select a bill due date", Toast.LENGTH_SHORT).show();
                    sendRequest = false;
                }
                if(InputDaysBefore.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please enter a days before reminder", Toast.LENGTH_SHORT).show();
                    sendRequest = false;
                }
                if(InputReminderTime.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please select a bill reminder time", Toast.LENGTH_SHORT).show();
                    sendRequest = false;
                }
                if(sendRequest) {
                    addBillsToUser();
                    Fragment summary = new Summary();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentLayout, summary);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        return view;
    }

    public void prepopulateBillFromEdit() {
        final Bill bill = new Bill();
        bill.setBillName(theBillName);
        db_reference.child("Bills").child(bill.getBillName()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(USER_BILLS_AMOUNT)) {
                    bill.setAmount(Integer.parseInt(dataSnapshot.getValue().toString()));
                }
                if(dataSnapshot.getKey().equals(USER_BILLS_DAYSBEFOREREMINDER)) {
                    bill.setDaysBefore(Integer.parseInt(dataSnapshot.getValue().toString()));
                }
                if(dataSnapshot.getKey().equals(USER_BILLS_DUEDAYOFMONTH)) {
                    bill.setDueDate(Integer.parseInt(dataSnapshot.getValue().toString()));
                }
                if(dataSnapshot.getKey().equals(USER_BILLS_SPLITBILL)) {
                    if(dataSnapshot.getValue().toString().equals("true")) {
                        bill.setSplitBill(true);
                        CheckBoxSplitEvenly.setChecked(true);
                    } else {
                        bill.setSplitBill(false);
                        CheckBoxSplitEvenly.setChecked(false);
                    }
                }
                InputBillName.setVisibility(View.VISIBLE);
                InputBillName.setText(bill.getBillName());
                InputAmount.setText(String.valueOf(bill.getAmount()));
                InputDaysBefore.setText(String.valueOf(bill.getDaysBefore()));
                InputDueDate.setText(String.valueOf(bill.getDueDate()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addBillsToUser() {
        Bill bill = new Bill();
        bill.setBillName(InputBillName.toString());
        Log.d("bill name", bill.getBillName());
        bill.setAmount(Integer.parseInt(InputAmount.getText().toString()));
        bill.setDaysBefore(Integer.parseInt(InputDaysBefore.getText().toString()));
        if(CheckBoxSplitEvenly.isChecked()) {
            bill.setSplitBill(true);
        } else {
            bill.setSplitBill(false);
        }
        String date = InputDueDate.getText().toString();
        String[] month = date.split("-");
        date = month[0];
        bill.setDueDate(Integer.parseInt (date));
        FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = crntUser.getUid();
        DatabaseReference db_user = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = db_user.child("Users").child(userID).child("Bills").child(InputBillName.getText().toString());
        Map newPost = new HashMap();
        newPost.put(USER_BILLS_AMOUNT, bill.getAmount());
        newPost.put(USER_BILLS_DUEDAYOFMONTH, bill.getDueDate());
        newPost.put(USER_BILLS_DAYSBEFOREREMINDER, bill.getDaysBefore());
        newPost.put(USER_BILLS_SPLITBILL, bill.getSplitBill());
        users.setValue(newPost);
        Toast.makeText(getActivity(), InputBillName.getText().toString() , Toast.LENGTH_SHORT).show();
    }
}