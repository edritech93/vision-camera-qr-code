#import <Foundation/Foundation.h>

#import "VisionCameraScanner.h"
#if defined __has_include && __has_include("VisionCameraQrCode-Swift.h")
#import "VisionCameraQrCode-Swift.h"
#else
#import <VisionCameraQrCode/VisionCameraQrCode-Swift.h>
#endif

@implementation RegisterPlugins

+ (void) load {
    [FrameProcessorPluginRegistry addFrameProcessorPlugin:@"scanCode"
                                          withInitializer:^FrameProcessorPlugin*(NSDictionary* options) {
        return [[VisionCameraScanner alloc] init];
    }];
}

@end
