package com.example.fei.istore;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.logging.StreamHandler;

/**
 * Created by fei on 2017/5/8.
 */

public class BoxController {
    private static BoxController mBoxController;
    private StoreDBHepler mStoreDB;
    private int mTotalDevice;

    private BoxController(Context context){
        //TODO
        mStoreDB = new StoreDBHepler(context,"store.db", null, 1);
        mTotalDevice = 1;
    }

    public static void initBoxController(Context context){
        if(mBoxController == null){
            mBoxController = new BoxController(context);
        }
        return;
    }

    public static BoxController getBoxController(){
        return mBoxController;
    }

    public StoreBox getEmptyDoor(){
        Cursor cur = mStoreDB.getEmptyRow();
        cur.moveToFirst();
        if (cur.getCount() != 0) {
            return new StoreBox(mTotalDevice, cur.getInt(0));
        }
        return null;
    }

    public StoreBox getSpecifiedDoor(int index){
        if( mTotalDevice*12 < index){
            return null;
        }else {
            Cursor cur = mStoreDB.selectByIndex(index);
            cur.moveToFirst();
            StoreBox box =  new StoreBox(mTotalDevice, cur.getInt(0));
            box.setPassword(cur.getString(1));
            box.setEmpty((cur.getInt(2)==1)?true:false);
            return box;
        }
    }

    public boolean storeNewDoor(StoreBox box){
        int index = (box.getDeviceID()-1)*12 + box.getDoorID();
        Cursor cur = mStoreDB.selectByIndex(index);
        cur.moveToFirst();
        if(cur.getInt(2) == 0){
            return false;
        }
        mStoreDB.updateByIndex(index, box.getPassword(), 0);
        return true;
    }

    public void cleanAllBox(){
        mStoreDB.cleanAll();
    }

    public StoreBox checkBarcode(String input) {
        input = input.replaceAll("\r|\n", "");
        int index = Util.decode(input);
        if (index != 0) {
            StoreBox sBox = getSpecifiedDoor(index);
            if (sBox != null) {
                if (sBox.getPassword().equals(input) && !sBox.isEmpty()) {
                    return sBox;
                }
            }
        }
        return null;
    }

    public boolean cleanSpecifiedBox(int index){
        if(index > mTotalDevice*12){
            return false;
        }
        mStoreDB.cleanByIndex(index);
        return true;
    }
}
