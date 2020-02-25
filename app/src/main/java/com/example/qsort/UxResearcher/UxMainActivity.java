package com.example.qsort.UxResearcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsort.R;

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

    static int FILE_REQUEST_CODE = 1;
    Uri filePath;
    TextView path;
    Map<String,List<String>> projectMap =new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_main);

        path=findViewById(R.id.pathTextView);
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
//
//            System.out.println(categories);
//            System.out.println(labels);

        }
    }



}
