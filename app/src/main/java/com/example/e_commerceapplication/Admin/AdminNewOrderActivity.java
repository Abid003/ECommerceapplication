package com.example.e_commerceapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.e_commerceapplication.Model.AdminOrders;
import com.example.e_commerceapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList = findViewById(R.id.order_list);

        orderList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(orderRef,AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int position, @NonNull AdminOrders model) {
                        holder.username.setText(model.getName());
                        holder.userPhone.setText(model.getPhone());
                        holder.userAddress.setText("address- "+model.getAddress()+", "+model.getCity());
                        holder.userTime.setText("Ordered at "+model.getDate()+" at "+model.getTime());
                        holder.userTotalPrice.setText("Total amount "+model.getTotalAmount()+"Tk");

                        holder.showOrdersButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uid = getRef(position).getKey();

                                Intent intent = new Intent(AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Yes",
                                        "no"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                                builder.setTitle("have you delivered this oder?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0){
                                            
                                            String uid = getRef(position).getKey();
                                            removeOrder(uid);
                                        }
                                        else {
                                            finish();
                                        }

                                    }

                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrderViewHolder(view);
                    }
                };



        orderList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder{

        public TextView username,userPhone,userTotalPrice,userTime,userAddress;
        public Button showOrdersButton;

        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.ordered_user_name);
            userPhone = itemView.findViewById(R.id.ordered_phone_number);
            userTotalPrice = itemView.findViewById(R.id.orderd_total_price);
            userTime = itemView.findViewById(R.id.ordered_time);
            userAddress = itemView.findViewById(R.id.ordered_address);
            showOrdersButton = itemView.findViewById(R.id.show_all_products_btn);
        }
    }

    private void removeOrder(String uid) {
        orderRef.child(uid).removeValue();
    }

}