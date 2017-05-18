package com.dcc.momentizeapp.Data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MorcosS on 5/9/17.
 */

public class Blob {
    private LinkedUsers linkedUsers;

    public Blob (){

    }

    public LinkedUsers getLinkedUsers() {
        return linkedUsers;
    }

    public void setLinkedUsers(String baseUID , String linkedUID) {
        linkedUsers = new LinkedUsers(baseUID,linkedUID);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("LinkedUsers", linkedUsers.toMap());

        return result;
    }

}
