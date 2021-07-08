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

    //FULL MÀN HÌNH
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
