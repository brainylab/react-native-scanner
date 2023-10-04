import React, {useCallback} from 'react';
import type {NativeSyntheticEvent} from 'react-native';

import NativeCameraScanner from './ReactNativeScannerViewNativeComponent';
import type {NativeProps} from './ReactNativeScannerViewNativeComponent';

type Props = Omit<NativeProps, 'onNativeCodeScanned'> & {
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
