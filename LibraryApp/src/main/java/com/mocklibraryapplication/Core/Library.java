package com.mocklibraryapplication.Core;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Abdullah on 18/10/15.
 */
public class Library {

    private ArrayList<Entry> library ;

    public Library () {
        library = new ArrayList<Entry>();
    }

    public void addEntry( Entry entry ) {
        library.add(entry);
    }

    public void removeEntry ( Entry entry ) {
        if (library.contains(entry))
            library.remove(entry);
        else Log.d ( "Library" , "Entry Not Found");
    }

    public ArrayList<Entry> getLibrary() {
        return library;
    }

    @Override
    public String toString() {
        return "Library{" +
                "library=" + library +
                '}';
    }
}