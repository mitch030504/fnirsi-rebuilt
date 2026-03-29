package com.uct.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.internal.view.SupportMenu
import com.officialwebsite.R
import kotlin.math.max

class ShadowContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {
    private var shadowColor: Int
    private var shadowRadius: Float
    private val deltaLength: Float
    private val cornerRadius: Float
    private var dx: Float
    private var dy: Float
    private var drawShadow: Boolean
    private val shadowPaint: Paint =
        Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowContainer)
        shadowColor =
            typedArray.getColor(
                R.styleable.ShadowContainer_containerShadowColor,
                SupportMenu.CATEGORY_MASK,
            )
        shadowRadius = typedArray.getDimension(R.styleable.ShadowContainer_containerShadowRadius, 0.0f)
        deltaLength = typedArray.getDimension(R.styleable.ShadowContainer_containerDeltaLength, 0.0f)
        cornerRadius = typedArray.getDimension(R.styleable.ShadowContainer_containerCornerRadius, 0.0f)
        dx = typedArray.getDimension(R.styleable.ShadowContainer_deltaX, 0.0f)
        dy = typedArray.getDimension(R.styleable.ShadowContainer_deltaY, 0.0f)
        drawShadow = typedArray.getBoolean(R.styleable.ShadowContainer_drawShadow, true)
        typedArray.recycle()
        updateShadowPaint()
    }

    fun setcolor(color: Int) {
        shadowColor = color
        updateShadowPaint()
        postInvalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (drawShadow && childCount > 0) {
            if (layerType != LAYER_TYPE_SOFTWARE) {
                setLayerType(LAYER_TYPE_SOFTWARE, null)
            }
            val child = getChildAt(0)
            val left = child.left.toFloat()
            val top = child.top.toFloat()
            val right = child.right.toFloat()
            val bottom = child.bottom.toFloat()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, shadowPaint)
            } else {
                val path = Path()
                path.moveTo(cornerRadius + left, top)
                path.arcTo(RectF(left, top, (cornerRadius * 2.0f) + left, (cornerRadius * 2.0f) + top), -90.0f, -90.0f, false)
                path.lineTo(left, bottom - cornerRadius)
                path.arcTo(
                    RectF(left, bottom - (cornerRadius * 2.0f), (cornerRadius * 2.0f) + left, bottom),
                    180.0f,
                    -90.0f,
                    false,
                )
                path.lineTo(right - cornerRadius, bottom)
                path.arcTo(
                    RectF(right - (cornerRadius * 2.0f), bottom - (cornerRadius * 2.0f), right, bottom),
                    90.0f,
                    -90.0f,
                    false,
                )
                path.lineTo(right, cornerRadius + top)
                path.arcTo(
                    RectF(right - (cornerRadius * 2.0f), top, right, (cornerRadius * 2.0f) + top),
                    0.0f,
                    -90.0f,
                    false,
                )
                path.close()
                canvas.drawPath(path, shadowPaint)
            }
        }
        super.dispatchDraw(canvas)
    }

    fun setDrawShadow(enabled: Boolean) {
        if (drawShadow == enabled) {
            return
        }
        drawShadow = enabled
        postInvalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        check(childCount == 1) { "子View只能有一个" }

        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight
        val child = getChildAt(0)
        val layoutParams = child.layoutParams as LayoutParams
        val bottomInset = (max(deltaLength, layoutParams.bottomMargin.toFloat()) + 1.0f).toInt()
        val leftInset = (max(deltaLength, layoutParams.leftMargin.toFloat()) + 1.0f).toInt()
        val rightInset = (max(deltaLength, layoutParams.rightMargin.toFloat()) + 1.0f).toInt()
        val topInset = (max(deltaLength, layoutParams.topMargin.toFloat()) + 1.0f).toInt()

        val childWidthMode: Int
        val childWidth: Int
        when {
            MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED -> {
                childWidth = MeasureSpec.getSize(widthMeasureSpec)
                childWidthMode = MeasureSpec.UNSPECIFIED
            }

            layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT -> {
                childWidth = (measuredWidth - leftInset) - rightInset
                childWidthMode = MeasureSpec.EXACTLY
            }

            layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                childWidth = (measuredWidth - leftInset) - rightInset
                childWidthMode = MeasureSpec.AT_MOST
            }

            else -> {
                childWidth = layoutParams.width
                childWidthMode = MeasureSpec.EXACTLY
            }
        }

        val childHeightMode: Int
        val childHeight: Int
        when {
            MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED -> {
                childHeight = MeasureSpec.getSize(heightMeasureSpec)
                childHeightMode = MeasureSpec.UNSPECIFIED
            }

            layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT -> {
                childHeight = (measuredHeight - bottomInset) - topInset
                childHeightMode = MeasureSpec.EXACTLY
            }

            layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                childHeight = (measuredHeight - bottomInset) - topInset
                childHeightMode = MeasureSpec.AT_MOST
            }

            else -> {
                childHeight = layoutParams.height
                childHeightMode = MeasureSpec.EXACTLY
            }
        }

        measureChild(
            child,
            MeasureSpec.makeMeasureSpec(childWidth, childWidthMode),
            MeasureSpec.makeMeasureSpec(childHeight, childHeightMode),
        )

        var resolvedHeight =
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
                topInset + child.measuredHeight + bottomInset
            } else {
                measuredHeight
            }
        var resolvedWidth =
            if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
                rightInset + child.measuredWidth + leftInset
            } else {
                measuredWidth
            }

        if (resolvedWidth.toFloat() < (deltaLength * 2.0f) + child.measuredWidth) {
            resolvedWidth = (child.measuredWidth + (deltaLength * 2.0f)).toInt()
        }
        if (resolvedHeight.toFloat() < (deltaLength * 2.0f) + child.measuredHeight) {
            resolvedHeight = (child.measuredHeight + (deltaLength * 2.0f)).toInt()
        }
        if (resolvedHeight != measuredHeight || resolvedWidth != measuredWidth) {
            setMeasuredDimension(resolvedWidth, resolvedHeight)
        }
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams =
        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun generateLayoutParams(layoutParams: ViewGroup.LayoutParams): ViewGroup.LayoutParams = LayoutParams(layoutParams)

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams = LayoutParams(context, attrs)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val child = getChildAt(0)
        val containerWidth = measuredWidth
        val containerHeight = measuredHeight
        val childWidth = child.measuredWidth
        val childHeight = child.measuredHeight
        child.layout(
            (containerWidth - childWidth) / 2,
            (containerHeight - childHeight) / 2,
            (containerWidth + childWidth) / 2,
            (containerHeight + childHeight) / 2,
        )
    }

    private fun updateShadowPaint() {
        shadowPaint.color = shadowColor
        shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor)
    }

    class LayoutParams : MarginLayoutParams {
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: MarginLayoutParams) : super(source)

        constructor(source: ViewGroup.LayoutParams) : super(source)
    }
}
