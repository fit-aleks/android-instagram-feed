package com.fitaleks.instafeed.data;

import java.util.ArrayList;

/**
 * Created by alexanderkulikovskiy on 20.06.15.
 */
public class InstagramPhoto {
    public String description;
    public String imgUrl;
    public ArrayList<CommentData> commentDataArrayList;

    public InstagramPhoto(String imageUrl, String descr) {
        this.imgUrl = imageUrl;
        this.description = descr;
    }
}
