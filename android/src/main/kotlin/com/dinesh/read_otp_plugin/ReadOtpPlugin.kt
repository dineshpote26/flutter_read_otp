package com.dinesh.read_otp_plugin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/** ReadOtpPlugin */
class ReadOtpPlugin : FlutterPlugin, MethodCallHandler, PluginRegistry.RequestPermissionsResultListener, ActivityAware {

  private lateinit var channel : MethodChannel

  private val smsReceiver by lazy { SmsReceiver() }

  private var isListening: Boolean = false

  private var filterNumber : String? = null

  val TAG:String = "ReadOtp"

  private lateinit var activity: Activity

  private lateinit var applicationContext: Context

  val PERMISSION_REQUEST_CODE = 12
  private var permissionGranted: Boolean = false

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    this.applicationContext = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "read_otp_plugin")
    channel.setMethodCallHandler(this)
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

          checkPermission()

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
    Log.d(TAG,"startListening");
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
    intentFilter.priority =500
    activity.registerReceiver(smsReceiver,intentFilter)
  }

  private fun unRegisterListening(){
    Log.d(TAG,"unRegisterListening");

    if(isListening){
      activity.unregisterReceiver(smsReceiver)
    }
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

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
    Log.d(TAG, "checkPermission requestCode $requestCode")
    when (requestCode) {
      PERMISSION_REQUEST_CODE -> {
        if ( null != grantResults ) {
          permissionGranted = grantResults.isNotEmpty() &&
                  grantResults.get(0) == PackageManager.PERMISSION_GRANTED
        }
        filterNumber?.let { startListening(it) };
        // Only return true if handling the requestCode
        return true
      }
    }
    return false
  }

  // If permission is already granted this completes initialization, otherwise it requests
  // a permission check from the system. This is using the READ_EXTERNAL_STORAGE
  // permission as an example. Change that to whatever permission(s) your app needs.
  private fun checkPermission() {
    Log.d(TAG, "checkPermission")
    permissionGranted = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    if ( !permissionGranted ) {
      ActivityCompat.requestPermissions(activity,
              arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), PERMISSION_REQUEST_CODE )
    }
    else {
      filterNumber?.let { startListening(it) };
    }
  }

  override fun onDetachedFromActivity() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    this.activity =  binding.activity
    binding.addRequestPermissionsResultListener(this)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity =  binding.activity
    binding.addRequestPermissionsResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }
}
