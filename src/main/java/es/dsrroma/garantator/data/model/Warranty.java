package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static es.dsrroma.garantator.utils.MyStringUtils.isNotEmpty;

public class Warranty extends AbstractBaseModel {
    private Product product;
    private long productId;
    private Date startDate;
    private Date endDate;
    private int length;
    private String period;
    private List<Picture> pictures = new ArrayList<>();
    private List<Picture> picturesToDelete = new ArrayList<>();

    public Warranty() {
    }

    private Warranty(Parcel in) {
        super(in);
        product = in.readParcelable(Product.class.getClassLoader());
        startDate = readDate(in);
        endDate = readDate(in);
        length = in.readInt();
        period = in.readString();
        in.readTypedList(pictures, Picture.CREATOR);
        in.readTypedList(picturesToDelete, Picture.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(product, flags);
        writeDate(dest, startDate);
        writeDate(dest, endDate);
        dest.writeInt(length);
        dest.writeString(period);
        dest.writeTypedList(pictures);
        dest.writeTypedList(picturesToDelete);
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

    public void setStartDate(long time) {
        if (time != 0) {
            this.startDate = new Date(time);
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(long time) {
        if (time != 0) {
            this.endDate = new Date(time);
        }
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setLength(String s) {
        if (isNotEmpty(s)) {
            setLength(Integer.parseInt(s));
        } else {
            setLength(0);
        }
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
        for (Picture picture: pictures) {
            picture.setWarranty(this);
        }
    }

    public void addPicture(Picture picture) {
        if (pictures == null) {
            pictures = new ArrayList<Picture>();
        }
        picture.setToCreate(true);
        pictures.add(picture);
        picture.setWarranty(this);
    }

    public int getPicturesSize() {
        int res = 0;
        if (pictures != null) {
            res = pictures.size();
        }
        return res;
    }

    public List<Picture> getPicturesToDelete() {
        return picturesToDelete;
    }

    public void setPicturesToDelete(List<Picture> picturesToDelete) {
        this.picturesToDelete = picturesToDelete;
    }

    public void removePicture(Picture picture) {
        picturesToDelete.add(picture);
        pictures.remove(picture);
    }

    public static final Parcelable.Creator<Warranty> CREATOR = new Parcelable.Creator<Warranty>() {
        public Warranty createFromParcel(Parcel in) {
            return new Warranty(in);
        }

        public Warranty[] newArray(int size) {
            return new Warranty[size];
        }
    };

    public Object clone() throws CloneNotSupportedException {
        Warranty w = (Warranty)super.clone();
        w.setProduct((Product)getProduct().clone());
        List<Picture> pictures = getPictures();
        List<Picture> clonedPictures = new ArrayList<>();
        for (Picture picture : pictures) {
            clonedPictures.add((Picture)picture.clone());
        }
        w.setPictures(clonedPictures);
        List<Picture> picturesToDelete = getPicturesToDelete();
        List<Picture> clonedPicturesToDelete = new ArrayList<>();
        for (Picture picture : picturesToDelete) {
            clonedPicturesToDelete.add((Picture)picture.clone());
        }
        w.setPicturesToDelete(clonedPicturesToDelete);
        return w;
    }
}
