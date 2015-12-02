package com.shaiban.geo.geoapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mohammed on 11/20/2015.
 */
public class MyMarker {
    String name;
    Double latitude;
    Double longitude;
    String mLabel;
    String mIcon;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    String place;
    Context context;

    public MyMarker(Context context,String n, Double latitude, Double langitude,String icon){
        this.context=context;
        this.name=n;
        this.latitude=latitude;
        this.longitude =langitude;
        this.place= getLocation(latitude,longitude);
        this.mIcon=icon;
    }

    public String getLocation(double lat, double lng) {
        try {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0)
                return addresses.get(0).getLocality();
        } catch (IOException e) {
            return "";
        }
        return "";
    }


    public String getmLabel() {
        return mLabel;
    }

    public void setmLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


}
