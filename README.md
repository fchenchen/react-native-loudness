
# react-native-loudness

## Getting started

`$ npm install react-native-loudness --save`

### Mostly automatic installation

`$ react-native link react-native-loudness`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-loudness` and add `RNLoudness.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNLoudness.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNLoudnessPackage;` to the imports at the top of the file
  - Add `new RNLoudnessPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-loudness'
  	project(':react-native-loudness').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-loudness/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-loudness')
  	```


## Usage
```javascript
import RNLoudness from 'react-native-loudness';

// TODO: What to do with the module?
RNLoudness;
```
  