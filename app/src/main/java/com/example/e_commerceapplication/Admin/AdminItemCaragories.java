package com.example.e_commerceapplication.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.e_commerceapplication.HomeActivity;
import com.example.e_commerceapplication.MainActivity;
import com.example.e_commerceapplication.R;

public class AdminItemCaragories extends AppCompatActivity implements View.OnClickListener {
    private Button mangso, dal, tel,mosla, others, logoutBtn ,piajAdaRosunBtn, maintainProductBtn, checkOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_caragories);

        mangso = findViewById(R.id.mangso);
        tel = findViewById(R.id.catagoryTel);
        dal = findViewById(R.id.catagoryDal);
        mosla = findViewById(R.id.catagoryMosla);
        piajAdaRosunBtn = findViewById(R.id.piaj_ada_rosun);
        others = findViewById(R.id.catagoryOthers);
        logoutBtn = findViewById(R.id.logut_btn);
        checkOrderBtn = findViewById(R.id.view_orders);
        maintainProductBtn = findViewById(R.id.maintain_products);

        mangso.setOnClickListener(this);
        tel.setOnClickListener(this);
        mosla.setOnClickListener(this);
        dal.setOnClickListener(this);
        others.setOnClickListener(this);
        piajAdaRosunBtn.setOnClickListener(this);
        maintainProductBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        checkOrderBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.mangso){
            Intent intent = new Intent(AdminItemCaragories.this, AddnewProductActivity.class);
            intent.putExtra("category","mangso");
            startActivity(intent);
        }
        if(v.getId() == R.id.catagoryTel){
            Intent intent = new Intent(AdminItemCaragories.this,AddnewProductActivity.class);
            intent.putExtra("category","tel");
            startActivity(intent);
        }
        if(v.getId() == R.id.catagoryDal){
            Intent intent = new Intent(AdminItemCaragories.this,AddnewProductActivity.class);
            intent.putExtra("category","dal");
            startActivity(intent);
        }
        if(v.getId() == R.id.catagoryMosla){
            Intent intent = new Intent(AdminItemCaragories.this,AddnewProductActivity.class);
            intent.putExtra("category","mosla");
            startActivity(intent);
        }
        if(v.getId() == R.id.catagoryOthers){
            Intent intent = new Intent(AdminItemCaragories.this,AddnewProductActivity.class);
            intent.putExtra("category","others");
            startActivity(intent);
        }

        if(v.getId() == R.id.logut_btn){
            Intent intent = new Intent(AdminItemCaragories.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish();
        }

        if(v.getId() == R.id.view_orders){
            Intent intent = new Intent(AdminItemCaragories.this, AdminNewOrderActivity.class);
            startActivity(intent);
        }
        if(v.getId() == R.id.piaj_ada_rosun){
            Intent intent = new Intent(AdminItemCaragories.this,AddnewProductActivity.class);
            intent.putExtra("category","piaj_ada_rosun");
            startActivity(intent);
        }
        //maintain product
        if(v.getId() == R.id.maintain_products){
            Intent intent = new Intent(AdminItemCaragories.this, HomeActivity.class);
            intent.putExtra("Admin","Admin");
            startActivity(intent);
        }


    }
}