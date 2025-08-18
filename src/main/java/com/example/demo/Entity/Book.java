//This is Entity or Model in MVC architecture

package com.example.demo.Entity;
public class Book {
    private String bookID;
    private String bookName;
    private String author;
    private String ISBN;
    private double price;
    private int bookQuantity;


    public Book(String bookID, String bookName, String author, String ISBN, double price, int bookQuantity) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.author = author;
        this.ISBN = ISBN;
        this.price = price;
        this.bookQuantity = bookQuantity;
    }

    public int getBookQuantity() {
        return bookQuantity;
    }

    public void setBookQuantity(int bookQuantity) {
        this.bookQuantity = bookQuantity;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
