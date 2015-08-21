package com.fitaleks.instafeed;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by alexanderkulikovskiy on 20.06.15.
 */
public class InstaFeedAdapter extends CursorAdapter {
    private static final String LOG_TAG = InstaFeedAdapter.class.getSimpleName();

    public InstaFeedAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_photo, parent, false);
        PhotoViewHolder viewHolder = new PhotoViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final PhotoViewHolder photoViewHolder = (PhotoViewHolder) view.getTag();

        final String description = cursor.getString(MainActivityFragment.COL_PHOTO_DESCR);
        photoViewHolder.mDescription.setText(description);

        final String imageUrl = cursor.getString(MainActivityFragment.COL_PHOTO_URL);
        Picasso.with(context)
                .load(imageUrl)
                .into(photoViewHolder.mImageView);
    }

    public static class PhotoViewHolder {
        public final ImageView mImageView;
        public final TextView mDescription;

        public PhotoViewHolder(View v) {
            mImageView = (ImageView)v.findViewById(R.id.post_img);
            mDescription = (TextView)v.findViewById(R.id.post_description);
        }
    }

}
