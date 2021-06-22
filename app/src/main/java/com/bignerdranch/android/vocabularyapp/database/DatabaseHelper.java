package com.bignerdranch.android.vocabularyapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION =1;
    private static final String DB_NAME ="dict.db";
    private static final String AV_TABLE = "av";
    private static final String WORD = "word";
    private static String DB_PATH = "";
    private final Context mContext;
    private SQLiteDatabase mDataBase;

    public DatabaseHelper(Context context){
        super(context,DB_NAME, null, VERSION );
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void createDataBase(){
        //If the database does not exist, copy it from the assets.

        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    //Get English words that like the input word
    public ArrayList<String> getEngWord(String query){
        ArrayList<String> engList= new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        //truy vấn ra những từ gần giống theo từ mình search
        Cursor cursor = sqLiteDatabase.query(
                AV_TABLE,
                new String[]{WORD},
                WORD + " LIKE ?",
                new String[]{query + "%"},
                null, null,
                WORD
        );
        int index = cursor.getColumnIndex(WORD);
        while (cursor.moveToNext()){
            engList.add(cursor.getString(index));
        }
        sqLiteDatabase.close();
        cursor.close();
        return engList;
    }

    //Display word by html column
    public String displayWord(String word) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Log.d(TAG, "getAns: " + word);
        String ans = null;
        String sql = "SELECT html FROM " + AV_TABLE + " WHERE word = '" + word + "'";
        Log.d(TAG, "getAns: " + sql);
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            ans = cursor.getString(cursor.getColumnIndex("html"));
        }
        return ans;
    }

    //Get all the whole favorite word
    public ArrayList<String> getAllFavWord(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ArrayList<String> favWordList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT av.word as word, av.description as description FROM av, fav WHERE fav.id_av = av.id", null);
        while (cursor.moveToNext()){
            Log.d(TAG, "getAllFavWord: "+ cursor.getString(cursor.getColumnIndex("word")));
            Log.d(TAG, "getAllFavWord1: ");
            favWordList.add(cursor.getString(cursor.getColumnIndex("word")));
        }
        return favWordList;
    }
}
