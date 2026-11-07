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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etMail, etPass;
    private Button btnSignIn,btnRegister;

    private TextView tvAuthStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        etMail = findViewById(R.id.etAuthEmail);
        etPass = findViewById(R.id.etAuthPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        tvAuthStatus = findViewById(R.id.tvAuthStatus);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
        }

    }

    private void registerUser() {
        String email = etMail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if(password.isEmpty()||email.isEmpty()){
            status("אחד או יותר מהשדות ריקים");
            return;
        }
        if(password.length()<6){
            status("אורך הסיסמה חייב להיות לפחות 6 תוים");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user!=null){
                    startActivity(new Intent(AuthActivity.this, NextAuthStepActivity.class));
                    finish();
                }else{
                    status("שגיאה ביצירת החשבון");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                status("שגיאה ביצירת החשבון");
                Log.d("AUTH", e.toString());
            }
        });
    }



    private void startSignIn(){
        String email = etMail.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        if(email.isEmpty()||password.isEmpty()){
            status("אימייל או סיסמה ריקים");
        }else {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("SIGN_IN", "signInWithEmailAndPassword:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);

                    } else {
                        Log.w("SIGN_IN", "signInWithEmailAndPassword:failure", task.getException());
                        status("אימייל או סיסמה שגויים או שגיאת רשת");
                        updateUI(null);
                    }
                }
            });
        }
    }

    private void status(String s) {
        tvAuthStatus.setText(s);
    }

    private void updateUI(FirebaseUser user){
        if(user!=null) {
            Intent intent = new Intent(AuthActivity.this, ViewDatabaseActivity.class);
            startActivity(intent);
            finish();
        }
    }
}


