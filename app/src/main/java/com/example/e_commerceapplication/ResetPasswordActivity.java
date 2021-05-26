package com.example.e_commerceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapplication.Admin.AdminMaintainProductsActivity;
import com.example.e_commerceapplication.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private  String  check= "";
    private TextView pageTitle,titleQuestion;
    private EditText phoneNumber, question1, question2;
    private Button varifyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.page_title);
        titleQuestion = findViewById(R.id.title_question);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        varifyBtn = findViewById(R.id.varify_btn);



    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);

        if(check.equals("settings")){

            pageTitle.setText("Set question");
            titleQuestion.setText("Please answer the questions");
            varifyBtn.setText("Set");

            displayPreviousAnswers();

            varifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();

                }
            });

        }
        else if(check.equals("login")){

            phoneNumber.setVisibility(View.VISIBLE);

            varifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    varifyUser();
                }

            });

        }


    }

    private void setAnswers(){

        String answer1 = question1.getText().toString().toLowerCase();
        String answer2= question2.getText().toString().toLowerCase();

        if(TextUtils.isEmpty(answer1)){
            question1.setError("please type an answer");
            question1.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(answer2)){
            question2.setError("please type an answer");
            question2.requestFocus();
            return;
        }
        else {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String,Object> userDataMap = new HashMap<>();
            userDataMap.put("answer1",answer1);
            userDataMap.put("answer2",answer2);

            ref.child("Security questions").updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this,"set successfully",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }

    }


    private void displayPreviousAnswers() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String ans1 = snapshot.child("answer1").getValue().toString();
                    String ans2 = snapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void varifyUser() {

        String phone = phoneNumber.getText().toString();
        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question1.getText().toString().toLowerCase();

        if (!answer1.equals("") && !answer2.equals("") && !phone.equals("")) {

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        String mPhone = snapshot.child("phone").getValue().toString();

                        if (snapshot.hasChild("Security questions")) {

                            String ans1 = snapshot.child("Security questions").child("answer1").getValue().toString();
                            String ans2 = snapshot.child("Security questions").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1)) {
                                Toast.makeText(ResetPasswordActivity.this, "answer 1 is incorrect", Toast.LENGTH_LONG).show();
                            } else if (!ans1.equals(answer2)) {
                                Toast.makeText(ResetPasswordActivity.this, "answer 2 is incorrect", Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("write new password here..");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!newPassword.getText().toString().equals("")) {
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ResetPasswordActivity.this, "password has been changed successfully", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "you haven't set the security question", Toast.LENGTH_LONG).show();

                        }
                    }
                    else {
                        Toast.makeText(ResetPasswordActivity.this, "phone number doesn't exist.", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else {
            Toast.makeText(ResetPasswordActivity.this, "please complete the form.", Toast.LENGTH_LONG).show();
        }
    }
}