package com.dcc.momentizeapp.Data;

/**
 * Created by MorcosS on 5/17/17.
 */

public class Comments {
    String comments_1,comments_2;

    public Comments(){

    }

    public Comments(String comments_1, String comments_2) {
        this.comments_1 = comments_1;
        this.comments_2 = comments_2;
    }

    public String getComments_1() {
        return comments_1;
    }

    public void setComments_1(String comments_1) {
        this.comments_1 = comments_1;
    }

    public String getComments_2() {
        return comments_2;
    }

    public void setComments_2(String comments_2) {
        this.comments_2 = comments_2;
    }
}
