package com.example.firebasesample;

public class AllIssuedBook {

    private AllIssuedBook(){}

    private AllIssuedBook(String BookName,String bookAuthor,String bookCover,String issuedDate,String returnDate,String studentEmail,String username,String userphone){
        this.BookName = BookName;
        this.bookAuthor = bookAuthor;
        this.bookCover = bookCover;
        this.issuedDate = issuedDate;
        this.returnDate = returnDate;
        this.studentEmail = studentEmail;
        this.username = username;
        this.userphone = userphone;
    }

    private String BookName;
    private String bookAuthor;
    private String bookCover;

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
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

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    private String issuedDate;
    private String returnDate;
    private String studentEmail;
    private String username;
    private String userphone;
}
