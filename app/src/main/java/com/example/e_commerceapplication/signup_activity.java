package com.example.e_commerceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class signup_activity extends AppCompatActivity {
private Button signUpButton;
private EditText inputName,inputPhone,inputPassword;
private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);
        inputName = findViewById(R.id.usernameEditTxt);
        inputPhone = findViewById(R.id.phoneNumberEditTxt);
        inputPassword = findViewById(R.id.passwordEditTxt);
        signUpButton = findViewById(R.id.signUpButton);
        loadingBar = new ProgressDialog(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

            }

            private void createAccount() {
                String name = inputName.getText().toString();
                String phone = inputPhone.getText().toString();
                String password = inputPassword.getText().toString();

                if(TextUtils.isEmpty(name)){
                    inputName.setError("please enter username");
                    inputName.requestFocus();
                    return;
                }
               else if(TextUtils.isEmpty(phone)){
                    inputPhone.setError("please enter phone number");
                    inputPhone.requestFocus();
                    return;
                }

               else if(TextUtils.isEmpty(password)){
                    inputPassword.setError("please enter password");
                    inputPassword.requestFocus();
                    return;
                }
               else{
                   loadingBar.setTitle("Create Account");
                   loadingBar.setMessage("Please wait");
                   loadingBar.setCanceledOnTouchOutside(false);
                   loadingBar.show();
                   checkValidity(phone, name, password);
                }

            }

            private void checkValidity(String phone, String name,String password) {
                final DatabaseReference rootRef;
                rootRef = FirebaseDatabase.getInstance().getReference();
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.child("Users").child(phone).exists()){
                            HashMap<String,Object> userDataMap = new HashMap<>();
                            userDataMap.put("phone",phone);
                            userDataMap.put("password",password);
                            userDataMap.put("name",name);

                            rootRef.child("Users").child(phone).updateChildren(userDataMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(signup_activity.this,"successfully registered ",Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();

                                                Intent intent = new Intent(signup_activity.this,MainActivity.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                loadingBar.dismiss();
                                                Toast.makeText(signup_activity.this,"registration unsuccessful, check your internet and try again",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(signup_activity.this,"this phone number is already exist",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            Toast.makeText(signup_activity.this,"please try again with another phone number",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}