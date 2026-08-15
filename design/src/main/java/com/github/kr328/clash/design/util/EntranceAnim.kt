package com.github.kr328.clash.design.util

import android.view.View
import com.github.kr328.clash.design.R

/**
 * 列表项入场动画：滚动时新 item 淡入上浮，提升界面质感。
 * 用 position 做延迟形成级联效果。
 * 通过 view tag 标记防止 RecyclerView 滚动复用时的重复闪烁动画。
 */
object EntranceAnim {
    fun animate(view: View, position: Int) {
        // 已播放过动画的 item 不再重复触发（避免滚动时闪烁）
        if (view.getTag(R.id.entrance_anim_played) != null) {
            // 复用时确保可见状态正确（防止回收时 alpha 未恢复）
            view.alpha = 1f
            view.translationY = 0f
            return
        }

        view.alpha = 0f
        view.translationY = 24f * view.resources.displayMetrics.density
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(260)
            .setStartDelay((position % 12) * 24L)
            .withEndAction {
                view.setTag(R.id.entrance_anim_played, true)
                // 动画结束后确保状态正确
                view.alpha = 1f
                view.translationY = 0f
            }
            .start()
    }
}