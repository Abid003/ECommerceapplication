package com.example.e_commerceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapplication.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditTxt, phoneEditTxt, addressEditTxt, cityEditTxt;
    private TextView totalAmountTextView;
    private Button confirmOrderButton;
    private String totalAmount = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");

        nameEditTxt = findViewById(R.id.delevery_name);
        phoneEditTxt = findViewById(R.id.delevery_number);
        addressEditTxt = findViewById(R.id.delevery_address);
        cityEditTxt = findViewById(R.id.delevery_city);
        totalAmountTextView = findViewById(R.id.totalProceTV);
        confirmOrderButton = findViewById(R.id.confirm_order_button);

        totalAmountTextView.setText("Total price = "+totalAmount+"TK");

        cityEditTxt = findViewById(R.id.delevery_city);

        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }


        });
    }
    private void checkFields() {
        if(TextUtils.isEmpty(nameEditTxt.getText().toString())){
            nameEditTxt.setError("please enter your name");
            nameEditTxt.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(phoneEditTxt.getText().toString())){
            phoneEditTxt.setError("please enter your phone number");
            phoneEditTxt.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(addressEditTxt.getText().toString())){
            addressEditTxt.setError("please enter delevery address");
            addressEditTxt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(cityEditTxt.getText().toString())){
            cityEditTxt.setError("please enter your city");
            cityEditTxt.requestFocus();
            return;
        }
        else {
            confirmOrder();
            
        }
    }

    private void confirmOrder() {
       final String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat cuurentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = cuurentDate.format(calForDate.getTime());

        SimpleDateFormat cuurentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = cuurentTime.format(calForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").
                child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String,Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",nameEditTxt.getText().toString());
        ordersMap.put("phone",phoneEditTxt.getText().toString());
        ordersMap.put("address",addressEditTxt.getText().toString());
        ordersMap.put("city",cityEditTxt.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("status","not delivered");

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("cart list")
                            .child("User view").child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this,"your order has been placed successfully",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                }
            }
        });
    }


}