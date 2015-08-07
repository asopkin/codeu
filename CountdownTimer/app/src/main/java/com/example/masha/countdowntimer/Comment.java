package com.example.masha.countdowntimer;

/**
 * Created by asopkin on 7/22/2015.
 */
public class Comment {
    private long id;
    private String comment;
    //private String descrip;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    //public String getDescription(){return descrip;}

    //public void setDescrip(String descrip){this.descrip = descrip;}

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return comment;
    }
}
