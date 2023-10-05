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
import React, {useRef, useState} from 'react';
import {Text, SafeAreaView, TouchableOpacity, Modal, View} from 'react-native';

import {useCameraPermission} from '@brainylab/react-native-permissions';
import {CameraScanner} from '@brainylab/react-native-scanner';

export default function App() {
  const [open, setOpen] = useState(false);
  const [code, setCode] = useState<string | null>(null);
  const {status, requestPermission} = useCameraPermission();

  const count = useRef(0);

  if (code) {
    return (
      <Text style={{fontSize: 30, color: 'red'}}>Code Scanned is: {code}</Text>
    );
  }

  if (status === 'authorized') {
    return (
      <SafeAreaView style={{flex: 1}}>
        <Modal animationType="none" transparent={true} visible={open}>
          <CameraScanner
            watcher={false}
            style={{flex: 1}}
            onCodeScanned={value => {
              if (value) {
                setCode(value);
              }
              count.current++;
              console.log(value, count.current);
            }}
          />
          <View
            style={{
              display: 'flex',
              margin: 20,
              justifyContent: 'center',
              alignContent: 'center',
              alignItems: 'center',
            }}>
            <TouchableOpacity
              style={{width: '100%', padding: 5, backgroundColor: 'green'}}
              onPress={() => setOpen(prev => !prev)}>
              <Text style={{textAlign: 'center', color: 'white'}}>
                Close Camera
              </Text>
            </TouchableOpacity>
          </View>
        </Modal>
        <View
          style={{
            flex: 1,
            justifyContent: 'center',
            alignItems: 'center',
            padding: 20,
          }}>
          <TouchableOpacity
            style={{
              width: '100%',
              backgroundColor: 'green',
              padding: 5,
            }}
            onPress={() => setOpen(prev => !prev)}>
            <Text style={{color: 'white', textAlign: 'center'}}>
              Open Camera
            </Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  } else {
    return (
      <>
        <View
          style={{
            flex: 1,
            justifyContent: 'center',
            alignItems: 'center',
            padding: 20,
          }}>
          <Text
            style={{
              textAlign: 'center',
              fontSize: 20,
              color: 'red',
              marginBottom: 50,
            }}>
            You need to grant camera permission first
          </Text>
          <TouchableOpacity
            style={{
              width: '100%',
              backgroundColor: 'green',
              padding: 5,
            }}
            onPress={requestPermission}>
            <Text style={{color: 'white', textAlign: 'center'}}>
              Get Camera Permission
            </Text>
          </TouchableOpacity>
        </View>
      </>
    );
  }
}
```

### API

| APIs  | Value  | iOS | Android |
| -------------- | -------------  | -------------- | --------------- |
| `watcher` |  activates continuous reading, `default: true` | ❌  | ✅ |
| `onCodeScanned` |  Receives the value of the QR/BarCode | ✅  | ✅ |

### Watcher Mode
Observer mode was added with a very specific function, when the mode is `true`, it continuously reads to find a barcode, when it is `false`, it reads once and pauses the reading while the object is in front from the camera, when the object leaves, it releases the reading again. To carry out this method, Google's ML Kit was used.

### Examples

The source code for the example (showcase) app is under the Example/ directory. If you want to play with the API but don't feel like trying it on a real app, you can run the example project.

## License

MIT

---

Development by [BrainyLab Development](https://brainylab.com.br)
