package com.android.androidsidebar;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * <pre>
 * Created by zhuguohui
 * Date: 2023/3/22
 * Time: 13:53
 * Desc:
 * </pre>
 */
public class DrawerFrameLayout extends FrameLayout {
    DrawerLayout drawerLayout;
    public DrawerFrameLayout(@NonNull Context context) {
        this(context,null);
    }

    public DrawerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
        this.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                //设置为这个状态LOCK_MODE_LOCKED_CLOSED
                //避免drawerLayout拦截childView 的事件
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        target.setOnTouchListener(null);
        setTouchListener=false;
        return nestedScrollAxes==1;

    }

    View target;
    boolean setTouchListener=false;

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
       // Log.d("zzz", "onNestedScroll() called with:  dxConsumed = [" + dxConsumed + "], dyConsumed = [" + dyConsumed + "], dxUnconsumed = [" + dxUnconsumed + "], dyUnconsumed = [" + dyUnconsumed + "]");
        //内部滑动结束调用
        if(dxUnconsumed<0&&!setTouchListener){
            //表示已经滑到左边边界了
            if(drawerLayout!=null){
                //开始拦截事件
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
                this.target=target;
                target.setOnTouchListener(new MyTouchListener());
                setTouchListener=true;
            }

        }
    }
  class  MyTouchListener  implements OnTouchListener {
        boolean callDown=false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(!callDown){
                MotionEvent obtain = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN, event.getX(), event.getY(), event.getMetaState());
                drawerLayout.onTouchEvent(obtain);
                callDown=true;
            }
            drawerLayout.onTouchEvent(event);
            return true;
        }
    };
}
