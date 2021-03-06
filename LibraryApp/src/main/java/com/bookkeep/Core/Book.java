package com.bookkeep.Core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abdullah on 18/10/15.
 */
public class Book implements Parcelable {

    private String title;
    private String author;
    private String isbn ;
    private double rating;
    private int ratingCount;
    private int pageCount;
    private String imageURL;
    private String overview;

    public Book(String title, String author, String isbn, double rating, int ratingCount, int pageCount, String imageURL, String overview) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.pageCount = pageCount;
        this.imageURL = imageURL;
        this.overview = overview;
    }

    public Book ( ) {
        title = null;
        author = null;
        isbn = "0";
        pageCount = 0;
        overview = null;
        imageURL = null;
    }



// Getters and Setters

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String image) {
        this.imageURL = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", pageCount=" + pageCount +
                ", imageURL=" + imageURL +
                ", overview='" + overview + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.isbn);
        dest.writeDouble(this.rating);
        dest.writeInt(this.ratingCount);
        dest.writeInt(this.pageCount);
        dest.writeString(this.imageURL);
        dest.writeString(this.overview);
    }

    protected Book(Parcel in) {
        this.title = in.readString();
        this.author = in.readString();
        this.isbn = in.readString();
        this.rating = in.readDouble();
        this.ratingCount = in.readInt();
        this.pageCount = in.readInt();
        this.imageURL = in.readString();
        this.overview = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
