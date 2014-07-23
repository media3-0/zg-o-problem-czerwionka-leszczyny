package com.media30.zglosproblem.mobileapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;


public class Information implements Parcelable {
    private int id;
    private String Title;
    private String Info;
    private String ImageUrl;

    Information(int id, String Title, String Info, String ImageUrl){
        this.id = id;
        this.Title = Title;
        this.Info = Info;
        this.ImageUrl = ImageUrl;
    }

    Information(JSONObject obj){
        try {
            this.id = obj.getInt("id");
            this.Title = obj.getString("title");
            this.Info = obj.getString("info");
            this.ImageUrl = obj.getString("url");

        }catch (JSONException e){
            Log.e("JSONException", e.toString());
        }
    }

    Information(Parcel in){
        this.id = in.readInt();
        this.Title = in.readString();
        this.Info = in.readString();
        this.ImageUrl = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getInfo() {
        return Info;
    }

    public String getImageUrl() {
        return "info/" + this.getId() + "." + ImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Information> CREATOR
            = new Parcelable.Creator<Information>() {
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        public Information[] newArray(int size) {
            return new Information[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.Title);
        parcel.writeString(this.Info);
        parcel.writeString(this.ImageUrl);
    }
}
