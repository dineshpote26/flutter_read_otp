package com.dinesh.read_otp_plugin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import java.util.regex.Matcher
import java.util.regex.Pattern


class SmsReceiver : BroadcastReceiver(){

    private var listener : Listener?= null
    private var phoneNumberFilter : String ?= null

    fun setListener(listener: Listener){
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("dinesh","onReceive");
        val bundle = intent!!.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle["pdus"] as Array<Any>?
                for (i in pdusObj!!.indices) {
                    val currentMessage: SmsMessage = getIncomingMessage(pdusObj[i], bundle)
                    val phoneNumber: String = currentMessage.getDisplayOriginatingAddress()
                    if (phoneNumberFilter != null && !phoneNumber.contains(phoneNumberFilter!!)) {
                        return
                    }
                    val message: String = currentMessage.getDisplayMessageBody()
                    listener?.onSmsReceived(parseCode(message))
                } // end for loop
            } // bundle is null
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")
        }
    }

    private fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage {
        val currentSMS: SmsMessage
        currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString("format")
            SmsMessage.createFromPdu(aObject as ByteArray, format)
        } else {
            SmsMessage.createFromPdu(aObject as ByteArray)
        }
        return currentSMS
    }

    fun setPhoneNumberFilter(phoneNumberFilter: String) {
        this.phoneNumberFilter = phoneNumberFilter
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
   fun parseCode(message: String): String {
        val p = Pattern.compile("\\b\\d{6}\\b")
        val m: Matcher = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        return code
    }

    interface Listener {
        fun onSmsReceived(otp: String)
    }
}