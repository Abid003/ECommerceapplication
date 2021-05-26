package com.example.e_commerceapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.EthiopicCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapplication.Model.Users;
import com.example.e_commerceapplication.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

   private CircleImageView profileImageView;
    private EditText fullnameEditTxt, userPhoneEditTxt, addressEditTxt;
    private TextView profileChangeTxtBtn, closeTxtBtn, saveTxtBtn;
    private Uri imageUri;
    private String myUrl = "";
    private String checker = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private Button sequriyuqBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("profile pictures");

        fullnameEditTxt = findViewById(R.id.editTextName);
        addressEditTxt = findViewById(R.id.editTextAdress);
         userPhoneEditTxt = findViewById(R.id.editTextpgoneNumber);
        profileImageView = findViewById(R.id.profile_image_setting);
        profileChangeTxtBtn = findViewById(R.id.profiel_change_btn);
        closeTxtBtn = findViewById(R.id.close_textview);
        saveTxtBtn = findViewById(R.id.update_textview);
        sequriyuqBtn = findViewById(R.id.sequrity_Q_btn);

        displayUserInfo(profileImageView, fullnameEditTxt, userPhoneEditTxt, addressEditTxt);

        closeTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                   userInfoSaved();
                }
                else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker ="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

            }

        });

        sequriyuqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new  Intent(SettingsActivity.this,ResetPasswordActivity.class);
               intent.putExtra("check","settings");
               startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "Error occurs please try again", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void displayUserInfo(CircleImageView profileImageView, EditText fullnameEditTxt, EditText userPhoneEditTxt, EditText addressEditTxt) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameEditTxt.setText(name);
                        userPhoneEditTxt.setText(phone);
                        addressEditTxt.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String , Object> userMap = new HashMap<>();
        userMap.put("name",fullnameEditTxt.getText().toString());
        userMap.put("address",addressEditTxt.getText().toString());
        userMap.put("phoneOrder",userPhoneEditTxt.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }



    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullnameEditTxt.getText().toString())){
            fullnameEditTxt.setError("please enter full name ");
            fullnameEditTxt.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(addressEditTxt.getText().toString())){
            addressEditTxt.setError("please enter address");
            addressEditTxt.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(userPhoneEditTxt.getText().toString())){
            userPhoneEditTxt.setError("please enter phone number");
            userPhoneEditTxt.requestFocus();
            return;
        }
        else if (checker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog  progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("updating profile");
        progressDialog.setMessage("please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone()+".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String , Object> userMap = new HashMap<>();
                        userMap.put("name",fullnameEditTxt.getText().toString());
                        userMap.put("address",addressEditTxt.getText().toString());
                        userMap.put("phoneOrder",userPhoneEditTxt.getText().toString());
                        userMap.put("image",myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(SettingsActivity.this, "please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}