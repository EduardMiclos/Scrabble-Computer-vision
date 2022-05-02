package com.andyedy.scrabble_computer_vision.Util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Locale;

public class Letter implements Comparable, Parcelable {
    private String value;
    private int frequency;

    public static ArrayList<Letter> getArrayFromString(String MyLetters) {
        ArrayList<Letter> letterList = new ArrayList<>();
        for (char c: MyLetters.toCharArray()) {
            boolean exists = false;
            for(Letter l: letterList) {
                //if (l.getValue().compareTo(String.format("%c", c)) == 0) {
                if(l.getValue().toUpperCase(Locale.ROOT).toCharArray()[0] == Character.toUpperCase(c)) {
                    l.frequency++;
                    exists = true;
                }
                if(exists) break;
            }
            if(!exists) letterList.add(new Letter(String.format("%c", c), 1));
        }

        return letterList;
    }

    public Letter(String value, int frequency) {
        this.value = value.toUpperCase(Locale.ROOT);
        this.frequency = frequency;
    }

    protected Letter(Parcel in) {
        value = in.readString();
        frequency = in.readInt();
    }

    public static final Creator<Letter> CREATOR = new Creator<Letter>() {
        @Override
        public Letter createFromParcel(Parcel in) {
            return new Letter(in);
        }

        @Override
        public Letter[] newArray(int size) {
            return new Letter[size];
        }
    };

    public String getValue() {
        return value;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(Object o) {
        Letter toLetter = (Letter)o;
        return value.compareTo(toLetter.getValue());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(value);
        parcel.writeInt(frequency);
    }
}
