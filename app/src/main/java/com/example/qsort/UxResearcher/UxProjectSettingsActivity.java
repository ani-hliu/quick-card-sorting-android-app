package com.example.qsort.UxResearcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qsort.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UxProjectSettingsActivity extends AppCompatActivity {

    String categories, labels;
    EditText categoriesTextView,labelsTextView,projectTitleTextView;
    ImageView projectPicture;
    String timestamp;
    Uri pictureUri;

    String uid;
    private FirebaseAuth mAuth;

    static int GALLERY_CODE = 1;
    static int CAMERA_CODE = 2;


    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_project_settings);

        categoriesTextView = findViewById(R.id.categoryTextView);
        labelsTextView = findViewById(R.id.labelTextView);
        projectTitleTextView = findViewById(R.id.projectTitleTextView);
        projectPicture = findViewById(R.id.projectPicture);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        categories = intent.getExtras().getString("Categories");
        labels = intent.getExtras().getString("Labels");

        categoriesTextView.setText(categories);
        labelsTextView.setText(labels);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

    }

    public void submitProject(View view){

        final String categories = categoriesTextView.getText().toString();
        final String labels = labelsTextView.getText().toString();

        String[] categoriesArray = categories.split("\n");
        String[] labelsArray = labels.split("\n");
        final String projectTitle = projectTitleTextView.getText().toString();
        timestamp = String.valueOf(Timestamp.now().getSeconds());

        for (int i=0; i<labelsArray.length;i++){
            Map<String, Integer> label = new HashMap<>();

            for (int j=0; j<categoriesArray.length;j++){
                label.put(categoriesArray[j],0);
            }
            firebaseFirestore.collection(timestamp).document(labelsArray[i])
                    .set(label)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("Project successfully created!");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Error writing document", e);
                        }
                    });
        }

        if(pictureUri == null){
            /**TODO
             * User didn't upload any profile picutre
             */
            storeProject(projectTitle,"");
        }
        else{
            storageReference = FirebaseStorage.getInstance().getReference().child("project pictures");
            final StorageReference imageFilePath = storageReference.child(pictureUri.getLastPathSegment());

            imageFilePath.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            storeProject(projectTitle, uri.toString());
                        }
                    });
                }
            });
        }

    }

    public void addPicture(View view){

        final String[] items = {"Camera","Gallery"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(UxProjectSettingsActivity.this);
        listDialog.setTitle("Choose:");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch(which){
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        listDialog.show();
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_CODE);

    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == GALLERY_CODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pictureUri = data.getData();
            projectPicture.setImageURI(pictureUri);

        }

        if (resultCode == RESULT_OK && requestCode == CAMERA_CODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                pictureUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                //ImgUserPhoto.setImageBitmap(bitmap);
                projectPicture.setImageURI(pictureUri);
            }

        }
    }


    public void storeProject(String projectTitle, String uri){
        Map project = new HashMap<>();
        project.put("Project Name",projectTitle);
        project.put("Project ID",uid+"_"+timestamp);
        project.put("Participants",0);
        project.put("Designer",uid);
        project.put("timestamp",timestamp);
        project.put("Project Picture",uri);
        project.put("Labels",categories);
        project.put("Categories",labels);



        firebaseFirestore.collection("projects").document(uid+"_"+timestamp).set(project)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent intent = new Intent(getApplicationContext(),UxShareActivity.class);

                        intent.putExtra("Project ID",uid+"_"+timestamp);
                        // start the activity
                        startActivity(intent);
                    }
                });
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }





}
