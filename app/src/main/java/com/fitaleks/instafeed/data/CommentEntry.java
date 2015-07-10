package com.fitaleks.instafeed.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by alexanderkulikovskiy on 02.07.15.
 */
@Table(name = "CommentEntry")
public class CommentEntry extends Model {

    @Column(name = "comment_id")
    public String instaId;
    @Column(name = "text”")
    public String text;
    @Column(name = "text")
    public String authorName;
    @Column(name = "author_username")
    public long time;
    @Column(name = "photo_id")
    public String photoId;
}
