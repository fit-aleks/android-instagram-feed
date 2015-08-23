package com.fitaleks.instafeed.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexander on 21.08.15.
 */
public class User {
    @SerializedName("username")
    public String userName;
    public String profilePicture;
    public long id;
    public String fullName;
}
