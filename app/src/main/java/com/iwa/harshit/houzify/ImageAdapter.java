package com.iwa.harshit.houzify;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by harshit on 6/22/15.
 */
public class ImageAdapter extends CursorAdapter {

    public ImageAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imgView= (ImageView)view.findViewById(R.id.img_view);
        Picasso.with(context)
                    .load(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))))
                    .fit()
                    .into(imgView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.wtf("Picasso", "Success");
                        }

                        @Override
                        public void onError() {
                            Log.wtf("Picasso", "Error");
                        }
                    });

    }
}
