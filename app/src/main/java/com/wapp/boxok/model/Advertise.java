package com.wapp.boxok.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MacBook on 27/07/2015.
 */
public class Advertise extends RealmObject {
    @PrimaryKey
    private String             path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
