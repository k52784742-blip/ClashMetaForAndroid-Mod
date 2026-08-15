package com.github.kr328.clash.design.util

import android.view.View
import com.github.kr328.clash.design.R

/**
 * 列表项入场动画：滚动时新 item 淡入上浮，提升界面质感。
 * 用 position 做延迟形成级联效果。
 * 通过 view tag 标记防止 RecyclerView 滚动复用时的重复闪烁动画。
 *
 * 性能优化要点：
 * - 短级联延迟（8×16ms）避免尾部拖沓
 * - 启用硬件加速层减少栅格化开销
 * - 复用 tag 对象避免 withEndAction 闭包分配
 */
object EntranceAnim {
    private val TAG_PLAYED = true

    fun animate(view: View, position: Int) {
        // 已播放过动画的 item 不再重复触发（避免滚动时闪烁）
        if (view.getTag(R.id.entrance_anim_played) != null) {
            // 复用时确保可见状态正确（防止回收时 alpha 未恢复）
            view.alpha = 1f
            view.translationY = 0f
            return
        }

        view.alpha = 0f
        view.translationY = 16f * view.resources.displayMetrics.density
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(200)
            .setStartDelay((position % 8) * 16L)
            .withLayer()
            .withEndAction {
                view.setTag(R.id.entrance_anim_played, TAG_PLAYED)
                view.alpha = 1f
                view.translationY = 0f
                view.setLayerType(View.LAYER_TYPE_NONE, null)
            }
            .start()
    }
}