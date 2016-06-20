package me.sethallen.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Genre implements Parcelable
{
    private int id;
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public Genre() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
    }

    protected Genre(Parcel in) {
        this.id   = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        public Genre createFromParcel(Parcel source) {
            return new Genre(source);
        }

        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genre otherItem = (Genre) o;

        if (id != otherItem.id) return false;
        return name != null ? name.equals(otherItem.name) : otherItem.name == null;

    }

    @Override
    public int hashCode() {
        int result = id ^ (id >>> 32);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}