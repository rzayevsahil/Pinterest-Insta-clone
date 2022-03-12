package com.sahilrzayev.instagramclone.view;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sahilrzayev.instagramclone.databinding.ActivityUpdateProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class UpdateProfileActivity extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;


    Uri imageData;
    String image;

    FirebaseStorage firebaseStorage;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseUser currentUser;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        System.out.println(currentUser.getUid());
        documentReference = firebaseFirestore.collection("Profiles").document(currentUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                    binding.userNameEditText.setText(documentSnapshot.get("userName").toString());
                    binding.emailEditText.setText(documentSnapshot.get("email").toString());
                    binding.passwordEditText.setText(documentSnapshot.get("password").toString());
                    image = documentSnapshot.get("profileImage").toString();
                    imageData=Uri.parse(image);
                    Picasso.get().load(documentSnapshot.get("profileImage").toString()).into((ImageView) binding.updateProfileImageView);
                    }
                }
            }
        });


    }

    public void backButtonClick(View view) {
        Intent intent = new Intent(UpdateProfileActivity.this, FeedActivity.class);
        startActivity(intent);
    }

    public void updateProfileButtonClick(View view)
        {
            String userName = binding.userNameEditText.getText().toString();
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            String passwordTekrar = binding.passwordEditText2.getText().toString();
            ImageView imageUrl = binding.updateProfileImageView;
            currentUser.updateEmail(email);
            currentUser.updatePassword(password);

            System.out.println("-------1---------");
            if (!email.equals("") || !password.equals("")){
                System.out.println("-------1.1---------");

                if (password.equals(passwordTekrar)){
                    System.out.println("-------1.2---------");
                    if (imageData != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .setPhotoUri(Uri.parse(imageUrl.toString()))
                                .build();
                        currentUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                            //UUID uuid = UUID.randomUUID();
                                            String imageName = "profileImages/" + image + ".jpg";
                                            System.out.println("--------4--------");

                                            String profileImage = image;
                                            HashMap<String, Object> userData = new HashMap<String, Object>();
                                            userData.put("email", email);
                                            userData.put("profileImage", profileImage);
                                            userData.put("userName", userName);
                                            userData.put("password", password);
                                            DocumentReference documentReference1 = firebaseFirestore.collection("Profiles").document(currentUser.getUid());
                                            //documentReference1.update({})
                                            firebaseFirestore.collection("Profiles").document(currentUser.getUid()).update(userData).addOnCompleteListener(UpdateProfileActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        Toast.makeText(UpdateProfileActivity.this, "Güncelleme işlemi yapıldı.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(UpdateProfileActivity.this,FeedActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });


                                        }
                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(UpdateProfileActivity.this, "Passwords cannot be equal!!!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(UpdateProfileActivity.this, "Fields not empty!!!", Toast.LENGTH_SHORT).show();
            }



        }



}