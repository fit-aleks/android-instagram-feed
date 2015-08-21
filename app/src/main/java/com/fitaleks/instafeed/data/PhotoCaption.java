package com.fitaleks.instafeed.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexander on 21.08.15.
 */
public class PhotoCaption {
    public long createdTime;
    public String text;
    @SerializedName("from")
    public User fromUser;

}
