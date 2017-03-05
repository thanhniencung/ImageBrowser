package com.kiennguyen.imagebrowser;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class ImageInfo implements Parcelable {
    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
    public int width;
    public int height;
    public int top;
    public int left;
    private float[] initMatrixData = new float[9];

    public ImageInfo() {
    }

    protected ImageInfo(Parcel in) {
        this.width = in.readInt();
        this.height = in.readInt();
        this.top = in.readInt();
        this.left = in.readInt();
        this.initMatrixData = in.createFloatArray();
    }

    public float[] getInitMatrixData() {
        return initMatrixData;
    }

    public void setInitMatrixData(float[] initMatrixData) {
        this.initMatrixData = initMatrixData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.top);
        dest.writeInt(this.left);
        dest.writeFloatArray(this.initMatrixData);
    }
}