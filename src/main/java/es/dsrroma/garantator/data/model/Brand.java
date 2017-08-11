package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Brand extends AbstractBaseModel {

    public Brand() {

    }

    private Brand(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>() {
        public Brand createFromParcel(Parcel in) {
            return new Brand(in);
        }

        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };
}
