package gridentertainment.net.fridgeit.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class InventoryItem implements Parcelable {

    public String name;
    public String quantity;
    public String expiryDate;
    public String price;


    public InventoryItem(Parcel in) {
        name = in.readString();
        quantity = in.readString();
        expiryDate = in.readString();
        price = in.readString();

    }

    public static final Creator<InventoryItem> CREATOR = new Creator<InventoryItem>() {
        @Override
        public InventoryItem createFromParcel(Parcel in) {
            return new InventoryItem(in);
        }

        @Override
        public InventoryItem[] newArray(int size) {
            return new InventoryItem[size];
        }
    };

    public InventoryItem() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expDate) {
        this.expiryDate = expDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(quantity);
        parcel.writeString(expiryDate);
        parcel.writeString(price);
    }
}
