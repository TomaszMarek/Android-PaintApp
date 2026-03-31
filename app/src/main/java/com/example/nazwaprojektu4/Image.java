package com.example.nazwaprojektu4;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable { //paczka do przesylania, musi byc creator
    public long id;
    public String name;

    public Image(long id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Image(Parcel in) { //deserializuje czyli odczytuje
        id = in.readLong();
        name = in.readString();
    }


    public static final Creator<Image> CREATOR = new Creator<Image>() {
        //tworzenie nowych instancji z paczek
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) { //zapisuje do paczki
        parcel.writeLong(id);
        parcel.writeString(name);
    }
}
