package com.example.multiimageselector.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gparati on 08/07/14.
 */

//TODO I'll first do this for a String since is simpler and try to implement it for a Hashmap after

public class ImagePathParcelable implements Parcelable {
    private String imagePath;

    public ImagePathParcelable(String imageMap){
        this.imagePath = imageMap;
    }

    public ImagePathParcelable(Parcel source){
        this.imagePath = source.readString();
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public int describeContents() {
        return 0; // Typically returning 0 suffices unless you have numerous parcelable objects and require special serialization for some.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Here is where the conversion(serialization) happens
        //TODO Problem no given functions for HasMap !?!
        dest.writeString(this.imagePath);

    }

    public static final Parcelable.Creator<ImagePathParcelable> CREATOR
            = new Parcelable.Creator<ImagePathParcelable>(){

        @Override
        public ImagePathParcelable createFromParcel(Parcel source) {
            return new ImagePathParcelable(source); // RECREATE VENUE GIVEN SOURCE
        }

        @Override
        public ImagePathParcelable[] newArray(int size) {
            return new ImagePathParcelable[size]; // CREATING AN ARRAY OF VENUES
        }
    };
}