# Frosted Glass Effect

一个具有动态流光边框和刷新效果的自定义毛玻璃视图组件，适用于Android平台。

## 功能特点

### 1. 核心效果
- **10%黑色透明高斯模糊背景**：提供柔和的毛玻璃效果
- **动态流光边框**：白色流光顺时针流动，持续动画
- **斜角刷新效果**：从左上角到右下角的白色光效，每5秒触发一次

### 2. 自定义属性
- **圆角半径**：支持通过代码和XML布局文件设置
  - `cornerRadius`：统一设置所有角的圆角半径
  - `topLeftRadius`：设置左上角圆角半径
  - `topRightRadius`：设置右上角圆角半径
  - `bottomLeftRadius`：设置左下角圆角半径
  - `bottomRightRadius`：设置右下角圆角半径
- **边框宽度**：固定为4dp的白色边框
- **刷新效果**：1/6对角线宽度的白色光效

### 3. 技术实现
- **自定义View**：继承自ConstraintLayout，支持子视图
- **Canvas绘制**：使用Paint和LinearGradient创建视觉效果
- **属性动画**：使用ValueAnimator实现平滑动画
- **XML属性**：支持通过布局文件配置圆角半径
- **性能优化**：对象复用、计算缓存、避免频繁创建临时对象

## 性能优化

### 优化措施

#### 1. 对象复用
- **Path对象**：缓存backgroundPath、borderPath、clipPath等对象，避免每次绘制时创建新对象
- **Matrix对象**：复用matrix和refreshMatrix对象进行变换操作
- **Paint对象**：缓存backgroundPaint、lightPaint等画笔对象
- **数组对象**：缓存radii数组存储圆角半径值
- **渐变对象**：缓存borderGradient和refreshGradient对象，避免每次绘制时创建新对象

#### 2. 计算缓存
- **几何计算**：缓存对角线长度、刷新效果宽度等计算结果
- **三角函数**：避免重复计算sin、cos等三角函数值
- **布局计算**：缓存子视图布局位置计算结果

#### 3. 绘制优化
- **路径重置**：使用Path.reset()方法重置路径，避免创建新Path对象
- **矩阵重置**：使用Matrix.reset()方法重置矩阵，避免创建新Matrix对象
- **渐变复用**：只在尺寸变化时创建新的LinearGradient对象，避免每次绘制时创建
- **矩阵变换**：使用Matrix变换调整渐变位置，而不是重新创建渐变对象
- **移除昂贵操作**：减少canvas.clipPath等昂贵的绘制操作
- **缩小绘制区域**：只绘制必要的区域，减少绘制开销
- **圆角半径更新优化**：添加radiiChanged标志，只在圆角半径变化时更新radii数组

#### 4. 动画优化
- **使用ViewCompat.postInvalidateOnAnimation()**：提高动画流畅度，与系统刷新频率同步
- **消除动画间隙**：使用post()方法确保动画无缝衔接
- **优化动画更新**：合理设置动画持续时间和插值器
- **硬件加速**：在支持的设备上启用硬件加速，提高绘制性能

#### 5. 尺寸变化处理
- **缓存重置**：当视图尺寸改变时，重置缓存的计算值，确保正确性
- **按需计算**：只在需要时进行复杂计算

### 技术细节

#### 核心优化代码
- **缓存对象初始化**：在类初始化时创建并缓存常用对象，包括Path、Matrix、Paint和渐变对象
- **updateRadiiArray()**：统一更新圆角半径数组，避免重复代码，并在更新后设置radiiChanged标志为false
- **onLayout()**：处理尺寸变化，重置缓存值，包括渐变对象和计算值
- **绘制方法**：使用缓存对象进行绘制操作，使用矩阵变换调整渐变位置
- **动画监听器**：优化动画重启机制，消除间隙
- **硬件加速**：在支持的设备上启用硬件加速，提高绘制性能
- **setWillNotDraw()**：重写该方法，确保视图会被绘制，因为需要自定义绘制逻辑

#### 性能提升
- **减少GC压力**：避免频繁创建临时对象，减少垃圾回收
- **提高绘制速度**：缓存计算结果，减少重复计算
- **优化内存使用**：复用对象，降低内存占用
- **解决卡顿问题**：移除昂贵操作，提高动画流畅度

### 解决卡顿问题的措施

#### 1. 绘制优化
- **保留必要的clipPath操作**：确保刷新效果被限制在圆角矩形内，同时优化使用方式
- **缩小绘制区域**：只绘制必要的区域，减少绘制开销

#### 2. 动画优化
- **使用ViewCompat.postInvalidateOnAnimation()**：与系统刷新频率同步，提高动画流畅度
- **优化动画更新**：合理设置动画持续时间，确保平滑过渡
- **消除动画间隙**：使用post()方法确保动画无缝衔接

