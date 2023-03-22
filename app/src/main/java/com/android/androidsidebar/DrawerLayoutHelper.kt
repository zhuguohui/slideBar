package com.android.androidsidebar

import android.app.Activity
import android.graphics.Point
import androidx.drawerlayout.widget.DrawerLayout
import androidx.customview.widget.ViewDragHelper

/**
 *<pre>
 * Created by zhuguohui
 * Date: 2023/3/22
 * Time: 11:21
 * Desc:
 *
 * *</pre>
 */
object DrawerLayoutHelper {

    /**
     * 通过反射的方式将 DrawerLayout 的侧滑范围设为全屏
     * 该方法存在一个问题，在侧滑范围内长按，也会划出菜单
     * 通过查看 DrawerLayout 的源码分析，其内部类 ViewDragCallback
     * 重写了 onEdgeTouched 方法，然后调用一个 Runnable 属性的变量 “mPeekRunnable”
     * 该变量调用了 peekDraw 方法，实现了长按划出侧滑菜单的功能
     * 同样使用反射将该 Runnable 更改为空实现
     *
     * @param activity
     * @param drawerLayout
     * @param displayWidthPercentage
     */
    fun setDrawerLeftEdgeSize(activity: Activity?,
                              drawerLayout: androidx.drawerlayout.widget.DrawerLayout?,
                              displayWidthPercentage: Float) {
        if (activity == null || drawerLayout == null) return
        try {
            //获取 ViewDragHelper，更改其 edgeSizeField 为 displayWidthPercentage*屏幕大小
            val leftDraggerField = drawerLayout.javaClass.getDeclaredField("mLeftDragger")
            leftDraggerField.isAccessible = true
            val leftDragger = leftDraggerField.get(drawerLayout) as androidx.customview.widget.ViewDragHelper

            val edgeSizeField = leftDragger.javaClass.getDeclaredField("mEdgeSize")
            edgeSizeField.isAccessible = true
            val edgeSize = edgeSizeField.getInt(leftDragger)

            val displaySize = Point()
            activity.windowManager.defaultDisplay.getSize(displaySize)
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (displaySize.x * displayWidthPercentage).toInt()))

            //获取 Layout 的 ViewDragCallBack 实例“mLeftCallback”
            //更改其属性 mPeekRunnable
            val leftCallbackField = drawerLayout.javaClass.getDeclaredField("mLeftCallback")
            leftCallbackField.isAccessible = true

            //因为无法直接访问私有内部类，所以该私有内部类实现的接口非常重要，通过多态的方式获取实例
            val leftCallback = leftCallbackField.get(drawerLayout) as androidx.customview.widget.ViewDragHelper.Callback

            val peekRunnableField = leftCallback.javaClass.getDeclaredField("mPeekRunnable")
            peekRunnableField.isAccessible = true
            val nullRunnable = Runnable {  }
            peekRunnableField.set(leftCallback, nullRunnable)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDrawerLeftEdgeFullScreen(activity: Activity?, drawerLayout: androidx.drawerlayout.widget.DrawerLayout?) {
        setDrawerLeftEdgeSize(activity, drawerLayout, 1.0f)
    }
}