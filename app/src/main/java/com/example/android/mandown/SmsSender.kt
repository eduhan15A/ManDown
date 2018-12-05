package com.example.android.mandown

import android.telephony.SmsManager

class SmsSender {


    public fun sendSMS(phoneNumber: String, message: String) {
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, message, null, null)
    }
}