#### 3. 内存优化
- **对象复用**：避免频繁创建临时对象，减少GC压力
- **计算缓存**：缓存计算结果，减少重复计算

### 修复圆角显示问题

#### 问题描述
刷新效果可能会显示到圆角矩形之外，影响视觉效果。

#### 解决方案
- **保留clipPath操作**：在drawRefreshEffect方法中使用canvas.clipPath()确保刷新效果被限制在圆角矩形内
- **优化裁剪路径**：使用缓存的clipPath对象，减少对象创建
- **合理设置绘制区域**：确保刷新效果完全在圆角矩形内显示

#### 实现细节
- **使用Path.addRoundRect()**：创建与视图形状匹配的裁剪路径
- **路径重置**：使用clipPath.reset()方法重置路径，避免创建新Path对象
- **顺序优化**：先设置裁剪路径，再绘制刷新效果，确保正确的绘制顺序

## 实现细节

### 核心类
- **FrostedGlassView.kt**：主自定义视图实现
  - `drawFlowingBorder()`：绘制动态流光边框
  - `drawRefreshEffect()`：绘制斜角刷新效果
  - `setCornerRadius(radius)`：设置所有角的圆角半径
  - `setCornerRadii(topLeft, topRight, bottomLeft, bottomRight)`：设置各个角的圆角半径
  - `setTopLeftRadius(radius)`：设置左上角圆角半径
  - `setTopRightRadius(radius)`：设置右上角圆角半径
  - `setBottomLeftRadius(radius)`：设置左下角圆角半径
  - `setBottomRightRadius(radius)`：设置右下角圆角半径

### 关键技术
- **LinearGradient**：创建平滑的渐变效果
- **ValueAnimator**：实现持续的动画效果
- **Canvas.rotate()**：确保刷新效果的正确角度
- **Path.addRoundRect()**：支持圆角矩形

## 使用方法

### 1. 在布局文件中使用

#### 示例1：统一设置所有角的圆角半径
```xml
<com.cn.frosted.glass.frostedglasseffect.view.FrostedGlassView
    android:id="@+id/frostedGlassView"
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:cornerRadius="24dp"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintBottom_toBottomOf="parent">
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        android:textColor="#FFFFFF"
        android:textSize="24sp"
        android:layout_gravity="center" />
        
</com.cn.frosted.glass.frostedglasseffect.view.FrostedGlassView>
```

#### 示例2：为每个角单独设置圆角半径
```xml
<com.cn.frosted.glass.frostedglasseffect.view.FrostedGlassView
    android:id="@+id/frostedGlassView"
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:topLeftRadius="32dp"
    app:topRightRadius="8dp"
    app:bottomLeftRadius="8dp"
    app:bottomRightRadius="32dp"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintBottom_toBottomOf="parent">
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        android:textColor="#FFFFFF"
        android:textSize="24sp"
        android:layout_gravity="center" />
        
</com.cn.frosted.glass.frostedglasseffect.view.FrostedGlassView>
```

### 2. 在代码中设置

#### 示例1：统一设置所有角的圆角半径
```kotlin
val frostedGlassView = findViewById<FrostedGlassView>(R.id.frostedGlassView)
frostedGlassView.setCornerRadius(32f) // 设置32dp圆角半径
```

#### 示例2：为每个角单独设置圆角半径
```kotlin
val frostedGlassView = findViewById<FrostedGlassView>(R.id.frostedGlassView)

// 方法1：使用setCornerRadii设置所有角
frostedGlassView.setCornerRadii(32f, 8f, 8f, 32f) // 左上角32dp，右上角8dp，左下角8dp，右下角32dp

// 方法2：单独设置每个角
frostedGlassView.setTopLeftRadius(32f)     // 设置左上角圆角半径
frostedGlassView.setTopRightRadius(8f)    // 设置右上角圆角半径
frostedGlassView.setBottomLeftRadius(8f)  // 设置左下角圆角半径
frostedGlassView.setBottomRightRadius(32f) // 设置右下角圆角半径
```

## 项目结构

```
app/
├── src/main/
│   ├── java/com/cn/frosted/glass/frostedglasseffect/view/
│   │   ├── FrostedGlassView.kt     # 主视图实现
│   │   └── MainActivity.kt          # 示例活动
│   ├── res/layout/
│   │   └── activity_main.xml        # 主布局
│   ├── values/
│       ├── themes.xml               # 透明主题配置
│       └── attrs.xml                # 自定义属性
```

## 运行效果

应用启动后，会显示一个带有以下效果的卡片：
- 半透明黑色背景（10%不透明度）
- 白色流光边框顺时针流动
- 每5秒一次的斜角白色刷新效果

## 兼容性

- 支持Android 5.0+
- 使用标准Android API，无第三方依赖

## 许可证

MIT License