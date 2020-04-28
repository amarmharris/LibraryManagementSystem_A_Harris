package sample;

import java.util.Date;

public class Media extends Item {

    public String Director;

    public Media(){}

    public Media(int id, String title, String mediaType, Date doe, boolean available, String Director) {
        super(id, title, mediaType, doe, available);
        this.Director=Director;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String Director) {
        Director = Director;
    }

}