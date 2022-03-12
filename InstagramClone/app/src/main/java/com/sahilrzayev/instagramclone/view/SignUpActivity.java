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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sahilrzayev.instagramclone.databinding.ActivitySignUpBinding;
import com.sahilrzayev.instagramclone.databinding.ActivityUploadBinding;
import com.sahilrzayev.instagramclone.model.Profile;

import java.util.HashMap;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    Uri imageData=null;
    String username;

    // galeriye girmek için başlatıp veriyi geli aldığımız launcher
    ActivityResultLauncher<Intent> activityResultLauncher;

    // izin isteyeceğimiz launcher
    ActivityResultLauncher<String> permissionLauncher;

    private ActivitySignUpBinding binding;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

    }


    public void backButtonClick(View view) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void signUpButtonClick(View view) {
        String userName = binding.userNameEditText.getText().toString();
        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();
        String passwordTekrar = binding.passwordEditText2.getText().toString();

        System.out.println("-------1---------");
        if (!email.equals("") || !password.equals("")){
            System.out.println("-------1.1---------");

            if (password.equals(passwordTekrar)) {
                System.out.println("-------1.2---------");
                if (imageData != null) {
                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            currentUser = auth.getCurrentUser();
                            System.out.println("--------2--------");
                            if (currentUser != null) {

                                System.out.println("--------3--------");
                                UUID uuid = UUID.randomUUID();
                                String imageName = "profileImages/" + uuid + ".jpg";

                                // klasör oluşturup resimleri oraya eklemek
                                storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // storage'den url çekmek
                                        StorageReference newReference = firebaseStorage.getReference(imageName);
                                        newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                System.out.println("--------4--------");

                                                String profileImage = uri.toString();
                                                Profile profile = new Profile(email, userName, password, profileImage);

                                                firebaseFirestore.collection("Profiles").document(currentUser.getUid()).set(profile).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SignUpActivity.this, "Kayıt olma işlemi başarılı", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });


                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Toast.makeText(SignUpActivity.this, "Boş kullanıcı", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this, "You need to add photo!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignUpActivity.this, "Passwords cannot be equal!!!", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(SignUpActivity.this, "Fields not empty!!!", Toast.LENGTH_SHORT).show();
        }



    }

    public void selectProfileImage(View view) {
        // eğer resim paylaşmak için izin yoksa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // eğer bu doğruysa
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // izin göstermemiz gerekiyor
                // LENGTH_INDEFINITE - belirsiz bi süre yani kullanıcı tarafından bi hareket olduktan sonra
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ask permission - izin isteme
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            } else {
                // ask permission - izin isteme
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        // izin verilmişse
        else {
            // resim paylaşmak için galeriye git resmi al
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }
    }


    private void registerLauncher() {
        // StartActivityForResult() ---> sonuç için bir aktivite başlatıyoruz
        // yani galeriye gidip veri almak
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    // her şey ok ise datayı yani resmi çekiyoruz
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        // boş değilse
                        // verinin nerde kayıtlı olduğu uri(adresi) döndürür
                        imageData = intentFromResult.getData();
                        binding.signUpimageView.setImageURI(imageData);

                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    // izin verildi
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                } else {
                    Toast.makeText(SignUpActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
