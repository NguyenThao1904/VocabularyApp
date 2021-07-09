package com.bignerdranch.android.vocabularyapp;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

public class DisplayWordActivity extends AppCompatActivity {
    TextView mWord, mAns;
    ImageView btnSave;
    boolean wordSaved;
    private DatabaseHelper mDatabaseHelper;
    String word, ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_word);

        mWord = (TextView) findViewById(R.id.txt_word);
        mAns = (TextView) findViewById(R.id.txt_ans);
        btnSave = (ImageView) findViewById(R.id.btnsave);

        mAns.setMovementMethod(new ScrollingMovementMethod());

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.createDataBase();

        word = getIntent().getStringExtra("word");
        ans = getIntent().getStringExtra("answer");
        setData();
    }

    public void setData(){
        if(!ans.isEmpty()){
            mWord.setText(word);
            mAns.setText(ans);
        }
    }

    public void btnback(View view) {
        finish();
    }
}
