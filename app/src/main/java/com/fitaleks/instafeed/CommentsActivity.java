package com.fitaleks.instafeed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alexanderkulikovskiy on 23.06.15.
 */
public class CommentsActivity extends AppCompatActivity {

    public static final String KEY_PHOTO_ID = "comment_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        if (savedInstanceState == null) {
            String photoId = getIntent().getStringExtra(KEY_PHOTO_ID);
            CommentsFragment commentsFragment = CommentsFragment.newInstance(photoId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.comments_activity, commentsFragment)
                    .commit();
        }
    }
}
