package me.sethallen.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Allense on 4/21/2016.
 */
public class ProductionCompany implements Parcelable
{
    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    private int id;
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public ProductionCompany() {
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

    protected ProductionCompany(Parcel in) {
        this.id   = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<ProductionCompany> CREATOR = new Creator<ProductionCompany>() {
        public ProductionCompany createFromParcel(Parcel source) {
            return new ProductionCompany(source);
        }

        public ProductionCompany[] newArray(int size) {
            return new ProductionCompany[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductionCompany otherItem = (ProductionCompany) o;

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