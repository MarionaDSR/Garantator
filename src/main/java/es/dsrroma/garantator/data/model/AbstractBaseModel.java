package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public abstract class AbstractBaseModel implements Parcelable, Cloneable {
    protected String SEP = ":";

    private long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;

    protected AbstractBaseModel() {

    }

    protected AbstractBaseModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        createdAt = readDate(in);
        updatedAt = readDate(in);
    }


    protected Date readDate(Parcel in) {
        long time = in.readLong();
        if (time > 0) {
            return new Date(time);
        }
        return null;
    }

    protected void writeDate(Parcel dest, Date date) {
        long time = 0;
        if (date != null) {
            time = date.getTime();
        }
        dest.writeLong(time);
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + SEP + getId() + SEP + getName() ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        writeDate(dest, createdAt);
        writeDate(dest, updatedAt);

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
