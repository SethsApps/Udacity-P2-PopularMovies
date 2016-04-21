package me.sethallen.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Allense on 4/21/2016.
 */
public class ProductionCountry implements Parcelable
{
    private String iso_3166_1;
    public String getIso31661() { return this.iso_3166_1; }
    public void setIso31661(String iso_3166_1) { this.iso_3166_1 = iso_3166_1; }

    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public ProductionCountry() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iso_3166_1);
        dest.writeString(this.name);
    }

    protected ProductionCountry(Parcel in) {
        this.iso_3166_1 = in.readString();
        this.name       = in.readString();
    }

    public static final Parcelable.Creator<ProductionCountry> CREATOR = new Parcelable.Creator<ProductionCountry>() {
        public ProductionCountry createFromParcel(Parcel source) {
            return new ProductionCountry(source);
        }

        public ProductionCountry[] newArray(int size) {
            return new ProductionCountry[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductionCountry otherItem = (ProductionCountry) o;

        if (!iso_3166_1.equals(otherItem.iso_3166_1)) return false;
        return name != null ? name.equals(otherItem.name) : otherItem.name == null;

    }

    @Override
    public int hashCode() {
        int result = iso_3166_1 != null ? iso_3166_1.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}