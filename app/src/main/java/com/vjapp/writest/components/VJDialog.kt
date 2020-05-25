package com.vjapp.writest.components

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import com.vjapp.writest.R
import kotlin.math.truncate

class VJDialog(val contextActivity: FragmentActivity, val layoutResId: Int, val matchParent:Boolean=false): Dialog(contextActivity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        setContentView(layoutResId)

        val dpWidth2=getDialogDefaultWidth(contextActivity)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window!!.attributes)

        if (!matchParent)
        {lp.width = truncate(dpWidth2 * 0.9).toInt()
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        else
        {lp.width  = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
        }

        if (window!=null) {
            if (!matchParent) window!!.setBackgroundDrawableResource(R.drawable.round_corners)
            else window!!.setBackgroundDrawableResource(R.drawable.small_round_corners)
            window!!.attributes = lp
        }
    }


    fun getDialogDefaultWidth(context: FragmentActivity):Int {
        val display = context.getWindowManager().getDefaultDisplay()
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val dpWidth = outMetrics.widthPixels
        return dpWidth
    }

}