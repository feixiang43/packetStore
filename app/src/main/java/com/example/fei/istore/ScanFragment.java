package com.example.fei.istore;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fei on 2017/6/28.
 */

public class ScanFragment extends Fragment {
    private static final String TAG = "ScanFragment";
    private Application mApplication;
    private SerialPort mBarcodeScannerSerialPort;
    private InputStream mScannerInputStream;
    private String barcode = null;
    private Thread scanThread = null;
    private RelativeLayout mRelativeLayout;
    private Button mInputButton;

    @Override
    public void onCreate(Bundle onSavedInstanceStat){
        super.onCreate(onSavedInstanceStat);

        mApplication = (Application) getActivity().getApplication();
        try {
            mBarcodeScannerSerialPort = mApplication.getBarcodeScannerSerialPort();
            mScannerInputStream = mBarcodeScannerSerialPort.getInputStream();
        }catch (IOException e){
            e.printStackTrace();
        }

        scanThread = new Thread() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    int size;
                    try {
                        byte[] buffer = new byte[64];
                        if (mScannerInputStream == null) return;
                        size = mScannerInputStream.available();
                        if(size == 0){
                            continue;
                        }else{
                            size = mScannerInputStream.read(buffer);
                            if (size == CONST.BARCODE_LEN) {
                                Log.i(TAG, new String(buffer, 0, size) + "   " + size);
                                barcode = new String(buffer, 0, size);
                                Message msg = new Message();
                                msg.what = CONST.BARCODE;
                                msg.obj = barcode;
                                ((StoreActivity)getActivity()).getHandler().sendMessage(msg);
                            }
                        }
                        Thread.currentThread().sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle onSavedInstanceStat){
        View view = inflater.inflate(R.layout.fragment_scan, parent, false);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.layout_input);
        mInputButton = (Button) view.findViewById(R.id.button_barcode);
        mInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRelativeLayout.removeView(mInputButton);
                mRelativeLayout.addView(createView());
            }
        });
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        scanThread.start();
    }

    @Override
    public void onStop(){
        super.onStop();
        //Now scanner and printer use the same port,so we can't close the Stream.
        /*
        try {
            mScannerInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        scanThread.interrupt();

    }

    /*
    protected View createView(){
        EditText mEditText = new EditText(getContext());
        //mEditText.setId();
        mEditText.setHint("please input barcode");
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEditText.setHeight(50);
        mEditText.setWidth(200);
        return  mEditText;
    }
    */

    protected View createView(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService( getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_barcode_input, null);
        return view;
    }

}
