<h1 align="center">React Native Scanner</h1>

This library was developed to support the new architecture of React Native - Fabric, it does not support old versions of react native.

> Support only New Architecture an react-native > 0.71.0

## Installation

```bash
# use npm
npm install @brainylab/react-native-scanner
# or use yarn
yarn add @brainylab/react-native-scanner
# or use pnpm
pnpm i @brainylab/react-native-scanner
```
# Enable the New Architecture
### on android
You will only need to update your `android/gradle.properties` file as follows:

```diff
-newArchEnabled=false
+newArchEnabled=true
```

### on ios
You will only need to reinstall your pods by running `pod install` with the right flag:
```bash
# Run pod install with the flag:
RCT_NEW_ARCH_ENABLED=1 bundle exec pod install
```

## Usage

to use this library, you need to configure a library to ask permission to use the camera. take a look at [@brainylab/react-native-permissions](https://github.com/brainylab/react-native-permissions) -> using React Native | New Architecture and TurboModule

```typescript
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
```

### API

| APIs  | Value  | iOS | Android |
| -------------- | -------------  | -------------- | --------------- |
| `onCodeScanned` |  Receives the value of the QR/BarCode | ✅  | ✅ |

### Examples

The source code for the example (showcase) app is under the Example/ directory. If you want to play with the API but don't feel like trying it on a real app, you can run the example project.

## License

MIT

---

Development by [BrainyLab Development](https://brainylab.com.br)
