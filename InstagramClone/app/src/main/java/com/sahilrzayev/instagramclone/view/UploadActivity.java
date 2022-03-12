package com.sahilrzayev.instagramclone.view;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sahilrzayev.instagramclone.R;
import com.sahilrzayev.instagramclone.databinding.ActivityUploadBinding;
import com.sahilrzayev.instagramclone.model.Post;
import com.sahilrzayev.instagramclone.model.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UploadActivity extends  AppCompatActivity {
    String username, profileImage;
    //ArrayList<Profile> profileList;

    private BottomNavigationView mBottomBar;
    Uri imageData;

    // galeriye girmek için başlatıp veriyi geli aldığımız launcher
    ActivityResultLauncher<Intent> activityResultLauncher;

    // izin isteyeceğimiz launcher
    ActivityResultLauncher<String> permissionLauncher;

    private ActivityUploadBinding binding;

    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        mBottomBar = binding.uploadActivityBottomNavigationView;

        registerLauncher();
        //profileList = new ArrayList<>();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        mBottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.bottombar_menu_home:
                        Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();

                        return true;

                    case R.id.bottombar_menu_accountupdate:
                        Intent intent1 = new Intent(UploadActivity.this,UpdateProfileActivity.class);
                        startActivity(intent1);
                        Toast.makeText(getApplicationContext(), "Profile Update", Toast.LENGTH_SHORT).show();

                        return true;

                    default:
                        return false;
                }
            }
        });

    }

    public void uploadButtonClick(View view){


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Profiles").document(currentUser.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println("byeeeeeeeee " + value.getData().get("userName"));
                username = value.getData().get("userName").toString();
                profileImage = value.getData().get("profileImage").toString();
                if (imageData != null){
                    System.out.println("------1---------");
                    // uuid - universal unique id
                    // her seferinde daha önceden olmayan bir isim oluşturuyor
                    // ve bu isimle kaydetmememize olanak tanıyor
                    UUID uuid = UUID.randomUUID();
                    String imageName = "images/" + uuid + ".jpg";

                    // klasör oluşturup resimleri oraya eklemek
                    storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // storage'den url çekmek
                            System.out.println("------2---------");

                            StorageReference newReference = firebaseStorage.getReference(imageName);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    System.out.println("------3---------");

                                    String downloadUrl = uri.toString();
                                    String comment = binding.commentText.getText().toString();

                                    HashMap<String, Object> postData = new HashMap<>();
                                    postData.put("userName",username);
                                    postData.put("profileImage",profileImage);
                                    postData.put("downloadUrl",downloadUrl);
                                    postData.put("comment",comment);
                                    postData.put("date", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            System.out.println("------4 finish---------");

                                            Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // herşeyi kapatıyor finish() demeye gerek yok yani
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }else{
                    Toast.makeText(UploadActivity.this, "Please select photo", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void selectImage(View view){
        // eğer resim paylaşmak için izin yoksa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                // eğer bu doğruysa
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                // izin göstermemiz gerekiyor
                // LENGTH_INDEFINITE - belirsiz bi süre yani kullanıcı tarafından bi hareket olduktan sonra
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ask permission - izin isteme
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }else {
                // ask permission - izin isteme
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        // izin verilmişse
        else{
            // resim paylaşmak için galeriye git resmi al
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }
    }


    private void registerLauncher(){
        // StartActivityForResult() ---> sonuç için bir aktivite başlatıyoruz
        // yani galeriye gidip veri almak
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    // her şey ok ise datayı yani resmi çekiyoruz
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        // boş değilse
                        // verinin nerde kayıtlı olduğu uri(adresi) döndürür
                        imageData = intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);

                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    // izin verildi
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }else {
                    Toast.makeText(UploadActivity.this, "Permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}