package com.bignerdranch.android.vocabularyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;

public class GameMain extends AppCompatActivity {

    private static final String KEY_SCORE = "key_score";
    private static final String KEY_EDIT_TEXT = "key_edit_text";
    private static final String KEY_KEYS = "key_keys";
    private static final String KEY_CURRENT_ANSWER = "current_answer";

    private int presCounter = 0;
    private int maxPresCounter;
    private int currentAnswer = 0;
    private int numToGameOver = 0;
    private int score = 0;
    private boolean isReloadBrick;

    private ArrayList<String> keys = new ArrayList<String>();
    private ArrayList<String> textAnswers;
    private String textAnswer;
    private TextView mTextQuestion, mTextScreen, mScore;
    private ProgressBar mProgressLevel;
    private EditText editText;
    Animation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        textAnswers = new ArrayList<String>();
        textAnswers.add("MORNING");
        textAnswers.add("BIRD");
        textAnswers.add("AFTERNOON");

        mProgressLevel = (ProgressBar) findViewById(R.id.progressBar_level);
        mProgressLevel.setProgress(0);
        mProgressLevel.setMax(textAnswers.size());

        editText = (EditText) findViewById(R.id.edit_text);
        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString(KEY_EDIT_TEXT));
            currentAnswer = savedInstanceState.getInt(KEY_CURRENT_ANSWER);
            score = savedInstanceState.getInt(KEY_SCORE);
        }

        mScore = (TextView) findViewById(R.id.text_score);
        Log.d("TAG", "score: "+ score);
        mScore.setText(getString(R.string.text_score, score));

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.smallbigforth);
        isReloadBrick = false;

        showGame(savedInstanceState);

    }

    private void showGame(Bundle savedInstanceState){
        textAnswer = textAnswers.get(currentAnswer);
        Log.d("TAG", "showGame: "+ currentAnswer);

        if(savedInstanceState != null){
            //neu khong phai reload lai chu thi nhan lai gia tri
            if(!isReloadBrick){
                maxPresCounter = textAnswers.get(currentAnswer).length();
                keys = savedInstanceState.getStringArrayList(KEY_KEYS);
            }else{
                maxPresCounter = textAnswer.length();
                for(int i = 0; i < textAnswer.length(); i++){
                    keys.add(String.valueOf(textAnswer.charAt(i)));
                }
            }
        }else{
            maxPresCounter = textAnswer.length();
            for(int i = 0; i < textAnswer.length(); i++){
                keys.add(String.valueOf(textAnswer.charAt(i)));
            }
        }
        keys = shuffleArray(keys);

        showWordBrick(savedInstanceState);

    }

    private void addView(LinearLayout viewParent, final String text, Bundle savedInstanceState) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.rightMargin = 10;
        final TextView textView = new TextView( (this));

        textView.setLayoutParams(layoutParams);
        textView.setBackground(this.getResources().getDrawable(R.drawable.bgpink));
        textView.setTextColor(this.getResources().getColor(R.color.colorPurple));
        textView.setGravity(Gravity.CENTER);
        textView.setText(""+text);
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setTextSize(20);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FredokaOneRegular.ttf");

        mTextQuestion = (TextView) findViewById(R.id.text_question);
        mTextScreen = (TextView) findViewById(R.id.text_screen);

        mTextQuestion.setTypeface(typeface);
        mTextScreen.setTypeface(typeface);

        editText.setTypeface(typeface);
        textView.setTypeface(typeface);

        textView.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presCounter < maxPresCounter){
//                    if(presCounter == 0) {
//                        editText.setText("");
//                    }

                    editText.setText(editText.getText().toString() + text);
                    for(int i = 0; i < keys.size(); i++){
                        if (keys.get(i) == text) {
                            keys.remove(i);
                        }
                    }
                    textView.startAnimation(mAnimation);
                    textView.animate().alpha(0).setDuration(300);
                    presCounter++;
                    if(keys.isEmpty()){
                        doValidate(savedInstanceState);
                    }
                }
            }
        }));
        viewParent.addView(textView);
    }

    private void doValidate(Bundle savedInstanceState) {
        presCounter = 0;
        LinearLayout linearLayout1 = findViewById(R.id.layout_parent_1);
        LinearLayout linearLayout2 = findViewById(R.id.layout_parent_2);

        if(editText.getText().toString().equals(textAnswer)){
            Toast.makeText(GameMain.this, "Correct",Toast.LENGTH_SHORT).show();
            editText.setText("");
            currentAnswer++;
            numToGameOver = 0;
            isReloadBrick = true;
            score += 100;
            mScore.setText(getString(R.string.text_score, score));

            Log.d("TAG", "score1: "+ score);
            int currentLevel = mProgressLevel.getProgress();
            if(currentLevel >= mProgressLevel.getMax()){
                currentLevel = 0;
            }
            mProgressLevel.setProgress(currentLevel + 1);
            if(currentAnswer >= textAnswers.size()){
                Intent intent = new Intent(GameMain.this, BossActivity.class);
                startActivity(intent);
            }else{
                showGame(savedInstanceState);
            }
        }else{
            Toast.makeText(GameMain.this, "Incorrect",Toast.LENGTH_SHORT).show();
            editText.setText("");
            numToGameOver++;
            isReloadBrick = true;
            showGame(savedInstanceState);
            if(numToGameOver == 2){
                Toast.makeText(GameMain.this, "Game over",Toast.LENGTH_SHORT).show();
            }
        }
        keys = shuffleArray(keys);
        linearLayout1.removeAllViews();
        linearLayout2.removeAllViews();

        showWordBrick(savedInstanceState);
    }

    private void showWordBrick (Bundle savedInstanceState){
        int i;
        if(keys.size() < 5){
            for (i = 0; i < keys.size(); i++){
                addView(((LinearLayout) findViewById(R.id.layout_parent_1)), keys.get(i), savedInstanceState);
            }
        }else{
            for(i =0; i<5; i++){
                addView(((LinearLayout) findViewById(R.id.layout_parent_1)), keys.get(i), savedInstanceState);
            }
            for(i = 5; i< keys.size(); i++){
                addView(((LinearLayout) findViewById(R.id.layout_parent_2)), keys.get(i), savedInstanceState);
            }
        }
    }

    private ArrayList<String> shuffleArray(ArrayList<String> keys) {
        Random random = new Random();
        for(int i = keys.size() -1; i > 0; i--){
            int index = random.nextInt(i+1);
            String a = keys.get(index);
            keys.set(index, keys.get(i));
            keys.set(i,a);
        }
        return  keys;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_CURRENT_ANSWER, currentAnswer);
        outState.putString(KEY_EDIT_TEXT, editText.getText().toString());
        outState.putStringArrayList(KEY_KEYS, keys);
    }
}