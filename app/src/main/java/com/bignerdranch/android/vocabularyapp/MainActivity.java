package com.bignerdranch.android.vocabularyapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private Button mButtonFavorite, mButtonGame, mButtonLearn, mButtonFloatingWindow;
    private SimpleCursorAdapter dataAdapter;
    private Button mNewWord;
    ListView listViewNewWord;
    private ImageView mPreviewIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mPreviewIv = findViewById(R.id.imageIv);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Jua-Regular.ttf");
        TextView mAppName = findViewById(R.id.text_app_name);
        mAppName.setTypeface(typeface);

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.createDataBase();

        mButtonGame = findViewById(R.id.btn_game_main);
        mButtonGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDatabaseHelper.getAllFavWord().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Phải chọn từ yêu thích trước", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(MainActivity.this, StartGameScreenActivity.class);
                    startActivity(i);
                }
            }
        });

        mButtonLearn = findViewById(R.id.btn_learn_word);
        mButtonLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, StartingScreenActivity.class);
                startActivity(i);
            }
        });

        mButtonFavorite = findViewById(R.id.btn_fav_word);
        mButtonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FavoriteActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        mButtonFloatingWindow = findViewById(R.id.btn_looking);
        getPermission();
        mButtonFloatingWindow.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    getPermission();
                } else {
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
    }

    //Display word in an activity
    public void getDescription(String word) {
        String ans = mDatabaseHelper.getDescription(word);
        Intent intent = new Intent(MainActivity.this, DisplayWordActivity.class);
        intent.putExtra("word", word);
        intent.putExtra("answer", ans);
        startActivity(intent);
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
                //get item's position and retrieve it from db
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
            Intent intent= new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,1);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if (requestCode == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}