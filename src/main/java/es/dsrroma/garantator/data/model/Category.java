package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category extends AbstractBaseModel {

    public Category() {

    }

    private Category(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
