package com.example.grocerylistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ViewDatabaseActivity extends AppCompatActivity {

    Button btnEditDB,btnSearch;
    ListView lv;
    EditText etSearchName;

    FirestoreDAO fsDAO = new FirestoreDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_view_database);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_db), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnEditDB = findViewById(R.id.btnEditDB);
        btnSearch = findViewById(R.id.btnSearch);
        etSearchName = findViewById(R.id.etSearchName);
        lv = findViewById(R.id.listView);


        btnEditDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDatabaseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        fsDAO.readAllDocuments(new IFirestoreCallback() {
            @Override
            public void onDataReceived(ArrayList<Grocery> groceries) {
                GroceryAdapter adapter = new GroceryAdapter(ViewDatabaseActivity.this,groceries);
                lv.setAdapter(adapter);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etSearchName.getText().toString().trim().isEmpty()) {
                    fsDAO.readDocumentById(etSearchName.getText().toString(), new IFirestoreCallback() {
                        @Override
                        public void onDataReceived(ArrayList<Grocery> groceries) {
                            GroceryAdapter adapter = new GroceryAdapter(ViewDatabaseActivity.this,groceries);
                            lv.setAdapter(adapter);
                        }
                    });
                }else{
                    Toast.makeText(ViewDatabaseActivity.this, "אנא הכנס שם לפני החיפוש", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}