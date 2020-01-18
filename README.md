
# react-native-loudness
Read microphone loudness in React Native

## Getting started

`$ npm install react-native-loudness --save`

`$ react-native link react-native-loudness`

For RN>0.60, no need to link but please run `pod install` in the project `ios` directory.

### Required Additional Steps

#### iOS
1. In `Info.plist`, add `Privacy - Micronphone Usage Description` using XCode.

#### Android
1. In `AndroidManifest.xml`, add `<uses-permission android:name="android.permission.RECORD_AUDIO" />`.


## Permission
The app needs to ask permission for microphone usage. If the loudness reading is not a number or is always a fixed number, check the app permission first. For Android 6.0 (API level 23) or later, the permission needs to be asked when running the app. Learn more [here](https://developer.android.com/guide/topics/permissions/overview). For iOS devices, the permission will be automatically asked when needed. It can also be manually asked. Check out [react-native-permissions](https://github.com/react-native-community/react-native-permissions).

## Usage
Loudness is in dbFS from -160 to 0, where -160 represents near absolute silence and 0 represents the maximum loudness the microphone can sense.

```javascript
import Loudness from 'react-native-loudness';

Loudness.start();

Loudness.getLoudness((loudness) => {
  console.log(loudness);
});

Loudness.stop();
```

While reading the loudness, this module can save the microphone reading into a WAV audio file at the same time. The file is 16 bit, 44.1kHz, and mono channel both for iOS and Android. The file is located in the document directory of the app. Please use another library such as [rn-fetch-blob](https://github.com/joltup/rn-fetch-blob) to move, delete, or copy the file.

```javascript
Loudness.start('test'); // Supply a file name string to save the file
```

## Example
Check out this example app [LoudnessMeter](https://github.com/fchenchen/LoudnessMeter) for more details.
