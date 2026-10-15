package com.example.grocerylistapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnAdd, btnDelete, btnUpdate, btnShow;
    private EditText etName, etAmount;

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

        FirestoreDAO fsDAO = new FirestoreDAO();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etAmount.getText().toString().trim().isEmpty()&&!etName.getText().toString().trim().isEmpty()) {
                    fsDAO.createDocument(etName.getText().toString(), Integer.parseInt(etAmount.getText().toString()));
                }else{
                    Toast.makeText(MainActivity.this, "אחד מהשדות לא מלא", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}