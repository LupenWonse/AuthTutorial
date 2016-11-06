package com.example.ahmet.inclass10tutorial;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    private EditText editFirstname, editLastname, editUsername, editPassword, editRepeat, editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseDatabase = FirebaseDatabase.getInstance();

        editFirstname = (EditText) findViewById(R.id.editSignupFirstName);
        editLastname = (EditText) findViewById(R.id.editSignupLastName);
        editUsername = (EditText) findViewById(R.id.editSignupUsername);
        editEmail = (EditText) findViewById(R.id.editSignupEmail);
        editPassword = (EditText) findViewById(R.id.editSignupPassword);
        editRepeat = (EditText) findViewById(R.id.editPasswordRepeat);
    }

    public void onClickSignup(View v){
        final DatabaseReference userDatabase = firebaseDatabase.getReference("users");

        final String firstName = ((EditText) findViewById(R.id.editSignupFirstName)).getText().toString();
        final String lastName = ((EditText) findViewById(R.id.editSignupLastName)).getText().toString();
        final String email = ((EditText) findViewById(R.id.editSignupEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.editSignupPassword)).getText().toString();

        // Create user in the authorization
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String codedEmail = email.replace("@","_");
                    codedEmail = codedEmail.replace(".","_");
                    userDatabase.child(codedEmail).setValue(new AppUser(firstName,lastName,"",email,""));
                } else {
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        editPassword.setError("Password too weak");
                    }catch (FirebaseAuthInvalidUserException e) {
                        editUsername.setError("Username not valid");
                    } catch (Exception e) {
                        Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

class AppUser{
    private String firstName, lastName, username, email, id;

    public AppUser(){

    }

    public AppUser(String firstName, String lastName, String username, String email, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
