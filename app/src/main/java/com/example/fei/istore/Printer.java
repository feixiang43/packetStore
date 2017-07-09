package com.example.fei.istore;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

public class Printer {
    private static Printer printer;
    private static OutputStreamWriter mWriter = null;

    public static final byte ESC = 27;
    public static final byte FS = 28;
    public static final byte GS = 29;
    public static final byte DLE = 16;
    public static final byte EOT = 4;
    public static final byte ENQ = 5;
    public static final byte SP = 32;
    public static final byte HT = 9;
    public static final byte LF = 10;
    public static final byte CR = 13;
    public static final byte FF = 12;
    public static final byte CAN = 24;
    /**
     * CodePage table
     */
    public static class CodePage {
        public static final byte PC437       = 0;
        public static final byte KATAKANA    = 1;
        public static final byte PC850       = 2;
        public static final byte PC860       = 3;
        public static final byte PC863       = 4;
        public static final byte PC865       = 5;
        public static final byte WPC1252     = 16;
        public static final byte PC866       = 17;
        public static final byte PC852       = 18;
        public static final byte PC858       = 19;
    }

    /**
     * BarCode table
     */
    public static class BarCode {
        public static final byte UPC_A       = 0;
        public static final byte UPC_E       = 1;
        public static final byte EAN13       = 2;
        public static final byte EAN8        = 3;
        public static final byte CODE39      = 4;
        public static final byte ITF         = 5;
        public static final byte NW7         = 6;
        public static final byte CODE93      = 72;
        public static final byte CODE128     = 73;
    }

    /**
     * init ESC POS printer device
     *
     * @param os
     *
     * @throws IOException
     */
    public static Printer getPrinter(OutputStream os){
        if(printer == null) {
            try {
                printer = new Printer(os);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidParameterException e) {
                e.printStackTrace();
            }
        }
        return printer;
    }


