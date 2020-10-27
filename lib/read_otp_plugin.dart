import 'dart:async';
import 'dart:io';

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

  Future<bool> startListening({providerName: String}) async {
    if (Platform.isAndroid) {
      bool smsCode =
          await _channel.invokeMethod('startListening', providerName);
      return smsCode;
    } else {
      return null;
    }
  }

  Future<void> unRegisterListening() async {
    if (Platform.isAndroid) {
      await _channel.invokeMethod('unRegisterListening');
    } else {
      return null;
    }
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
