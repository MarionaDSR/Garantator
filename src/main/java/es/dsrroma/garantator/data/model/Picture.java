package es.dsrroma.garantator.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture extends AbstractBaseIdModel {

    private String fileName;
    private Warranty warranty;
    private long warrantyId;
    private int position;

    public Picture() {

    }

    private Picture(Parcel in) {
        super(in);
        fileName = in.readString();
        warranty = in.readParcelable(Warranty.class.getClassLoader());
        position = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(fileName);
        dest.writeParcelable(warranty, flags);
        dest.writeInt(position);
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Warranty getWarranty() {
        return warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }

    public long getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(long warrantyId) {
        this.warrantyId = warrantyId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    @Override
    public String toString() {
        return super.toString() + SEP + getFileName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Picture) {
            return fileName.equals(((Picture) o).getFileName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }
}