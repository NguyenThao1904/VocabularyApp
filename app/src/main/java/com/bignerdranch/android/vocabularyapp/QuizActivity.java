package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "extraScore";
    public static final long COUNTDOWN_IN_MILIS = 30000;

    private static final String KEY_SCORE= "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED= "keyAnswered";
    private static final String KEY_QUESTION_LIST= "keyQuestionList";

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    //private TextView textViewLevel;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;
    private int score;
    private boolean answered;

    private long backPressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/LuckiestGuyRegular.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(),"fonts/Baloo.ttf");
        Typeface tf3 = Typeface.createFromAsset(getAssets(),"fonts/JomhuriaRegular.ttf");

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        //textViewLevel = findViewById(R.id.text_view_level);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textViewQuestionCount.setTypeface(tf);
        textViewQuestion.setTypeface(tf2);
        rb1.setTypeface(tf2);
        rb2.setTypeface(tf2);
        rb3.setTypeface(tf2);
        textViewScore.setTypeface(tf2);
        buttonConfirmNext.setTypeface(tf3);
        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.createDataBase();

        Intent intent= getIntent();
        String level= intent.getStringExtra(StartingScreenActivity.EXTRA_DIFFICULTY);
        int positionToShowToSpinner = intent.getIntExtra(StartingScreenActivity.POSITION,0);
        //textViewLevel.setText("Level: "+level);

        if (savedInstanceState == null) {
            if (positionToShowToSpinner == 1){
                questionCountTotal = 30;
            }else if (positionToShowToSpinner == 2){
                questionCountTotal = 50;
            }else{
                questionCountTotal = 10;
            }
            questionList = dbHelper.getAllQuestion(questionCountTotal);
            //questionCountTotal = questionList.size();
            Collections.shuffle(questionList);
            showNextQuestion();
        }else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCounter= savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion=questionList.get(questionCounter -1 );
            score= savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis=savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered= savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered){
                startCountDown();
            }else{
                updateCountDownText();
                showSolution();
            }
        }
        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });
    }
    private void showNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();
        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);
            textViewQuestion.setText(currentQuestion.getCauhoi());
            rb1.setText(currentQuestion.getCaseA());
            rb2.setText(currentQuestion.getCaseB());
            rb3.setText(currentQuestion.getCaseC());
            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + " of " + questionCountTotal);
            answered = false;
            buttonConfirmNext.setText("Confirm");

            timeLeftInMillis = COUNTDOWN_IN_MILIS;
            startCountDown();
        } else {
            Intent intent = new Intent(this, FinishScreenActivity.class);
            intent.putExtra(KEY_SCORE,score);
            startActivity(intent);
        }
    }

    private  void startCountDown(){
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText(){
        int minute = (int) (timeLeftInMillis / 1000) / 60;
        int second = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted= String.format(Locale.getDefault(), "%02d:%02d",minute,second);

        textViewCountDown.setText(timeFormatted);

        if (timeLeftInMillis <10000){
            textViewCountDown.setTextColor(Color.RED);
        }else {
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    private void checkAnswer() {
        answered = true;

        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;
        if (answerNr == currentQuestion.getTrueCase()) {
            score++;
            textViewScore.setText("Score: " + score);
        }
        showSolution();
    }
    private void showSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        switch (currentQuestion.getTrueCase()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is correct");
                break;
        }
        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("Next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }
    private void finishQuiz() {
        Intent resultIntent= new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();

    }

    //nhấn 2 lần để thoát về trang startquiz và bỏ ngang trò chơi

    @Override
    public void onBackPressed() {
        if(backPressedTime +2000 > System.currentTimeMillis()){
            finishQuiz();
        }else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer !=null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT,questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED,answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}