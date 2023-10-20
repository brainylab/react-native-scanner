import type {ViewProps} from 'react-native';
import type {DirectEventHandler} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

type Event = {
  value: string;
};

export interface NativeProps extends ViewProps {
  watcher?: boolean;
  formats: Array<string>;
  onNativeCodeScanned: DirectEventHandler<Event>;
}

export default codegenNativeComponent<NativeProps>('ReactNativeScannerView');
