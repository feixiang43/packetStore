package com.example.fei.istore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by fei on 2017/3/18.
 */

public class ButtonFragment extends Fragment {
    public static String TAG = "ButtonFragment";
    private Printer mPrinter = null;
    private Lock mLock = null;

    @Override
    public void onCreate(Bundle onSavedInstanceStat){
        super.onCreate(onSavedInstanceStat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle onSavedInstanceStat){
       return  inflater.inflate(R.layout.fragment_imageview, parent, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPrinter = Printer.getPrinter(null);
        mLock = Lock.getLock(null);
        ImageView mPutBtn = (ImageView) getActivity().findViewById(R.id.putBag);
        ImageView mGetBtn = (ImageView) getActivity().findViewById(R.id.getBag);

        mPutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreBox box = BoxController.getBoxController().getEmptyDoor();
                if (box == null) {
                    Toast.makeText(getActivity(), "箱柜已满！", Toast.LENGTH_LONG).show();
                }else{
                    int deivceNo = 1;
                    int doorId = box.getDoorID();
                    String tmpBarcode = Util.encode(deivceNo, doorId, System.currentTimeMillis());
                    Toast.makeText(getActivity(), "open door " + doorId, Toast.LENGTH_LONG).show();
                    try {
                        mPrinter.init();
                        //Set center justify
                        mPrinter.setJustification(1);
                        mPrinter.setFontZoomInWidth();
                        mPrinter.printlnStr("欢迎光临");
                        mPrinter.setRelaivePos();
                        mPrinter.printBarcode(tmpBarcode, 73, 200, 2, 0, 2);
                        mPrinter.printlnStr(deivceNo + " 柜 " + doorId + " 箱 ");
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

        mGetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Fragment scanFragment = new ScanFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();
                tx.replace(R.id.main_fragment,scanFragment);
                tx.addToBackStack(null);
                tx.commit();
            }
        });

    }
}
