package com.example.e_commerceapplication.Admin;

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

import com.example.e_commerceapplication.HomeActivity;
import com.example.e_commerceapplication.LoginActivity;
import com.example.e_commerceapplication.Model.Admins;
import com.example.e_commerceapplication.Model.Users;
import com.example.e_commerceapplication.Prevalent.Prevalent;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.ResetPasswordActivity;
import com.example.e_commerceapplication.signup_activity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText inputPhone,inputPassword;
    private Button adminloginButton;
    private TextView im_not_admin;
    private ProgressDialog loadingBar;
    private String parentDBName = "Admins";
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);




        inputPhone = findViewById(R.id.adminphnEditTxt);
        inputPassword = findViewById(R.id.adminpasswordEditTxt);
        im_not_admin = findViewById(R.id.im_notadmin);
        adminloginButton = findViewById(R.id.adminLoginBtn);
        loadingBar = new ProgressDialog(this);



        adminloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adminLogin();

            }
        });

        im_not_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    private void adminLogin() {
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
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(parentDBName).child(phone).exists()) {

                    Admins adminsData = snapshot.child(parentDBName).child(phone).getValue(Admins.class);

                    if (adminsData.getPhone().equals(phone)) {
                        if (adminsData.getPassword().equals(password)) {
                            Toast.makeText(AdminLoginActivity.this, "logged in as admin", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(AdminLoginActivity.this, AdminItemCaragories.class);
                            startActivity(intent);
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(AdminLoginActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                        }


                    }
                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(AdminLoginActivity.this, "Admin doesn't exist", Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}