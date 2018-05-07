import { NativeModules } from 'react-native';
import { Platform } from 'react-native';

const Loudness = function () {
  this.start = NativeModules.RNLoudness.start;

  this.stop = NativeModules.RNLoudness.stop;

  if (Platform.OS === 'android'){
    this.getLoudness = function(callback){
      NativeModules.RNLoudness.getLoudness((loudness) => {
        dbFS = 20*Math.log(loudness/32768)/Math.log(10);
        // console.log('dbFS: ' + dbFS);
        callback(dbFS);
      });
    }
  } else if (Platform.OS === 'ios'){
    this.getLoudness = NativeModules.RNLoudness.getLoudness;
  } else{
    this.getLoudness = null;
  }
}

export {Loudness};
