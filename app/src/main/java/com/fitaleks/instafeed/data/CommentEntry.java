package com.fitaleks.instafeed.data;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alexanderkulikovskiy on 02.07.15.
 */
@Table(name = "CommentEntry", id = BaseColumns._ID)
public class CommentEntry extends Model {

    @Column(name = "comment_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String commentId;
    @Column(name = "text")
    public String text;
    @Column(name = "author_username")
    public String authorName;
    @Column(name = "time")
    public long time;

    @SerializedName("from")
    public User authorUser;

    @Column(name = "photo", onDelete = Column.ForeignKeyAction.CASCADE)
    public PhotoEntry photoEntry;

}
