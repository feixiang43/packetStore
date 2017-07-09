package com.example.fei.istore;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * Created by donghui on 2017/4/23.
 */

public class Lock {
    private static Lock mLock;
    private static OutputStream mOutputStream = null;

    public static Lock getLock(OutputStream os){
        if(mLock == null) {
            try {
                mLock = new Lock(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mLock;
    }

    private Lock(OutputStream os) throws IOException {
        this.mOutputStream = os;
    }

    public void OpenLock(int deviceId, int lockId) throws IOException {
        if (deviceId < 1 || deviceId > 4 || lockId < 1 || lockId > 12) {
            return;
        }

        short data[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        data[0] = (new Integer(deviceId)).byteValue();

        if (lockId <= 8) {
            data[1] = (short) (0x01 << (lockId - 1));
//            data[1] = (new Integer(0x01 << (lockId - 1))).byteValue();
        } else {
            data[1] = (short) (0x01 << (lockId - 9));
//            data[2] = (new Integer(0x01 << (lockId - 9))).byteValue();
        }

        int checksum = XcDatVerification(data);
        byte checksumLow = (byte)checksum;
        byte checksumHigh = (byte) (checksum >> 8);

        mOutputStream.write(getByte(data));
        mOutputStream.write(checksumLow);
        mOutputStream.write(checksumHigh);
        mOutputStream.flush();
    }

    public void QueryLockStatus(int deviceId) throws IOException {
        if (deviceId < 1 || deviceId > 4) {
            return;
        }
        byte header = 0x10;
        header += deviceId;
        short data[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

//        short checksum = XcDatVerification(data);
//        byte checksumLow = (byte) checksum;
//        byte checksumHigh = (byte) (checksum >> 8);
//
//        mOutputStream.write(header);
////        mOutputStream.write(data);
//        mOutputStream.write(checksumLow);
//        mOutputStream.write(checksumHigh);
//        mOutputStream.flush();
    }

    public static int XcDatVerification(short[] frame) {
        int value = 0x0668;
        int len = frame.length;
        for (int i = 0; i < len; i++) {
            value ^= frame[i];
            for (int j = 0; j < 8; j++){
                if ((value & 0x0001) > 0 ) {
                    value = ((value>>1)^0x500a);
                } else {
                    value = (value>>1);
                }
            }
        }
        return value;
    }

    public static short getShort(byte[] b, int index) {
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
    }

    public static byte[] getByte(short[] input){
        int length = input.length;
        byte output[] = new byte[length];
        for (int i = 0; i<length;i++){
            output[i] = (byte) input[i];
        }
        return output;
    }
}
