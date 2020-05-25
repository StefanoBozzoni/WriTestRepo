package com.vjapp.writest.components

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ViewFlipper
import com.vjapp.writest.R

class ViewFlipperEng : ViewFlipper {
    private var initialPage:Int=0

    constructor(context: Context) : super(context){
        initialPage=0
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        // Attribute initialization
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewFlipperEng, 0, 0)
        initialPage = a.getInt(R.styleable.ViewFlipperEng_displayedChild,0)
        a.recycle()
    }

    override fun onAttachedToWindow()
    {
        if (initialPage!=0) displayedChild=initialPage
        super.onAttachedToWindow()
    }
}