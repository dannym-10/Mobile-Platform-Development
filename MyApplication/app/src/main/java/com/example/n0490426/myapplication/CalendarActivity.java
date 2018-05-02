package com.example.n0490426.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import com.example.n0490426.myapplication.utils.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_DUEDAYOFMONTH;

public class CalendarActivity extends Fragment {
    private CalendarView CalendarView;
    private ListView ListViewForCalendar;
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = crntUser.getUid();
    private DatabaseReference db_reference = FirebaseDatabase.getInstance().getReference("Users/" + userID);
    private ArrayList<Bill> BillsList = new ArrayList<Bill>();
    private ArrayList<String> billNames = new ArrayList<String>();
    private ArrayAdapter<String> ad;
    private String TAG = "Logging Error: ";

    public CalendarActivity() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CalendarActivity newInstance(String param1, String param2) {
        CalendarActivity fragment = new CalendarActivity();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.Calendar_page_title);
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ListViewForCalendar = (ListView) view.findViewById(R.id.listViewForCalendar);
        CalendarView = view.findViewById(R.id.calendarView);
        ad = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, billNames);
        ListViewForCalendar.setAdapter(ad);

        CalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, final int month, final int dom) {
                final int dayOfMonth = dom;
                ad.clear();
                db_reference.child("Bills").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Bill bill = new Bill();
                        bill.setBillName(dataSnapshot.getValue().toString());
                        for (DataSnapshot databaseBill : dataSnapshot.getChildren()) {
                            if ((databaseBill.getKey().toString().equals(USER_BILLS_DUEDAYOFMONTH))&& (databaseBill.getValue().toString().equals(String.valueOf(dom)))) {
                                bill.setDueDate(Integer.parseInt(databaseBill.getValue().toString()));
                                bill.setBillName(bill.getBillName());
                                Log.d(TAG, bill.getBillName());
                                billNames.add(dataSnapshot.getKey());
                                BillsList.add(bill);
                                ad.notifyDataSetChanged();
                            }
                        }
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
        });
        return view;
    }
}