    /**
     * init ESC POS printer device
     *
     * @param os
     *
     * @throws IOException
     */
    private Printer(OutputStream os) throws IOException {
            try {
                mWriter = new OutputStreamWriter(os, "GBK");
                //init printer
                mWriter.write(0x1B);
                mWriter.write("@");
                mWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    public void init() throws IOException {
        mWriter.write(0x1B);
        mWriter.write("@");
        mWriter.flush();
    }


    public void barCodeInit() throws IOException {
        mWriter.write(0x1B);
        mWriter.write("@");
        mWriter.write(0x1B);
        mWriter.write(0x50);
        mWriter.write(0x00);
        mWriter.write(0x30);
        mWriter.write(0x02);
        mWriter.write(0x60);
        mWriter.write(0x01);
        mWriter.write(0x0A);
        mWriter.write(0x3f);

        mWriter.flush();
    }

    public void setRelaivePos() throws IOException {
        mWriter.write(0x1B);
        mWriter.write(0x5C);
        mWriter.write(5);
        mWriter.write(0);
        mWriter.flush();
    }
    /**
     * resets all mWriter settings to default
     *
     */
    public void resetToDefault() throws IOException {
        setInverse(false);
        setBold(false);
        setFontDefault();
        setUnderline(0);
        setJustification(0);
        mWriter.flush();
    }

    /**
     * Sets bold
     *
     */
    public void setBold(Boolean bool) throws IOException {
        mWriter.write(0x1B);
        mWriter.write("E");
        mWriter.write((int) (bool ? 1 : 0));
        mWriter.flush();
    }

    /**
     *
     * @throws IOException
     */
    public void setFontZoomIn() throws IOException {
		/* 横向纵向都放大一倍 */
        mWriter.write(0x1c);
        mWriter.write(0x21);
        mWriter.write(12);
        mWriter.write(0x1b);
        mWriter.write(0x21);
        mWriter.write(12);
        mWriter.flush();
    }

    /**
     *
     * @throws IOException
     */
    public void setFontZoomInWidth() throws IOException {
		/* 横向放大一倍 */
        mWriter.write(0x1c);
        mWriter.write(0x21);
        mWriter.write(4);
        mWriter.write(0x1b);
        mWriter.write(0x21);
        mWriter.write(4);
        mWriter.flush();
    }

    /**
     *
     * @throws IOException
     */
    public void setFontZoomInHeight() throws IOException {
		/* 纵向放大一倍 */
        mWriter.write(0x1c);
        mWriter.write(0x21);
        mWriter.write(8);
        mWriter.write(0x1b);
        mWriter.write(0x21);
        mWriter.write(8);
        mWriter.flush();
    }

    /**
     *
     * @throws IOException
     */
    public void setFontDefault() throws IOException {

        mWriter.write(0x1c);
        mWriter.write(0x21);
        mWriter.write(1);
        mWriter.flush();
    }

    /**
     * Sets white on black printing
     *
     */
    public void setInverse(Boolean bool) throws IOException {
        mWriter.write(0x1D);
        mWriter.write("B");
        mWriter.write((int) (bool ? 1 : 0));
        mWriter.flush();
    }

    /**
     * Sets underline and weight
     *
     * @param val
     *            0 = no underline. 1 = single weight underline. 2 = double
     *            weight underline.
     *
     */
    public void setUnderline(int val) throws IOException {
        mWriter.write(0x1B);
        mWriter.write("-");
        mWriter.write(val);
        mWriter.flush();
    }

    /**
     * Sets left, center, right justification
     *
     * @param val
     *            0 = left justify. 1 = center justify. 2 = right justify.
     *
     */
    public void setJustification(int val) throws IOException {
        mWriter.write(0x1B);
        mWriter.write("a");
        mWriter.write(val);
        mWriter.flush();
    }

    /**
     *
     * @param str
     * @throws IOException
     */
    public void printStr(String str) throws IOException {
        mWriter.write(str);
        mWriter.flush();
    }

    /**
     *
     * @param str
     * @throws IOException
     */
    public void printlnStr(String str) throws IOException {
        mWriter.write(str + "\n");
        mWriter.flush();
    }

    /**
     * print String value of obj
     *
     * @param obj
     * @throws IOException
     */
    public void printObj(Object obj) throws IOException {
        mWriter.write(obj.toString());
        mWriter.flush();
    }

    public void printlnObj(Object obj) throws IOException {
        mWriter.write(obj.toString() + "\n");
        mWriter.flush();
    }

    /**
     * print String value of element, and separator fill the gap between
     * elements
     *
     * @param lst
     * @param separator
     *            for example, use "\n" to break line
     * @throws IOException
     */
    public void printLst(List lst, String separator) throws IOException {
        for (Object o : lst) {
            this.printObj(o);
            this.printStr(separator);

        }
    }

    /**
     * print String value of element, and separator fill the gap between
     * elements
     *
     * @param map
     * @param kvSeparator
     * @param itemSeparator
     * @throws IOException
     */
    public void printMap(Map map, String kvSeparator, String itemSeparator)
            throws IOException {
        for (Object key : map.keySet()) {
            this.printObj(key);
            this.printStr(kvSeparator);
            this.printObj(map.get(key));
            this.printStr(itemSeparator);

        }

    }

    /**
     * Encode and print QR code
     *
     * @param str
     *            String to be encoded in QR.
     * @param errCorrect
     *            The degree of error correction. (48 <= n <= 51) 48 = level L /
     *            7% recovery capacity. 49 = level M / 15% recovery capacity. 50
     *            = level Q / 25% recovery capacity. 51 = level H / 30% recovery
     *            capacity.
     *
     * @param moduleSize
     *            The size of the QR module (pixel) in dots. The QR code will
     *            not print if it is too big. Try setting this low and
     *            experiment in making it larger.
     */
    public void printQR(String str, int errCorrect, int moduleSize)
            throws IOException {
        // save data function 80
        mWriter.write(0x1D);// init
        mWriter.write("(k");// adjust height of barcode
        mWriter.write(str.length() + 3); // pl
        mWriter.write(0); // ph
        mWriter.write(49); // cn
        mWriter.write(80); // fn
        mWriter.write(48); //
        mWriter.write(str);

        // error correction function 69
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(3); // pl
        mWriter.write(0); // ph
        mWriter.write(49); // cn
        mWriter.write(69); // fn
        mWriter.write(errCorrect); // 48<= n <= 51

        // size function 67
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(3);
        mWriter.write(0);
        mWriter.write(49);
        mWriter.write(67);
        mWriter.write(moduleSize);// 1<= n <= 16

        // print function 81
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(3); // pl
        mWriter.write(0); // ph
        mWriter.write(49); // cn
        mWriter.write(81); // fn
        mWriter.write(48); // m

        mWriter.flush();
    }

    /**
     * Encode and print barcode
     *
     * @param code
     *            String to be encoded in the barcode. Different barcodes have
     *            different requirements on the length of data that can be
     *            encoded.
     * @param type
     *            Specify the type of barcode 65 = UPC-A. 66 = UPC-E. 67 =
     *            JAN13(EAN). 68 = JAN8(EAN). 69 = CODE39. 70 = ITF. 71 =
     *            CODABAR. 72 = CODE93. 73 = CODE128.
     *
     * @param h
     *            height of the barcode in points (1 <= n <= 255) @ param w
     *            width of module (2 <= n <=6). Barcode will not print if this
     *            value is too large. @param font Set font of HRI characters 0 =
     *            font A 1 = font B
     * @param pos
     *            set position of HRI characters 0 = not printed. 1 = Above
     *            barcode. 2 = Below barcode. 3 = Both abo ve and below barcode.
     */
    public void printBarcode(String code, int type, int h, int w, int font,
                             int pos) throws IOException {

        // need to test for errors in length of code
        // also control for input type=0-6
        // GS H = HRI position
        mWriter.write(0x1D);
        mWriter.write("H");
        mWriter.write(pos); // 0=no print, 1=above, 2=below, 3=above & below

        // GS f = set barcode characters
        mWriter.write(0x1D);
        mWriter.write("f");
        mWriter.write(font);

        // GS h = sets barcode height
        mWriter.write(0x1D);
        mWriter.write("h");
        mWriter.write(h);

        // GS w = sets barcode width
        mWriter.write(0x1D);
        mWriter.write("w");
        mWriter.write(w);// module = 1-6

        // GS p = sets barcode position
        mWriter.write(0x1D);
        mWriter.write("p");
        mWriter.write(1);// module = 1-6

        // GS k
        mWriter.write(0x1D); // GS
        mWriter.write("k"); // k
        mWriter.write(type);// m = barcode type 0-6
        mWriter.write(code.length()+2); // length of encoded string
        mWriter.write(0x7B);
        mWriter.write(0x42);
        mWriter.write(code);// d1-dk
        mWriter.write(0);// print barcode

        mWriter.flush();
    }

    /**
     * Encode and print PDF 417 barcode
     *
     * @param code
     *            String to be encoded in the barcode. Different barcodes have
     *            different requirements on the length of data that can be
     *            encoded.
     * @param type
     *            Specify the type of barcode 0 - Standard PDF417 1 - Standard
     *            PDF417
     *
     * @param h
     *            Height of the vertical module in dots 2 <= n <= 8. @ param w
     *            Height of the horizontal module in dots 1 <= n <= 4. @ param
     *            cols Number of columns 0 <= n <= 30. @ param rows Number of
     *            rows 0 (automatic), 3 <= n <= 90. @ param error set error
     *            correction level 48 <= n <= 56 (0 - 8).
     *
     */
    public void printPSDCode(String code, int type, int h, int w, int cols,
                             int rows, int error) throws IOException {

        // print function 82
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(code.length()); // pl Code length
        mWriter.write(0); // ph
        mWriter.write(48); // cn
        mWriter.write(80); // fn
        mWriter.write(48); // m
        mWriter.write(code); // data to be encoded

        // function 65 specifies the number of columns
        mWriter.write(0x1D);// init
        mWriter.write("(k");// adjust height of barcode
        mWriter.write(3); // pl
        mWriter.write(0); // pH
        mWriter.write(48); // cn
        mWriter.write(65); // fn
        mWriter.write(cols);

        // function 66 number of rows
        mWriter.write(0x1D);// init
        mWriter.write("(k");// adjust height of barcode
        mWriter.write(3); // pl
        mWriter.write(0); // pH
        mWriter.write(48); // cn
        mWriter.write(66); // fn
        mWriter.write(rows); // num rows

        // module width function 67
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(3);// pL
        mWriter.write(0);// pH
        mWriter.write(48);// cn
        mWriter.write(67);// fn
        mWriter.write(w);// size of module 1<= n <= 4

        // module height fx 68
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(3);// pL
        mWriter.write(0);// pH
        mWriter.write(48);// cn
        mWriter.write(68);// fn
        mWriter.write(h);// size of module 2 <= n <= 8

        // error correction function 69
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(4);// pL
        mWriter.write(0);// pH
        mWriter.write(48);// cn
        mWriter.write(69);// fn
        mWriter.write(48);// m
        mWriter.write(error);// error correction

        // choose pdf417 type function 70
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(3);// pL
        mWriter.write(0);// pH
        mWriter.write(48);// cn
        mWriter.write(70);// fn
        mWriter.write(type);// set mode of pdf 0 or 1

        // print function 81
        mWriter.write(0x1D);
        mWriter.write("(k");
        mWriter.write(3); // pl
        mWriter.write(0); // ph
        mWriter.write(48); // cn
        mWriter.write(81); // fn
        mWriter.write(48); // m

        mWriter.flush();

    }

    /**
     * send command to open cashbox through printer
     *
     * @throws IOException
     */
    public void openCashbox() throws IOException {
        mWriter.write(27);
        mWriter.write(112);
        mWriter.write(0);
        mWriter.write(10);
        mWriter.write(10);

        mWriter.flush();
    }

    /**
     * To control the printer performing paper feed and cut paper finally
     *
     * @throws IOException
     */
    public void feedAndCut() throws IOException {
        feed();
        cut();

        mWriter.flush();

    }

    /**
     * To control the printer performing paper feed
     *
     * @throws IOException
     */
    public void feed() throws IOException {
        // 下面指令为打印完成后自动走纸
        mWriter.write(27);
        mWriter.write(100);
        mWriter.write(4);
        mWriter.write(10);

        mWriter.flush();

    }

    /**
     * Cut paper, functionality whether work depends on printer hardware
     *
     * @throws IOException
     */
    public void cut() throws IOException {
        // cut
        mWriter.write(0x1D);
        mWriter.write("V");
        mWriter.write(66);
        mWriter.write(0);

        mWriter.flush();

    }

    /**
     * at the end, close mWriter and socketOut
     *
     */
    public void closeConn() {
        try {
            mWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}