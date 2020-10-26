import 'dart:async';

import 'package:flutter/services.dart';

class ReadOtpPlugin {
  static const MethodChannel _channel = const MethodChannel('read_otp_plugin');

  Function(String) onSmsReceived;

  ReadOtpPlugin(this.onSmsReceived) {
    _channel.setMethodCallHandler(_handleMethod);
  }

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case "onSmsReceived":
        if (onSmsReceived != null) {
          onSmsReceived(call.arguments);
        }
        break;
    }
  }

  Future<bool> startListening({phoneNumberFilter: String}) async {
    bool result =
        await _channel.invokeMethod('startListening', phoneNumberFilter);
    return result;
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}