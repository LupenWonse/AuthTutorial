package com.example.ahmet.inclass10tutorial;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {

    private EditText editUsername, editPassword, editPasswordRepeat;
    private Task<AuthResult> authTask;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUsername = (EditText) findViewById(R.id.editSignupUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPasswordRepeat = (EditText) findViewById(R.id.editPasswordRepeat);

        Button signupButton = (Button) findViewById(R.id.buttonCreateUser);
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        signupButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        //DEBUG
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(this);
        firebaseAuth.signOut();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonCreateUser:
                if (validateInputs()){
                    String username = editUsername.getText().toString();
                    String password = editPassword.getText().toString();
                    authTask = firebaseAuth.createUserWithEmailAndPassword(username,password);
                    authTask.addOnCompleteListener(this);
                }
                break;

            case R.id.buttonLogin:
                if (validateLoginInputs()){
                    String username = ((EditText) findViewById(R.id.editLoginEmail)).getText().toString();
                    String password = ((EditText) findViewById(R.id.editLoginPassword)).getText().toString();

                    firebaseAuth.signInWithEmailAndPassword(username,password).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;

        }


    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (editUsername.getText().toString().length() == 0) {
            editUsername.setError("Username cannot be blank");
            isValid = false;
        }

        if (editPassword.getText().toString().length() == 0) {
            editPassword.setError("Password cannot be blank");
            isValid = false;
        } else if (!editPassword.getText().toString().equals(editPasswordRepeat.getText().toString())){
            editPasswordRepeat.setError("Passwords do not match");
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null){
            // Somebody just logged in or signed-up
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user.getDisplayName() == null) {
                // This is a new user
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName("New User")
                        .build();
                user.updateProfile(profileChangeRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                    firebaseAuth.signOut();
                                    firebaseAuth.signInWithEmailAndPassword(editUsername.getText().toString(),editPassword.getText().toString()).addOnCompleteListener(MainActivity.this);
                                }
                            }
                        });
            } else {
                // This is a log-in
                welcomeUser();
            }
        }
    }

    private void welcomeUser(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Toast.makeText(this,"Welcome " + user.getDisplayName(),Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,Database.class);
        startActivity(intent);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        try {
            throw authTask.getException();
        }
        catch (FirebaseAuthWeakPasswordException e) {
            editPassword.setError("Password too weak");
        }
        catch (FirebaseAuthInvalidCredentialsException e){
            editUsername.setError("Please enter a valid e-mail address");
        }
        catch (FirebaseAuthUserCollisionException e){
            editUsername.setError("User with e-mail already exists");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateLoginInputs() {
        boolean isValid = true;

        EditText editLoginEmail = (EditText) findViewById(R.id.editLoginEmail);
        EditText editLoginPassword = (EditText) findViewById(R.id.editLoginPassword);


        if (editLoginEmail.getText().toString().length() == 0) {
            editLoginEmail.setError("Please enter your email");
            isValid = false;
        }

        if (editLoginPassword.getText().toString().length() == 0) {
            editLoginPassword.setError("Please enter your password");
            isValid = false;
        }
        return isValid;
    }
}
