package com.github.kr328.clash.design.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.core.graphics.drawable.DrawableCompat
import com.github.kr328.clash.design.R

/**
 * 统一的图标装饰工具：
 * 玻璃圆形底托 + 主题色着色，让所有列表项图标有统一的高级质感。
 * 全程使用 mutate() 防止污染共享 drawable。
 * tint 可传 0 表示不强制着色（保留原图标自身颜色/渐变）。
 */
fun Context.decorateIcon(
    icon: Drawable?,
    tint: Int,
    insetDp: Int = 5,
    glassColor: Int = 0,
    strokeColor: Int = 0,
): Drawable? {
    if (icon == null) return null

    // 着色（mutate 防止污染原始资源，先 mutate 再 wrap 确保 tint 独立）
    val tinted = if (tint != 0) {
        val mutated = icon.mutate()
        DrawableCompat.wrap(mutated).also {
            DrawableCompat.setTint(it, tint)
        }
    } else {
        icon
    }

    // 玻璃圆形底托
    val resolvedGlass = if (glassColor != 0) glassColor else resolveThemedColor(R.attr.colorGlass)
    val resolvedStroke = if (strokeColor != 0) strokeColor else resolveThemedColor(R.attr.colorGlassStroke)

    val backgroundDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(if (resolvedGlass != 0) resolvedGlass else 0x4DFFFFFF.toInt())
        setStroke(1, if (resolvedStroke != 0) resolvedStroke else 0x33FFFFFF.toInt())
    }

    val inset = (resources.displayMetrics.density * insetDp).toInt()

    return LayerDrawable(arrayOf(backgroundDrawable, tinted)).apply {
        setLayerInset(1, inset, inset, inset, inset)
    }
}
