package com.bignerdranch.android.vocabularyapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Locale;

public class FloatingWindow extends Service implements View.OnClickListener{

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;
    ImageView imageClose;
    AutoCompleteTextView mAutoCompleteTextView;
    TextView mAns;
    private ImageView mBtnBookMark,mBtnSound;
    private View layoutcollapsed_widget;
    private View layoutexpanded_widget;
    float height, width;
    private DatabaseHelper mDatabaseHelper;
    ArrayList<String> mList;
    String ans;
    TextToSpeech mTextToSpeech;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //Database & array list to show word
        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.createDataBase();
        mList = new ArrayList<>();


        //inflate widget layout
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating, null);

        //setting the layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //initial position _ vi tri cua floating window
        layoutParams.gravity= Gravity.TOP|Gravity.RIGHT;
        layoutParams.x = 0;
        layoutParams.y = 100;

        //layout params for close button - vi tri cua nut close duoi man hinh khi muon tat di
        WindowManager.LayoutParams imageParams =  new WindowManager.LayoutParams(140,
                140,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        imageParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
        imageParams.y = 100;


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_close_float_white);
        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose,imageParams);
        windowManager.addView(mFloatingView,layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        height= windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();


        //getting the collapsed and expanded view from the floating view
        layoutcollapsed_widget = mFloatingView.findViewById(R.id.layoutCollapsed);
        layoutexpanded_widget = mFloatingView.findViewById(R.id.layoutExpanded);

        //adding click listener to close view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);

        //AutoCompleteTextView
        // Show word's description
        mAutoCompleteTextView = mFloatingView.findViewById(R.id.auto_txt);

        //TextView _ show description
        mAns = mFloatingView.findViewById(R.id.txt_ans);
        mAns.setMovementMethod(new ScrollingMovementMethod());
        //Handle sound of word
        mBtnSound = mFloatingView.findViewById(R.id.btn_sound);
        mBtnSound.setVisibility(View.GONE);


        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    mAutoCompleteTextView.setAdapter(new ArrayAdapter<String>(FloatingWindow.this,
                            android.R.layout.simple_list_item_1, mDatabaseHelper.getEngWord(s.toString())));
                }
                mAutoCompleteTextView.setThreshold(1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String word = (String) parent.getItemAtPosition(position);
                ans = mDatabaseHelper.getDescription(word);
                mAns.setText(ans);
                mBtnSound.setVisibility(View.VISIBLE);
                mBtnSound.setOnClickListener(v ->
                        mTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
                            if(status == TextToSpeech.SUCCESS){
                                //Setting language
                                mTextToSpeech.setLanguage(Locale.UK);
                                mTextToSpeech.setSpeechRate(1.0f);
                                mTextToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null);
                            }
                        }));

                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = layoutParams;
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                // The Layout Flag is changed back to FLAG_NOT_FOCUSABLE. and the Layout is updated with new Flag
                windowManager.updateViewLayout(mFloatingView, floatWindowLayoutParamUpdateFlag);

                // INPUT_METHOD_SERVICE with Context is used
                // to retrieve a InputMethodManager for
                // accessing input methods which is the soft keyboard here
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                // The soft keyboard slides back in
                inputMethodManager.hideSoftInputFromWindow(mFloatingView.getApplicationWindowToken(), 0);
            }
        });

        // Floating Window Layout Flag is set to FLAG_NOT_FOCUSABLE,
        // so no input is possible to the EditText. But that's a problem.
        // So, the problem is solved here. The Layout Flag is
        // changed when the EditText is touched.
        mAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutoCompleteTextView.setCursorVisible(true);
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = layoutParams;
                // Layout Flag is changed to FLAG_NOT_TOUCH_MODAL which
                // helps to take inputs inside floating window, but
                // while in EditText the back button won't work and
                // FLAG_LAYOUT_IN_SCREEN flag helps to keep the window
                // always over the keyboard
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

                // WindowManager is updated with the Updated Parameters
                windowManager.updateViewLayout(mFloatingView, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });

        //adding an touch listener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.layout_floating).setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    //Press on logo
                    case MotionEvent.ACTION_DOWN:

                        imageClose.setVisibility(View.VISIBLE);

                        initialX= layoutParams.x;
                        initialY=layoutParams.y;

                        //touch position:
                        initialTouchX= event.getRawX();
                        initialTouchY= event.getRawY();

                        return true;

                        
                    case MotionEvent.ACTION_UP:

                        //when the drag is ended switching the state of the widget
                        layoutcollapsed_widget.setVisibility(View.GONE);
                        layoutexpanded_widget.setVisibility(View.VISIBLE);
                        imageClose.setVisibility(View.GONE);

                        layoutParams.x = initialX +(int)(initialTouchY-event.getRawX());
                        layoutParams.y = initialY + (int)(event.getRawY() - initialTouchY);

                        //remove widget
                        if(layoutParams.y > (height * 0.6)) {
                                stopSelf();
                        }
                        return true;

                        //move logo
                    case MotionEvent.ACTION_MOVE:
                        //calculate X & Y coordinates of view
                        layoutParams.x= initialX + (int)(initialTouchX-event.getRawX());
                        layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //update layout with new coordinates
                        windowManager.updateViewLayout(mFloatingView,layoutParams);

                        if (layoutParams.y > (height * 0.6)){
                            imageClose.setImageResource(R.drawable.ic_close_float_red);
                        }else{
                            imageClose.setImageResource(R.drawable.ic_close_float_white);
                        }
                        return true;
                }
                return false;
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView!=null) {
            windowManager.removeView(mFloatingView);
        }

        if (imageClose!=null){
            windowManager.removeView(imageClose);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonClose) {//switching views
            layoutcollapsed_widget.setVisibility(View.VISIBLE);
            layoutexpanded_widget.setVisibility(View.GONE);
        }
    }
}
