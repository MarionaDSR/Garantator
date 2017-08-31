package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public abstract class AbstractBaseIdModel implements Parcelable, Cloneable {
    protected final String SEP = ":";

    private long id;
    private Date createdAt;
    private Date updatedAt;

    protected AbstractBaseIdModel() {

    }

    protected AbstractBaseIdModel(Parcel in) {
        id = in.readLong();
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

    protected boolean readBoolean(Parcel in) {
        return in.readByte() != 0;
    }

    protected byte writeBoolean(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return getClass().getSimpleName() + SEP + getId() ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        writeDate(dest, createdAt);
        writeDate(dest, updatedAt);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
