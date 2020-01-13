package com.simarjot.bookwala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.simarjot.bookwala.helpers.Helper;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class RegistrationActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final String TAG = "nerd";
    private EditText name;
    private EditText email;
    private RoundedImageView profileView;
    private ImageView backView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profileView = findViewById(R.id.profile_image);
        backView = findViewById(R.id.back_button);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            name.setText(user.getDisplayName());
            profileView.setImageURI(user.getPhotoUrl());
        }


        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileView.setOnClickListener(v -> {
            if(Helper.isReadStoragePermissionGranted(RegistrationActivity.this) &&
                    Helper.isReadStoragePermissionGranted(RegistrationActivity.this)){
                Helper.pickImageFromGallery(RegistrationActivity.this, GALLERY_REQUEST_CODE);
            }
        });
        RoundedImageView plusIcon = findViewById(R.id.plus_icon);
        plusIcon.setOnClickListener(v -> {
            if(Helper.isReadStoragePermissionGranted(RegistrationActivity.this) &&
                    Helper.isReadStoragePermissionGranted(RegistrationActivity.this)){
                Helper.pickImageFromGallery(RegistrationActivity.this, GALLERY_REQUEST_CODE);
            }
        });
    }

    public void onClickNext(View view){
        String nameString = name.getText().toString();
        String emailString = email.getText().toString();

        boolean hasErrors=false;
        if(!Helper.isValidEmail(emailString)){
            email.setError("Enter a valid Email Address");  
            hasErrors = true;
        }
        if(nameString.length()<2) {
            name.setError("Name should have atleast 2 characters");
            hasErrors = true;
        }
        if(!hasErrors){
            updateUserDetails(nameString);
        }
    }

    private void updateUserDetails(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                             Log.d(TAG, "failed to update profile", e);
                        Toast.makeText(RegistrationActivity.this, "failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    Helper.pickImageFromGallery(RegistrationActivity.this, GALLERY_REQUEST_CODE);
                }else{
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    Helper.pickImageFromGallery(RegistrationActivity.this, GALLERY_REQUEST_CODE);
                }else{
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    Log.d("nerd","user selected this image" + selectedImage.toString());
                    File file = Helper.getImageFile(); // 2
                    Uri destinationUri = Uri.fromFile(file);

                    UCrop.Options options = new UCrop.Options();
                    options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

                    options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
                    options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey));
                    options.setCropFrameColor(ContextCompat.getColor(this, R.color.dark_grey));
                    options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.black));

                    options.setCircleDimmedLayer(true);
                    options.setCompressionQuality(100);

                    UCrop.of(selectedImage,destinationUri)
                            .withMaxResultSize(100, 100)
                            .withOptions(options)
                            .start(RegistrationActivity.this);
                    break;
                case UCrop.RESULT_ERROR:
                    final Throwable cropError = UCrop.getError(data);
                    Log.d("nerd", "UCrop error occured\n", cropError);
                    break;
                case UCrop.REQUEST_CROP:
                    imageUri = UCrop.getOutput(data);
                    Log.d("nerd", "recieved cropped image: " + imageUri.toString());
                    profileView.setImageURI(imageUri);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        } else if (resultCode == Activity.RESULT_CANCELED){
            Log.d("nerd", "result cancelled");
        }

        if(resultCode==96){
            final Throwable cropError = UCrop.getError(data);
            Log.d("nerd", "UCrop error occured\n", cropError);
        }
    }
}
