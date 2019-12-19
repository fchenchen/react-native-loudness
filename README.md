
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
Loudness is in dbFS from -160 to 0, where -160 represents absolute silence and 0 represents the maximum loudness the microphone can sense.

```javascript
import {Loudness} from 'react-native-loudness';

const loudness = new Loudness();
loudness.start();

loudness.getLoudness((loudness) => {
  console.log(loudness);
});

loudness.stop();
```

## Example
There is a simple example project `LoudnessMeter` in the examples folder of this repository.
