package com.bignerdranch.android.vocabularyapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BossActivity extends AppCompatActivity {
    TextView mTextQuestion, mTextTitle, mTextScreen, mButton;
    ImageView mImageView;
    Animation mAnimation_bigboss;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);

        mAnimation_bigboss = AnimationUtils.loadAnimation(this, R.anim.small_to_big);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FredokaOneRegular.ttf");

        mTextQuestion = (TextView) findViewById(R.id.text_question_game);
        mTextScreen = (TextView) findViewById(R.id.text_screen);
        mTextTitle = (TextView) findViewById(R.id.text_back_to_home);
        mButton = (TextView) findViewById(R.id.button_continue);
        mImageView = (ImageView) findViewById(R.id.image_boss);
        mImageView.startAnimation(mAnimation_bigboss);

        mTextQuestion.setTypeface(typeface);
        mTextScreen.setTypeface(typeface);
        mButton.setTypeface(typeface);
    }
}
