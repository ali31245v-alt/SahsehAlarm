package com.sahseh.alarm
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class AlarmReceiver:BroadcastReceiver(){override fun onReceive(context:Context,intent:Intent?){val i=Intent(context,ChallengeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP);try{context.startActivity(i)}catch(e:Exception){}}}