import React, {useCallback} from 'react';
import type {NativeSyntheticEvent} from 'react-native';

import NativeCameraScanner from './ReactNativeScannerViewNativeComponent';
import type {NativeProps} from './ReactNativeScannerViewNativeComponent';
export type BarCodeFormats =
  | 'all_formats'
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
  formats?: BarCodeFormats[];
  onCodeScanned?: (code: string) => void;
};

type Event = {
  value: string;
};

export const CameraScanner = ({
  watcher = true,
  onlyCenter = false,
  formats = ['all_formats'],
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
      formats={formats}
      watcher={watcher}
      onlyCenter={onlyCenter}
      onNativeCodeScanned={onNativeCodeScanned}
      {...props}
    />
  );
};
