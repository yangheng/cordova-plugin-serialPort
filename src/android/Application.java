package com.ionicframework.starter;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import org.apache.cordova.CallbackContext;
public class Application extends android.app.Application {

	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;
	private CallbackContext callbackContext;
	public void setCallback(CallbackContext callbackContext){
		this.callbackContext=callbackContext;
	}
	public CallbackContext getCallbackContext(){
		return this.callbackContext;
	}
	public SerialPort getSerialPort(String serial, int baudrate)
			throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Check parameters */
			String path = serial;
			if ((path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}
			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
