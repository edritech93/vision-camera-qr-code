import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Dimensions, Platform, StyleSheet, Text, View } from 'react-native';
import {
  Camera,
  CameraRuntimeError,
  useCameraDevice,
  useCameraFormat,
} from 'react-native-vision-camera';
import { BarcodeFormat, useScanBarcodes } from 'vision-camera-qr-code';

const SCREEN_WIDTH = Dimensions.get('window').width;
const SCREEN_HEIGHT = Platform.select<number>({
  android: Dimensions.get('screen').height - 60,
  ios: Dimensions.get('window').height,
}) as number;
const screenAspectRatio = SCREEN_HEIGHT / SCREEN_WIDTH;
const enableHdr = false;
const enableNightMode = false;
const targetFps = 30;

export default function App() {
  const [hasPermission, setHasPermission] = useState(false);
  const [frameProcessor, barcodes] = useScanBarcodes([
    BarcodeFormat.ALL_FORMATS,
    BarcodeFormat.QR_CODE,
  ]);
  const camera = useRef<Camera>(null);

  const device = useCameraDevice('back', {
    physicalDevices: [
      'ultra-wide-angle-camera',
      'wide-angle-camera',
      'telephoto-camera',
    ],
  });
  const format = useCameraFormat(device, [
    { fps: targetFps },
    { videoAspectRatio: screenAspectRatio },
    { videoResolution: 'max' },
    { photoAspectRatio: screenAspectRatio },
    { photoResolution: 'max' },
  ]);
  const fps = Math.min(format?.maxFps ?? 1, targetFps);

  useEffect(() => {
    async function _getPermission() {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'granted');
    }
    _getPermission();
  }, []);

  const onError = useCallback((error: CameraRuntimeError) => {
    console.error(error);
  }, []);

  const onInitialized = useCallback(() => {
    console.log('Camera initialized!');
  }, []);

  useEffect(() => {
    console.log('barcodes => ', barcodes);
  }, [barcodes]);

  if (device != null && format != null && hasPermission) {
    console.log(
      `Device: "${device.name}" (${format.photoWidth}x${format.photoHeight} photo / ${format.videoWidth}x${format.videoHeight} video @ ${fps}fps)`
    );
    const pixelFormat = format.pixelFormats.includes('yuv') ? 'yuv' : 'native';
    return (
      <View style={styles.container}>
        <Camera
          ref={camera}
          style={StyleSheet.absoluteFill}
          device={device}
          format={format}
          fps={fps}
          hdr={enableHdr}
          lowLightBoost={device.supportsLowLightBoost && enableNightMode}
          isActive={true}
          onInitialized={onInitialized}
          onError={onError}
          enableZoomGesture={false}
          enableFpsGraph={false}
          orientation={'portrait'}
          pixelFormat={pixelFormat}
          photo={false}
          video={false}
          audio={false}
          frameProcessor={frameProcessor}
        />
        {barcodes.map((barcode: any, idx: number) => (
          <Text key={idx} style={styles.barcodeTextURL}>
            {barcode.displayValue}
          </Text>
        ))}
      </View>
    );
  } else {
    return null;
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  barcodeTextURL: {
    fontSize: 20,
    color: 'white',
    fontWeight: 'bold',
  },
});
