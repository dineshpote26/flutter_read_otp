package com.dinesh.read_otp_plugin

import android.Manifest
import android.app.Activity
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.app.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** ReadOtpPlugin */
class ReadOtpPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, FlutterActivity() {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private val smsReceiver by lazy { SmsReceiver() }

  private var isListening: Boolean = false

  private var filterNumber : String? = null

  val TAG:String = "ReadOtp"

  private lateinit var activity: Activity

  val PERMISSION_REQUEST_CODE = 12

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "read_otp_plugin")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromActivity() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity = binding.activity;
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if(call.method == "startListening"){

      if(call.arguments!=null){
        filterNumber = call.arguments as String
      }


      Log.d(TAG, "filterNumber"+filterNumber)
      synchronized(this){
        if(!isListening){
          isListening = true
          requestReadAndSendSmsPermission()

          if(hasReadSmsPermission()){
            Log.d(TAG, "hasReadSmsPermission")
            filterNumber?.let { startListening(it) };
          }

          Log.d(TAG, "SMS Receiver Registered")
          result.success(true)
        }else{
          Log.d(TAG, "Preventing register multiple SMS receiver")
          result.success(false)
        }
      }
    }else if(call.method == "unRegisterListening"){
      unRegisterListening()
    } else {
      result.notImplemented()
    }
  }

  private fun hasReadSmsPermission(): Boolean {
    return ContextCompat.checkSelfPermission(activity,
            Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
  }

  private fun startListening(filterNumber : String) {
    Log.d("dinesh","startListening");
    var listener =  object:SmsReceiver.Listener{
      override fun onSmsReceived(otp: String) {
        channel.invokeMethod("onSmsReceived", otp)
        activity.unregisterReceiver(smsReceiver)
        isListening = false
      }
    }
    smsReceiver.setListener(listener)
    smsReceiver.setPhoneNumberFilter(filterNumber)
    val intentFilter = IntentFilter()
    intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
    activity.registerReceiver(smsReceiver,intentFilter)
  }

  private fun unRegisterListening(){
    Log.d("dinesh","unRegisterListening");
    activity.unregisterReceiver(smsReceiver)
    isListening = false
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private fun requestReadAndSendSmsPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
      return
    }
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
            PERMISSION_REQUEST_CODE)
  }
}
