package com.example.firebasesample;

public class IssuedBookList {
    private String BookName;
    private String issuedDate;
    private String returnDate;
    private String studentName;
    private String BookAuthor;

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBookAuthor() {
        return BookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        BookAuthor = bookAuthor;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    private String bookCover;

    private IssuedBookList(){}

    private IssuedBookList(String bookName, String issuedDate, String returnDate, String studentName, String bookAuthor, String bookCover) {
        this.BookName = bookName;
        this.issuedDate = issuedDate;
        this.returnDate = returnDate;
        this.studentName = studentName;
        this.BookAuthor = bookAuthor;
        this.bookCover = bookCover;
    }
}
