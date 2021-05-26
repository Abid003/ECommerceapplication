package com.example.e_commerceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.e_commerceapplication.Model.Products;
import com.example.e_commerceapplication.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.PrimitiveIterator;

public class ProductDetailsActivity extends AppCompatActivity {
  private   FloatingActionButton addToCartFloatingBtn;
  private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productDescription, productName;
    private String productID = "", status = "normal";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        addToCartBtn = findViewById(R.id.pd_add_to_cart_btn);
        numberButton = findViewById(R.id.number_btn);
        productPrice = findViewById(R.id.product_price_details);
        productDescription = findViewById(R.id.product_description_details);
        productName = findViewById(R.id.product_name_details);
        productImage = findViewById(R.id.product_image_details);

        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status.equals("Order placed") || status.equals("Order delivered")){
                    Toast.makeText(ProductDetailsActivity.this,"you can add more product if the first order is confirmed",Toast.LENGTH_LONG).show();
                }
                else {
                    addingToCartList();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkStatus();
    }

    private void addingToCartList() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat cuurentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = cuurentDate.format(calForDate.getTime());

        SimpleDateFormat cuurentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = cuurentTime.format(calForDate.getTime());

       final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("cart list");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("time",saveCurrentTime);
        cartMap.put("discount","");

        cartListRef.child("User view").child(Prevalent.currentOnlineUser.getPhone()).
                child("cartProducts").child(productID).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            cartListRef.child("Admin view").child(Prevalent.currentOnlineUser.getPhone()).
                                    child("cartProducts").child(productID).updateChildren(cartMap)
                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {

                              if(task.isSuccessful()){
                                  Toast.makeText(ProductDetailsActivity.this,"Added to cart list",Toast.LENGTH_LONG).show();

                                  Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                  startActivity(intent);
                              }

                          }
                      });
                        }
                    }
                });
    }

    private void getProductDetails(String productID) {
        DatabaseReference prductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        prductRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);
                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkStatus(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String shippingStatus = snapshot.child("status").getValue().toString();

                    if(shippingStatus.equals("delivered")){

                        status = "Order delivered";

                    }
                    else if(shippingStatus.equals("not delivered")){
                        status = "Order placed";
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}