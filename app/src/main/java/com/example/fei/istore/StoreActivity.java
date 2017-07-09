package com.example.fei.istore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fei on 2017/6/19.
 */

public class StoreActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private Application mApplication;
    private SerialPort mPrinterSerialPort;
    private SerialPort mBarcodeScannerSerialPort;
    private SerialPort mLockSerialPort;
    private OutputStream mPrinterOutputStream;
    private InputStream mPrinterInputStream;
    private OutputStream mLockOutputStream;
    private InputStream mLockInputStream;
    private OutputStream mScannerOutputStream;
    private InputStream mScannerInputStream;
    private Printer mPrinter = null;
    private Lock mLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_main);
        BoxController.initBoxController(this);
        mApplication = (Application) getApplication();

        SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
        String printerPath = sp.getString("DEVICE_PRINTER", "");
        String scannerPath = sp.getString("DEVICE_BARCODESCANNER", "");
        String lockPath = sp.getString("DEVICE_LOCK", "");
        SharedPreferences.Editor editor = sp.edit();
        if (sp.getString("BAUDRATE", "").length() == 0)
            editor.putString("BAUDRATE", "9600");
        editor.putString("DEVICE_PRINTER", "/dev/ttyS3");
        editor.putString("DEVICE_BARCODESCANNER", "/dev/ttyS3");
        editor.putString("DEVICE_LOCK", "/dev/ttyS4");
        editor.commit();

        try {
            mPrinterSerialPort = mApplication.getPrinterSerialPort();
            mPrinterOutputStream = mPrinterSerialPort.getOutputStream();
            mPrinterInputStream = mPrinterSerialPort.getInputStream();

            mBarcodeScannerSerialPort = mApplication.getBarcodeScannerSerialPort();
            mScannerOutputStream = mBarcodeScannerSerialPort.getOutputStream();
            mScannerInputStream = mBarcodeScannerSerialPort.getInputStream();

            mLockSerialPort = mApplication.getLockSerialPort();
            mLockOutputStream = mLockSerialPort.getOutputStream();
            mLockInputStream = mLockSerialPort.getInputStream();

            mPrinter = Printer.getPrinter(mPrinterOutputStream);
            mLock = Lock.getLock(mLockOutputStream);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }

        CycleViewPager mCycle = (CycleViewPager)findViewById(R.id.cycle_page);
        List<Drawable> mList = new ArrayList<Drawable>();
        mList.add(getResources().getDrawable(R.drawable.yonna2));
        mList.add(getResources().getDrawable(R.drawable.yonna3));
        mList.add(getResources().getDrawable(R.drawable.yonna));
        mCycle.setDatasource(mList);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main_fragment);

        if(fragment == null)
        {
            fragment = new ButtonFragment();//here defined a fragment
            fm.beginTransaction().add(R.id.main_fragment,fragment).commit();
        }
    }

    public Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CONST.BARCODE:
                    Toast.makeText(StoreActivity.this, "获取 " + msg.obj, Toast.LENGTH_LONG).show();
                    StoreBox box = BoxController.getBoxController().checkBarcode(msg.obj.toString());
                    if (null != box) {
                        Log.i(TAG, "开门" + box.getDeviceID()  + "柜" + box.getDoorID() + "箱");
                        Toast.makeText(StoreActivity.this, "开门" + box.getDeviceID() + "柜" + box.getDoorID() + "箱", Toast.LENGTH_LONG).show();
                        try {
                            mLock.OpenLock(box.getDeviceID(), box.getDoorID());
                            BoxController.getBoxController().cleanSpecifiedBox((box.getDeviceID()-1)*12 + box.getDoorID());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(StoreActivity.this, "wrong barcode!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case CONST.LOCK:
                    Toast.makeText(StoreActivity.this, "获取 " + msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };

    public Handler getHandler(){
        return mHandler;
    }
}
