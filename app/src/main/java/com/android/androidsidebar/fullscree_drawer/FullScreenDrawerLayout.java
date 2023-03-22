package com.android.androidsidebar.fullscree_drawer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * <pre>
 * Created by zhuguohui
 * Date: 2023/3/22
 * Time: 16:02
 * Desc:
 * </pre>
 */
public class FullScreenDrawerLayout extends DrawerLayout {

    private int screenWidth;

    public FullScreenDrawerLayout(@NonNull Context context) {
        super(context,null);
    }

    public FullScreenDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        //支持全屏侧滑
        DrawerLayoutHelperV2.setDrawerLeftEdgeFullScreen(getContext(),this);
        //设置背景蒙层颜色
        setScrimColor(Color.TRANSPARENT);
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
                //侧边栏状态
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //获取主页内容view
                View mContent = getChildAt(0);
                //主页面移动
                mContent.setTranslationX(screenWidth*slideOffset);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

        });
    }





    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        DrawerLayout.LayoutParams params1= (LayoutParams) params;
        if(params1.gravity==0){
            //使用DrawerFrameLayout,处理滑动冲突
            DrawerFrameLayout frameLayout=new DrawerFrameLayout(getContext());
            frameLayout.addView(child,new FrameLayout.LayoutParams(-1,-1));
            frameLayout.setDrawerLayout(this);
            child=frameLayout;

        }else {
            params.width = screenWidth;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
        }
        super.addView(child, index, params);
    }
}
