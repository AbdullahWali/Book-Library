package com.mocklibraryapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mocklibraryapplication.Core.Entry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abdullah Wali on 26/06/2016.
 */
//Adapter class for Book List
public class LibraryListAdapter extends ArrayAdapter<Entry> {


    private final Activity activity;
    private ArrayList<Entry> list;

    public LibraryListAdapter(Activity activity, ArrayList<Entry> library) {
        super(activity, R.layout.library_list_row, library);
        this.activity = activity;
        this.list = library;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null )
            rowView= LayoutInflater.from(activity).inflate(R.layout.library_list_row, parent, false);

        TextView title = (TextView) rowView.findViewById(R.id.titleRow);
        TextView author = (TextView) rowView.findViewById(R.id.authorRow);
        TextView daysLeft = (TextView) rowView.findViewById(R.id.daysLeft);
        ImageView image = (ImageView) rowView.findViewById(R.id.imageRow);
        ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progressRow);

        title.setText(list.get(position).getBook().getTitle());
        author.setText(list.get(position).getBook().getAuthor());
        Picasso.with(getContext()).load(list.get(position).getBook().getImageURL()).into(image);
        String daysLeftString = activity.getString(R.string.daysLeft) + list.get(position).getDaysLeft();
        daysLeft.setText(daysLeftString);
        progressBar.setProgress(list.get(position).getPercentDaysPassed());
        return rowView;
    }
}
