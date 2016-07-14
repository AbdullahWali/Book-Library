package com.bookkeep;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.bookkeep.Core.Book;
import com.bookkeep.Core.Entry;
import com.bookkeep.Core.Library;
import com.bookkeep.barcodereader.BarcodeCaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int RC_BOOK_DETAILS = 1;

    final static String TAG = "MainActivity";

    static Library myLibrary;
    FloatingActionButton readBarcode;
    ListView bookList;
    static LibraryListAdapter adapter;
    String url;
    View mainView;
    LinearLayout introImage;


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
        introImage = (LinearLayout) findViewById(R.id.introImage);

        //If Library is available in SharedPref, Import it, otherwise Create a new Library
        Library tempLibrary = Utilities.loadLibraryFromPref(getApplicationContext());
        if ( tempLibrary == null ) myLibrary = new Library();
        else myLibrary = tempLibrary;


        //Set Adapter for bookList and register for context menu
        adapter = new LibraryListAdapter(this,myLibrary.getLibrary());
        bookList.setAdapter(adapter);
        registerForContextMenu(bookList);

        //Set AlarmManager
        Utilities.setAlarmIfRequired(getApplicationContext());

        //Click Listener for Listview: Check Book Details again

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book curBook = ((Entry) (bookList.getItemAtPosition(position))).getBook();
                Intent intent = new Intent(MainActivity.this,BookDetails.class );
                intent.putExtra("ShowSave", false);
                intent.putExtra ("Book", curBook);
                startActivityForResult(intent, RC_BOOK_DETAILS);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_context_menu, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If library is empty display IntroImage
        if (myLibrary.getLibrary().isEmpty() ) introImage.setVisibility(View.VISIBLE);
        else introImage.setVisibility(View.INVISIBLE);
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
        //Returning from Activity: BarcodeCapture
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

                                        double rating = 0.0;
                                        int ratingCount = 0;
                                        try {
                                            rating = response.getJSONObject("volumeInfo").getDouble("averageRating");
                                            ratingCount = response.getJSONObject("volumeInfo").getInt("ratingsCount");
                                        }
                                        catch (JSONException exception){
                                            Log.d("JSON", exception.toString());
                                        }
                                        int pageCount = response.getJSONObject("volumeInfo").getInt("pageCount");
                                        String overview = response.getJSONObject("volumeInfo").getString("description");
                                        String imageURLString = response.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");

                                        //Create Book From Extracted Info
                                        Book book = new Book ( title, author,isbn,rating, ratingCount, pageCount, imageURLString, overview);

                                        //Show the Button again
                                        readBarcode.show();
                                        //Start Book Details Activity
                                        Intent intent = new Intent(MainActivity.this,BookDetails.class );
                                        intent.putExtra("ShowSave", true);
                                        intent.putExtra ("Book", book);
                                        startActivityForResult(intent, RC_BOOK_DETAILS);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("JSON",e.toString());
                                        Snackbar.make(mainView, "Could not parse Book info...", Snackbar.LENGTH_LONG).show();
                                        readBarcode.show();

                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Snackbar.make(mainView, "Could not Connect to server..", Snackbar.LENGTH_LONG).show();
                                    readBarcode.show();
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

        //Returning From Activity: BookDetails
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

    public void returnBook(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Entry entryToRemove = (Entry) bookList.getItemAtPosition(info.position);
        myLibrary.removeEntry(entryToRemove);
        adapter.notifyDataSetChanged();
        Snackbar snackbar = Snackbar.make(mainView,"Entry was deleted...",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLibrary.addEntry(entryToRemove);
                adapter.notifyDataSetChanged();
            }
        });
        snackbar.show();
    }

    public void changeDate(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(info.position);
        datePickerFragment.show(getFragmentManager(),"datePicker");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.saveLibraryToPref(getApplicationContext(),myLibrary);

    }

}



