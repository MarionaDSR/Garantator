package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class AbstractBaseModel implements Parcelable {
    protected String SEP = ":";

    private long id;
    private String name;

    protected AbstractBaseModel() {

    }

    protected AbstractBaseModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getName() + SEP + getId() + SEP + getName() ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }
}
