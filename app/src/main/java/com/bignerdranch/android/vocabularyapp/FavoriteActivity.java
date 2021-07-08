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
        // Hành động nhấn 1 từ vựng để ra định nghĩa từ đó
        listViewFavWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Object clickItemObject = parent.getAdapter().getItem(position);
                //SQLiteCursor cursor = (SQLiteCursor) clickItemObject;
                SQLiteCursor cursor = (SQLiteCursor) mDatabaseHelper.getNewWord();
                String word= cursor.getString(cursor.getColumnIndex("word"));
                getDescription(word);
            }
        });

        ////////Search TOÀN BỘ từ vựng
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
                //Toast.makeText(MainActivity.this, mWord , Toast.LENGTH_LONG).show();
                getDescription(mWord);
            }
        });


        // / ////Search từ vựng YÊU THÍCH////////////
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
    }
    public void getDescription(String word) {
        String ans = mDatabaseHelper.getDescription(word);
        SQLiteCursor cursor = (SQLiteCursor) mDatabaseHelper.getNewWord();
        Intent intent = new Intent(FavoriteActivity.this, DisplayWordActivity.class);//hàm AnswerAct là để hiển thị html từ
        intent.putExtra("word", word);
        intent.putExtra("answer", ans);
        startActivity(intent);
    }
    /*public void getDisplayWord(String word) {
        Word ans = mDatabaseHelper.displayWord(word);
        Intent intent = new Intent(FavoriteActivity.this, DisplayWordActivity.class);//hàm AnswerAct là để hiển thị html từ
        intent.putExtra("word", word);
        intent.putExtra("answer", ans.getHtml());
        startActivity(intent);
    }*/

    //FULL MÀN HÌNH
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
