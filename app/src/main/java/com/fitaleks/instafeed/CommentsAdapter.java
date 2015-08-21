package com.fitaleks.instafeed;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by alexanderkulikovskiy on 23.06.15.
 */
public class CommentsAdapter extends CursorAdapter {

    public CommentsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
        CommentViewHolder viewHolder = new CommentViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final CommentViewHolder photoViewHolder = (CommentViewHolder) view.getTag();

        final String author = cursor.getString(cursor.getColumnIndex("author_username"));
        photoViewHolder.mAuthorName.setText(author);

        final String text = cursor.getString(cursor.getColumnIndex("text"));
        photoViewHolder.mText.setText(text);
    }

    public static class CommentViewHolder {
        public final TextView mAuthorName;
        public final TextView mText;

        public CommentViewHolder(View v) {
            mAuthorName = (TextView)v.findViewById(R.id.list_item_comment_author);
            mText = (TextView)v.findViewById(R.id.list_item_comment_text);
        }
    }
}
