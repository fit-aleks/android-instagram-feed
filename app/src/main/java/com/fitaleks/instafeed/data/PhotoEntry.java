package com.fitaleks.instafeed.data;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alexanderkulikovskiy on 02.07.15.
 */
@Table(name = "PhotoEntry", id = BaseColumns._ID)
public class PhotoEntry extends Model implements BaseModel {

    @SerializedName("id")
    @Column(name = "insta_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String instaId;

    @Column(name = "image_url")
    public String imageUrl;

    @Column(name = "description")
    public String description;

    public String type;
    @Column(name = "created_time")
    public long createdTime;

    @SerializedName("comments")
    public List<CommentEntry> commentEntries;

    @SerializedName("caption")
    public PhotoCaption photoCaption;
    @SerializedName("images")
    public Images allImages;

    @Override
    public void saveModel() {
        if (photoCaption != null) {
            this.description = photoCaption.text;
        } else {
            this.description = "";
        }
        this.imageUrl = allImages.standardResolution.url;
        save();
        for (CommentEntry commentEntry : commentEntries) {
            commentEntry.photoEntry = this;
            commentEntry.save();
        }
    }

}
