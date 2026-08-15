package com.github.kr328.clash.design.component

import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.model.ProxyState

class ProxyViewState(
    val config: ProxyViewConfig,
    val proxy: Proxy,
    private val parent: ProxyState,
    private val link: ProxyState?
) {
    val paint = Paint()
    val rect = Rect()
    val path = Path()

    var title: String = ""
    var subtitle: String = ""
    var delayText: String = ""
    var background: Int = config.unselectedBackground
    var controls: Int = config.unselectedControl

    var delay: Int = 0
    private var selected: Boolean = false
    private var parentNow: String = ""
    private var linkNow: String? = null

    /** 当前节点是否为选中节点（供 ProxyView 绘制选中高亮描边） */
    val isSelected: Boolean
        get() = selected

    /** 更新视图状态，返回是否需要重绘 */
    fun update(snap: Boolean): Boolean {
        var invalidate = false

        if (proxy.isGroup) {
            title = proxy.name

            if (link == null) {
                subtitle = proxy.type
            } else {
                // 用值比较（!=）而非引用比较（!==），避免字符串内容相同但引用不同时不更新
                if (linkNow != link.now) {
                    linkNow = link.now

                    subtitle = "%s(%s)".format(
                        proxy.type,
                        link.now.ifEmpty { "*" }
                    )
                }
            }
        } else {
            title = proxy.title
            subtitle = proxy.subtitle
        }

        if (delay != proxy.delay) {
            delay = proxy.delay
            delayText = if (proxy.delay in 0..Short.MAX_VALUE) proxy.delay.toString() else ""
        }

        // 用值比较（!=）而非引用比较（!==），确保 now 变化时正确更新选中状态
        if (parentNow != parent.now) {
            parentNow = parent.now
            selected = proxy.name == parent.now
        }

        controls = if (selected) config.selectedControl else config.unselectedControl

        // 背景切换：不用帧动画插值，直接跳转（减少每帧 postInvalidate 开销）
        val target = if (selected) config.selectedBackground else config.unselectedBackground
        if (background != target) {
            background = target
            invalidate = true
        }

        return invalidate
    }
}