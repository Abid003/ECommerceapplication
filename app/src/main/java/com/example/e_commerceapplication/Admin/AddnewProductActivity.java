package com.example.e_commerceapplication.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_commerceapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddnewProductActivity extends AppCompatActivity {

    private String categoryName, pname,price,description,saveCurrentDate,saveCurrentTime;
    private ImageView addImage;
    private EditText addItemName, addDescription, addPrice;
    private Button addItemButton;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private  String productRandomKey,downloadImageUrl;
    private StorageReference mStorageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew_product);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("Product images");

        categoryName = getIntent().getExtras().get("category").toString();
        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        addImage = findViewById(R.id.addImage);
        addItemName = findViewById(R.id.addName);
        addDescription = findViewById(R.id.addDescription);
        addPrice = findViewById(R.id.addPrice);
        addItemButton = findViewById(R.id.addButton);

        loadingBar = new ProgressDialog(this);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              openGallery();
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkProductValidity();
            }
        });

    }

    private void checkProductValidity() {
        pname = addItemName.getText().toString();
        description = addDescription.getText().toString();
        price = addPrice.getText().toString();

        if(imageUri == null){

            Toast.makeText(this,"please add an image",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(pname)){
            addItemName.setError("please enter item name");
            addItemName.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(description)){
            addDescription.setError("please enter description");
            addDescription.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(price)){
            addPrice.setError("please enter the price");
            addPrice.requestFocus();
            return;
        }
        else {
            storeImageInfo();
        }

    }

    private void storeImageInfo() {

        loadingBar.setTitle("Adding new product");
        loadingBar.setMessage("please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = mStorageRef.child(imageUri.getLastPathSegment() + productRandomKey);

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddnewProductActivity.this,"error occurs please try again",Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddnewProductActivity.this,"uploaded successfully",Toast.LENGTH_LONG).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            saveProductInfo();
                        }
                    }
                });

            }

        });


        
    }



    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick && resultCode == RESULT_OK && data!= null){
            imageUri = data.getData();
            addImage.setImageURI(imageUri);
        }
    }


    private void saveProductInfo() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",price);
        productMap.put("pname",pname);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent(AddnewProductActivity.this, AdminItemCaragories.class);
                            startActivity(intent);

                            loadingBar.dismiss();

                            Toast.makeText(AddnewProductActivity.this,"product added successfully",Toast.LENGTH_LONG);
                        }
                        else{
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddnewProductActivity.this,"Error occurs "+message,Toast.LENGTH_LONG);
                        }
                    }
                });

    }
}