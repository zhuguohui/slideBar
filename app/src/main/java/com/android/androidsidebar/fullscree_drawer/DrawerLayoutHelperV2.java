package com.android.androidsidebar.fullscree_drawer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.customview.widget.ViewDragHelper;
import android.view.WindowManager;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

import kotlin.jvm.internal.Intrinsics;

/**
 * <pre>
 * Created by zhuguohui
 * Date: 2023/3/22
 * Time: 11:25
 * Desc:
 * 这个工具的作用是让DrawerLayout支持全屏右滑打开菜单
 *  * 默认情况下，只支持边缘侧滑打开
 * <a href="https://febers.github.io/%E5%88%A9%E7%94%A8%E5%8F%8D%E5%B0%84%E5%AE%9E%E7%8E%B0-DrawerLayout-%E5%85%A8%E5%B1%8F%E6%BB%91%E5%8A%A8/">实现原理</a>
 * </pre>
 */
public class DrawerLayoutHelperV2 {


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
    public   static  void setDrawerLeftEdgeSize(@Nullable Context activity, @Nullable DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity != null && drawerLayout != null) {
            try {
                Field leftDraggerField = DrawerLayout.class.getDeclaredField("mLeftDragger");
                Intrinsics.checkNotNullExpressionValue(leftDraggerField, "leftDraggerField");
                leftDraggerField.setAccessible(true);
                Object var10000 = leftDraggerField.get(drawerLayout);
                if (var10000 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type android.support.v4.widget.ViewDragHelper");
                }

                ViewDragHelper leftDragger = (ViewDragHelper)var10000;
                Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
                edgeSizeField.setAccessible(true);
                int edgeSize = edgeSizeField.getInt(leftDragger);

                int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
                edgeSizeField.setInt(leftDragger,widthPixels);
                Field leftCallbackField = DrawerLayout.class.getDeclaredField("mLeftCallback");
                leftCallbackField.setAccessible(true);
                var10000 = leftCallbackField.get(drawerLayout);
                if (var10000 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type android.support.v4.widget.ViewDragHelper.Callback");
                }

                ViewDragHelper.Callback leftCallback = (ViewDragHelper.Callback)var10000;
                Field peekRunnableField = leftCallback.getClass().getDeclaredField("mPeekRunnable");
                Intrinsics.checkNotNullExpressionValue(peekRunnableField, "peekRunnableField");
                peekRunnableField.setAccessible(true);
                Runnable nullRunnable = () -> {
                };
                peekRunnableField.set(leftCallback, nullRunnable);
            } catch (Exception var13) {
                var13.printStackTrace();
            }

        }
    }

   public   static void setDrawerLeftEdgeFullScreen(@Nullable Context activity, @Nullable DrawerLayout drawerLayout) {
        setDrawerLeftEdgeSize(activity, drawerLayout, 1.0F);
    }


}
