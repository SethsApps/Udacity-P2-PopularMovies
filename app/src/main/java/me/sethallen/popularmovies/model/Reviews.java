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
        "page",
        "results",
        "total_pages",
        "total_results"
})
public class Reviews implements Parcelable, ITMDBResponse<Review> {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("page")
    private Integer page;
    @JsonProperty("results")
    private List<Review> results = new ArrayList<Review>();
    @JsonProperty("total_pages")
    private Integer totalPages;
    @JsonProperty("total_results")
    private Integer totalResults;

    /**
     * No args constructor for use in serialization
     *
     */
    public Reviews() {
    }

    /**
     *
     * @param id
     * @param results
     * @param totalResults
     * @param page
     * @param totalPages
     */
    public Reviews(Integer id, Integer page, List<Review> results, Integer totalPages, Integer totalResults) {
        this.id = id;
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

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

    public Reviews withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     *
     * @return
     * The page
     */
    @JsonProperty("page")
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    @JsonProperty("page")
    public void setPage(Integer page) {
        this.page = page;
    }

    public Reviews withPage(Integer page) {
        this.page = page;
        return this;
    }

    /**
     *
     * @return
     * The results
     */
    @JsonProperty("results")
    public List<Review> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    @JsonProperty("results")
    public void setResults(List<Review> results) {
        this.results = results;
    }

    public Reviews withResults(List<Review> results) {
        this.results = results;
        return this;
    }

    /**
     *
     * @return
     * The totalPages
     */
    @JsonProperty("total_pages")
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     * The total_pages
     */
    @JsonProperty("total_pages")
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Reviews withTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    /**
     *
     * @return
     * The totalResults
     */
    @JsonProperty("total_results")
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     *
     * @param totalResults
     * The total_results
     */
    @JsonProperty("total_results")
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Reviews withTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.page);
        dest.writeList(this.results);
        dest.writeValue(this.totalPages);
        dest.writeValue(this.totalResults);
    }

    protected Reviews(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.page = (Integer) in.readValue(Integer.class.getClassLoader());
        this.results = new ArrayList<Review>();
        in.readList(this.results, Review.class.getClassLoader());
        this.totalPages = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalResults = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel source) {
            return new Reviews(source);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
}