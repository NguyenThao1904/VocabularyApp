package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class StartingScreenActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXTRA_SCORE = "extraScore";
    public static final String KEY_HIGHSCORE = "keyHighscore";
    public static final String POSITION = "position";
    private TextView textViewHighscore, textviewheader;
    private Spinner spinnerLevel;
    private int highscore;
    private int positionOfSelectedDataFromSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_starting_screen);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/LuckiestGuyRegular.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(),"fonts/JomhuriaRegular.ttf");
        textviewheader = findViewById(R.id.text_view_awesome);
        textviewheader.setTypeface(tf);
        textViewHighscore = findViewById(R.id.text_view_highscore);
        spinnerLevel = findViewById(R.id.spiner_level);

        String[] difficultyLevels = {"Easy","Medium","Hard"};
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this, R.layout.spinner_modify, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapterDifficulty);
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionOfSelectedDataFromSpinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        int score = getIntent().getIntExtra(EXTRA_SCORE,0);
        if (score > highscore){
            updateHighscore(score);
        }
        loadHighscore();
        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);
        buttonStartQuiz.setTypeface(tf2);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(StartingScreenActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    private void startQuiz() {

        String level=  spinnerLevel.getSelectedItem().toString();
        Intent intent = new Intent(StartingScreenActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY, level);
        intent.putExtra(POSITION, positionOfSelectedDataFromSpinner);
        startActivity(intent);
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_QUIZ) {
//            if (resultCode == RESULT_OK) {
//                int score = data.getIntExtra(FinishScreenActivity.KEY_SCORE, 0);
//                if (score > highscore) {
//                    updateHighscore(score);
//                }
//            }
//        }
//    }
    private void loadHighscore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highscore = prefs.getInt(KEY_HIGHSCORE, 0);
        textViewHighscore.setText(getString(R.string.text_high_score, highscore));
    }
    private void updateHighscore(int highscoreNew) {
        highscore = highscoreNew;
        textViewHighscore.setText(getString(R.string.text_high_score, highscore));
        //         save data in prefreferences
        //        luôn nhận được dữ liệu mới nhất được lưu,
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.apply();
    }
}