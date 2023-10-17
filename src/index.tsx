import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'vision-camera-qr-code' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type VisionCameraQrCodeProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'VisionCameraQrCodeView';

export const VisionCameraQrCodeView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<VisionCameraQrCodeProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
