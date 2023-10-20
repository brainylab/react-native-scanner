import React, {useCallback} from 'react';
import type {NativeSyntheticEvent} from 'react-native';

import NativeCameraScanner from './ReactNativeScannerViewNativeComponent';
import type {NativeProps} from './ReactNativeScannerViewNativeComponent';
type BarCodeFormats =
  | 'code-128'
  | 'code-39'
  | 'code-93'
  | 'codabar'
  | 'ean-13'
  | 'ean-8'
  | 'itf'
  | 'upc-e'
  | 'qr-code'
  | 'pdf-417'
  | 'aztec'
  | 'data-matrix';

type Props = Omit<NativeProps, 'onNativeCodeScanned'> & {
  formats: BarCodeFormats[];
  onCodeScanned?: (code: string) => void;
};

type Event = {
  value: string;
};

export const CameraScanner = ({
  watcher = true,
  onCodeScanned,
  ...props
}: Props) => {
  const onNativeCodeScanned = useCallback(
    ({nativeEvent}: NativeSyntheticEvent<Event>) => {
      if (nativeEvent && nativeEvent?.value) {
        const code = nativeEvent.value;
        onCodeScanned && onCodeScanned(code);
      }
    },
    [onCodeScanned],
  );

  return (
    <NativeCameraScanner
      watcher={watcher}
      onNativeCodeScanned={onNativeCodeScanned}
      {...props}
    />
  );
};
