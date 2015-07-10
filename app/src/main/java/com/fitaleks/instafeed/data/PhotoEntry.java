package com.fitaleks.instafeed.data;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by alexanderkulikovskiy on 02.07.15.
 */
@Table(name = "PhotoEntry", id = BaseColumns._ID)
public class PhotoEntry extends Model {

    @Column(name = "insta_id")
    public String instaId;
    @Column(name = "image_url")
    public String imageUrl;
    @Column(name = "description")
    public String description;
    @Column(name = "created_time")
    public long createdTime;

    public static List<PhotoEntry> getAllPhotos() {
        return new Select().from(PhotoEntry.class).orderBy("created_time DESC ").execute();
    }

}
