#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

#import <AVFoundation/AVFoundation.h>


@interface RNLoudness : NSObject <RCTBridgeModule>{
  AVAudioRecorder* recorder;
  NSURL* tmpDirURL;
  NSURL* saveDirURL;
}

@end
