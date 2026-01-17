package com.cn.frosted.glass.frostedglasseffect.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout

class FrostedGlassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG) // 边框画笔
    private val refreshPaint = Paint(Paint.ANTI_ALIAS_FLAG) // 刷新效果画笔
    private val borderWidth = 4f // 边框宽度
    // 圆角半径 (默认16f)
    private var topLeftRadius = 16f
    private var topRightRadius = 16f
    private var bottomLeftRadius = 16f
    private var bottomRightRadius = 16f
    private var animator: ValueAnimator? = null // 边框动画
    private var refreshAnimator: ValueAnimator? = null // 刷新效果动画
    private var progress = 0f // 边框动画进度
    private var refreshProgress = 0f // 刷新效果动画进度
    private var isInitialized = false // 初始化状态

    init {
        // 从XML布局中读取自定义属性
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FrostedGlassView)
            try {
                // 读取圆角半径属性，如果未设置则使用默认值
                val defaultRadius = 16f
                topLeftRadius = typedArray.getDimension(R.styleable.FrostedGlassView_topLeftRadius, defaultRadius)
                topRightRadius = typedArray.getDimension(R.styleable.FrostedGlassView_topRightRadius, defaultRadius)
                bottomLeftRadius = typedArray.getDimension(R.styleable.FrostedGlassView_bottomLeftRadius, defaultRadius)
                bottomRightRadius = typedArray.getDimension(R.styleable.FrostedGlassView_bottomRightRadius, defaultRadius)
                // 保持向后兼容：如果设置了cornerRadius，则使用它
                val cornerRadius = typedArray.getDimension(R.styleable.FrostedGlassView_cornerRadius, -1f)
                if (cornerRadius >= 0) {
                    setCornerRadius(cornerRadius)
                }
            } finally {
                typedArray.recycle()
            }
        }
        
        setupPaints()
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (!isInitialized) {
                    setupBlurEffect()
                    // 当布局准备就绪时，同时启动两个动画
                    startAnimations()
                    isInitialized = true
                }
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
    
    /**
     * 设置单个角的圆角半径
     * @param topLeft 左上角圆角半径
     * @param topRight 右上角圆角半径
     * @param bottomLeft 左下角圆角半径
     * @param bottomRight 右下角圆角半径
     */
    fun setCornerRadii(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        this.topLeftRadius = topLeft
        this.topRightRadius = topRight
        this.bottomLeftRadius = bottomLeft
        this.bottomRightRadius = bottomRight
        invalidate() // 触发重绘以应用新的圆角半径
    }
    
    /**
     * 设置左上角圆角半径
     * @param radius 圆角半径值
     */
    fun setTopLeftRadius(radius: Float) {
        this.topLeftRadius = radius
        invalidate()
    }
    
    /**
     * 设置右上角圆角半径
     * @param radius 圆角半径值
     */
    fun setTopRightRadius(radius: Float) {
        this.topRightRadius = radius
        invalidate()
    }
    
    /**
     * 设置左下角圆角半径
     * @param radius 圆角半径值
     */
    fun setBottomLeftRadius(radius: Float) {
        this.bottomLeftRadius = radius
        invalidate()
    }
    
    /**
     * 设置右下角圆角半径
     * @param radius 圆角半径值
     */
    fun setBottomRightRadius(radius: Float) {
        this.bottomRightRadius = radius
        invalidate()
    }
    
    /**
     * 获取左上角圆角半径
     * @return 左上角圆角半径值
     */
    fun getTopLeftRadius(): Float {
        return topLeftRadius
    }
    
    /**
     * 获取右上角圆角半径
     * @return 右上角圆角半径值
     */
    fun getTopRightRadius(): Float {
        return topRightRadius
    }
    
    /**
     * 获取左下角圆角半径
     * @return 左下角圆角半径值
     */
    fun getBottomLeftRadius(): Float {
        return bottomLeftRadius
    }
    
    /**
     * 获取右下角圆角半径
     * @return 右下角圆角半径值
     */
    fun getBottomRightRadius(): Float {
        return bottomRightRadius
    }

    private fun setupPaints() {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth
        // 设置默认边框颜色为10%透明度的白色
        borderPaint.color = Color.argb(25, 255, 255, 255) // 10%不透明度白色
        
        // 设置刷新效果画笔
        refreshPaint.style = Paint.Style.FILL
        refreshPaint.strokeWidth = 2f
    }

    private fun setupBlurEffect() {
        // 在dispatchDraw中处理模糊效果，而不是使用RenderEffect
        // 这样可以只模糊背景，不模糊子视图
    }

    private fun startAnimations() {
        // 同时启动两个动画，确保效果同步
        setupBorderAnimation()
        setupRefreshAnimation()
    }
    
    private fun setupBorderAnimation() {
        // 边框动画（流光效果）
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.duration = 5000
        animator?.repeatCount = ValueAnimator.INFINITE
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        animator?.start()
    }
    
    private fun setupRefreshAnimation() {
        refreshAnimator = ValueAnimator.ofFloat(0f, 1f)
        refreshAnimator?.duration = 5000
        refreshAnimator?.repeatCount = 0 // 不无限重复
        refreshAnimator?.interpolator = LinearInterpolator()
        refreshAnimator?.addUpdateListener {
            refreshProgress = it.animatedValue as Float
            invalidate()
        }
        refreshAnimator?.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // 5秒后安排下一次刷新
                postDelayed({ setupRefreshAnimation() }, 0)
            }
            
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
        refreshAnimator?.start()
    }

    override fun dispatchDraw(canvas: Canvas) {
        // 绘制10%黑色透明背景
        val backgroundPaint = Paint()
        backgroundPaint.color = Color.argb(25, 0, 0, 0) // 10%黑色透明度
        
        // 使用Path绘制带不同圆角半径的背景
        val backgroundPath = Path()
        val radii = floatArrayOf(
            topLeftRadius, topLeftRadius,
            topRightRadius, topRightRadius,
            bottomRightRadius, bottomRightRadius,
            bottomLeftRadius, bottomLeftRadius
        )
        backgroundPath.addRoundRect(
            borderWidth, borderWidth, width - borderWidth, height - borderWidth,
            radii,
            Path.Direction.CW
        )
        canvas.drawPath(backgroundPath, backgroundPaint)

        // 绘制动态流光边框
        drawFlowingBorder(canvas)
        
        // 绘制刷新效果（左上角到右下角）
        drawRefreshEffect(canvas)

        super.dispatchDraw(canvas)
    }
    
    private fun drawRefreshEffect(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        
        // 计算对角线长度 (sqrt(width² + height²))
        val diagonalLength = Math.sqrt((width * width + height * height).toDouble()).toFloat()
        
        // 计算刷新效果的宽度（1/6对角线长度，更窄）
        val refreshWidth = diagonalLength / 6
        
        // 根据refreshProgress计算位置
        val progress = refreshProgress
        
        // 先设置裁剪路径，确保刷新效果在圆角矩形内
        val clipPath = Path()
        val radii = floatArrayOf(
            topLeftRadius, topLeftRadius,
            topRightRadius, topRightRadius,
            bottomRightRadius, bottomRightRadius,
            bottomLeftRadius, bottomLeftRadius
        )
        clipPath.addRoundRect(
            borderWidth, borderWidth, width - borderWidth, height - borderWidth,
            radii,
            Path.Direction.CW
        )
        canvas.clipPath(clipPath)
        
        // 计算当前位置：从左上角外到右下角外
        val startX = -refreshWidth
        val startY = -refreshWidth
        val endX = width + refreshWidth
        val endY = height + refreshWidth
        
        // 计算当前点
        val currentX = startX + (endX - startX) * progress
        val currentY = startY + (endY - startY) * progress
        
        // 创建线性渐变，方向为左上角到右下角的斜角
        val gradient = LinearGradient(
            currentX - refreshWidth,
            currentY - refreshWidth,
            currentX + refreshWidth,
            currentY + refreshWidth,
            intArrayOf(
                Color.TRANSPARENT,
                Color.argb(32, 255, 255, 255), // 12.5%透明白色，更透明
                Color.TRANSPARENT
            ),
            floatArrayOf(
                0f,
                0.5f,
                1f
            ),
            Shader.TileMode.CLAMP
        )
        
        refreshPaint.shader = gradient
        
        // 绘制刷新效果，覆盖整个视图
        canvas.drawRect(
            0f, 0f,
            width, height,
            refreshPaint
        )
    }

    private fun drawFlowingBorder(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        
        // 定义圆角半径数组
        val radii = floatArrayOf(
            topLeftRadius, topLeftRadius,
            topRightRadius, topRightRadius,
            bottomRightRadius, bottomRightRadius,
            bottomLeftRadius, bottomLeftRadius
        )
        
        // 首先绘制默认边框（10%透明白色）
        // 这提供了基础边框
        val borderPath = Path()
        borderPath.addRoundRect(
            borderWidth, borderWidth, width - borderWidth, height - borderWidth,
            radii,
            Path.Direction.CW
        )
        canvas.drawPath(borderPath, borderPaint)
        
        // 为流光效果创建更强烈的渐变
        // 这会使光效更明显
        val gradient = LinearGradient(
            -width, -height,
            0f, 0f,
            intArrayOf(
                Color.TRANSPARENT,
                Color.WHITE, // 完全不透明的白色，确保最大可见度
                Color.TRANSPARENT
            ),
            floatArrayOf(
                0f,
                0.5f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        // 计算边框路径（已在上面创建）

        // 创建渐变变换矩阵
        val matrix = Matrix()
        
        // 根据进度计算位置
        val angle = progress * 360
        val radius = Math.sqrt((width/2).toDouble() * (width/2).toDouble() + (height/2).toDouble() * (height/2).toDouble()).toFloat()
        
        // 根据角度计算偏移
        val offsetX = (Math.cos(Math.toRadians(angle.toDouble())) * radius).toFloat()
        val offsetY = (Math.sin(Math.toRadians(angle.toDouble())) * radius).toFloat()
        
        // 平移渐变以创建流动效果
        matrix.setTranslate(offsetX + width/2, offsetY + height/2)
        
        // 旋转渐变以跟随边框
        matrix.postRotate(angle + 45, width/2, height/2)
        
        gradient.setLocalMatrix(matrix)
        
        // 创建单独的画笔用于流光效果，确保它始终明亮
        val lightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        lightPaint.style = Paint.Style.STROKE
        lightPaint.strokeWidth = borderWidth
        lightPaint.shader = gradient

        // 在基础边框上绘制流光效果
        canvas.drawPath(borderPath, lightPaint)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val childLeft = (width - childWidth) / 2
            val childTop = (height - childHeight) / 2
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
        refreshAnimator?.cancel()
    }
    
    /**
     * 设置所有角的圆角半径（保持向后兼容）
     * @param radius 圆角半径值
     */
    fun setCornerRadius(radius: Float) {
        topLeftRadius = radius
        topRightRadius = radius
        bottomLeftRadius = radius
        bottomRightRadius = radius
        invalidate() // 触发重绘以应用新的圆角半径
    }
    
    /**
     * 获取当前圆角半径（保持向后兼容）
     * @return 当前圆角半径值
     */
    fun getCornerRadius(): Float {
        return topLeftRadius // 返回左上角的圆角半径作为默认值
    }
}
