package com.kiennguyen.imagebrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class AsyncTaskLover extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "AsyncTaskLover";
    private Context mContext;
    private String imageUrl;
    private BitmapListener mListener;

    public AsyncTaskLover(Context context, BitmapListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            imageUrl = params[0];
            if (!imageUrl.equals(null) && !imageUrl.isEmpty()) {
                Log.i(TAG, imageUrl);
            }
            return Picasso.with(mContext).load(imageUrl).get();
        } catch (Exception exp) {
            Log.i(TAG, exp.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mListener != null) {
            mListener.onBitmapLoaded(bitmap);
        } else {
            mListener.onError();
            Log.i(TAG, "can not load bitmap from url = " + imageUrl);
        }
    }

    public interface BitmapListener {
        void onBitmapLoaded(Bitmap bitmap);

        void onError();
    }
}
