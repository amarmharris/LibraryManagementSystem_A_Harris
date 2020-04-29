package sample.classes;

import java.util.Date;

public class Media {

    int mediaID;
    String title;
    String director;
    Date doe;
    String Status;
    Date checkoutDate;
    Date dueDate;
    int issueID;

    public Media(){}

    public Media(int mediaID, int issueID, String title, String director, Date checkoutDate, Date dueDate) {
        this.mediaID=mediaID;
        this.issueID = issueID;
        this.title = title;
        this.director = director;
        this.checkoutDate=checkoutDate;
        this.dueDate=dueDate;
    }

    public Media(int mediaID, String title, String director, String status, Date dueDate) {
        this.mediaID=mediaID;
        this.title=title;
        this.director=director;
        this.Status=status;
        this.dueDate=dueDate;
    }

    public Media(int mediaID, String title, String director, Date doe, String status) {
        this.mediaID = mediaID;
        this.title = title;
        this.director = director;
        this.doe = doe;
        Status = status;
    }

    public int getMediaID() {
        return mediaID;
    }

    public void setMediaID(int mediaID) {
        this.mediaID = mediaID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
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