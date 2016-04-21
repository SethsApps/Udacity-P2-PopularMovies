package me.sethallen.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Allense on 4/21/2016.
 */
public class Image implements Parcelable {

    @JsonProperty("aspect_ratio")
    private Double aspectRatio;
    @JsonProperty("file_path")
    private String filePath;
    private Integer height;
    @JsonProperty("iso_639_1")
    private String iso6391;
    @JsonProperty("vote_average")
    private Double voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
    private Integer width;

    /**
     *
     * @return
     * The aspectRatio
     */
    public Double getAspectRatio() {
        return aspectRatio;
    }

    /**
     *
     * @param aspectRatio
     * The aspect_ratio
     */
    public void setAspectRatio(Double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    /**
     *
     * @return
     * The filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     *
     * @param filePath
     * The file_path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     *
     * @return
     * The height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     *
     * @param height
     * The height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     *
     * @return
     * The iso6391
     */
    public String getIso6391() {
        return iso6391;
    }

    /**
     *
     * @param iso6391
     * The iso_639_1
     */
    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    /**
     *
     * @return
     * The voteAverage
     */
    public Double getVoteAverage() {
        return voteAverage;
    }

    /**
     *
     * @param voteAverage
     * The vote_average
     */
    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     *
     * @return
     * The voteCount
     */
    public Integer getVoteCount() {
        return voteCount;
    }

    /**
     *
     * @param voteCount
     * The vote_count
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    /**
     *
     * @return
     * The width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     *
     * @param width
     * The width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.aspectRatio);
        dest.writeString(this.filePath);
        dest.writeValue(this.height);
        dest.writeString(this.iso6391);
        dest.writeValue(this.voteAverage);
        dest.writeValue(this.voteCount);
        dest.writeValue(this.width);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.aspectRatio = (Double) in.readValue(Double.class.getClassLoader());
        this.filePath = in.readString();
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
        this.iso6391 = in.readString();
        this.voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        this.voteCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}