package com.mocklibraryapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mocklibraryapplication.Core.Book;
import com.mocklibraryapplication.Core.Entry;
import com.mocklibraryapplication.Core.Library;
import com.mocklibraryapplication.barcodereader.BarcodeCaptureActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int RC_BOOK_DETAILS = 1;
    private static final int NOTIFICATION_ID = 5411;




    Library myLibrary; // = new Library() ;
    FloatingActionButton readBarcode;
    static ListView bookList;
    LibraryListAdapter adapter;
    final static String TAG = "MainActivity";
    String url;
    View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This Allows to create Network Operations on  Main Thread. WARNING: might cause UI Freezes if not used correctly
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Objects from XML
        readBarcode = (FloatingActionButton) findViewById(R.id.button);
        bookList = (ListView) findViewById( R.id.bookList);
        mainView = (View) findViewById(R.id.parentViewMain);


        //If Library is available in SharedPref, Import it, otherwise Create a new Library
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson =  Converters.registerLocalDate(new GsonBuilder()).create();   //Convert To JSON to store in SharedPref, using a library to parse jodatime (joda-time-serializer)
        String json = mPrefs.getString("Library", "");
        Library tempLibrary = gson.fromJson(json, Library.class);
        if ( tempLibrary == null ) { myLibrary = new Library(); Log.d("GSONDebug", "Created New Library"); }
        else { myLibrary = tempLibrary;  Log.d("GSONDebug", "Imported Library"); }


        //Set Adapter for bookList
        adapter = new LibraryListAdapter(this,myLibrary.getLibrary());
        bookList.setAdapter(adapter);

        //Set Notifications AlarmManager
        AlarmManager AM = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent (this, NotificationAlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.setTimeInMillis(cal.getTimeInMillis() + 1000); //5 Seconds from now
        AM.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 1000 * 60 * 60 , pending); //12 Hour Interval



        //Click Listener for Listview
        //TODO: Change ItemclickListener to open another Activity
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book curBook = ((Entry) (bookList.getItemAtPosition(position))).getBook();

                Notification bookNotification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("Return Book Soon")
                        .setContentText(curBook.getTitle() + " is due in " + ((Entry) (bookList.getItemAtPosition(position))).getDaysLeft() + " days")
                        .setSmallIcon(R.drawable.ic_book_white_18dp)
                        .build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, bookNotification);

            }
        });

        // launch barcode activity.
        url = " "; //Give URL Default Value
        readBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true); //Set auto-focus on
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false); //Set flash off
                startActivityForResult(intent, RC_BARCODE_CAPTURE);

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

                    //Create Book and Start Book Details
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + barcode.displayValue
                            + "&key" + R.string.Google_API;

                    /// Make a JSON Request And Post Results to TEXTBOX

                    JsonObjectRequest jsonrequest = new JsonObjectRequest
                            (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {


                                        // Create Book With extracted Data
                                        response = response.getJSONArray("items").getJSONObject(0);
                                        String title = response.getJSONObject("volumeInfo").getString("title");
                                        String author = response.getJSONObject("volumeInfo").getJSONArray("authors").getString(0);
                                        String isbn = response.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier");
                                        double rating = response.getJSONObject("volumeInfo").getDouble("averageRating");
                                        int ratingCount = response.getJSONObject("volumeInfo").getInt("ratingsCount");
                                        int pageCount = response.getJSONObject("volumeInfo").getInt("pageCount");
                                        String overview = response.getJSONObject("volumeInfo").getString("description");


                                        String imageURLString = response.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");

                                        //Create Book From Extracted Info
                                        Book book = new Book ( title, author,isbn,rating, ratingCount, pageCount, imageURLString, overview);

                                        //Show the Button again
                                        readBarcode.show();

                                        //Start Book Details Activity
                                        Intent intent = new Intent(MainActivity.this,BookDetails.class );
                                        intent.putExtra ("Book", book);
                                        startActivityForResult(intent, RC_BOOK_DETAILS);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                    //Hide Button
                    readBarcode.hide();
                    Snackbar.make(mainView, "Loading...Please Wait..", Snackbar.LENGTH_LONG).show();
                    Volley.newRequestQueue(MainActivity.this).add(jsonrequest);



                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {

                CommonStatusCodes.getStatusCodeString(resultCode);
            }
        }
        else if (requestCode == RC_BOOK_DETAILS ) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if ( data != null ) {
                    //Sets default days to 10 if no day found
                    Book dataBook =(Book) data.getExtras().get("Book");
                    int numOfDays = data.getIntExtra("numOfDays" , 10 );
                    myLibrary.addEntry( new Entry(dataBook, numOfDays ));
                    Log.d("MainActivity" , myLibrary.toString());
                    adapter.notifyDataSetChanged();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    //Adapter class for Book List
    private class LibraryListAdapter extends ArrayAdapter <Entry> {


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
            daysLeft.setText("Days Left: "  + list.get(position).getDaysLeft());
            progressBar.setProgress(list.get(position).getPercentDaysPassed());
            return rowView;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        //Save Library data to Shared preferences
        //-----------------------------------------------------------
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson =  Converters.registerLocalDate(new GsonBuilder()).create();   //Convert To JSON to store in SharedPref, using a library to parse jodatime (joda-time-serializer)
        String json = gson.toJson(myLibrary); //
        prefsEditor.putString("Library", json);
        prefsEditor.commit();

        //-----------------------------------------------------------

    }

}



