package com.example.fei.istore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int BARCODE = 1;
    private static final int LOCK = 2;
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
    private Button storeBtn = null;
    private Button scanBtn = null;
    private Button printBtn = null;
    private Button openBoxBtn = null;
    private Button cleanBtn = null;
    private EditText barcodeEdit = null;
    private EditText printEdit = null;
    private EditText deviceIdEdit = null;
    private EditText doorIdEdit = null;
    private Spinner printerSpinner = null;
    private Spinner scannerSpinner = null;
    private Spinner lockSpinner = null;
    private String barcode = null;
    private SerialPortFinder mSerialPortFinder;
    private ArrayAdapter<String> adapter;
    private Printer mPrinter = null;
    private Lock mLock = null;
    private int deviceId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storeBtn = (Button) findViewById(R.id.action_store);
        scanBtn = (Button) findViewById(R.id.action_barscanner);
        printBtn = (Button) findViewById(R.id.action_print);
        openBoxBtn = (Button) findViewById(R.id.action_open);
        cleanBtn = (Button) findViewById(R.id.action_clear_status);

        barcodeEdit = (EditText) findViewById(R.id.data_barcode);
        printEdit = (EditText) findViewById(R.id.data_print);
        deviceIdEdit = (EditText) findViewById(R.id.data_open_device);
        doorIdEdit = (EditText) findViewById(R.id.data_open_door);

        printerSpinner = (Spinner) findViewById(R.id.spinner_printer);
        scannerSpinner = (Spinner) findViewById(R.id.spinner_scanner);
        lockSpinner = (Spinner) findViewById(R.id.spinner_lock);

        mApplication = (Application) getApplication();
        mSerialPortFinder = mApplication.mSerialPortFinder;
        String[] entries = mSerialPortFinder.getAllDevices();
        final String[] entryValues = mSerialPortFinder.getAllDevicesPath();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, entryValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        printerSpinner.setAdapter(adapter);
        scannerSpinner.setAdapter(adapter);
        lockSpinner.setAdapter(adapter);

        BoxController.initBoxController(this);

        printerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("DEVICE_PRINTER", entryValues[position]);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scannerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("DEVICE_BARCODESCANNER", entryValues[position]);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("DEVICE_LOCK", entryValues[position]);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
        String printerPath = sp.getString("DEVICE_PRINTER", "");
        String scannerPath = sp.getString("DEVICE_BARCODESCANNER", "");
        String lockPath = sp.getString("DEVICE_LOCK", "");
        SharedPreferences.Editor editor = sp.edit();

        if (sp.getString("BAUDRATE", "").length() == 0)
            editor.putString("BAUDRATE", "9600");

        /*
        if (printerPath.length() == 0 && entryValues.length > 0) {
            editor.putString("DEVICE_PRINTER", entryValues[0]);
        }else {
            printerSpinner.setSelection(Util.getFirstPostion(entryValues, printerPath));
        }

        if (scannerPath.length() == 0 && entryValues.length > 0) {
            editor.putString("DEVICE_BARCODESCANNER", entryValues[0]);
        }else {
            scannerSpinner.setSelection(Util.getFirstPostion(entryValues, scannerPath));
        }

        if (lockPath.length() == 0 && entryValues.length > 0) {
            editor.putString("DEVICE_LOCK", entryValues[0]);
        }else {
            lockSpinner.setSelection(Util.getFirstPostion(entryValues, lockPath));
        }
        */

        editor.putString("DEVICE_PRINTER", "/dev/ttyS3");
        editor.putString("DEVICE_BARCODESCANNER", "/dev/ttyS3");
        editor.putString("DEVICE_LOCK", "/dev/ttyS4");
        editor.commit();

        for (String entry:entries) {
            Log.i(TAG, entry);
        }

        for (String entryValue:entryValues) {
            Log.i(TAG, entryValue);
        }

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
        SharedPreferences preference = getSharedPreferences("doors_status", MODE_PRIVATE);
        if (preference.getString("status", "").length() == 0) {
            SharedPreferences.Editor statueEditor = preference.edit();
            statueEditor.putString("status", "000000000000");
            statueEditor.commit();
        }

        if (preference.getInt("deviceId", 0) == 0) {
            SharedPreferences.Editor statueEditor = preference.edit();
            statueEditor.putInt("deviceId", 1);
            statueEditor.commit();
        }
        closeBar();

        /*
        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("doors_status", MODE_PRIVATE);
                String status = sp.getString("status", "");
                deviceId = sp.getInt("deviceId", 0);
                if (status.length() != 0) {
                    int doorId = Util.getBox(status);
                    String tmpBarcode = Util.encode(deviceId, doorId, System.currentTimeMillis());
                    Toast.makeText(MainActivity.this, "open door " + doorId, Toast.LENGTH_LONG).show();
                    try {
                        mPrinter.init();
                        mPrinter.printBarcode(tmpBarcode, 73, 200, 3, 0, 2);
                        mPrinter.feedAndCut();
                        Log.i(TAG, "开锁" + deviceId + "柜" + doorId + "箱");
                        Log.i(TAG, tmpBarcode);
                        mLock.OpenLock(deviceId, doorId);
                        SharedPreferences.Editor tmpEditor = sp.edit();
                        tmpEditor.putString("status", Util.getPostStatus(status, doorId));
                        tmpEditor.commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        */

        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                StoreBox box = BoxController.getBoxController().getEmptyDoor();
                if (box == null) {
                    Toast.makeText(MainActivity.this, "Store box if full", Toast.LENGTH_LONG).show();
                }else{
                    //int deivceNo = box.getDeviceID();
                    //int doorId = box.getDoorID();
                    int deivceNo = 1;
                    int doorId = box.getDoorID();
                    String tmpBarcode = Util.encode(deivceNo, doorId, System.currentTimeMillis());
                    Toast.makeText(MainActivity.this, "open door " + doorId, Toast.LENGTH_LONG).show();
                    try {
                        mPrinter.init();
                        mPrinter.printBarcode(tmpBarcode, 73, 200, 2, 0, 2);
                        mPrinter.feedAndCut();
                        Log.i(TAG, "开锁" + deivceNo + "柜" + doorId + "箱");
                        Log.i(TAG, tmpBarcode);
                        box.setPassword(tmpBarcode);
                        box.setEmpty(false);
                        if(BoxController.getBoxController().storeNewDoor(box)){
                            mLock.OpenLock(deivceNo, doorId);
                        }
                        mLock.OpenLock(deivceNo,doorId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = BARCODE;
                msg.obj = barcodeEdit.getText().toString();
                handler.sendMessage(msg);
            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPrinter.init();
                    mPrinter.printBarcode(printEdit.getText().toString(), 73, 200, 2, 0, 2);
                    mPrinter.feedAndCut();
                    Log.i(TAG, "打印：" + printEdit.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        openBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i(TAG, "开锁 " + deviceIdEdit.getText() + "柜" + doorIdEdit.getText() + "箱");
                    mLock.OpenLock(Integer.decode(deviceIdEdit.getText().toString()), Integer.decode(doorIdEdit.getText().toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /*
        cleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor statueEditor = getSharedPreferences("doors_status", MODE_PRIVATE).edit();
                statueEditor.putString("status", "000000000000");
                statueEditor.commit();
            }
        });
        */


        cleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxController.getBoxController().cleanAllBox();
            }
        });

        new Thread() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    int size;
                    try {
                        byte[] buffer = new byte[64];
                        if (mScannerInputStream == null) return;
                        size = mScannerInputStream.read(buffer);
                        if (size > 0) {
                            Log.i(TAG, new String(buffer, 0, size) + "   " + size);
                            barcode = new String(buffer, 0, size);
                            Message msg = new Message();
                            msg.what = BARCODE;
                            msg.obj = barcode;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }.start();
    }

    /*
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case BARCODE:
                    Toast.makeText(MainActivity.this, "获取 " + msg.obj, Toast.LENGTH_LONG).show();
                    Barcode barcode = Util.decode(msg.obj.toString());
                    if (null != barcode) {
                        Log.i(TAG, "开门" + barcode.deviceId  + "柜" + barcode.doorId + "箱");
                        Toast.makeText(MainActivity.this, "开门" + barcode.deviceId  + "柜" + barcode.doorId + "箱", Toast.LENGTH_LONG).show();
                        try {
                            mLock.OpenLock(barcode.deviceId, barcode.doorId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case LOCK:
                    Toast.makeText(MainActivity.this, "获取 " + msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };
    */

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case BARCODE:
                    Toast.makeText(MainActivity.this, "获取 " + msg.obj, Toast.LENGTH_LONG).show();
                    StoreBox box = BoxController.getBoxController().checkBarcode(msg.obj.toString());
                    if (null != box) {
                        Log.i(TAG, "开门" + box.getDeviceID()  + "柜" + box.getDoorID() + "箱");
                        Toast.makeText(MainActivity.this, "开门" + box.getDeviceID() + "柜" + box.getDoorID() + "箱", Toast.LENGTH_LONG).show();
                        try {
                            mLock.OpenLock(box.getDeviceID(), box.getDoorID());
                            BoxController.getBoxController().cleanSpecifiedBox((box.getDeviceID()-1)*12 + box.getDoorID());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "wrong barcode!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case LOCK:
                    Toast.makeText(MainActivity.this, "获取 " + msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };

    /**
     * 关闭Android导航栏，实现全屏
     */
    private void closeBar() {
        try {
            Log.i(TAG, "close bar");
            Runtime.getRuntime().exec("su -c service call activity 42 s16 com.android.systemui");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示导航栏
     */
    public static void showBar() {
        try {
            String command;
            command = "su -c am startservice -n com.android.systemui/.SystemUIService ";
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPrinterSerialPort) {
            mPrinterSerialPort.close();
        }
        if (null != mPrinterInputStream) {
            try {
                mPrinterInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != mPrinterOutputStream) {
            try {
                mPrinterOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (null != mBarcodeScannerSerialPort) {
            mBarcodeScannerSerialPort.close();
        }
        if (null != mScannerInputStream) {
            try {
                mScannerInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != mScannerOutputStream) {
            try {
                mScannerOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (null != mLockSerialPort) {
            mLockSerialPort.close();
        }
        if (null != mLockInputStream) {
            try {
                mLockInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != mLockOutputStream) {
            try {
                mLockOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
