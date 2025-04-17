package com.example.jewellerycomparison;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText editTextAdminEmail, editTextAdminPassword;
    private Button buttonAdminLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        editTextAdminEmail = findViewById(R.id.editTextAdminEmail);
        editTextAdminPassword = findViewById(R.id.editTextAdminPassword);
        buttonAdminLogin = findViewById(R.id.buttonAdminLogin);

        mAuth = FirebaseAuth.getInstance();

        buttonAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextAdminEmail.getText().toString().trim();
                String password = editTextAdminPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AdminLoginActivity.this, "Please enter admin email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // **Important:** You'll need a way to identify admin users.
                // This could be:
                // 1. Checking for a specific email address.
                // 2. Checking for a specific role in your Firebase Realtime Database or Firestore.
                // 3. Using Firebase Custom Claims (more secure).

                // **For this basic example, we'll check for a specific email.**
                if (email.equals("admin") && password.equals("admin123")) {
                    // Successful admin login, navigate to AdminMainActivity
                    Intent intent = new Intent(AdminLoginActivity.this, AdminAddJewelActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Authenticate with Firebase (you might have specific admin users in your Auth)
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser adminUser = mAuth.getCurrentUser();
                                        if (adminUser != null) {
                                            // **More robust admin check needed here (e.g., database role)**
                                            // For now, just a successful Firebase login is considered admin.
                                            Toast.makeText(AdminLoginActivity.this, "Admin Login Successful (Firebase)", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AdminLoginActivity.this, AdminAddJewelActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        // Handle admin login failure
                                        Toast.makeText(AdminLoginActivity.this, "Admin Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
