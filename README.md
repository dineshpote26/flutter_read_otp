# read_otp_plugin

Flutter plugin for reading incoming-and-expected SMS only

Currently developed for Android: reading message without requesting SMS permission

#Example

ReadOtpPlugin _smsReceiver = ReadOtpPlugin(onSmsReceived);

#1. StartListening

_smsReceiver.startListening(phoneNumberFilter: "8169920332");

#2. UnRegisterListening

_smsReceiver.unRegisterListening();

![Read OTP](https://github.com/dineshpote26/flutter_read_otp/blob/master/screenshot/example2.jpeg)