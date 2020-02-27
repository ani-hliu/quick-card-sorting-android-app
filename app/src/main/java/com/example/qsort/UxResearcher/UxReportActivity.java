package com.example.qsort.UxResearcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.text.SimpleDateFormat;

import com.bumptech.glide.Glide;
import com.example.qsort.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UxReportActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView projectView;
    private TextView projectName;
    private TextView projectTime;
    private TextView projectPartiNum;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private String timestamp;

    private String project_id = "";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_report);

        if(getIntent().hasExtra("project_id")){
            project_id = getIntent().getStringExtra("project_id");
        }

        context = UxReportActivity.this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        projectView = findViewById(R.id.reportProjectImage);
        projectName = findViewById(R.id.reportProjectName);
        projectTime = findViewById(R.id.reportProjectTime);
        projectPartiNum = findViewById(R.id.reportPartiNum);

        DocumentReference documentReference = db.collection("projects").document(project_id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                projectName.setText(documentSnapshot.getData().get("Project Name").toString());
                projectPartiNum.setText(documentSnapshot.getData().get("Participants").toString());
                timestamp = documentSnapshot.getData().get("timestamp").toString();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(Long.parseLong(timestamp));
                projectTime.setText(sf.format(date));
                Glide.with(UxReportActivity.this).load(documentSnapshot.getString("Project Picture")).into(projectView);
            }
        });

    }

    public void backToMain(View view){
        startActivity(new Intent(getApplicationContext(),UxMainActivity.class));
        finish();
    }
}
