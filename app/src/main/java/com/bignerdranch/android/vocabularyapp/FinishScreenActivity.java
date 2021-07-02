package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
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
    private TextView playerscore, congratulation, textViewCheerup;
    private Button backHomebtn;
    private int score;


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
        congratulation.setTypeface(tf);
        playerscore.setTypeface(tf);
        textViewCheerup.setTypeface(tf);

        score = getIntent().getIntExtra(KEY_SCORE, 0);
        playerscore.setText("Your score: " + score);

        backHomebtn = findViewById(R.id.button_back);
        backHomebtn.setTypeface(tf2);
        backHomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartingScreenActivity.class);
                intent.putExtra(EXTRA_SCORE, score);
                startActivity(intent);

            }
        });
    }
}
