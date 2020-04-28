package sample;

import java.util.Date;

public class Item {

    public int id;
    public String title;
    public String mediaType;
    public Date doe;
    public boolean available;

    public Item(){}

    public Item(int id, String title, String mediaType, Date doe, boolean available) {
        this.id = id;
        this.title = title;
        this.mediaType = mediaType;
        this.doe = doe;
        this.available = available;
    }


    public void checkOut() {
        available = false;
    }

    public void checkin() {
        available = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Date getDoe() {
        return doe;
    }

    public void setDoe(Date doe) {
        this.doe = doe;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

}