package com.bignerdranch.android.vocabularyapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private LinearLayout mLayoutFavorite, mLayoutGame, mLayoutLearn, mLayoutFloatingWindow;
    private Button btnNewWord;
    private SimpleCursorAdapter dataAdapter;
    private TextView mNewWord;
    ListView listViewNewWord;
    private static final int REQUEST_CODE_SPEECH_INPUT = 2;

    private ImageButton mButtonVoice;
    private EditText mEdtText;
    private TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        // remove title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.createDataBase();

        mLayoutGame = findViewById(R.id.layoutGame);
        mLayoutGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDatabaseHelper.getAllFavWord().isEmpty()){
                    Toast.makeText(MainActivity.this,"Phải chọn từ yêu thích trước", Toast.LENGTH_LONG).show();
                }else{
                    Intent i = new Intent(MainActivity.this, StartGameScreenActivity.class);
                    startActivity(i);
                }
            }
        });

        mLayoutLearn = findViewById(R.id.layoutLearn);
        mLayoutLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, StartingScreenActivity.class);
                    startActivity(i);
            }
        });

        mLayoutFavorite = findViewById(R.id.layoutFavorite);
        mLayoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FavoriteActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        mLayoutFloatingWindow = findViewById(R.id.layoutLooking);
        getPermission();
        mLayoutFloatingWindow.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (!Settings.canDrawOverlays(MainActivity.this)){
                    getPermission();
                }else {
                    Intent intent = new Intent(MainActivity.this, FloatingWindow.class);
                    startService(intent);
                    finish();
                }
            }
        });

        mNewWord = findViewById(R.id.new_word_refresh);
        mNewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayListView();
            }
        });
        displayListView();

        mEdtText = (EditText) findViewById(R.id.edit_search);
        mButtonVoice = (ImageButton) findViewById(R.id.button_voice);
        mButtonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }

        });
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void getDescription(String word) {
        String ans = mDatabaseHelper.getDescription(word);
//        Intent intent = new Intent(MainActivity.this, AnswerAct.class);
//        intent.putExtra("word", word);
//        intent.putExtra("answer", ans);
//        startActivity(intent);
    }

    private void displayListView() {
        Cursor cursor = mDatabaseHelper.getNewWord();

        // The desired columns to be bound
        String[] columns = new String[] {
                DatabaseHelper.WORD,
                DatabaseHelper.DES
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.text_view_eng_word,
                R.id.text_view_description,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.activity_listview,
                cursor,
                columns,
                to,
                0);

        listViewNewWord = (ListView) findViewById(R.id.list_new_word);
        // Assign adapter to ListView
        listViewNewWord.setAdapter(dataAdapter);

        listViewNewWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object clickItemObject = parent.getAdapter().getItem(position);
                SQLiteCursor c = (SQLiteCursor)clickItemObject;
                String word= c.getString(c.getColumnIndex(DatabaseHelper.WORD));
                getDescription(word);
            }
        });
    }


    //Floating window
    public void getPermission(){
        //check for alert windows permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            Intent intent= new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" +getPackageName()));
            startActivityForResult(intent,1);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:{
                if (requestCode==1){
                    if (!Settings.canDrawOverlays(MainActivity.this)){
                        Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case REQUEST_CODE_SPEECH_INPUT:{
                if(resultCode == RESULT_OK && data != null){
                    //get text data from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //set to text view
                    Log.d("", "onActivityResult: " + result.get(0));
                    mEdtText.setText(result.get(0).toString());
                }
                break;

            }
        }
    }
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