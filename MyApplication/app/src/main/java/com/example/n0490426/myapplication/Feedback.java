package com.example.n0490426.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.n0490426.myapplication.utils.Constants.USER_FEEDBACK;
import static com.example.n0490426.myapplication.utils.Constants.USER_FEEDBACK_RATING;

public class Feedback extends Fragment {
    private TextView TextRatingFeedback;
    private TextView InputFeedback;
    private RatingBar RatingBarFeedback;
    private Button SubmitButton;
    private FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = crntUser.getUid();
    private DatabaseReference db_reference = FirebaseDatabase.getInstance().getReference("Users/" + userID);

    public Feedback() {
        // Required empty public constructor
    }

    public static Feedback newInstance(String param1, String param2) {
        Feedback fragment = new Feedback();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        getActivity().setTitle(R.string.feedback);
        InputFeedback = view.findViewById(R.id.inputFeedback);
        TextRatingFeedback = view.findViewById(R.id.textRatingFeedback);
        RatingBarFeedback = view.findViewById(R.id.ratingBarFeedback);
        SubmitButton = view.findViewById(R.id.submitButton);

        RatingBarFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                TextRatingFeedback.setText(String.valueOf(rating));
            }
        });

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserFeedback();
                Toast.makeText(getActivity(), "Thank you for the feedback", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void addUserFeedback() {
        Calendar calendar = Calendar.getInstance();
        FirebaseUser crntUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = crntUser.getUid();
        DatabaseReference db_user = FirebaseDatabase.getInstance().getReference();
        DatabaseReference feedback = db_user.child("Users").child(userID).child("Feedback");
        Map newPost = new HashMap();
        newPost.put(USER_FEEDBACK, InputFeedback.getText().toString());
        newPost.put(USER_FEEDBACK_RATING, RatingBarFeedback.getRating());
        feedback.setValue(newPost);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
