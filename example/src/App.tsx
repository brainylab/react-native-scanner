import React, {useEffect, useState} from 'react';
import {Text, SafeAreaView} from 'react-native';

import {useCameraPermission} from '@brainylab/react-native-permissions';
import {CameraScanner} from '@brainylab/react-native-scanner';

export default function App() {
  const [code, setCode] = useState<string | null>(null);
  const {status, requestPermission} = useCameraPermission();

  useEffect(() => {
    if (status === 'denied') {
      requestPermission();
    }
  }, [status, requestPermission]);

  if (code) {
    return (
      <Text style={{fontSize: 30, color: 'red'}}>Code Scanned is: {code}</Text>
    );
  }

  if (status === 'authorized') {
    return (
      <SafeAreaView style={{flex: 1}}>
        <CameraScanner
          style={{flex: 1}}
          onCodeScanned={value => {
            if (value.nativeEvent?.value) {
              setCode(value.nativeEvent.value);
            }
            console.log(value.nativeEvent.value);
          }}
        />
      </SafeAreaView>
    );
  } else {
    return (
      <Text style={{fontSize: 30, color: 'red'}}>
        You need to grant camera permission first
      </Text>
    );
  }
}
