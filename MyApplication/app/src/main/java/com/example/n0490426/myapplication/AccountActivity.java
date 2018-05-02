package com.example.n0490426.myapplication;

import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.n0490426.myapplication.utils.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.n0490426.myapplication.utils.Constants.*;

public class AccountActivity extends Fragment {
    private EditText InputCity;
    private RadioGroup RadioGroupHouseOrFlat;
    private RadioButton radioButton, RadioFlat, RadioHouse;
    private Button UpdateBtn;
    private FirebaseUser crntUser;
    private Spinner HouseMatesSpinner;

    public AccountActivity() {
        // Required empty public constructor
    }

    public static AccountActivity newInstance(String param1, String param2) {
        AccountActivity fragment = new AccountActivity();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account, container,false);
        getActivity().setTitle(R.string.Account_page_title);
        InputCity = (EditText) view.findViewById(R.id.inputCity);
        RadioGroupHouseOrFlat = (RadioGroup) view.findViewById(R.id.radioGroupHouseOrFlat);
        UpdateBtn = (Button) view.findViewById(R.id.updateBtn);
        HouseMatesSpinner = (Spinner) view.findViewById(R.id.inputNumberHousemates);
        RadioFlat = (RadioButton) view.findViewById(R.id.radioFlat);
        RadioHouse = (RadioButton) view.findViewById(R.id.radioHouse);
        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sendRequest = true;
                if(InputCity.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please enter account City", Toast.LENGTH_SHORT).show();
                    sendRequest = false;
                }
                if(!RadioFlat.isChecked() && !RadioHouse.isChecked()) {
                    Toast.makeText(getActivity(), "Please select house or flat", Toast.LENGTH_SHORT).show();
                    sendRequest = false;
                }
                if(sendRequest) {
                    addUserDetailsToDB();
                    Toast.makeText(getActivity(), "User details updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setupSpinner();

        RadioGroupHouseOrFlat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton) getActivity().findViewById(checkedId);
                switch(checkedId) {
                    case R.id.radioFlat:
                        Log.d("Radio Button Test", "onCheckedChanged:radio flat ");
                        break;
                    case R.id.radioHouse:
                        Log.d("Radio Button Test", "onCheckedChanged:radio house ");
                        break;
                }
            }
        });
        return view;
    }

    private ArrayList<Integer> spinnerItems;
    private ArrayAdapter<Integer> adapter;
    public void setupSpinner() {
        spinnerItems = new ArrayList<Integer>();
        for(int i = 0; i < 9; i++) {
            spinnerItems.add(i);
        }
        adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        HouseMatesSpinner.setAdapter(adapter);
    }

    private void addUserDetailsToDB() {
        Account account = new Account();
        account.setCity(InputCity.getText().toString());
        account.setNumberOfHouseates(Integer.parseInt(HouseMatesSpinner.getSelectedItem().toString()));
        switch (radioButton.getId()) {
            case R.id.radioFlat:
                account.setHouseOrFlat(ACCOMMODATION_TYPE_FLAT);
                break;
            case R.id.radioHouse:
                account.setHouseOrFlat(ACCOMMODATION_TYPE_HOUSE);
                break;
        }
        FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = crntUser.getUid();
        DatabaseReference db_user = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = db_user.child("Users").child(userID).child("UserDetails");
        Map newPost = new HashMap();
        newPost.put(USER_DETAILS_NAME_ID, crntUser.getDisplayName());
        newPost.put(USER_DETAILS_APPARTMENT_ID, account.getHouseOrFlat());
        newPost.put(USER_DETAILS_CITY_ID, account.getCity());
        newPost.put(USER_DETAILS_NUMBEROFFLATMATES_ID, account.getNumberOfHouseates());
        users.setValue(newPost);
    }
}





