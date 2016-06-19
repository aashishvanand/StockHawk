package com.aashish.stockhawk.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aashi on 6/19/2016.
 */

public class SymbolParcelable implements Parcelable{

    public String date;
    public double close;

    protected SymbolParcelable(Parcel in) {
        date = in.readString();
        close = in.readDouble();
    }

    public SymbolParcelable(String date, double close) {
        this.date = date;
        this.close = close;
    }

    public static final Creator<SymbolParcelable> CREATOR = new Creator<SymbolParcelable>() {
        @Override
        public SymbolParcelable createFromParcel(Parcel in) {
            return new SymbolParcelable(in);
        }

        @Override
        public SymbolParcelable[] newArray(int size) {
            return new SymbolParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeDouble(close);
    }
}