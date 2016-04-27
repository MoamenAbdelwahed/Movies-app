package com.example.moamen.moviesapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Moamen on 4/21/2016.
 */
public class TrailersAdapter extends ArrayAdapter<Trailers> {
    public TrailersAdapter(AppCompatActivity context, ArrayList<Trailers> trailers){
        super(context,0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if(listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.trailers_view, parent, false);
        }
        final Trailers trailer = getItem(position);
        Button button = (Button) listView.findViewById(R.id.trailer_but);
        button.setText(trailer.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.youtube.com/watch?v="+trailer.getKey());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });

        return listView;
    }
}
