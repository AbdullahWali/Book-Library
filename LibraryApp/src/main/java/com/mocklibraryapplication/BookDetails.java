package com.mocklibraryapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.mocklibraryapplication.Core.Book;
import com.squareup.picasso.Picasso;

public class BookDetails extends AppCompatActivity {

    Book book = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialise Objects from XML
        TextView title = (TextView) findViewById(R.id.titleRow);
        TextView author = (TextView) findViewById(R.id.authorRow);
        TextView pageCount = (TextView) findViewById(R.id.pageCount);
        TextView description = (TextView) findViewById(R.id.description);
        ImageView image = (ImageView) findViewById(R.id.imageRow);
        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
        TextView ratingCount = (TextView) findViewById(R.id.ratingCount);


        //Save Button: Show only if Activity was started to add a new Book
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        if (!getIntent().getExtras().getBoolean("ShowSave")) fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a NumPicker Layout
                final int[] numOfDays = new int[1];
                final LayoutInflater inflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View npView = inflater.inflate(R.layout.number_picker_dialog_layout, null);

                final NumberPicker numPicker = (NumberPicker) npView.findViewById(R.id.numPicker);
                numPicker.setMinValue(1);
                numPicker.setMaxValue(100);
                numPicker.setWrapSelectorWheel(true);
                numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        numOfDays[0] = newVal;
                    }
                });
                numPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
                    @Override
                    public void onScrollStateChange(NumberPicker view, int scrollState) {
                        if (scrollState != NumberPicker.OnScrollListener.SCROLL_STATE_IDLE)
                            numOfDays[0] = numPicker.getValue();
                    }
                });


                //Get Number from Dialog and finish Activity
                AlertDialog dialog = new AlertDialog.Builder(BookDetails.this, AlertDialog.THEME_HOLO_DARK)
                        .setTitle("Number of Days to borrow:")
                        .setView(npView)
                        .setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        //Get num of days from NumPicker and start Activity
                                        Intent data = new Intent();
                                        data.putExtra("Book", book);
                                        data.putExtra("numOfDays", numOfDays[0]);
                                        setResult(CommonStatusCodes.SUCCESS, data);
                                        finish();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                })
                        .create();
                dialog.show();

            }
        });

        // Extract Book Info from Calling Activity
        Intent intent = getIntent();
        book = (Book) intent.getExtras().get("Book");

        Log.e("BookDetails", book.toString());


        //Populate View with Book Info
        if (book != null) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            pageCount.setText(Integer.toString(book.getPageCount()) + " pages");
            description.setText(book.getOverview());
            Picasso.with(this).load(book.getImageURL()).into(image);
            rating.setRating((float) book.getRating());
            ratingCount.setText("(" + Integer.toString(book.getRatingCount()) + ")");
        }

    }
}
