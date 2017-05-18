package com.dcc.momentizeapp.Data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MorcosS on 5/9/17.
 */

public class LinkedUsers {
    private String BaseUID,LinkedUID;

    public LinkedUsers (String baseUID, String linkedUID){
        BaseUID = baseUID;
        LinkedUID = linkedUID;
    }

    public String getBaseUID() {
        return BaseUID;
    }

    public void setBaseUID(String baseUID) {
        BaseUID = baseUID;
    }

    public String getLinkedUID() {
        return LinkedUID;
    }

    public void setLinkedUID(String linkedUID) {
        LinkedUID = linkedUID;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("BaseUID", BaseUID);
        result.put("LinkedUID", LinkedUID);

        return result;
    }
}
