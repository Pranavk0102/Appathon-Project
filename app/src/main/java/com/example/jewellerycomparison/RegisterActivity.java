package com.example.jewellerycomparison;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterEmail, editTextRegisterPassword, editTextRegisterConfirmPassword, editTextRegisterUsername;
    private Button buttonRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextRegisterUsername = findViewById(R.id.editTextRegisterUsername);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        editTextRegisterConfirmPassword = findViewById(R.id.editTextRegisterConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextRegisterUsername.getText().toString().trim();
                String email = editTextRegisterEmail.getText().toString().trim();
                String password = editTextRegisterPassword.getText().toString().trim();
                String confirmPassword = editTextRegisterConfirmPassword.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        writeNewUser(user.getUid(), username, email);
                                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void writeNewUser(String userId, String username, String email) {
        User user = new User(username, email);
        mDatabase.child(userId).setValue(user);
    }

    public static class User {
        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}