package com.example.n0490426.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_AMOUNT;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_DAYSBEFOREREMINDER;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_DUEDAYOFMONTH;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_SPLITBILL;

public class bills extends Fragment {
    private Button UpdateBtn;
    private EditText InputAmount, InputDueDate, InputDaysBefore, InputReminderTime, InputBillName;
    private Spinner SpinnerBillType;
    private CheckBox CheckBoxSplitEvenly;
    private Boolean otherBillName;

    //DB and User References
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = crntUser.getUid();
    private DatabaseReference db_reference = FirebaseDatabase.getInstance().getReference("Users/" + userID);

    public bills() {
        // Required empty public constructor
    }
    public static bills newInstance(String param1, String param2) {
        bills fragment = new bills();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    EditedBill editedBill = new EditedBill();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills, container,false);
        getActivity().setTitle(R.string.Bills_page_title);
        InputAmount = (EditText) view.findViewById(R.id.inputAmount);
        InputDueDate = (EditText) view.findViewById(R.id.inputDueDate);
        InputDaysBefore = (EditText) view.findViewById(R.id.inputDaysBefore);
        InputBillName= (EditText) view.findViewById(R.id.inputBillName);
        SpinnerBillType = (Spinner) view.findViewById(R.id.spinnerBillType);
        UpdateBtn = (Button) view.findViewById(R.id.updateBtn);
        CheckBoxSplitEvenly = (CheckBox) view.findViewById(R.id.checkBoxSplitEvenly);
        InputReminderTime = (EditText) view.findViewById(R.id.inputReminderTime);
        setupSpinner();
        final Calendar myCalendar = Calendar.getInstance();
        final int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = myCalendar.get(Calendar.MINUTE);

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

        SpinnerBillType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if(selected.equals("Other")) {
                    InputBillName.setVisibility(View.VISIBLE);
                    otherBillName = true;
                } else {
                    InputBillName.setVisibility(View.GONE);
                    otherBillName = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                }
            }
        });

//        if(!theEditedBill.getEditBills().isEmpty()) {
//            prepopulateBillFromEdit();
//        }
        return view;
    }

//    Bundle bundle = getArguments();
//    EditedBill theEditedBill = (EditedBill) bundle.getSerializable("editedBill");

    private ArrayList<String> spinnerItems;
    private ArrayAdapter<String> adapter;

    public void setupSpinner() {
        spinnerItems = new ArrayList<String>();
        String[] billNames = getResources().getStringArray(R.array.bill_type);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, billNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerBillType.setAdapter(adapter);
    }

    public void addBillsToUser() {
        Bill bill = new Bill();
        if(otherBillName == true) {
            bill.setBillName(InputBillName.getText().toString());
        } else {
            bill.setBillName(SpinnerBillType.getSelectedItem().toString());
        }
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
        DatabaseReference users = db_user.child("Users").child(userID).child("Bills").child(bill.getBillName());
        Map newPost = new HashMap();
        newPost.put(USER_BILLS_AMOUNT, bill.getAmount());
        newPost.put(USER_BILLS_DUEDAYOFMONTH, bill.getDueDate());
        newPost.put(USER_BILLS_DAYSBEFOREREMINDER, bill.getDaysBefore());
        newPost.put(USER_BILLS_SPLITBILL, bill.getSplitBill());
        users.setValue(newPost);
        String toast = bill.getBillName() + " bill added";
        Toast.makeText(getActivity(), toast , Toast.LENGTH_SHORT).show();
        Fragment bills = new bills();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentLayout, bills);
        ft.addToBackStack(null);
        ft.commit();
    }
}