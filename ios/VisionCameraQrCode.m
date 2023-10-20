#import <VisionCamera/FrameProcessorPlugin.h>
#import <VisionCamera/FrameProcessorPluginRegistry.h>

#if __has_include("VisionCameraQrCode/VisionCameraQrCode-Swift.h")
#import "VisionCameraQrCode/VisionCameraQrCode-Swift.h"
#else
#import "VisionCameraQrCode-Swift.h"
#endif

VISION_EXPORT_SWIFT_FRAME_PROCESSOR(VisionCameraQrCodePlugin, scanCode)
