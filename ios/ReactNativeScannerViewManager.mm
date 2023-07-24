#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "RCTBridge.h"

@interface ReactNativeScannerViewManager : RCTViewManager
@end

@implementation ReactNativeScannerViewManager

RCT_EXPORT_MODULE(ReactNativeScannerView)

- (UIView *)view
{
  return [[UIView alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(color, NSString)

@end
