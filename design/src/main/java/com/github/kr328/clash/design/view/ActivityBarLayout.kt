package com.github.kr328.clash.design.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.util.resolveThemedColor

class ActivityBarLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    init {
        alpha = 0.92f

        // 玻璃质感工具栏：半透明玻璃色 + 底部高光描边（带 fallback，防止主题属性缺失时透明不可见）
        val glass = context.resolveThemedColor(R.attr.colorGlass)
        val fallbackSurface = context.resolveThemedColor(com.google.android.material.R.attr.colorSurface)
        val stroke = context.resolveThemedColor(R.attr.colorGlassStroke)
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(if (glass != 0) glass else fallbackSurface)
            if (stroke != 0) {
                setStroke(1, stroke)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)

        return true
    }
}