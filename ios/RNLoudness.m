#import "RNLoudness.h"
#import <React/RCTLog.h>

@implementation RNLoudness

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (id)init {
  if (!(self = [super init])){
    return nil;
  }
  NSURL *tmpDirURL = [NSURL fileURLWithPath:NSTemporaryDirectory() isDirectory:YES];
  NSURL *fileURL = [[tmpDirURL URLByAppendingPathComponent:@"micTmp"] URLByAppendingPathExtension:@"caf"];
  // RCTLogInfo(@"fileURL: %@", [fileURL path]);

  NSDictionary *settings = [NSDictionary dictionaryWithObjectsAndKeys:
                            [NSNumber numberWithFloat: 44100.0],                 AVSampleRateKey,
                            [NSNumber numberWithInt: kAudioFormatLinearPCM],     AVFormatIDKey,
                            [NSNumber numberWithInt: 1],                         AVNumberOfChannelsKey,
                            [NSNumber numberWithInt: AVAudioQualityMax],         AVEncoderAudioQualityKey,
                            nil];
  NSError *error;

  recorder = [[AVAudioRecorder alloc] initWithURL:fileURL settings:settings error:&error];
  if (recorder){
    // RCTLogInfo(@"Mic Initialized");
  } else {
    // RCTLogInfo(@"Error: %@", [error localizedDescription]);
  }

  [[AVAudioSession sharedInstance]
   setCategory:AVAudioSessionCategoryPlayAndRecord error:&error];

  if (error) {
    // NSLog(@"Error setting category: %@", [error description]);
  }

  return self;
}

// To export a module
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(start)
{
  if(!recorder){
    return;
  }

  // [recorder prepareToRecord];
  BOOL isStart = [recorder record];
  recorder.meteringEnabled = true;
  BOOL isMetering = [recorder isMeteringEnabled];

  if (!isStart){
    // RCTLogInfo(@"Fail to record");
  } else {
    // RCTLogInfo(@"Start to record with metering: %d", isMetering);
  }
}

RCT_EXPORT_METHOD(stop)
{
  if(!recorder){
    return;
  }
  [recorder stop];
}

RCT_EXPORT_METHOD(getLoudness:(RCTResponseSenderBlock)callback)
{
  if(!recorder){
    return;
  }

  [recorder updateMeters];
  float avgPower = [recorder averagePowerForChannel:0];
  NSNumber *loudness = [NSNumber numberWithFloat:avgPower];
  callback(@[loudness]);
}

@end
