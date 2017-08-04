package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class AbstractModel implements Parcelable {
    protected String SEP = ":";

    private int id;
    private String name;

    protected AbstractModel() {

    }

    protected AbstractModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        dest.writeInt(id);
        dest.writeString(name);
    }
}
