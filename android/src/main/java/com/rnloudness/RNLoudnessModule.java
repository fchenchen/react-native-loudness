
package com.rnloudness;

import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.MediaRecorder.AudioSource;

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
  private static final int SDK_INT = android.os.Build.VERSION.SDK_INT;

  private static final int RECORDER_SOURCE = AudioSource.MIC;
  private static final int RECORDER_SAMPLERATE = 44100;
  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

  private final ReactApplicationContext reactContext;

  private AudioRecord recorder;
  // Instead of setting a min buffersize, set a buffer size for 100ms of data
  // Buffersize should not be too small
  private int bufferSize = (int) ((double)RECORDER_SAMPLERATE*0.1); // 44100*0.1;

  public RNLoudnessModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNLoudness";
  }

  @ReactMethod
  public void start(){
    this.recorder = new AudioRecord(RECORDER_SOURCE,
                                    RECORDER_SAMPLERATE,
                                    RECORDER_CHANNELS,
                                    RECORDER_AUDIO_ENCODING,
                                    this.bufferSize);
    // System.out.println("ReactNativeJs: Buffer size: " + bufferSize);
    // Check State
    if (this.recorder.getState() == AudioRecord.STATE_INITIALIZED){
      // System.out.println("ReactNativeJs: State: Ready");
    } else {
      // System.out.println("ReactNativeJs: State: Not ready");
    }

    try{
      this.recorder.startRecording();
      // System.out.println("ReactNativeJs: Start recording");
    } catch(IllegalStateException e){
      // System.out.println("ReactNativeJs: Start recording failed");
    }

  }

  @ReactMethod
  public void stop(){
    try{
      this.recorder.stop();
      // System.out.println("ReactNativeJs: Stop recording");
    } catch(IllegalStateException e){
      // System.out.println("ReactNativeJs: Stop recording failed");
    }

  }

  @ReactMethod
  public void getLoudness(Callback cb) {
    // Read all data
    short[] audioData = new short[this.bufferSize];
    int r;
    if (this.SDK_INT < 23){
      // Assume blocking before SDK 23
      r = this.recorder.read(audioData,0,this.bufferSize);
    } else {
      // readMode parameter is added since SDK 23
      r = this.recorder.read(audioData,0,this.bufferSize,AudioRecord.READ_BLOCKING);
    }
    // System.out.println("ReactNativeJs: Read: " + r);
    // System.out.println("ReactNativeJs: Data: " + Arrays.toString(audioData));
    double loudness = calcRMS(audioData);
    // System.out.println("ReactNativeJs: RMS: " + loudness);
    cb.invoke(loudness);
  }

  private double calcRMS(short[] data){
    long sum = 0;
    for (short d:data){
      sum += d*d;
    }

    double rms = Math.sqrt(sum / data.length);
    return rms;
  }
}
