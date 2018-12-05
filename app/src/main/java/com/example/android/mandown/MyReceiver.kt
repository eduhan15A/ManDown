package com.example.android.mandown

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class MyReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
       /*     val pdu = (intent?.extras?.get("pdus") as? Array<*>)?.get(0) as? ByteArray
            pdu?.let {
                val message = SmsMessage.createFromPdu(it)
                Log.d("TAG","Received")
                Toast.makeText(context, "Phone : ${message.displayOriginatingAddress} \n Text : ${message.displayMessageBody}", Toast.LENGTH_LONG).show()
                Log.d("TAG","Phone : ${message.displayOriginatingAddress} \n Text : ${message.displayMessageBody}")

            }*/

        val extras = intent.extras
        if (extras != null){
            val sms = extras.get("pdus") as Array<Any>
            for(i in sms.indices){
                val format = extras.getString("format")

                var smsMessage = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[i] as ByteArray,format)

                }else{
                    SmsMessage.createFromPdu(sms[i] as ByteArray)

                }
                val phoneNumber = smsMessage.originatingAddress
                val messageText = smsMessage.messageBody.toString()
                var messageToast: CharSequence = "phone number" + phoneNumber+ "  message: "+ messageText
                //Toast.makeText(context,messageToast,Toast.LENGTH_LONG).show()
                val inst = Main2Activity.instances()
                inst.goForward(phoneNumber,messageText)
            }


        }


    }


    }

