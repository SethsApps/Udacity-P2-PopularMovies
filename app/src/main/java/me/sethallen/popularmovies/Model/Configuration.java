package me.sethallen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration implements Parcelable {

    private ImageConfiguration images;
    @JsonProperty("change_keys")
    private List<String> changeKeys = new ArrayList<>();
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     * The images
     */
    public ImageConfiguration getImages() {
        return images;
    }

    /**
     *
     * @param images
     * The images
     */
    public void setImages(ImageConfiguration images) {
        this.images = images;
    }

    /**
     *
     * @return
     * The changeKeys
     */
    public List<String> getChangeKeys() {
        return changeKeys;
    }

    /**
     *
     * @param changeKeys
     * The change_keys
     */
    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.images, flags);
        dest.writeStringList(this.changeKeys);
        //dest.writeParcelable(this.additionalProperties, flags);
    }

    public Configuration() {
    }

    protected Configuration(Parcel in) {
        this.images = in.readParcelable(Images.class.getClassLoader());
        this.changeKeys = in.createStringArrayList();
        //this.additionalProperties = in.readParcelable(Map<String, Object>.class.getClassLoader());
    }

    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() {
        public Configuration createFromParcel(Parcel source) {
            return new Configuration(source);
        }

        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
}