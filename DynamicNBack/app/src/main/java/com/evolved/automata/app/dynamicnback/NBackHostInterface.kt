package com.evolved.automata.app.dynamicnback

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import com.evolved.automata.app.dynamicnback.repo.ProfileRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NBackHostInterface(val context:Context, override val topScope: CoroutineScope, val profileRepo: ProfileRepo,
                         override val logger: Logger = NBackLogger(), val ringtoneManager: RingtoneManager
): HostInterface {

    override fun playRingtone(uri: String) {
        topScope.launch(Dispatchers.Default) {
            val pos = ringtoneManager.getRingtonePosition(Uri.parse(uri))
            val ringtone = ringtoneManager.getRingtone(pos)
            ringtone.play()
            delay(1500)
            ringtone.stop()
        }


    }

}