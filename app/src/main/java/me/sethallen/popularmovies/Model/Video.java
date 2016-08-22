package me.sethallen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "iso_639_1",
        "iso_3166_1",
        "key",
        "name",
        "site",
        "size",
        "type"
})
public class Video implements Parcelable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("iso_639_1")
    private String iso6391;
    @JsonProperty("iso_3166_1")
    private String iso31661;
    @JsonProperty("key")
    private String key;
    @JsonProperty("name")
    private String name;
    @JsonProperty("site")
    private String site;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The iso6391
     */
    @JsonProperty("iso_639_1")
    public String getIso6391() {
        return iso6391;
    }

    /**
     *
     * @param iso6391
     * The iso_639_1
     */
    @JsonProperty("iso_639_1")
    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    /**
     *
     * @return
     * The iso31661
     */
    @JsonProperty("iso_3166_1")
    public String getIso31661() {
        return iso31661;
    }

    /**
     *
     * @param iso31661
     * The iso_3166_1
     */
    @JsonProperty("iso_3166_1")
    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    /**
     *
     * @return
     * The key
     */
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key
     * The key
     */
    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The site
     */
    @JsonProperty("site")
    public String getSite() {
        return site;
    }

    /**
     *
     * @param site
     * The site
     */
    @JsonProperty("site")
    public void setSite(String site) {
        this.site = site;
    }

    /**
     *
     * @return
     * The size
     */
    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    /**
     *
     * @param size
     * The size
     */
    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     *
     * @return
     * The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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
        dest.writeString(this.id);
        dest.writeString(this.iso6391);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeValue(this.size);
        dest.writeString(this.type);
    }

    public Video() {
    }

    protected Video(Parcel in) {
        this.id = in.readString();
        this.iso6391 = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = (Integer) in.readValue(Double.class.getClassLoader());
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}

//import android.net.Uri;
//import android.os.Parcel;
//import android.os.Parcelable;
//import android.util.Log;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//public class Video implements Parcelable {
//
//    private static String LOG_TAG = Video.class.getSimpleName();
//
//    private String id;
//    @JsonProperty("iso_639_1")
//    private String iso6391;
//    private String key;
//    private String name;
//    private String site;
//    private Integer size;
//    private String type;
//
//    /**
//     *
//     * @return
//     * The id
//     */
//    public String getId() {
//        return id;
//    }
//
//    /**
//     *
//     * @param id
//     * The id
//     */
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    /**
//     *
//     * @return
//     * The iso6391
//     */
//    public String getIso6391() {
//        return iso6391;
//    }
//
//    /**
//     *
//     * @param iso6391
//     * The iso_639_1
//     */
//    public void setIso6391(String iso6391) {
//        this.iso6391 = iso6391;
//    }
//
//    /**
//     *
//     * @return
//     * The key
//     */
//    public String getKey() {
//        return key;
//    }
//
//    /**
//     *
//     * @param key
//     * The key
//     */
//    public void setKey(String key) { this.key = key; }
//
//    /**
//     *
//     * @return
//     * The name
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     *
//     * @param name
//     * The name
//     */
//    public void setName(String name) { this.name = name; }
//
//    /**
//     *
//     * @return
//     * The site
//     */
//    public String getSite() {
//        return site;
//    }
//
//    /**
//     *
//     * @param site
//     * The site
//     */
//    public void setSite(String site) { this.site = site; }
//
//    /**
//     *
//     * @return
//     * The size
//     */
//    public Integer getSize() {
//        return size;
//    }
//
//    /**
//     *
//     * @param size
//     * The size
//     */
//    public void setSize(Integer size) {
//        this.size = size;
//    }
//
//    /**
//     *
//     * @return
//     * The type
//     */
//    public String getType() {
//        return type;
//    }
//
//    /**
//     *
//     * @param type
//     * The type
//     */
//    public void setType(String type) { this.type = type; }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.id);
//        dest.writeString(this.iso6391);
//        dest.writeString(this.key);
//        dest.writeString(this.name);
//        dest.writeString(this.site);
//        dest.writeValue(this.size);
//        dest.writeString(this.type);
//    }
//
//    public Video() {
//    }
//
//    protected Video(Parcel in) {
//        this.id = in.readString();
//        this.iso6391 = in.readString();
//        this.key = in.readString();
//        this.name = in.readString();
//        this.site = in.readString();
//        this.size = (Integer) in.readValue(Double.class.getClassLoader());
//        this.type = in.readString();
//    }
//
//    public static final Creator<Video> CREATOR = new Creator<Video>() {
//        public Video createFromParcel(Parcel source) {
//            return new Video(source);
//        }
//
//        public Video[] newArray(int size) {
//            return new Video[size];
//        }
//    };
//
//    private Uri getUri(Uri baseUri, String size, String uniquePath)
//    {
//        Uri newURi = baseUri.buildUpon()
//                .appendEncodedPath(size + uniquePath)
//                .build();
//
//        Log.d(LOG_TAG, "URI was: " + newURi.toString());
//        return newURi;
//    }
//}