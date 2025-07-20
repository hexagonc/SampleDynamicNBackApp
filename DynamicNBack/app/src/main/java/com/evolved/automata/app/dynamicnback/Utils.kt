package com.evolved.automata.app.dynamicnback

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import com.evolved.automata.app.dynamicnback.di.toneType
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun <T> T?.illegalIfNull(exception:Throwable?=null): T {
    if (this == null) {
        if (exception != null)
            throw exception
        else
            throw IllegalStateException("Unexpected null pointer")
    }
    else
        return this
}


fun formatWithSignificantDigits(value: Double, digits: Int): String {
    val pattern = "#.${"#".repeat(digits - 1)}"
    val df = DecimalFormat(pattern)
    df.isGroupingUsed = false
    df.maximumFractionDigits = digits

    return df.format(value)
}

fun formatWithSignificantDigits(value: Float, digits: Int): String {
    return formatWithSignificantDigits(value.toDouble(), digits)
}

fun showDatetimeMoment(ts:Long): String {
    val format= "EEE, MMM dd yyyy hh:mm:ss a"
    val dateformat = SimpleDateFormat(format, Locale.getDefault())
    return dateformat.format(Date(ts))
}

fun serializeDataClassInstance(obj:Any): String {
    val gson = Gson()
    return gson.toJson(obj)
}

fun <T> deserializeDataClassString(serialized:String, clz:Class<T>): T {
    return Gson().fromJson<T>(serialized, clz)
}

fun getRingtonePickerIntent(context:Context):Intent {
    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, toneType)
        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select correct match ringtone")
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
            RingtoneManager.getActualDefaultRingtoneUri(context, toneType)
        )
    }
    return intent
}

