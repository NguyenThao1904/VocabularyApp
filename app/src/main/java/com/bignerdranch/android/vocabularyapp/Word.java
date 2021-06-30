package com.bignerdranch.android.vocabularyapp;

public class Word {
    private int id;
    private String word, html, description, pronounce, fav;


    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getHtml() {
        return html;
    }

    public String getDescription() {
        return description;
    }

    public String getPronounce() {
        return pronounce;
    }

    public String isFav() {
        return fav;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }
}
