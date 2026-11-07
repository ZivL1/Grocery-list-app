package com.example.grocerylistapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NextAuthStepActivity extends AppCompatActivity {

    EditText etFirstName,etLastName;
    Button btnAuthSave;
    TextView tvCompleteAuthStatus;
    String USER_COLLECTION_NAME = "users";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_next_auth_step);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnAuthSave = findViewById(R.id.btnAuthSave);
        tvCompleteAuthStatus = findViewById(R.id.tvCompleteAuthStatus);

        btnAuthSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etFirstName.getText().toString().isEmpty()||etLastName.getText().toString().trim().isEmpty()){
                    status("אחד או יותר מהשדות ריקים");
                }else{
                    saveUserToInterface(etFirstName.getText().toString(),etLastName.getText().toString());
                }
            }
        });
    }

    private void saveUserToInterface(String fName, String lName) {
        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fName", fName);
        userMap.put("lName",lName);
        db.collection(USER_COLLECTION_NAME).document(user.getUid()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(NextAuthStepActivity.this, "החשבון נוצר בהצלחה", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NextAuthStepActivity.this, ViewDatabaseActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FAIL_USET-INTERFACE" , e.toString());
            }
        });

    }

    private void status(String s) {
        tvCompleteAuthStatus.setText(s);
    }
}