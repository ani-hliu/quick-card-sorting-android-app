package com.example.qsort.UxResearcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.example.qsort.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private RecyclerView labelButtonRecyclerView;
    private TextView labelLabel, categoryRank;



    RecyclerView.LayoutManager layoutManager;
    UxReportButtonAdapter uxReportButtonAdapter;

    private String project_id = "";
    String noParticipants;
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
        labelButtonRecyclerView = findViewById(R.id.labelButtonRecyclerView);

        labelLabel = findViewById(R.id.labelLabelReport);
        categoryRank = findViewById(R.id.categoriesRank);
        displayProjectsInfo();
        displayProjectLabelButton();



    }

    public void backToMain(View view){
        startActivity(new Intent(getApplicationContext(),UxMainActivity.class));
        finish();
    }

    public void displayProjectsInfo(){

        DocumentReference documentReference = db.collection("projects").document(project_id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                noParticipants = documentSnapshot.getData().get("Participants").toString();
                projectName.setText(documentSnapshot.getData().get("Project Name").toString());
                projectPartiNum.setText(documentSnapshot.getData().get("Participants").toString());

                timestamp = documentSnapshot.getData().get("timestamp").toString();

                Long timestampLong = Long.parseLong(String.valueOf(timestamp));
                if (timestampLong < 10000000000L) {
                    timestampLong = timestampLong * 1000;
                }

                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(Long.parseLong(String.valueOf(timestampLong)));
                String sd = sf.format(new Date(Long.parseLong(String.valueOf(timestampLong))));
                projectTime.setText(sd);



                Glide.with(UxReportActivity.this).load(documentSnapshot.getString("Project Picture")).into(projectView);
            }
        });

    }

    public void displayProjectLabelButton(){

        db.collection("projects").document(project_id)
                .collection("labels")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    ArrayList<String> list = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String labelButtonMap = document.getId();
                        list.add(labelButtonMap);
                    }

                    System.out.println(list);

                    labelButtonRecyclerView = findViewById(R.id.labelButtonRecyclerView);
                    UxReportButtonAdapter myAdapter = new UxReportButtonAdapter(UxReportActivity.this,list,project_id);
                    layoutManager = new GridLayoutManager(UxReportActivity.this, 1);
                    labelButtonRecyclerView.setLayoutManager(layoutManager);
                    labelButtonRecyclerView.setAdapter(myAdapter);
                }

            }
        });


    }

    public void displayRank(View view){
        Button labelButton = (Button)view;
        final String labelButtonText = labelButton.getText().toString();

        db.collection("projects").document(project_id)
                .collection("labels").document(labelButtonText)
                .collection("categories")
                .orderBy("value",Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        StringBuilder categoriesRank = new StringBuilder();
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String value = document.getData().get("value").toString();
                                categoriesRank.append(document.getId()+": "+value+"/"+noParticipants+"\n");
                            }
                            labelLabel.setText(labelButtonText);
                            categoryRank.setText(categoriesRank);

                        }
                    }
                });
    }



}
