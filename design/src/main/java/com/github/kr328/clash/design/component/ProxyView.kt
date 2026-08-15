package com.github.kr328.clash.design.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.github.kr328.clash.common.compat.getDrawableCompat

class ProxyView(
    context: Context,
    config: ProxyViewConfig,
) : View(context) {

    init {
        background = context.getDrawableCompat(config.clickableBackground)
    }

    var state: ProxyViewState? = null
        set(value) {
            field = value
            // 状态变化时触发重绘
            shadowDirty = true
            postInvalidateOnAnimation()
        }

    /** 阴影缓存脏标记：true 时需要重绘阴影层 */
    private var shadowDirty = true
    /** 代理组布局模式缓存 */
    private var lastProxyLine = 0

    constructor(context: Context) : this(context, ProxyViewConfig(context, 2))
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val state = state ?: return super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED ->
                resources.displayMetrics.widthPixels
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY ->
                MeasureSpec.getSize(widthMeasureSpec)
            else ->
                throw IllegalArgumentException("invalid measure spec")
        }

        state.paint.apply {
            reset()

            textSize = state.config.textSize

            getTextBounds("Stub!", 0, 1, state.rect)
        }

        val textHeight = state.rect.height()
        val exceptHeight = (state.config.layoutPadding * 2 +
                state.config.contentPadding * 2 +
                textHeight * 2 +
                state.config.textMargin).toInt()

        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED ->
                exceptHeight
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY ->
                exceptHeight.coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
            else ->
                throw IllegalArgumentException("invalid measure spec")
        }

        setMeasuredDimension(width, height)
    }

    override fun draw(canvas: Canvas) {
        val state = state ?: return super.draw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val paint = state.paint

        // 背景填充（无阴影时直接绘制，避免离屏渲染开销）
        paint.reset()
        paint.color = state.background
        paint.style = Paint.Style.FILL

        if (state.config.proxyLine == 1) {
            // 单列模式：纯色矩形，无需路径/阴影
            canvas.drawRect(0f, 0f, width, height, paint)
            super.draw(canvas)
            return
        }

        // 多列模式：圆角卡片路径
        val path = state.path
        path.reset()
        path.addRoundRect(
            state.config.layoutPadding,
            state.config.layoutPadding,
            width - state.config.layoutPadding,
            height - state.config.layoutPadding,
            state.config.cardRadius,
            state.config.cardRadius,
            Path.Direction.CW,
        )

        // 阴影层：只在脏时重绘
        if (shadowDirty) {
            paint.setShadowLayer(
                state.config.cardRadius,
                state.config.cardOffset,
                state.config.cardOffset,
                state.config.shadow
            )
            shadowDirty = false
        }

        // 绘制卡片背景
        canvas.drawPath(path, paint)

        // 玻璃描边
        if (state.config.glassStroke != 0) {
            paint.reset()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color = state.config.glassStroke
            canvas.drawPath(path, paint)
        }

        // 选中高亮描边
        if (state.isSelected && state.config.selectedStrokeColor != 0) {
            paint.reset()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.color = state.config.selectedStrokeColor
            paint.setShadowLayer(
                state.config.cardRadius * 0.6f,
                0f, 0f,
                state.config.selectedStrokeColor
            )
            canvas.drawPath(path, paint)
            paint.setShadowLayer(0f, 0f, 0f, 0)
        }

        // 裁剪到圆角区域（避免子 View 溢出）
        canvas.clipPath(path)

        // 绘制子 View（文本内容）
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val state = state ?: return

        val paint = state.paint

        val width = width.toFloat()
        val height = height.toFloat()

        paint.textSize = state.config.textSize

        // measure delay text bounds
        val delayCount = paint.breakText(
            state.delayText,
            false,
            (width - state.config.layoutPadding * 2 - state.config.contentPadding * 2)
                .coerceAtLeast(0f),
            null
        )

        state.paint.getTextBounds(state.delayText, 0, delayCount, state.rect)

        val delayWidth = state.rect.width()

        val mainTextWidth = (width -
                state.config.layoutPadding * 2 -
                state.config.contentPadding * 2 -
                delayWidth -
                state.config.textMargin * 2
                )
            .coerceAtLeast(0f)

        // measure title text bounds
        val titleCount = paint.breakText(
            state.title,
            false,
            mainTextWidth,
            null,
        )

        // measure subtitle text bounds
        val subtitleCount = paint.breakText(
            state.subtitle,
            false,
            mainTextWidth,
            null,
        )

        // text draw measure
        val textOffset = (paint.descent() + paint.ascent()) / 2

        // delay 延迟文本着色：好延迟绿色，中延迟橙色，高延迟红色
        val delayColor = when {
            state.delay in 0..200 -> 0xFF10B981.toInt()   // 绿色
            state.delay in 201..500 -> 0xFFF59E0B.toInt() // 橙色
            else -> state.config.unselectedControl         // 默认色
        }

        paint.reset()

        paint.textSize = state.config.textSize
        paint.isAntiAlias = true
        paint.color = delayColor

        // draw delay
        canvas.apply {
            val x = width - state.config.layoutPadding - state.config.contentPadding - delayWidth
            val y = height / 2f - textOffset

            drawText(state.delayText, 0, delayCount, x, y, paint)
        }

        // draw title
        paint.reset()
        paint.textSize = state.config.textSize
        paint.isAntiAlias = true
        paint.color = state.controls

        canvas.apply {
            val x = state.config.layoutPadding + state.config.contentPadding
            val y = state.config.layoutPadding +
                    (height - state.config.layoutPadding * 2) / 3f - textOffset

            drawText(state.title, 0, titleCount, x, y, paint)
        }

        // draw subtitle
        canvas.apply {
            val x = state.config.layoutPadding + state.config.contentPadding
            val y = state.config.layoutPadding +
                    (height - state.config.layoutPadding * 2) / 3f * 2 - textOffset

            drawText(state.subtitle, 0, subtitleCount, x, y, paint)
        }
    }
}