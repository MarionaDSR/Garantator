package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Warranty extends AbstractModel {
    private Product product;
    private Date startDate;

    public Warranty() {

    }

    private Warranty(Parcel in) {
        super(in);
        product = in.readParcelable(Product.class.getClassLoader());
        long time = in.readLong();
        if (time > 0) {
            startDate = new Date(in.readLong());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(product, flags);
        long time = 0;
        if (startDate != null) {
            time = startDate.getTime();
        }
        dest.writeLong(time);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
