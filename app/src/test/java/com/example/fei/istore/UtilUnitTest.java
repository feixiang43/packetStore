package com.example.fei.istore;

import org.junit.Test;
import com.example.fei.istore.Util;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

//    @Test
//    public void date_test() throws Exception {
//        long t = System.currentTimeMillis();
//        System.out.println(Util.stampToDate(t));
////        assertEquals(t, Util.dateToStamp(Util.stampToDate(t)));
//    }

    @Test
    public void encode_test() throws Exception {
        assertEquals("0916206318", Util.encode(1, 2, Util.dateToStamp("2017-03-30 17:27:00")));
    }

    /*
    @Test
    public void decode_test() throws Exception {
        String barcode = "0916206318";

        assertEquals(1, Util.decode(barcode).deviceId);
        assertEquals(2, Util.decode(barcode).doorId);
    }
    */

    @Test
    public void getbox_test() throws Exception {
        String status = "111000100020000";
        byte data[] = {0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
//        System.out.println(Lock.XcDatVerification(data));
        assertEquals(4, Util.getBox(status));
    }

    @Test
    public void getpostbox_test() throws Exception {
        String status = "111000100020000";
        System.out.println(Util.getPostStatus(status, 5));
//        assertEquals(4, Util.getBox(status));
    }

    @Test
    public void checksum_test() throws Exception {
        short data[] = {0x01, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00};
        int ret = Lock.XcDatVerification(data);
//        System.out.println(ret);
        System.out.println("123");
    }

}