package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button signupButton;
    private TextView mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        signup();
    }
    private void signup(){
        mEmail=findViewById(R.id.email_signup);
        mPassword=findViewById(R.id.password_signup);
        signupButton=findViewById(R.id.btn_signup);
        mLogin=findViewById(R.id.login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String password=mEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email cannot be empty. Please enter a valid Email id");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password cannot be empty.");
                    return;
                }

            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}