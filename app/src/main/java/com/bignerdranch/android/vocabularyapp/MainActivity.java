package com.bignerdranch.android.vocabularyapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.bignerdranch.android.vocabularyapp.database.DatabaseHelper;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private LinearLayout mLayoutFavorite, mLayoutGame, mLayoutLearn, mLayoutFloatingWindow;
    private SimpleCursorAdapter dataAdapter;
    private TextView mNewWord;
    ListView listViewNewWord;
    private ImageButton mButtonVoice;
    private ImageButton mBtnCamera;
    private EditText mEdtText;
    private TextToSpeech mTextToSpeech;
    ImageView mPreviewIv;

    private static final int REQUEST_CODE_SPEECH_INPUT = 2;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Jua-Regular.ttf");
        TextView mAppName = findViewById(R.id.text_app_name);
        mAppName.setTypeface(typeface);

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

        //event search by voice
        mEdtText = (EditText) findViewById(R.id.edit_search);
        mButtonVoice = (ImageButton) findViewById(R.id.button_voice);
        mButtonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }

        });

        //define image view to set image when take a photo
        mPreviewIv = findViewById(R.id.imageIv);
        //event search by taking a photo
        mBtnCamera = (ImageButton) findViewById(R.id.button_camera);
        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageImportDialog();
            }
        });

        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    //Handle voice to text
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
        SQLiteCursor cursor = (SQLiteCursor) mDatabaseHelper.getNewWord();
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
            Intent intent= new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" +getPackageName()));
            startActivityForResult(intent,1);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //got image from gallery now crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //got image from camera now crop it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
                        .start(this);
            }
        }

        switch (requestCode){
            case 1:{
                //allow user to draw over lays
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
                    final String word = result.get(0).toString();
                    //set to text view
                    mEdtText.setText(word);
                    mEdtText.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            // If the event is a key-down event on the "enter" button
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                // Perform action on key press
                                getDescription(word);
                                return true;
                            }
                            return false;
                        }
                    });
                }
                break;
            }
            //get cropped image
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:{
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri(); //get image uri
                    // set image to image view
                    mPreviewIv.setImageURI(resultUri);

                    //get drawable bitmap for text recognition
                    BitmapDrawable bitmapDrawable= (BitmapDrawable) mPreviewIv.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!textRecognizer.isOperational()) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = textRecognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();
                        //get text from sb until there is no text
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock myItem = items.valueAt(i);
                            sb.append(myItem.getValue());
                            sb.append("\n");
                        }
                        //set text to edit text
                        mEdtText.setText(sb.toString());
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //if there any error show it
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Show dialog to select camera or gallery
    private void showImageImportDialog() {
        //items to display in dialog
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //set title
        dialog.setTitle("Select image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera option click
                    if(!checkCameraPermission()){
                        //camera permission not allowed, request it
                        requestCameraPermission();
                    }else{
                        //permission allowed, take picture
                        pickCamera();
                    }
                }

                if (which == 1) {
                    //gallery option click
                    if(!checkStoragePermission()){
                        //storage permission not allowed, request it
                        requestStoragePermission();
                    }else{
                        //permission allowed, take picture
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        //intent to take image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera, it will also be save to get high quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic"); //title of picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text"); //description
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //Check camera permission and return the result
        //In order to get high quality picture we have to save image to external storage first
        //before interesting to image view that's why storage permission will also be required
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }
}