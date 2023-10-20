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
            formats={['code-128', 'code-39', 'code-93', 'ean-13', 'ean-8']}
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
