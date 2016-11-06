package com.example.ahmet.inclass10tutorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);



        mDatabase = FirebaseDatabase.getInstance().getReference("messages");
        mDatabase.push().setValue("3");
        mDatabase.push().setValue("4");
        mDatabase.push().setValue("3");
        mDatabase.push().setValue("4");
    }
}
