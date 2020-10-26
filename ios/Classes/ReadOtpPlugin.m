#import "ReadOtpPlugin.h"
#if __has_include(<read_otp_plugin/read_otp_plugin-Swift.h>)
#import <read_otp_plugin/read_otp_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "read_otp_plugin-Swift.h"
#endif

@implementation ReadOtpPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftReadOtpPlugin registerWithRegistrar:registrar];
}
@end
