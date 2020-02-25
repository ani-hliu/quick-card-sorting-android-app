package com.example.qsort.Participants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.qsort.R;
import com.example.qsort.UxResearcher.UxProjectSettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class PartiWelcomeActivity extends AppCompatActivity {

    EditText enterCodeEditText;
    Button projectButton;

    String projectID;
    String categories, labels;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parti_welcome);

        enterCodeEditText = findViewById(R.id.enterCodeEditText);
        projectButton = findViewById(R.id.enterProjectButton);

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void enterProject(View view){

        projectID = enterCodeEditText.getText().toString();
        System.out.println(projectID);

        firebaseFirestore.collection("projects").document(projectID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        categories = documentSnapshot.getData().get("Categories").toString();
                        labels = documentSnapshot.getData().get("Labels").toString();

                        System.out.println(categories);

                        Intent intent = new Intent(getApplicationContext(), PartiMainActivity.class);

                        intent.putExtra("Categories",categories);
                        intent.putExtra("Labels",labels);

                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });

    }
}
