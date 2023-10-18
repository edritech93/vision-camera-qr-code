import React, { useState, useEffect } from 'react';
import { StyleSheet, Text } from 'react-native';
import { BarcodeFormat, useScanBarcodes } from 'vision-camera-qr-code';
import { Camera, useCameraDevices } from 'react-native-vision-camera';

export default function App() {
  const [hasPermission, setHasPermission] = useState(false);
  const [frameProcessor, barcodes] = useScanBarcodes([
    BarcodeFormat.ALL_FORMATS,
    BarcodeFormat.QR_CODE,
  ]);

  const devices = useCameraDevices();
  const device = devices.find((e) => e.position === 'back');

  useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'granted');
    })();
  }, []);

  React.useEffect(() => {
    console.log(barcodes);
  }, [barcodes]);

  return (
    device != null &&
    hasPermission && (
      <>
        <Camera
          style={StyleSheet.absoluteFill}
          device={device}
          isActive={true}
          pixelFormat={'yuv'}
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
