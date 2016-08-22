package me.sethallen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import me.sethallen.popularmovies.interfaces.ITMDBResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "results"
})
public class Videos implements Parcelable, ITMDBResponse<Video> {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("results")
    private List<Video> results = new ArrayList<Video>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    @JsonProperty("results")
    public List<Video> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    @JsonProperty("results")
    public void setResults(List<Video> results) {
        this.results = results;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeList(this.results);
    }

    public Videos() {
    }

    protected Videos(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.results = new ArrayList<>();
        in.readList(this.results, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        public Videos createFromParcel(Parcel source) {
            return new Videos(source);
        }

        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };
}

//import android.os.Parcel;
//import android.os.Parcelable;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Videos implements Parcelable {
//
//    private Integer     id;
//    private List<Video> videos = new ArrayList<>();
//
//    /**
//     *
//     * @return
//     * The id
//     */
//    public Integer getId() {
//        return id;
//    }
//
//    /**
//     *
//     * @param id
//     * The id
//     */
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    /**
//     *
//     * @return
//     * The videos
//     */
//    public List<Video> getVideos() {
//        return videos;
//    }
//
//    /**
//     *
//     * @param videos
//     * The videos
//     */
//    public void setVideos(List<Video> videos) {
//        this.videos = videos;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeValue(this.id);
//        dest.writeList(this.videos);
//    }
//
//    public Videos() {
//    }
//
//    protected Videos(Parcel in) {
//        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
//        this.videos = new ArrayList<>();
//        in.readList(this.videos, List.class.getClassLoader());
//    }
//
//    public static final Creator<Videos> CREATOR = new Creator<Videos>() {
//        public Videos createFromParcel(Parcel source) {
//            return new Videos(source);
//        }
//
//        public Videos[] newArray(int size) {
//            return new Videos[size];
//        }
//    };
//}