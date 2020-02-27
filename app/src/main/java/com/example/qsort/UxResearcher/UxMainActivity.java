package com.example.qsort.UxResearcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qsort.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UxMainActivity<map> extends AppCompatActivity {

    private static final String TAG = "UxMainActivity";
    private TextView username;
    private TextView bio;
//    private Button btnLogout;
    private Button btnGuide;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private String project_name;
    private String project_image;
    private String project_id;
    ArrayList<Projects> projectList;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    UxRecyclerViewAdapter recyclerViewAdapter;

    Context context;

    static int FILE_REQUEST_CODE = 1;
    Uri filePath;
    Map<String,List<String>> projectMap =new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_main);

        context = UxMainActivity.this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
//        btnLogout = findViewById(R.id.logout);
        btnGuide = findViewById(R.id.btnGuide);
        projectList  = new ArrayList<>();

        DocumentReference documentReference = db.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                username.setText(documentSnapshot.getString("username"));
                bio.setText(documentSnapshot.getString("bio"));
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        Toast.makeText(UxMainActivity.this, "Loading images",
                Toast.LENGTH_LONG).show();

        reloadProjects();

    }

    private void reloadProjects() {
        projectList.clear();
        db.collection("projects")
                .whereEqualTo("Designer", userId).orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                project_image = document.getData().get("Project Name").toString();
                                project_name = document.getData().get("Project Picture").toString();
                                project_id = document.getData().get("Project ID").toString();
                                Projects currentProject = new Projects(project_name, project_image, project_id);
                                projectList.add(currentProject);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        recyclerViewAdapter = new UxRecyclerViewAdapter(context, projectList);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerView.setHasFixedSize(true);
                    }
                });
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),UxLoginActivity.class));
        finish();
        Toast.makeText(UxMainActivity.this, "Logged Out",
                Toast.LENGTH_SHORT).show();
    }

    public void addProject(View view){
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("text/csv");
        startActivityForResult(fileIntent,FILE_REQUEST_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            filePath = data.getData();
            try {
                readTextFromUri(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void readTextFromUri(Uri uri) throws IOException {
        StringBuilder categories = new StringBuilder();
        StringBuilder labels = new StringBuilder();

        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] element = line.split(",");
                if(!element[0].equals("")){
                    categories.append(element[0]+"\n");
                }
                if(!element[1].equals("")){
                    labels.append(element[1]+"\n");
                }
            }

            Intent intent = new Intent(getApplicationContext(),UxProjectSettingsActivity.class);

            intent.putExtra("Categories",categories.toString());
            intent.putExtra("Labels",labels.toString());
            // start the activity
            startActivity(intent);

        }
    }



}
