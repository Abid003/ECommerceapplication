package com.example.e_commerceapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_commerceapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn;
    private EditText name, price, description;
    private ImageView imageView;

    private String productID = "";
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);


        applyChangesBtn = findViewById(R.id.product_maintain_updateBtn);
        name = findViewById(R.id.product_name_layout);
        description = findViewById(R.id.product_description_layout);
        price = findViewById(R.id.product_price_layout);
        imageView = findViewById(R.id.product_image_layout);

        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }


        });

    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String pname = snapshot.child("pname").getValue().toString();
                    String pprice = snapshot.child("price").getValue().toString();
                    String pdescription = snapshot.child("description").getValue().toString();
                    String pimage = snapshot.child("image").getValue().toString();

                    name.setText(pname);
                    price.setText(pprice);
                    description.setText(pdescription);
                    Picasso.get().load(pimage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void applyChanges() {
        String pname = name.getText().toString();
        String pprice = price.getText().toString();
        String pdescription = description.getText().toString();

        if(TextUtils.isEmpty(pname)){
            name.setError("please enter product name");
            name.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(pprice)){
            price.setError("please enter price");
            price.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(pdescription)){
            description.setError("please enter description");
            description.requestFocus();
            return;
        }
        else{
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("description",pdescription);
            productMap.put("price",pprice);
            productMap.put("pname",pname);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
               public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "updated successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminMaintainProductsActivity.this, AdminItemCaragories.class);

                        startActivity(intent);
                    }

              }
          });


        }

    }

}