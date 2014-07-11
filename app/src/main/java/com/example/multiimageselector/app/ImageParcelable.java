package com.example.multiimageselector.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gparati on 08/07/14.
 */

//TODO I'll first do this for a String since is simpler and try to implement it for a Hashmap after

public class ImageParcelable implements Parcelable {
    private int _position;
    private String _imagePath;
    private Boolean _selected;

    public ImageParcelable(int position, String imagePath, Boolean selected){
        _position = position;
        _imagePath = imagePath;
        _selected = selected;
    }

    public ImageParcelable(Parcel source){
        _imagePath = source.readString();
    }

    public int get_position() {
        return _position;
    }
    public void set_position(int position) { _position = position; }
    public String get_imagePath() {
        return _imagePath;
    }
    public void set_imagePath(String imagePath){ _imagePath = imagePath; }
    public Boolean get_selected() {
        return _selected;
    }
    public void set_selected(Boolean selected) {_selected = selected; }
    @Override
    public int describeContents() {
        return 0; // Typically returning 0 suffices unless you have numerous parcelable objects and require special serialization for some.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Here is where the conversion(serialization) happens
        dest.writeInt(_position);
        dest.writeString(_imagePath);
        dest.writeInt(_selected ? 1 : 0);
    }

    public static final Creator<ImageParcelable> CREATOR
            = new Creator<ImageParcelable>(){

        @Override
        public ImageParcelable createFromParcel(Parcel source) {
            return new ImageParcelable(source); // RECREATE VENUE GIVEN SOURCE
        }

        @Override
        public ImageParcelable[] newArray(int size) {
            return new ImageParcelable[size]; // CREATING AN ARRAY OF VENUES
        }
    };
}