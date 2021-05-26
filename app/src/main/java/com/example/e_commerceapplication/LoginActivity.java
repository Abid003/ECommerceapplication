package com.example.e_commerceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapplication.Admin.AdminLoginActivity;
import com.example.e_commerceapplication.Prevalent.Prevalent;
import com.example.e_commerceapplication.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputPhone,inputPassword;
    private Button loginButton;
    private TextView gotosighupPage, im_admin, forgetPassword;
    private ProgressDialog loadingBar;
    private String parentDBName = "Users";
    private CheckBox rememberMeCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gotosighupPage = findViewById(R.id.signupTxtView);
        loginButton = findViewById(R.id.loginBtn);
        inputPhone = findViewById(R.id.usernameEditTxt);
        inputPassword = findViewById(R.id.passwordEditTxt);
        forgetPassword = findViewById(R.id.forget_password);

        im_admin = findViewById(R.id.im_admin);

        loadingBar = new ProgressDialog(this);

        rememberMeCb = findViewById(R.id.checkBox);
        Paper.init(this);

        gotosighupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,signup_activity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();

            }
        });

        im_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(LoginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });


    }


    private void userLogin() {
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            inputPhone.setError("please enter phone number");
            inputPhone.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(password)){
            inputPassword.setError("please enter password");
            inputPassword.requestFocus();
            return;
        }
        else
        {

             loadingBar.setTitle("Login Account");
              loadingBar.setMessage("please wait");
              loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            checkAccountValidity(phone,password);
        }
    }

    private void checkAccountValidity(String phone, String password) {

        if(rememberMeCb.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey,phone);
            Paper.book().write(Prevalent.userPasswordKey,password);

        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(parentDBName).child(phone).exists()) {

                    Users userData = snapshot.child(parentDBName).child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone)) {
                        if (userData.getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = userData;
                            startActivity(intent);
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                        }


                    }
                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "user doesn't exist", Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}