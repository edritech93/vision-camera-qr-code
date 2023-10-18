import * as React from 'react';

import { StyleSheet, Text } from 'react-native';
import {
  Camera,
  useCameraDevices,
  useFrameProcessor,
  type Frame,
} from 'react-native-vision-camera';
import {
  BarcodeFormat,
  scanBarcodes,
  // useScanBarcodes,
} from 'vision-camera-qr-code';
import { runOnJS } from 'react-native-reanimated';

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  // const [frameProcessor, barcodes] = useScanBarcodes([
  //   BarcodeFormat.ALL_FORMATS,
  //   BarcodeFormat.QR_CODE,
  // ]);

  const [barcodes, setBarcodes] = React.useState<any>([]);
  const devices = useCameraDevices();
  const device = devices.find((e) => e.position === 'back');

  React.useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'granted');
    })();
  }, []);

  React.useEffect(() => {
    console.log(barcodes);
  }, [barcodes]);

  const frameProcessor = useFrameProcessor((frame: Frame) => {
    'worklet';
    const scannedFaces = scanBarcodes(frame, [
      BarcodeFormat.ALL_FORMATS,
      BarcodeFormat.QR_CODE,
    ]);
    runOnJS(setBarcodes)(scannedFaces);
  }, []);

  return (
    device != null &&
    hasPermission && (
      <>
        <Camera
          style={StyleSheet.absoluteFill}
          device={device}
          isActive={true}
          frameProcessor={frameProcessor}
        />
        {barcodes.map((barcode: any, idx: number) => (
          <Text key={idx} style={styles.barcodeTextURL}>
            {barcode.displayValue}
          </Text>
        ))}
      </>
    )
  );
}

const styles = StyleSheet.create({
  barcodeTextURL: {
    fontSize: 20,
    color: 'white',
    fontWeight: 'bold',
  },
});
