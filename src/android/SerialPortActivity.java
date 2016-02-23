package android_serialport_api.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.ionicframework.starter.Application;

import android.R.bool;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * 
 * @author jiadx callbackContext.success(m);m==1鎵撳紑绔彛鎴愬姛锛�0鎵撳紑绔彛澶辫触
 */
public class SerialPortActivity extends CordovaPlugin {

	private SerialPortFinder mSerialPortFinder = new SerialPortFinder();;
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private SerialPortActivity instance;
	private int baudRate;
	private String port;
	private int Size=0;
	private boolean isTimer=true;
	private byte[] dateBuffer=new byte[64];
	private Long interval=(long) 0;
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					
					byte[] buffer = new byte[64];
					size = mInputStream.read(buffer);
					interval=System.currentTimeMillis();
					for(int i=0;i<size;i++){
						dateBuffer[Size+i] = buffer[i];
					};
					Size+=size;
					if (size > 0&&Size>0) {
						if(isTimer){
							isTimer=false;
							final Timer timer = new Timer(); 
							timer.schedule(new TimerTask(){
								public void run(){
									Long tsLong = System.currentTimeMillis();
									if(tsLong-75>interval){
										onDataReceived(dateBuffer, Size);
										interval=(long) 0;
										Size=0;
										dateBuffer=new byte[64];
										timer.cancel();
										isTimer=true;
									}
								}
							}, 75, 75);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
		mApplication = (Application) cordova.getActivity().getApplication();
		
		instance = this;
		if (action.equals("getSerialPort")) {// 鑾峰彇鏈嶅姟绔彛
			JSONArray resArr = new JSONArray();
			String[] entryValues = mSerialPortFinder.getAllDevicesPath();
			for (int i = 0; i < entryValues.length; i++) {
				resArr.put(i, entryValues[i]);
			}
			callbackContext.success(resArr);
			return true;
		} else if (action.equals("openSerialPort")) {// 鎵撳紑绔彛
			openSerialPort(args, callbackContext);
			return true;
		} else if (action.equals("closeSerialPort")) {// 鍏抽棴绔彛
			closeSerialPort(callbackContext);
			return true;
		} else if (action.equals("emission")) {// 鍐欐暟鎹�
			emission(args,callbackContext);
			return true;
		}else if(action.equals("registry")){
			registry(callbackContext);
			return true;
		}
		else if (action.equals("portDetect")) {// 妫�娴嬬鍙ｅ紑鍏�
			if (mSerialPort != null) {
				callbackContext.success(1);
			} else {
				callbackContext.success(0);
			}
			return true;
		}
		return true;
	}

	private void openSerialPort(final CordovaArgs args, final CallbackContext callbackContext) {
		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				try {
					baudRate = args.getInt(1);
					port = args.getString(0);
					mSerialPort = mApplication.getSerialPort(port, baudRate);
					callbackContext.success(1);
				} catch (SecurityException e) {
					callbackContext.error(0);
				} catch (IOException e) {
					callbackContext.error(0);
				} catch (InvalidParameterException e) {
					callbackContext.error(0);
				} catch (JSONException e) {
					e.printStackTrace();
					callbackContext.error(0);
				}
			}
		});

	}
	
	
	protected void onDataReceived(final byte[] buffer, final int size) {
	
		cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				CallbackContext callback=mApplication.getCallbackContext();
				if (callback != null) {
		            PluginResult result = new PluginResult(PluginResult.Status.OK, buffer);
		            result.setKeepCallback(true);
		            callback.sendPluginResult(result);
		            Size=0;
		        }
			
			}
		});
	}

	public void registry(final CallbackContext callbackContext){
		if (mSerialPort != null) {
			mApplication.setCallback(callbackContext);
			PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
	        result.setKeepCallback(true);
	        mApplication.getCallbackContext().sendPluginResult(result);
	        startRead();
		} else {
			callbackContext.error("串口尚未打开");
		}
		
	}
	public void startRead(){
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		mReadThread = new ReadThread();
		mReadThread.start();
	}
	public JSONArray bytesToHexStrings(byte[] src) {
		if (src == null || src.length <= 0) {
			return null;
		}
		JSONArray res = new JSONArray();
		for (int i = 0; i < src.length; i++) {
			int v = src[i];
			try {
				res.put(i, v);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	public void emission(CordovaArgs args,final CallbackContext callbackContext) throws JSONException{
		final byte[] str = args.getArrayBuffer(0);
		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				try {
					mOutputStream.write(str);
					mOutputStream.write('\n');
					callbackContext.success();
				} catch (IOException e) {
					callbackContext.error(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
	}
	public void closeSerialPort(CallbackContext callbackContext) {
		if (mReadThread != null) {
			mReadThread.interrupt();
		}
		mApplication.closeSerialPort();
		mSerialPort = null;
		callbackContext.success(0);
	}
}
