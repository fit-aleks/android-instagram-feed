package com.fitaleks.instafeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitaleks.instafeed.data.InstagramPhoto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alexanderkulikovskiy on 20.06.15.
 */
public class InstaFeedAdapter extends RecyclerView.Adapter<InstaFeedAdapter.PhotoViewHolder> {

    private Context mContext;
    private ArrayList<InstagramPhoto> mList;

    public InstaFeedAdapter(@NonNull Context context, ArrayList<InstagramPhoto> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.list_item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder photoViewHolder, int i) {
        photoViewHolder.mDescription.setText(mList.get(i).description);
        Picasso.with(mContext)
                .load(mList.get(i).imgUrl)
                .into(photoViewHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public void addItems(ArrayList<InstagramPhoto> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void resetAndAddItems(ArrayList<InstagramPhoto> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mDescription;

        public PhotoViewHolder(View v) {
            super(v);
            mImageView = (ImageView)v.findViewById(R.id.post_img);
            mDescription = (TextView)v.findViewById(R.id.post_description);
        }
    }

}
