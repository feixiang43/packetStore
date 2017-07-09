package com.example.fei.istore;

import java.util.Objects;

/**
 * Created by fei on 2017/5/8.
 */

public class StoreBox implements Cloneable{

    private int deviceID;
    private int doorID;
    private String password;
    private boolean empty;

    public StoreBox(int mDeviceID, int mDoorID){
        this.deviceID = mDeviceID;
        this.doorID = mDoorID;
        this.password = null;
        this.empty = true;
    }

    @Override
    public Object clone(){
        StoreBox cloneBox = null;
        try{
            cloneBox = (StoreBox)super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return cloneBox;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public int getDoorID() {
        return doorID;
    }

    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
