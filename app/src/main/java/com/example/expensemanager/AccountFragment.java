package com.example.expensemanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView= inflater.inflate(R.layout.fragment_account2, container, false);
        EditText emailUser=myView.findViewById(R.id.email_account);
        EditText dateofCreation=myView.findViewById(R.id.dateofCreation);
        EditText timeOfCreation=myView.findViewById(R.id.timeOfCreation);
        EditText signInAt=myView.findViewById(R.id.lastSignInAt);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        emailUser.setText(user.getEmail());
        Long timestampCreate=user.getMetadata().getCreationTimestamp();
        Date date1 = new Date(timestampCreate);
        SimpleDateFormat jdf = new SimpleDateFormat("dd MMM yyyy");
        String java_date = jdf.format(date1);

        SimpleDateFormat jdf1 = new SimpleDateFormat("HH:mm:ss z");
        String TimeOfCreation = jdf1.format(date1);
        dateofCreation.setText(java_date);
        timeOfCreation.setText(TimeOfCreation);
        Long lastSignInTS=user.getMetadata().getLastSignInTimestamp();

        Date date2 = new Date(lastSignInTS);
        SimpleDateFormat jdf2 = new SimpleDateFormat("dd MMM yyyy    HH:mm:ss z");
        String SignInAt = jdf2.format(date2);
        signInAt.setText(SignInAt);




        return myView;
    }
}