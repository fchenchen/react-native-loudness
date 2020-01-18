package com.rnloudness;

import android.util.Log;
import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.MediaRecorder.AudioSource;
import android.os.storage.StorageManager;
import android.os.Environment;

import java.io.File;
import java.util.UUID;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;


import com.facebook.react.bridge.ReactApplicationContext;

public class RecordThread extends Thread {
  private static final String TAG = "RecordThread";
  private static final int RECORDER_SOURCE = AudioSource.MIC;
  private static final int RECORDER_SAMPLERATE = 44100;
  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  // Instead of setting a min buffersize, set a buffer size for 100ms of data
  // This should be big enough
  private static final int RECORDER_BUFFER_SIZE = (int) ((double)RECORDER_SAMPLERATE*0.1*2); // 44100*0.1;

  private AudioRecord recorder;
  private boolean shouldStop = false;
  private String fileName;
  private File tmpFile;
  private File saveFile;
  private FileOutputStream tmpFileOutputStream;
  private FileInputStream tmpFileInputStream;
  private FileOutputStream saveFileOutputStream;
  private boolean shouldSave = false;
  private double loudness = -160;
  private ReactApplicationContext reactContext;

  RecordThread(String fileName, ReactApplicationContext reactContext) {
    super();
    this.fileName = fileName;
    this.reactContext = reactContext;

    if (fileName != null) shouldSave = true;
  }

  public void run() {
    // Initialize recorder
    recorder = new AudioRecord(RECORDER_SOURCE,
                                    RECORDER_SAMPLERATE,
                                    RECORDER_CHANNELS,
                                    RECORDER_AUDIO_ENCODING,
                                    RECORDER_BUFFER_SIZE);
    int buffer = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
    if (buffer > RECORDER_BUFFER_SIZE){
      Log.e(TAG,"Buffer not big enough");
    }

    if (this.recorder.getState() == AudioRecord.STATE_INITIALIZED){
      // Log.d(TAG,"Audio recorder initialized successfully");
    } else {
      Log.e(TAG,"Audio recorder failed to initialize");
      return;
    }

    try{
      recorder.startRecording();
    } catch(IllegalStateException e){
      Log.e(TAG,"Failed to start recording");
    }

    // Initialize file and write stream
    if (shouldSave){
      try {
        tmpFile = File.createTempFile(fileName, ".pcm", reactContext.getCacheDir());
        tmpFileOutputStream = new FileOutputStream(tmpFile);
      } catch (Exception e){
        Log.e(TAG,"Failed to create file");
      }
    }

    // Read and save data
    while(!shouldStop){
      int bytes2Read = RECORDER_BUFFER_SIZE;
      ByteBuffer audioData = ByteBuffer.allocateDirect(bytes2Read);
      audioData.order(ByteOrder.LITTLE_ENDIAN);
      int bytesRead = this.recorder.read(audioData,bytes2Read);
      if (bytesRead >= 0){
        // Calculate loudness
        ShortBuffer audioDataShort = audioData.asShortBuffer();
        calcLoudness(audioDataShort);

        // Data read successfully
        if (shouldSave && tmpFileOutputStream != null){
          // Save to file
          try {
            tmpFileOutputStream.getChannel().write(audioData);
          } catch (Exception e){
            Log.e(TAG,"Failed to write to file");
          }
        }
      } else {
        Log.e(TAG,"Failed to read audio data: " + bytesRead);
      }
    }

    // Stop recording and write stream
    try{
      recorder.stop();
      recorder.release();
      recorder = null;
      if (tmpFileOutputStream != null) tmpFileOutputStream.close();
    } catch(Exception e){
      Log.e(TAG,"Failed to stop recording");
    }

    // Save wav file
    if (shouldSave){
      try {
        // File saveFile = new File(reactContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),fileName+".wav");
        File saveFile = new File(reactContext.getFilesDir(),fileName+".wav");
        tmpFileInputStream = new FileInputStream(tmpFile);
        saveFileOutputStream = new FileOutputStream(saveFile);

        // Write header
        long totalAudioLen = tmpFileInputStream.getChannel().size();
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 1;
        long byteRate = RECORDER_SAMPLERATE*16*channels/8;
        byte bitsPerSample = 16;
        saveFileOutputStream.write(wavFileHeader(totalAudioLen,
                                                 totalDataLen,
                                                 longSampleRate,
                                                 channels,
                                                 byteRate,
                                                 bitsPerSample));
        // Copy audio data
        byte[] tmpData = new byte[RECORDER_BUFFER_SIZE];
        int tmpBytesRead = tmpFileInputStream.read(tmpData);
        while(tmpBytesRead != -1) {
          saveFileOutputStream.write(tmpData,0,tmpBytesRead);
          tmpBytesRead = tmpFileInputStream.read(tmpData);
        }

        tmpFileInputStream.close();
        saveFileOutputStream.close();
      } catch (Exception e){
        Log.e(TAG,"Failed to create file");
      }
    }
    interrupt();
  }

  public void stopRecording(){
    shouldStop = true;
  }

  public double getLoudness(){
    return loudness;
  }

  private void calcLoudness(ShortBuffer data){
    long sum = 0;

    while(data.remaining() > 0){
      short d = data.get();
      sum += d * d;
    }

    double rms = Math.sqrt(sum / data.capacity());

    if (rms <= 0.00000001) {
      loudness = -160;
    } else {
      loudness = 20*Math.log10(rms/32768);
    }
    return;
  }

  private byte[] wavFileHeader(long totalAudioLen,
                               long totalDataLen,
                               long longSampleRate,
                               int channels,
                               long byteRate,
                               byte bitsPerSample) {
    byte[] header = new byte[44];
    header[0] = 'R'; // RIFF/WAVE header
    header[1] = 'I';
    header[2] = 'F';
    header[3] = 'F';
    header[4] = (byte) (totalDataLen & 0xff);
    header[5] = (byte) ((totalDataLen >> 8) & 0xff);
    header[6] = (byte) ((totalDataLen >> 16) & 0xff);
    header[7] = (byte) ((totalDataLen >> 24) & 0xff);
    header[8] = 'W';
    header[9] = 'A';
    header[10] = 'V';
    header[11] = 'E';
    header[12] = 'f'; // 'fmt ' chunk
    header[13] = 'm';
    header[14] = 't';
    header[15] = ' ';
    header[16] = 16; // 4 bytes: size of 'fmt ' chunk
    header[17] = 0;
    header[18] = 0;
    header[19] = 0;
    header[20] = 1; // format = 1
    header[21] = 0;
    header[22] = (byte) channels;
    header[23] = 0;
    header[24] = (byte) (longSampleRate & 0xff);
    header[25] = (byte) ((longSampleRate >> 8) & 0xff);
    header[26] = (byte) ((longSampleRate >> 16) & 0xff);
    header[27] = (byte) ((longSampleRate >> 24) & 0xff);
    header[28] = (byte) (byteRate & 0xff);
    header[29] = (byte) ((byteRate >> 8) & 0xff);
    header[30] = (byte) ((byteRate >> 16) & 0xff);
    header[31] = (byte) ((byteRate >> 24) & 0xff);
    header[32] = (byte) (channels * (bitsPerSample / 8)); //
    // block align
    header[33] = 0;
    header[34] = bitsPerSample; // bits per sample
    header[35] = 0;
    header[36] = 'd';
    header[37] = 'a';
    header[38] = 't';
    header[39] = 'a';
    header[40] = (byte) (totalAudioLen & 0xff);
    header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
    header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
    header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
    return header;
  }
}
