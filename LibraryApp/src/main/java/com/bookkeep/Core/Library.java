package com.bookkeep.Core;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Abdullah on 18/10/15.
 */
public class Library {

    private ArrayList<Entry> library ;

    public Library () {
        library = new ArrayList<Entry>();
    }

    public void sortLibrary(){
        Collections.sort(library);
    }

    public void addEntry( Entry entry ) {
        library.add(entry);
        sortLibrary();
    }

    public void removeEntry ( Entry entry ) {
        if (library.contains(entry)) {
            library.remove(entry);
            sortLibrary();
        }
        else Log.d ( "Library" , "Entry Not Found");
    }

    public ArrayList<Entry> getLibrary() {
        sortLibrary();
        return library;
    }

    @Override
    public String toString() {
        return "Library{" +
                "library=" + library +
                '}';
    }
}