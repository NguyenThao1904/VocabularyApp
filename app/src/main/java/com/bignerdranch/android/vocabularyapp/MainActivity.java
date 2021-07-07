package com.bignerdranch.android.vocabularyapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private LinearLayout mLayoutFavorite, mLayoutGame, mLayoutLearn, mLayoutFloatingWindow;

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

        if (requestCode==1){
            if (!Settings.canDrawOverlays(MainActivity.this)){
                Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
            }
        }
    }

}