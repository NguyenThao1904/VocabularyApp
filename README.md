# 1. Search word
#### a. Get arraylist word
- input: **String word**
- output: **Arraylist<String> word**
- Descrip: search word and drop down the others
```
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
```
#### b. Get data of word that is searched
- input: **String word**
- output: **String**
- Descrip: Get html column to display
```
```
#### c. Add favorite word
- input: **int id**
- output: **null**
- Descrip: Add id of word that is checked favorite to table fav
```
### DatabaseHelper
    // to get list of words saved in fav table
    public ArrayList<String> getFavList(){
        SQLiteDatabase sqd = this.getReadableDatabase();
        ArrayList<String> favList = new ArrayList<>();
        Cursor cursor = sqd.rawQuery("SELECT * FROM fav",null);
        while (cursor.moveToNext()){
            favList.add(cursor.getString(cursor.getColumnIndex("word")));
        }
        return favList;
    }

    // to save word in fav table
    public boolean SaveFavWord(String word,String mean){
        SQLiteDatabase sqd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("word",word);
        cv.put("mean",mean);
        // word && description is column name in fav table
        long res = sqd.insert("fav",null,cv);
        if(res == -1) return false;
        sqd.close();
        return true;
    }

    public  void deleteFav(String word){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM fav WHERE word = '"+word+"'");
    }
### Answer
#### onCreate()
    btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to save save word in db || delete word from databse if already saved
                // calling SaveFavword methods of DatabaseHelper  class
                if (wordSaved) {
                    // delete word from table
                    mDatabaseHelper.deleteFav(word);
                    Toast.makeText(AnswerAct.this, "Word deleted", Toast.LENGTH_SHORT).show();
                    btnSave.setImageResource(R.drawable.ic_action_bookmark);
                    wordSaved = false;
                } else {
                    boolean saved = mDatabaseHelper.SaveFavWord(word, ans);
                    if (saved) {
                        Toast.makeText(AnswerAct.this, "Word saved", Toast.LENGTH_SHORT).show();
                        // to change the fav button img
                        btnSave.setImageResource(R.drawable.ic_action_marked);
                        wordSaved = true;
                    } else
                        Toast.makeText(AnswerAct.this, "Word not saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkFavWord();
        setData();

    public void setData(){
        if(!ans.isEmpty()){
            mWord.setText(word);
            mAns.setText(ans);
        }
    }
#### Ngoài onCreate()
    public boolean checkFavWord() {
        ArrayList<String> lst = mDatabaseHelper.getFavList();
        if (lst.contains(word)) {
            // it mean word is already in favlist
            // change in bookmark btn
            btnSave.setImageResource(R.drawable.ic_action_marked);
            return true;
        } else {
            btnSave.setImageResource(R.drawable.ic_action_bookmark);
            return false;
        }
    }
```
# 2. Word favorite
#### a. Get word favorite
- intput: **null**
- output: **Arraylist<String> word**
- Descript: Get all word from table fav
```
    public ArrayList<String> getAllFavWord(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ArrayList<String> favWordList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT av.word, av.description FROM av, fav WHERE fav.id_av = av.id", null);
        while (cursor.moveToNext()){
            favWordList.add(cursor.getString(cursor.getColumnIndex("word")));
        }
        return favWordList;
    }
```

#### b. Display a word (use 1.b)
- input: **int word**
- output: **a alertDialog show html**
- Descrip: Click an item -> show a alertDialog that has html
```
```
# 3. Learning vocabulary
#### a. Get word from table fav
- input: **int id**
- output: **
```
```
# 4. Game
- input: **int id fav**
- output: **Word word**
- Descrip: Get word for game. Query word and description
```
```
# 5. New word - New day
- input: **int id**
- output: **Word word**
- Descrip: Get 3 word and display word, description
```
```
