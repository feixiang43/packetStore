package com.example.fei.istore;

import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class Application extends android.app.Application {
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;
	private SerialPort mPrinterSerialPort = null;
	private SerialPort mBarcodeScannerSerialPort = null;
	private SerialPort mLockSerialPort = null;

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
//			SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
//			String path = sp.getString("DEVICE", "");
//			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));
            String path = "/dev/ttyS1";//串口号（具体的根据自己的串口号来配置）
            int baudrate = 9600;//波特率（可自行设定）

			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}

		return mSerialPort;
	}

	public SerialPort getPrinterSerialPort() throws SecurityException, IOException, InvalidParameterException {
        SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
        String path = sp.getString("DEVICE_PRINTER", "");
        int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

		if (mPrinterSerialPort == null) {
			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			mPrinterSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mPrinterSerialPort;
	}

	public SerialPort getBarcodeScannerSerialPort() throws SecurityException, IOException, InvalidParameterException {
        SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
        String path = sp.getString("DEVICE_BARCODESCANNER", "");
        int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

		if (mBarcodeScannerSerialPort == null) {
			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			mBarcodeScannerSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mBarcodeScannerSerialPort;
	}


	public SerialPort getLockSerialPort() throws SecurityException, IOException, InvalidParameterException {
        SharedPreferences sp = getSharedPreferences("serialport_preferences", MODE_PRIVATE);
        String path = sp.getString("DEVICE_LOCK", "");
        int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

		if (mLockSerialPort == null) {
			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			mLockSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mLockSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}

    public void closePrinterSerialPort() {
        if (mPrinterSerialPort != null) {
            mPrinterSerialPort.close();
            mPrinterSerialPort = null;
        }
    }

    public void closeBarcodeScannerSerialPort() {
        if (mBarcodeScannerSerialPort != null) {
            mBarcodeScannerSerialPort.close();
            mBarcodeScannerSerialPort = null;
        }
    }

    public void closeLockSerialPort() {
        if (mLockSerialPort != null) {
            mLockSerialPort.close();
            mLockSerialPort = null;
        }
    }
}
