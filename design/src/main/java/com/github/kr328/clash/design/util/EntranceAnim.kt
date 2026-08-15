package com.github.kr328.clash.design.util

import android.view.View

/**
 * 列表项入场动画：滚动时新 item 淡入上浮，提升界面质感。
 * 用 position 做延迟形成级联效果。
 * 通过 view tag 标记防止 RecyclerView 滚动复用时的重复闪烁动画。
 */
object EntranceAnim {
    fun animate(view: View, position: Int) {
        // 已播放过动画的 item 不再重复触发（避免滚动时闪烁）
        if (view.getTag(R.id.entrance_anim_played) != null) return

        view.alpha = 0f
        view.translationY = 24f * view.resources.displayMetrics.density
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(260)
            .setStartDelay((position % 12) * 24L)
            .withEndAction {
                view.setTag(R.id.entrance_anim_played, true)
            }
            .start()
    }
}