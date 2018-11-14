
# react-native-loudness
Read microphone loudness in React Native

## Getting started

`$ npm install react-native-loudness --save`

### Mostly automatic installation

`$ react-native link react-native-loudness`

### Required Additional Steps

#### iOS
1. In `Info.plist`, add `Privacy - Micronphone Usage Description` using XCode.

#### Android
1. In `AndroidManifest.xml`, add `<uses-permission android:name="android.permission.RECORD_AUDIO" />`.


## Permission
For Android, you have to request permission in the code. Please refer to [PermissionsAndroid](https://facebook.github.io/react-native/docs/permissionsandroid) for more details. Example App also demonstrates the permission request process.  

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
