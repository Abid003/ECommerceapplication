package com.example.e_commerceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.Toast;

import com.example.e_commerceapplication.Model.Cart;
import com.example.e_commerceapplication.Prevalent.Prevalent;
import com.example.e_commerceapplication.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount, txtVmessage1;;
    private int  totalPrice =0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager =new  LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = findViewById(R.id.next_process_btn);
        txtTotalAmount = findViewById(R.id.total_price);

        txtVmessage1 = findViewById(R.id.message_1);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(totalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkStatus();

        final DatabaseReference cartlistRef = FirebaseDatabase.getInstance().getReference().child("cart list");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartlistRef.child("User view").child(Prevalent.currentOnlineUser.getPhone()).child("cartProducts"),Cart.class).build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new
                FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                        holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                        holder.txtProductPrice.setText(model.getPrice());
                        holder.txtProductName.setText(model.getPname());

                        int oneTypeProductTPrice =((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                        totalPrice = totalPrice + oneTypeProductTPrice;
                        txtTotalAmount.setText("Total price = "+String.valueOf(totalPrice)+" tk");


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Edit", "Remove"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                            intent.putExtra("pid",model.getPid());
                                            startActivity(intent);
                                        }
                                        if(which == 1){
                                            cartlistRef.child("User view").child(Prevalent.currentOnlineUser.getPhone()).
                                                    child("cartProducts").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

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
                    String username = snapshot.child("name").getValue().toString();

                    if(shippingStatus.equals("delivered")){
                        txtTotalAmount.setText("Dear"+username+"order is delvered successfully");
                        recyclerView.setVisibility(View.GONE);
                        nextProcessBtn.setVisibility(View.GONE);
                        txtVmessage1.setText("Congratulation, your order has been deliverd successfully, soon you will recive your products");
                        Toast.makeText(CartActivity.this, "After receiving the fast order you can purchase more products", Toast.LENGTH_LONG).show();
                        txtVmessage1.setVisibility(View.VISIBLE);



                    }
                    else if(shippingStatus.equals("not delivered")){
                        txtTotalAmount.setText("delivary status = not delivered");
                        recyclerView.setVisibility(View.GONE);
                        nextProcessBtn.setVisibility(View.GONE);
                        txtVmessage1.setVisibility(View.VISIBLE);
                        Toast.makeText(CartActivity.this, "After receiving the fast order you can purchase more products", Toast.LENGTH_LONG).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}