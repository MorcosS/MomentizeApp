package com.dcc.momentizeapp.Data;

import android.location.Location;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MorcosS on 5/17/17.
 */

public class Memory {
    Comments comments;
    String memoryTitle;
    String memoryDate;
    GPS location;
    ArrayList<Uri> imagesUri;

    public Memory(){

    }

    public Memory(Comments comments, String memoryTitle, Date memoryDate, GPS location) {
        this.comments = comments;
        this.memoryTitle = memoryTitle;
        this.memoryDate = memoryDate.toString();
        this.location = location;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public String getMemoryTitle() {
        return memoryTitle;
    }

    public void setMemoryTitle(String memoryTitle) {
        this.memoryTitle = memoryTitle;
    }

    public String getMemoryDate() {
        return memoryDate;
    }

    public void setMemoryDate(String memoryDate) {
        this.memoryDate = memoryDate;
    }

    public GPS getLocation() {
        return location;
    }

    public void setLocation(double lat,double lon) {

       this.location = new GPS(lat, lon);
    }

    public ArrayList<Uri> getImagesUri() {
        return imagesUri;
    }

    public void setImagesUri(ArrayList<Uri> imagesUri) {
        this.imagesUri = imagesUri;
    }
}
