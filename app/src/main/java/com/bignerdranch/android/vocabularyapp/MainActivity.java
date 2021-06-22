package com.bignerdranch.android.vocabularyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.createDataBase();
    }
}