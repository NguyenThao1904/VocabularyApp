package com.bignerdranch.android.vocabularyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Parsania Hardik on 26-Apr-17.
 */
public class WordAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Word> userModelArrayList;

    public WordAdapter(Context context, ArrayList<Word> userModelArrayList) {

        this.context = context;
        this.userModelArrayList = userModelArrayList;
    }


    @Override
    public int getCount() {
        return userModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return userModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_listview, null, true);

            holder.wordeng = (TextView) convertView.findViewById(R.id.text_view_eng_word);
            holder.descri = (TextView) convertView.findViewById(R.id.text_view_description);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
             holder = (ViewHolder)convertView.getTag();
        }

        holder.wordeng.setText(""+userModelArrayList.get(position).getWord());
        holder.descri.setText("Description: "+userModelArrayList.get(position).getDescription());

        return convertView;
    }

    private class ViewHolder {

        protected TextView wordeng, descri;
    }

}
