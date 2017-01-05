package com.example.rached.memory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.AbstractMap;
import java.util.Map;


public class MyCard implements Parcelable {
    Map.Entry<String,Integer> map_card;

    public MyCard(Map.Entry<String,Integer> entry) {
        map_card = entry;
    }

    public MyCard(String s, int i) {
        map_card = new AbstractMap.SimpleEntry<>(s,i);
    }

    private MyCard(Parcel in) {
        map_card = new AbstractMap.SimpleEntry<>(in.readString(),in.readInt());
    }

    public int describeContents() {
        return 0;
    }

    public int getValue(){
        return map_card.getValue();
    }

    public String getKey(){
        return map_card.getKey();
    }

    @Override
    public String toString() {
        return map_card.getKey() + ": " + map_card.getValue();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(map_card.getKey());
        out.writeInt(map_card.getValue());
    }

    public static final Parcelable.Creator<MyCard> CREATOR = new Parcelable.Creator<MyCard>() {
        public MyCard createFromParcel(Parcel in) {
            return new MyCard(in);
        }

        public MyCard[] newArray(int size) {
            return new MyCard[size];
        }
    };
}
