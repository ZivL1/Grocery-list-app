package com.example.grocerylistapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnAdd, btnDelete, btnUpdate, btnShow;
    private EditText etName, etAmount;

    private ListView groceryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnShow = findViewById(R.id.btnShow);
        etName = findViewById(R.id.etName);
        etAmount = findViewById(R.id.etAmount);
        groceryListView = findViewById(R.id.groceryListView);

        FirestoreDAO fsDAO = new FirestoreDAO();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAmount.getText().toString().trim().isEmpty()&&!etName.getText().toString().trim().isEmpty()) {
                    fsDAO.createDocument(etName.getText().toString(), Integer.parseInt(etAmount.getText().toString()));
                    Toast.makeText(MainActivity.this, "המוצר נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "לפחות אחד מהשדות לא מלא", Toast.LENGTH_SHORT).show();
                }
                etName.setText("");
                etAmount.setText("");
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etName.getText().toString().trim().isEmpty())
                {
                    fsDAO.deleteDocument(etName.getText().toString());
                    Toast.makeText(MainActivity.this, "המוצר נמחק", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "אנא מלא שם", Toast.LENGTH_SHORT).show();
                }
                etName.setText("");
                etAmount.setText("");
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAmount.getText().toString().trim().isEmpty()&&!etName.getText().toString().trim().isEmpty()){
                    fsDAO.updateDocument(etName.getText().toString(), Integer.parseInt(etAmount.getText().toString()));
                    Toast.makeText(MainActivity.this, "כמות המוצר עודכנה", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "לפחות אחד מהשדות לא מלא", Toast.LENGTH_SHORT).show();
                }
                etName.setText("");
                etAmount.setText("");
            }

        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etName.getText().toString().trim().isEmpty()){
                        fsDAO.readDocumentById(etName.getText().toString(), new IFirestoreCallback() {
                            @Override
                            public void onDataReceived(ArrayList<Grocery> groceries) {
                                ArrayList<Grocery> oneGrocery = new ArrayList<>();
                                if (!groceries.isEmpty()) {
                                    oneGrocery.add(groceries.get(0));


                                    ArrayAdapter<Grocery> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, oneGrocery);
                                    groceryListView.setAdapter(adapter);
                                    groceryListView.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(MainActivity.this, "המוצר לא נמצא", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }else{
                    fsDAO.readAllDocuments(new IFirestoreCallback() {
                        @Override
                        public void onDataReceived(ArrayList<Grocery> groceries) {
                            ArrayAdapter<Grocery> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,groceries);
                            groceryListView.setAdapter(adapter);
                            groceryListView.setVisibility(View.VISIBLE);
                        }
                    });
                }
                etName.setText("");
                etAmount.setText("");
            }
        });
    }
}