package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product extends AbstractBaseModel {

    private Category category;
    private Brand brand;
    private String model;
    private String serialNumber;

    private Product(Parcel in) {
        super(in);
        category = in.readParcelable(Category.class.getClassLoader());
        brand = in.readParcelable(Brand.class.getClassLoader());
        model = in.readString();
        serialNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(category, flags);
        dest.writeParcelable(brand, flags);
        dest.writeString(model);
        dest.writeString(serialNumber);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return super.toString() + SEP + getSerialNumber();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
