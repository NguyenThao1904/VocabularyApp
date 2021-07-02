package com.bignerdranch.android.vocabularyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private TextView mTextView, mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.createDataBase();

        mTextView = (TextView) findViewById(R.id.to_game);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDatabaseHelper.getAllFavWord().isEmpty()){
                    Toast.makeText(MainActivity.this,"Phải chọn từ yêu thích trước", Toast.LENGTH_LONG).show();
                }else{
                    Intent i = new Intent(MainActivity.this, StartGameScreenActivity.class);
                    startActivity(i);
                }
            }
        });

        mTextView2 = (TextView) findViewById(R.id.to_learning_word);
        mTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, StartingScreenActivity.class);
                    startActivity(i);
            }
        });
    }
}