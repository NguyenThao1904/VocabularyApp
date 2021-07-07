package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class FinishScreenActivity extends AppCompatActivity {
    public static final String KEY_SCORE = "keyScore";
    public static final String EXTRA_SCORE = "extraScore";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private TextView playerscore, congratulation, textViewCheerup;
    private TextView textViewHighscore;
    private Button backHomebtn;
    private int score, highScore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_finish_game);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/LuckiestGuyRegular.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/JomhuriaRegular.ttf");

        playerscore = findViewById(R.id.text_view_score);
        congratulation = findViewById(R.id.text_view_congra);
        textViewCheerup = findViewById(R.id.text_view_cheerup);
        textViewHighscore = findViewById(R.id.text_view_highscore);

        congratulation.setTypeface(tf);
        playerscore.setTypeface(tf);
        textViewCheerup.setTypeface(tf);
        textViewHighscore.setTypeface(tf);

        score = getIntent().getIntExtra(KEY_SCORE, 0);
        playerscore.setText("Your score: " + score);

        loadHighscore();
        if(score > highScore){
            updateHighScore(score);
        }

        backHomebtn = findViewById(R.id.button_back);
        backHomebtn.setTypeface(tf2);
        backHomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartingScreenActivity.class);
                intent.putExtra(EXTRA_SCORE, highScore);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), StartingScreenActivity.class);
        intent.putExtra(EXTRA_SCORE, score);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void loadHighscore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGHSCORE, 0);
        textViewHighscore.setText(getString(R.string.text_high_score, highScore));
    }

    private void updateHighScore(int highScoreNew) {
        highScore = highScoreNew;
        textViewHighscore.setText(getString(R.string.text_high_score, highScore));
        //         save data in prefreferences
        //        luôn nhận được dữ liệu mới nhất được lưu,
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highScore);
        editor.apply();
    }
}
