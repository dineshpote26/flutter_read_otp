# read_otp_plugin

Flutter plugin for reading incoming-and-expected SMS only using read sms permission

Currently developed for Android

**Follow below steps **

**1. Add Permission**

Add all wanted permissions to your app `android/app/src/main/AndroidManifest.xml` file:
```xml
 <uses-permission android:name="android.permission.RECEIVE_SMS" />
 <uses-permission android:name="android.permission.READ_SMS" />

```

**2. StartListening**

_smsReceiver.startListening(providerName: "Example");

**3. UnRegisterListening**

_smsReceiver.unRegisterListening();

![Read OTP](https://github.com/dineshpote26/flutter_read_otp/blob/master/screenshot/example2.jpeg)