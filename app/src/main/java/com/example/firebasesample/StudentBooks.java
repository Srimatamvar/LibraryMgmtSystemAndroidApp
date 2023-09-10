package com.example.firebasesample;

public class StudentBooks {

    private StudentBooks(){}

    private StudentBooks(String bookname, String author, String quantity, String bookcover){
        this.bookname = bookname;
        this.bookcover = bookcover;
        this.author = author;
        this.quantity = quantity;
    }

    private String bookname;
    private String author;

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBookcover() {
        return bookcover;
    }

    public void setBookcover(String bookcover) {
        this.bookcover = bookcover;
    }

    private String quantity;
    private String bookcover;
}
