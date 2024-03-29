package com.chordz.epracharbuzz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.chordz.epracharbuzz.data.ElectionDataHolder.msgDetails
import com.chordz.epracharbuzz.MainActivity
import com.chordz.epracharbuzz.preferences.AppPreferences
import com.chordz.epracharbuzz.preferences.AppPreferences.getBooleanValueFromSharedPreferences

class EndCallReceiver : BroadcastReceiver() {
    private var serviceManager: AccessibilityServiceManager? = null
    private var wm: WindowManager? = null
    private var params1: WindowManager.LayoutParams? = null
    private var phoneNumber: String = ""
    override fun onReceive(context: Context, intent: Intent) {
        /*if (!validate(context)) {
            return
        }*/
        val bundle = intent.extras
        phoneNumber = bundle!!.getString("incoming_number").toString()

        val phoneStateString = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        Log.e("TAG", "onReceive: $phoneNumber $phoneStateString")
        if (phoneNumber != null && !phoneNumber!!.isEmpty() && (phoneStateString!!.contains("OFFHOOK") || phoneStateString.contains(
                "RINGING"
            ))
        ) {
//            openWhatsApp(context, phoneNumber!!)
            showWindow(context, phoneNumber!!)
        }
    }

    private fun showWindow(context: Context, phoneNumber: String) {
        wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params1 = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
        params1!!.height = 75
        params1!!.width = 512
        params1!!.x = 265
        params1!!.y = 400
        params1!!.format = PixelFormat.TRANSLUCENT
        ly1 = LinearLayout(context)
        ly1!!.setBackgroundColor(Color.TRANSPARENT)
        val textView = TextView(context)
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        textView.text = "E-Prachar"
        ly1!!.addView(textView)
        if (getBooleanValueFromSharedPreferences(AppPreferences.WHATSAPP_ON_OFF)) {
            ly1!!.setOnClickListener { v -> //                openWhatsApp(context, phoneNumber);
                val intent = Intent(ly1!!.context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("PHONE_NUMBER", phoneNumber)
                v.context.startActivity(intent)
                wm!!.removeView(ly1)
            }
        }
        ly1!!.orientation = LinearLayout.VERTICAL
        wm!!.addView(ly1, params1)
        ly1!!.callOnClick()
    }

    private fun openWhatsApp(context: Context, phoneNumber: String) {
        serviceManager = AccessibilityServiceManager(context)
        if (serviceManager!!.hasAccessibilityServicePermission(MyAccessibilityService::class.java)) {
            val response = msgDetails
            if (response != null && response.data != null) {
                val details = response.data
                val defaultMessage = details[0]!!.aMessage
                val defaultImage = details[0]!!.aImage
                sendSMSMessage(phoneNumber, defaultMessage)

                //Code For New Line
//                String formattedMessage = defaultMessage.replace("|", "\n");


                // Format the phone number to include the country code (e.g., +1 for the US)
                val formattedPhoneNumber = formatPhoneNumber(phoneNumber)

                // Create an Intent to open WhatsApp with the specified phone number and default message
                val whatsappIntent = Intent(Intent.ACTION_SEND)
                whatsappIntent.data = Uri.parse(
                    "https://wa.me/" + formattedPhoneNumber + "?text=" + Uri.encode(defaultMessage)
                )

                // Add FLAG_ACTIVITY_NEW_TASK flag
                whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                // Start the activity
                context.startActivity(whatsappIntent);
                Log.e("TAG", "onReceive: MyAccessibilityServicephoneNumber: $formattedPhoneNumber")
            }
        } else {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            //            serviceManager.requestUserForAccessibilityService( context);
        }
    }


    private fun sendSMSMessage(phoneNumber: String, defaultMessage: String?) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, phoneNumber, defaultMessage, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openWhatsAppIntent(context: Context) {
        val intent = Intent()
        intent.setPackage("com.whatsapp.w4b")
        context.startActivity(intent)
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        // Implement your phone number formatting logic if needed
        return phoneNumber
    }

    internal interface OnPhoneStateReceived {
        fun onPhoneStateReceived(phoneNumber: String?)
    }

    companion object {
        private var ly1: LinearLayout? = null
    }
}