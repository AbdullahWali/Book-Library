package com.mocklibraryapplication.Core;

import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 * Created by Abdullah on 18/10/15.
 * The Entry will consist of a Book and additional user specific information: Borrow Date, Return Date, etc; It Will be used in the Library Class
 */
public class Entry {
    Book book ;
    final LocalDate borrowDate; // Borrow Date immutable
    LocalDate dueDate;


    //Constructor using numOfDays; Initialises borrowDate to (now); and dueDate to borrowDate.day  + numDays;
    public Entry(Book book, int numOfDays ) {
        this.book = book;
        this.borrowDate = new LocalDate();
        this.dueDate = borrowDate.plusDays(numOfDays);
    }


    //To String
    @Override
    public String toString() {
        return "Entry{" +
                "book=" + book.getTitle() +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", Days Left = " + getDaysLeft() +
                ", Percentage = " + getPercentDaysPassed() +
                '}';
    }



    public void extendDueDate ( int numOfDays ) {

        dueDate = dueDate.plusDays(numOfDays);

    }


    //Getters and Setters
    public Book getBook() {
        return book;
    }

    public int getPercentDaysPassed() {
        //Returns Percentage of Days passed if it is > 10;
        // If 10 or less returns 10, for graphical clarity on progress bar
        double passed = Days.daysBetween(borrowDate ,new LocalDate()).getDays();
        double totalPeriod = Days.daysBetween(borrowDate, dueDate).getDays();
        if (totalPeriod == 0 ) return 100;
        if ( ((passed / totalPeriod)*100) <= 10 ) return 10;
        else return  (int)  ((passed / totalPeriod)*100);

    }

    public int getDaysLeft() {
        if (Days.daysBetween(new LocalDate() , dueDate).getDays() <0 )
            return 0;
        return Days.daysBetween(new LocalDate() , dueDate).getDays();
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
