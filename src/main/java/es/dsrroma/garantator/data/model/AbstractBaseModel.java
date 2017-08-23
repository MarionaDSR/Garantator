package es.dsrroma.garantator.data.model;

import android.os.Parcel;

public abstract class AbstractBaseModel extends AbstractBaseIdModel {

    private String name;


    protected AbstractBaseModel() {

    }

    protected AbstractBaseModel(Parcel in) {
        super(in);
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

     @Override
    public String toString() {
        return super.toString() + SEP + getName() ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
