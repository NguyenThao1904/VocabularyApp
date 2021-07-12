package com.bignerdranch.android.vocabularyapp;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

import java.util.Locale;

public class DisplayWordActivity extends AppCompatActivity {
    private TextView mTxtWord, mTxtAns;
    private ImageView mBtnBookMark, mBtnSound;
    private DatabaseHelper mDatabaseHelper;
    private Word mWord;
    private WordAdapter dataAdapter;

    TextToSpeech mTextToSpeech;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_word);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mDatabaseHelper = new DatabaseHelper(this);

        mTxtWord = (TextView) findViewById(R.id.txt_word);
        mTxtAns = (TextView) findViewById(R.id.txt_ans);

        mTxtAns.setMovementMethod(new ScrollingMovementMethod());

        //get word, html, fav by word
        mWord = mDatabaseHelper.displayWord(getIntent().getStringExtra("word"));
        setData();

        //Handle whether favorite column
        mBtnBookMark = (ImageView) findViewById(R.id.btn_book_mark);
        setColorBtnBookMark();
        mBtnBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.updateFav(mWord.isFav(), mWord.getId());
                if(mWord.isFav()){
                    mWord.setFav(false);
                }else {
                    mWord.setFav(true);
                }
                setColorBtnBookMark();
            }
        });

        //Handle sound of word
        mBtnSound = findViewById(R.id.btn_sound);
        mBtnSound.setOnClickListener(v ->
                mTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
                    if(status == TextToSpeech.SUCCESS){
                        //Setting language
                        mTextToSpeech.setLanguage(Locale.UK);
                        mTextToSpeech.setSpeechRate(1.0f);
                        mTextToSpeech.speak(mWord.getWord(), TextToSpeech.QUEUE_ADD, null);
                    }
                }));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setColorBtnBookMark() {
        //check fav to change color for book mark
        if(mWord.isFav()) {
            mBtnBookMark.setColorFilter(getColor(R.color.colorIsFav));
        }else {
            mBtnBookMark.setColorFilter(getColor(R.color.colorPrimary));
        }
    }

    public void setData(){
        if(!mWord.getHtml().isEmpty()){
            mTxtWord.setText(mWord.getWord());
            mTxtAns.setText(Html.fromHtml(mWord.getHtml()).toString());
        }
    }


    public void onClickBtnBack(View view) {
        finish();
    }
}
