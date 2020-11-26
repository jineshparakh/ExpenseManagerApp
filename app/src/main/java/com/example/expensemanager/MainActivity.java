package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button loginButton;
    private TextView mForgotPassword;
    private TextView mSignup;
    private TextView m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login();
    }
    private void login(){
        mEmail=findViewById(R.id.email_login);
        mPassword=findViewById(R.id.password_login);
        loginButton=findViewById(R.id.btn_login);
        mForgotPassword=findViewById(R.id.forgot_password);
        mSignup=findViewById(R.id.signup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

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
        // Redirect to SignUp activity
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
        // Redirect to Login activity
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResetPasswordActivity.class));
            }
        });
    }
}