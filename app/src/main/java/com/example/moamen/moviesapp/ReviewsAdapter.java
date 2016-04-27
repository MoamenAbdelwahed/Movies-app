package com.example.moamen.moviesapp;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Moamen on 4/22/2016.
 */
public class ReviewsAdapter extends ArrayAdapter<Reviews> {
    public ReviewsAdapter(AppCompatActivity context, ArrayList<Reviews> reviews){
        super(context,0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if(listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.reviews_view, parent, false);
        }
        final Reviews review = getItem(position);
        TextView authorText = (TextView) listView.findViewById(R.id.author);
        authorText.setText(review.getAuthor()+":");
        TextView contentText = (TextView) listView.findViewById(R.id.content);
        contentText.setText(review.getContent());

        return listView;
    }
}
