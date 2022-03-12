package com.sahilrzayev.instagramclone.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.telephony.BarringInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sahilrzayev.instagramclone.R;
import com.sahilrzayev.instagramclone.adapter.PostAdapter;
import com.sahilrzayev.instagramclone.databinding.ActivityFeedBinding;
import com.sahilrzayev.instagramclone.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    ArrayList<Post> postArrayList;

    private ActivityFeedBinding binding;

    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot(); // görünümü alıyoruz
        setContentView(view);

        postArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getData();

        // postaların listelenmesi
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

        binding.feedActivityBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottombar_menu_home:
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                        System.out.println("Home");
                        return true;

                    case R.id.bottombar_menu_accountupdate:
                        Intent intent1 = new Intent(FeedActivity.this,UpdateProfileActivity.class);
                        startActivity(intent1);
                        Toast.makeText(getApplicationContext(), "Profile Update", Toast.LENGTH_SHORT).show();
                        System.out.println("Profile Update");
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void getData(){
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if (value != null){
                    for (DocumentSnapshot snapshot: value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        // Casting
                        String userEmail = (String) data.get("userName");
                        String comment = (String) data.get("comment");
                        String downloadUrl = (String) data.get("downloadUrl");
                        String profileImage = (String) data.get("profileImage");

                        Post post = new Post(userEmail,comment,downloadUrl,profileImage);
                        postArrayList.add(post);
                    }

                    // recycylerView'a yeni veri geldi haber ver göstersin :)
                    postAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    // bağlama işlemi yapıyoruz
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // seçilince ne olacağını yazıyoruz
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post){
            //Upload Activity
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
            // işlemden var geçebilir yani
            // buraya geri dönebilir diye finish() yapmıyorum
        }else if (item.getItemId() == R.id.signout){
            //Signout
            auth.signOut();

            Intent intentToMain = new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intentToMain);
            // çıkış yapıyor diye buraya geri dönemez o yüzden finish()
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}