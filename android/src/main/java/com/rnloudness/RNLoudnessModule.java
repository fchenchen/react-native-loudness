
package com.rnloudness;

import android.util.Log;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Arrays;


public class RNLoudnessModule extends ReactContextBaseJavaModule {
  private final ReactApplicationContext reactContext;

  private static final String TAG = "RNLoudnessModule";
  private static final int SDK_INT = android.os.Build.VERSION.SDK_INT;
  private RecordThread recordThread;

  public RNLoudnessModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNLoudness";
  }

  @ReactMethod
  public void start(String fileName){
    recordThread = new RecordThread(fileName, reactContext);
    recordThread.start();
  }

  @ReactMethod
  public void stop(){
    if (recordThread != null){
      recordThread.stopRecording();
      recordThread = null;
    }
  }

  @ReactMethod
  public void getLoudness(Callback cb) {
    if (recordThread != null){
      cb.invoke(recordThread.getLoudness());
    } else {
      cb.invoke(1); // Error
    }

    return;
  }
}
