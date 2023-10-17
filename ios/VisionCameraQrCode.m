#import <VisionCamera/FrameProcessorPlugin.h>
#import <VisionCamera/FrameProcessorPluginRegistry.h>

#if __has_include("VisionCameraQrCode/VisionCameraQrCode-Swift.h")
#import "VisionCameraQrCode/VisionCameraQrCode-Swift.h"
#else
#import "VisionCameraQrCode-Swift.h"
#endif

@interface VisionCameraQrCodePlugin (FrameProcessorPluginLoader)
@end

@implementation VisionCameraQrCodePlugin (FrameProcessorPluginLoader)

+ (void)load
{
  [FrameProcessorPluginRegistry addFrameProcessorPlugin:@"scanCode"
                                        withInitializer:^FrameProcessorPlugin*(NSDictionary* options) {
    return [[VisionCameraQrCodePlugin alloc] initWithOptions:options];
  }];
}

@end