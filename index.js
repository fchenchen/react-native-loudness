import { NativeModules } from 'react-native';
import { Platform } from 'react-native';

let start = (fileName) => {
  if (fileName){
    NativeModules.RNLoudness.start(fileName);
  } else {
    NativeModules.RNLoudness.start(null);
  }
}

let stop = NativeModules.RNLoudness.stop;

let getLoudness = NativeModules.RNLoudness.getLoudness;

export default {
  start,
  stop,
  getLoudness
};
