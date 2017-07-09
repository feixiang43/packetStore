package com.example.fei.istore;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final String TAG = "Util";
    private static final int PASSWORD[] = {
            0xB0, 0xA1, 0x92, 0x82, 0x73, 0x64,
            0x54, 0x45, 0x36, 0x26, 0x17, 0x08,
            0xF9, 0xE9, 0xDA, 0xCB, 0xBB, 0xAC,
            0x9D, 0x8D, 0x7E, 0x6F, 0x5F, 0x50,
            0x41, 0x31, 0x22, 0x13, 0x03, 0xF4,
            0xE5, 0xD6, 0xC6, 0xB7, 0xA8, 0x98,
            0x89, 0x7A, 0x6A, 0x5B, 0x4B, 0x3B,
            0x2C, 0x1D, 0x0D, 0xFE, 0xEF, 0xDF
    };

    public static long dateToStamp(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(str);

        return date.getTime();
    }

    public static String stampToDate(long s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    static String encode(int deviceId, int doorId, long seed) {
        int barcode[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        StringBuffer barcodeStr = new StringBuffer();
        Random rand  = new Random(seed);
        int rand_num = rand.nextInt(100000);

        barcode[5] = rand_num / 10000;
        barcode[6] = (rand_num % 10000) / 1000;
        barcode[7] = (rand_num %1000) / 100;
        barcode[8] = (rand_num %100) / 10;
        barcode[9] = rand_num % 10;

        barcode[0] = (deviceId + barcode[9]) / 10;
        barcode[1] = (deviceId + barcode[9]) % 10;

        barcode[2] = (PASSWORD[doorId - 1] + barcode[8]) / 100;
        barcode[3] = ((PASSWORD[doorId - 1] + barcode[8]) % 100) / 10;
        barcode[4] = (PASSWORD[doorId - 1] + barcode[8]) % 10;

        for (int i : barcode) {
            barcodeStr.append(i);
        }

        return barcodeStr.toString();
    }

    /*
    static Barcode decode(String input) {
        Log.i(TAG, "input is " + input);
        //"0916206318"
        if (10 != input.length()) {
            return  null;
        }
        int deviceId =  (input.charAt(0) - '0') * 10 + (input.charAt(1) - '0') - (input.charAt(9) - '0');
        int doorId = 0;
        for (int i = 0; i < PASSWORD.length; i++) {
            if (input.charAt(8) - '0' + PASSWORD[i] == (input.charAt(2) - '0') * 100 + (input.charAt(3) - '0') * 10 + (input.charAt(4) - '0')){
                doorId = i + 1;
                break;
            }
        }
        if (deviceId == 0 || doorId == 0) {
            return null;
        }

        Barcode barcode = new Barcode(deviceId, doorId);
        return barcode;
    }
    */

    static int decode(String input) {
        Log.i(TAG, "input is " + input);
        //"0916206318"
        if (10 != input.length()) {
            return  0;
        }
        int deviceId =  (input.charAt(0) - '0') * 10 + (input.charAt(1) - '0') - (input.charAt(9) - '0');
        int doorId = 0;
        for (int i = 0; i < PASSWORD.length; i++) {
            if (input.charAt(8) - '0' + PASSWORD[i] == (input.charAt(2) - '0') * 100 + (input.charAt(3) - '0') * 10 + (input.charAt(4) - '0')){
                doorId = i + 1;
                break;
            }
        }
        if (deviceId == 0 || doorId == 0) {
            return 0;
        }

        return (deviceId-1)*12+doorId;
    }

    /**
     * status 为了方便存储，这里使用字符串代替数组；
     * @param status
     * @return
     */
    static int getBox(String status) {
        int doorId = -1;
        for (int i = 0; i < status.length(); i++) {
            if (status.charAt(i) == '0') {
                doorId = i + 1;
                break;
            }
        }

        return doorId;
    }

    static String getPostStatus(String status, int doorId) {
        char arr[] = status.toCharArray();
        arr[doorId - 1] = '1';
        return String.valueOf(arr);
    }

    static int getFirstPostion(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
