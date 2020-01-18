#import "RNLoudness.h"
#import <React/RCTLog.h>
#import <Foundation/Foundation.h>

@implementation RNLoudness

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_queue_create("com.rnloudness", DISPATCH_QUEUE_SERIAL);
}

- (id)init {
  if (!(self = [super init])){
    return nil;
  }

  NSError *error;

  // Initialize directory for save and temp files
  saveDirURL = [[NSFileManager defaultManager] URLForDirectory:NSDocumentDirectory
                                                             inDomain:NSUserDomainMask
                                                    appropriateForURL:nil
                                                               create:false
                                                                error:&error];
  tmpDirURL = [NSURL fileURLWithPath:NSTemporaryDirectory() isDirectory:YES];
  // Set this app session to play and record mode
  [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord error:&error];

  return self;
}

// To export a module
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(start:(NSString * _Nullable)fileName)
{
  NSError *error;
  NSURL *fileURL;
  if (fileName){
    fileURL = [[saveDirURL URLByAppendingPathComponent:fileName] URLByAppendingPathExtension:@"wav"];
  } else {
    fileURL = [[tmpDirURL URLByAppendingPathComponent:@"micTmp"] URLByAppendingPathExtension:@"wav"];
  }

  NSDictionary *settings = [NSDictionary dictionaryWithObjectsAndKeys:
                             [NSNumber numberWithFloat: 44100.0],AVSampleRateKey,
                 [NSNumber numberWithInt: kAudioFormatLinearPCM],AVFormatIDKey,
                                     [NSNumber numberWithInt: 1],AVNumberOfChannelsKey,
                     [NSNumber numberWithInt: AVAudioQualityMax],AVEncoderAudioQualityKey,
                                    [NSNumber numberWithInt: 16],AVEncoderBitDepthHintKey,
                                                                 nil];

  recorder = [[AVAudioRecorder alloc] initWithURL:fileURL settings:settings error:&error];

  if(!recorder){
    RCTLogError(@"Error: %@", [error localizedDescription]);
    return;
  }

  BOOL isStart = [recorder record];
  recorder.meteringEnabled = true;

  if (!isStart) RCTLogError(@"Error: Failed to start recording.");
}

RCT_EXPORT_METHOD(stop)
{
  if(!recorder) return;

  [recorder stop];
}

RCT_EXPORT_METHOD(getLoudness:(RCTResponseSenderBlock)callback)
{
  if(!recorder) return;

  [recorder updateMeters];
  float avgPower = [recorder averagePowerForChannel:0];
  NSNumber *loudness = [NSNumber numberWithFloat:avgPower];
  callback(@[loudness]);
}

@end
