package sample.classes;

import java.util.Date;

public class Book {

    int bookID;
    String title;
    String author;
    Date doe;
    String Status;
    Date checkoutDate;
    Date dueDate;
    int issueID;

    public Book(){}

    public Book(int bookID, int issueID, String title, String author, Date checkoutDate, Date dueDate) {
        this.bookID=bookID;
        this.issueID=issueID;
        this.title=title;
        this.author=author;
        this.checkoutDate=checkoutDate;
        this.dueDate=dueDate;
    }

    public Book(int bookID, String title, String author, String status, Date dueDate) {
        this.bookID=bookID;
        this.title=title;
        this.author=author;
        this.Status=status;
        this.dueDate=dueDate;
    }

    public Book(int bookID, String title, String author, Date doe, String status) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.doe = doe;
        Status = status;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
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

    public Date getDoe() {
        return doe;
    }

    public void setDoe(Date doe) {
        this.doe = doe;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }



}
