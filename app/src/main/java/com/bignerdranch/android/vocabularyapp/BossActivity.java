package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.bignerdranch.android.vocabularyapp.GameMain.EXTRA_CONGRATULATION;
import static com.bignerdranch.android.vocabularyapp.GameMain.EXTRA_SCORE;

public class BossActivity extends AppCompatActivity {
    private TextView mTextQuestion, mTextTitle, mTextScreen, mButton, mScore, mHighScore;
    private ImageView mImageView;
    private Animation mAnimation_bigboss;
    private Typeface typeface;
    private Intent intent;

    private int score, highScore;

    private static final String SHARED_PREFS = "shared_prefs";
    private static final String KEY_HIGH_SCORE = "key_high_score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnimation_bigboss = AnimationUtils.loadAnimation(this, R.anim.small_to_big);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/FredokaOneRegular.ttf");

        intent = getIntent();
        score = intent.getIntExtra(EXTRA_SCORE,0);
        if(intent.getBooleanExtra(EXTRA_CONGRATULATION, true)){
            setContentView(R.layout.activity_boss);
        }else{
            setContentView(R.layout.activity_game_over);
        }

        mTextQuestion = (TextView) findViewById(R.id.text_question_game);
        mTextScreen = (TextView) findViewById(R.id.text_screen);

        mTextTitle = (TextView) findViewById(R.id.text_back_to_home);
        mTextTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(BossActivity.this, StartGameScreenActivity.class);
                startActivity(intent);
            }
        });

        mButton = (TextView) findViewById(R.id.button_play_again);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(BossActivity.this, GameMain.class);
                startActivity(intent);
            }
        });

        mScore = (TextView) findViewById(R.id.text_score_end);
        mScore.setText(getString(R.string.text_score_end, score));

        mHighScore = (TextView) findViewById(R.id.text_high_score);
        loadHighScore();
        if(score > highScore){
            updateHighScore(score);
        }

        mImageView = (ImageView) findViewById(R.id.image_boss);
        mImageView.startAnimation(mAnimation_bigboss);

        mTextQuestion.setTypeface(typeface);
        mTextScreen.setTypeface(typeface);
        mButton.setTypeface(typeface);
    }
    private void loadHighScore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGH_SCORE, 0);
        mHighScore.setText(getString(R.string.text_high_score, highScore));
    }
    private void updateHighScore(int highscoreNew) {
        highScore = highscoreNew;
        mHighScore.setText(getString(R.string.text_high_score, highScore));
        //         save data in prefreferences
        //        luôn nhận được dữ liệu mới nhất được lưu,
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGH_SCORE, highScore);
        editor.apply();
    }

}
