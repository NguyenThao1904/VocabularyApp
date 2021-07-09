package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity{

    AutoCompleteTextView mAutoCompleteTextView1;
    AutoCompleteTextView mAutoCompleteTextView2;
    private ListView listViewFavWord;
    private ArrayList<Word> textAnswers;
    private DatabaseHelper mDatabaseHelper;
    private WordAdapter dataAdapter;
    private Button mBtnLearn;
    private Button mBtnGame;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mAutoCompleteTextView1 = findViewById(R.id.searchAll);
        mAutoCompleteTextView2 = findViewById(R.id.searchFav);
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
            }
        });

        //search the whole vocabulary
        mAutoCompleteTextView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    mAutoCompleteTextView1.setAdapter(new ArrayAdapter<String>(FavoriteActivity.this,
                            android.R.layout.simple_list_item_1, mDatabaseHelper.getEngWord(s.toString())));

                    mAutoCompleteTextView1.setThreshold(1);//chi can nhap 1 tu la se bat dau loc
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAutoCompleteTextView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String mWord = mList.get(position);
                String mWord = (String) parent.getItemAtPosition(position);
                getDescription(mWord);
            }
        });


        //Search the favorite word
        mAutoCompleteTextView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    mAutoCompleteTextView2.setAdapter(new ArrayAdapter<String>(FavoriteActivity.this,
                            android.R.layout.simple_list_item_1, mDatabaseHelper.getFavEngWord(s.toString())));

                    mAutoCompleteTextView2.setThreshold(1);//chi can nhap 1 tu la se bat dau loc
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAutoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        intent.putExtra("answer", mDatabaseHelper.getDescription(word));
        startActivity(intent);
    }
}
