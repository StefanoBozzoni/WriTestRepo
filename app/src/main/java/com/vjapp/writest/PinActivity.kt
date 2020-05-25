package com.vjapp.writest

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CompletableDeferred

/**
 * Wraps the parameters of onActivityResult
 *
 * @property resultCode the result code returned from the activity.
 * @property data the optional intent returned from the activity.
 */
class ActivityResult(
    val resultCode: Int,
    val data: Intent?) {
}

interface pinRequest {
    public fun launchIntent(context:Context,intent: Intent) : CompletableDeferred<ActivityResult?>
}

class PinActivity : pinRequest {

    var currentCode : Int = 0
    var resultByCode = mutableMapOf<Int, CompletableDeferred<ActivityResult?>>()

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultByCode[requestCode]?.let {
            it.complete(ActivityResult(resultCode, data))
            resultByCode.remove(requestCode)
        } ?: run {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    */

    /**
     * Launches the intent allowing to process the result using await()
     * @param intent the intent to be launched.
     * @return Deferred<ActivityResult>
     */

    override fun launchIntent(context: Context, intent: Intent) : CompletableDeferred<ActivityResult?>
    {
        val activityResult = CompletableDeferred<ActivityResult?>()

        if (intent.resolveActivity(context.packageManager) != null) {
            val resultCode = currentCode++
            resultByCode[resultCode] = activityResult
            (context as MainActivity).startActivityForResult(intent, resultCode)
        } else {
            activityResult.complete(null)
        }
        return activityResult
    }
}