package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Warranty extends AbstractBaseModel {
    private Product product;
    private long productId;
    private Date startDate;

    public Warranty() {

    }

    private Warranty(Parcel in) {
        super(in);
        product = in.readParcelable(Product.class.getClassLoader());
        startDate = readDate(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(product, flags);
        writeDate(dest, startDate);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public static final Parcelable.Creator<Warranty> CREATOR = new Parcelable.Creator<Warranty>() {
        public Warranty createFromParcel(Parcel in) {
            return new Warranty(in);
        }

        public Warranty[] newArray(int size) {
            return new Warranty[size];
        }
    };
}
