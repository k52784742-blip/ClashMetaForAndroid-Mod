package com.github.kr328.clash.design.component

import android.content.Context
import android.graphics.Color
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.util.getPixels
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.resolveThemedResourceId

class ProxyViewConfig(val context: Context, var proxyLine: Int) {
    private val colorGlass = context.resolveThemedColor(R.attr.colorGlass)
    private val colorSurface = context.resolveThemedColor(com.google.android.material.R.attr.colorSurface)
    private val effectiveGlassBackground = if (colorGlass != 0) colorGlass else colorSurface

    val glassStroke = context.resolveThemedColor(R.attr.colorGlassStroke)

    val clickableBackground =
        context.resolveThemedResourceId(android.R.attr.selectableItemBackground)

    val selectedControl = context.resolveThemedColor(com.google.android.material.R.attr.colorOnPrimary)
    val selectedBackground = context.resolveThemedColor(com.google.android.material.R.attr.colorPrimary)

    /** 选中节点高亮描边色（品牌紫） */
    val selectedStrokeColor = context.resolveThemedColor(com.google.android.material.R.attr.colorPrimary)

    val unselectedControl = context.resolveThemedColor(com.google.android.material.R.attr.colorOnSurface)
    val unselectedBackground: Int
        get() = if (proxyLine==1) Color.TRANSPARENT else effectiveGlassBackground

    val layoutPadding = context.getPixels(R.dimen.proxy_layout_padding).toFloat()
    val contentPadding
        get() = if (proxyLine==2) context.getPixels(R.dimen.proxy_content_padding).toFloat() else context.getPixels(R.dimen.proxy_content_padding_grid3).toFloat()
    val textMargin
        get() = if (proxyLine==2) context.getPixels(R.dimen.proxy_text_margin).toFloat() else context.getPixels(R.dimen.proxy_text_margin_grid3).toFloat()
    val textSize
        get() = if (proxyLine==2) context.getPixels(R.dimen.proxy_text_size).toFloat() else context.getPixels(R.dimen.proxy_text_size_grid3).toFloat()

    val shadow = Color.argb(
        0x15,
        Color.red(Color.DKGRAY),
        Color.green(Color.DKGRAY),
        Color.blue(Color.DKGRAY),
    )

    val cardRadius = context.getPixels(R.dimen.proxy_card_radius).toFloat()
    var cardOffset = context.getPixels(R.dimen.proxy_card_offset).toFloat()
}