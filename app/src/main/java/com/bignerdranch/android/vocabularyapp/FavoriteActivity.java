package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHANGED_STATUS = 10;

    AutoCompleteTextView mAutoTxtSearchFav;
    private ListView listViewFavWord;
    private ArrayList<Word> textAnswers;
    private DatabaseHelper mDatabaseHelper;
    private WordAdapter dataAdapter;
    private Button mBtnLearn;
    private Button mBtnGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mAutoTxtSearchFav = findViewById(R.id.searchFav);
        listViewFavWord  = (ListView) findViewById(R.id.list_fav_word);

        mDatabaseHelper = new DatabaseHelper(this);
        textAnswers = mDatabaseHelper.getAllFavWord();
        dataAdapter = new WordAdapter(FavoriteActivity.this, textAnswers);

        // Assign adapter to ListView
        listViewFavWord.setAdapter(dataAdapter);
        //display a description of word when click on it
        listViewFavWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDescription(textAnswers.get(position).getWord());
                dataAdapter = new WordAdapter(FavoriteActivity.this, textAnswers);

                // Assign adapter to ListView
                listViewFavWord.setAdapter(dataAdapter);
            }
        });

        //Search the favorite word
        mAutoTxtSearchFav.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    mAutoTxtSearchFav.setAdapter(new ArrayAdapter<String>(FavoriteActivity.this,
                            android.R.layout.simple_list_item_1, mDatabaseHelper.getFavEngWord(s.toString())));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAutoTxtSearchFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mWord = (String) parent.getItemAtPosition(position);
                getDescription(mWord);
            }
        });

        //Call learn vocabulary act
        mBtnLearn = findViewById(R.id.btn_learn);
        mBtnLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavoriteActivity.this, StartingScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        //Call game activity
        mBtnGame = findViewById(R.id.btn_game);
        mBtnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavoriteActivity.this, StartGameScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }
    public void getDescription(String word) {
        Intent intent = new Intent(FavoriteActivity.this, DisplayWordActivity.class);
        intent.putExtra("word", word);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        textAnswers = mDatabaseHelper.getAllFavWord();
        dataAdapter = new WordAdapter(FavoriteActivity.this, textAnswers);
        // Assign adapter to ListView
        listViewFavWord.setAdapter(dataAdapter);
    }
}
