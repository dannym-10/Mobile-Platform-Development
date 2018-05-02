package com.example.n0490426.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.n0490426.myapplication.utils.Account;
import com.example.n0490426.myapplication.utils.Bill;
import com.example.n0490426.myapplication.utils.EditedBill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_AMOUNT;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_DAYSBEFOREREMINDER;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_DUEDAYOFMONTH;
import static com.example.n0490426.myapplication.utils.Constants.USER_BILLS_SPLITBILL;
import static com.example.n0490426.myapplication.utils.Constants.USER_DETAILS_APPARTMENT_ID;
import static com.example.n0490426.myapplication.utils.Constants.USER_DETAILS_CITY_ID;
import static com.example.n0490426.myapplication.utils.Constants.USER_DETAILS_NUMBEROFFLATMATES_ID;

public class Summary extends Fragment {
    private Button ButttonUpdate;
    private ListView ListBillsSummary;
    private TextView AccountSummaryText;
    private ProgressBar ProgressBar;
    //DB and User References
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = crntUser.getUid();
    private DatabaseReference db_reference = FirebaseDatabase.getInstance().getReference("Users/" + userID);

    private ArrayList<Bill> allBills = new ArrayList<Bill>();
    private ArrayList<String> billNames = new ArrayList<String>();
    private ArrayAdapter<String> ad;

    public Summary() {
        // Required empty public constructor
    }

    public static Summary newInstance(String param1, String param2) {
        Summary fragment = new Summary();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.Summary_page_title);
        final View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ListBillsSummary = (ListView) view.findViewById(R.id.listBillsSummary);
        ad = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, billNames);
        AccountSummaryText = (TextView) view.findViewById(R.id.accountSummaryText);
        ProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        ListBillsSummary.setAdapter(ad);
        ButttonUpdate = (Button) view.findViewById(R.id.butttonUpdate);
        final Account account = new Account();
        ButttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment account = new AccountActivity();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentLayout, account);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        db_reference.child("Bills").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Bill bill = new Bill();
                bill.setBillName(dataSnapshot.getKey().toString());
                db_reference.child("Bills").child(bill.getBillName()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.getKey().equals(USER_BILLS_AMOUNT)) {
                            bill.setAmount(Integer.parseInt(dataSnapshot.getValue().toString()));
                        } else if (dataSnapshot.getKey().equals(USER_BILLS_DAYSBEFOREREMINDER)) {
                            bill.setDaysBefore(Integer.parseInt(dataSnapshot.getValue().toString()));
                        } else if (dataSnapshot.getKey().equals(USER_BILLS_DUEDAYOFMONTH)) {
                            bill.setDueDate(Integer.parseInt(dataSnapshot.getValue().toString()));
                        } else if (dataSnapshot.getKey().equals(USER_BILLS_SPLITBILL)) {
                            if(dataSnapshot.getValue().toString().equals("true")) {
                                bill.setSplitBill(true);
                            } else {
                                bill.setSplitBill(false);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        ad.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                billNames.add(bill.getBillName());
                allBills.add(bill);
                ad.notifyDataSetChanged();
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
        ProgressBar.setVisibility(View.INVISIBLE);
        db_reference.child("UserDetails").addChildEventListener(new ChildEventListener() {
            String theString = "";
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(USER_DETAILS_APPARTMENT_ID)) {
                    theString += "You live in a " + dataSnapshot.getValue().toString();
                    account.setHouseOrFlat(dataSnapshot.getValue().toString());
                }
                if(dataSnapshot.getKey().equals(USER_DETAILS_CITY_ID)) {
                    theString += " in " + dataSnapshot.getValue().toString();
                    account.setCity(dataSnapshot.getValue().toString());
                }
                if(dataSnapshot.getKey().equals(USER_DETAILS_NUMBEROFFLATMATES_ID)) {
                    theString += " with " + dataSnapshot.getValue().toString() + " sharers ";
                    account.setNumberOfHouseates(Integer.parseInt(dataSnapshot.getValue().toString()));
                }
                AccountSummaryText.setText(theString);
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

        ListBillsSummary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(billNames.get(position));
                String message = "";
                int amount;
                int dueDate = allBills.get(position).getDueDate();
                String dueDateWith = "";
                if((dueDate == 1) || (dueDate == 21) || (dueDate == 31)) {
                    dueDateWith = dueDate + "st";
                } else if ((dueDate == 2) || (dueDate == 22)) {
                    dueDateWith = dueDate + "nd";
                } else if ((dueDate == 3) || (dueDate == 23)) {
                    dueDateWith = dueDate + "rd";
                } else {
                    dueDateWith = dueDate + "th";
                }
                if (allBills.get(position).getSplitBill()) {
                    amount = allBills.get(position).getAmount() / account.getNumberOfHouseates();
                    message = "Bill Amount: £" + amount + " split between " + account.getNumberOfHouseates() + " sharers." + "\n"
                            + "Bill Due Date " + dueDateWith + " of the month." + "\n"
                            + allBills.get(position).getDaysBefore() + " Days reminder before the bill is due";
                } else {
                    message = "Bill Amount: £" + allBills.get(position).getAmount() + "\n" + "Bill Due Date " + dueDateWith + " of the month" + "\n" + allBills.get(position).getDaysBefore() + " Days reminder";
                }
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setPositiveButton("Edit Bill", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditedBill editedBill = new EditedBill();
                        editedBill.setEditBill(allBills.get(position).getBillName());
                        Bundle bundle = new Bundle();
                        bundle.putString("editedBill", editedBill.getEditBills());
                        Fragment editbillsFragment = new EditBills();
                        editbillsFragment.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentLayout, editbillsFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        ListBillsSummary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Delete " + allBills.get(position).getBillName() + " Bill?");
                alertDialogBuilder.setPositiveButton("Delete Bill", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db_reference.child("Bills").child(allBills.get(position).getBillName()).removeValue();
                        allBills.remove(position);
                        ad.notifyDataSetChanged();
                        Fragment summaryFragment = new Summary();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentLayout, summaryFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                AlertDialog ad = alertDialogBuilder.create();
                ad.show();
                return true;
            }
        });
        return view;
    }
}