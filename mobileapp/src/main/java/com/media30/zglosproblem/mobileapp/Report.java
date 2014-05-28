package com.media30.zglosproblem.mobileapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

public class Report implements Parcelable{
    private int id;
    private String description;
    private LatLng location;
    private String ext;

    Report(JSONObject obj){
        try {
            this.id = obj.getInt("id");
            this.description = obj.getString("description");
            double lat = obj.getDouble("lat");
            double lng = obj.getDouble("lng");
            this.location = new LatLng(lat, lng);
            this.ext = obj.getString("ext");
        }catch (JSONException e){
            Log.e("JSONException", e.toString());
        }
    }

    Report(Parcel in){
        this.id = in.readInt();
        this.description = in.readString();
        this.ext = in.readString();
        Double lat = in.readDouble();
        Double lng = in.readDouble();
        this.location = new LatLng(lat, lng);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getThumbUrl(){
        if(this.ext.contains("null")) return "";
        return MainActivity.HOST + "thumbs/" + this.id + "." + this.ext;
    }

    public String getImageUrl(){
        if(this.ext.contains("null")) return "";
        return MainActivity.HOST + "images/" + this.id + "." + this.ext;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Report> CREATOR
            = new Parcelable.Creator<Report>() {
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.description);
        parcel.writeString(this.ext);
        parcel.writeDouble(this.location.latitude);
        parcel.writeDouble(this.location.longitude);
    }
}