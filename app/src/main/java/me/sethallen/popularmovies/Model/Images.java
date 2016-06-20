package me.sethallen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Images implements Parcelable {

    private Integer id;
    private List<Image> backdrops = new ArrayList<>();
    private List<Image> posters   = new ArrayList<>();
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The backdrops
     */
    public List<Image> getBackdrops() {
        return backdrops;
    }

    /**
     *
     * @param backdrops
     * The backdrops
     */
    public void setBackdrops(List<Image> backdrops) {
        this.backdrops = backdrops;
    }

    /**
     *
     * @return
     * The posters
     */
    public List<Image> getPosters() {
        return posters;
    }

    /**
     *
     * @param posters
     * The posters
     */
    public void setPosters(List<Image> posters) {
        this.posters = posters;
    }

//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeList(this.backdrops);
        dest.writeTypedList(posters);
        //dest.writeParcelable(this.additionalProperties, flags);
    }

    public Images() {
    }

    protected Images(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.backdrops = new ArrayList<>();
        in.readList(this.backdrops, List.class.getClassLoader());
        this.posters = new ArrayList<>();
        in.readList(this.posters, List.class.getClassLoader());
        //this.posters = in.createT.createTypedArrayList(Poster.CREATOR);
        //this.additionalProperties = in.readParcelable(Map<String, Object>.class.getClassLoader());
    }

    public static final Parcelable.Creator<Images> CREATOR = new Parcelable.Creator<Images>() {
        public Images createFromParcel(Parcel source) {
            return new Images(source);
        }

        public Images[] newArray(int size) {
            return new Images[size];
        }
    };
}