package com.view.text.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.view.text.annotation.Align;

public class BitmapPackageBean implements Parcelable {

    private String name;
    private int width;
    private int height;
    private Bitmap bitmap;

    private int marginTop;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;
    private int align = Align.TOP;

    public BitmapPackageBean() {
    }

    public BitmapPackageBean(int width, int height, Bitmap bitmap) {
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
    }

    public BitmapPackageBean(String name, int width, int height, Bitmap bitmap) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
    }

    protected BitmapPackageBean(Parcel in) {
        name = in.readString();
        width = in.readInt();
        height = in.readInt();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeParcelable(bitmap, flags);
        dest.writeInt(marginTop);
        dest.writeInt(marginBottom);
        dest.writeInt(marginLeft);
        dest.writeInt(marginRight);
        dest.writeInt(align);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BitmapPackageBean> CREATOR = new Creator<BitmapPackageBean>() {
        @Override
        public BitmapPackageBean createFromParcel(Parcel in) {
            return new BitmapPackageBean(in);
        }

        @Override
        public BitmapPackageBean[] newArray(int size) {
            return new BitmapPackageBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

}
