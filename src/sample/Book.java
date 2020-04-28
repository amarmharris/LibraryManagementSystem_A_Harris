package sample;

import java.util.Date;

public class Book extends Item {

    public String Author;

    public Book(){}

    public Book(int id, String title, String mediaType, Date doe, boolean available, String Author) {
        super(id, title, mediaType, doe, available);
        this.Author=Author;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

}
