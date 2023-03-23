package com.android.androidsidebar.fullscree_drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        this(context, null);
    }

    public DrawerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

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



            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //侧边栏状态
                boolean isClose = !drawerLayout.isDrawerOpen(Gravity.LEFT);
                if(newState==0&&isClose){
                    //设置为这个状态LOCK_MODE_LOCKED_CLOSED
                    //避免drawerLayout拦截childView 的事件
                    closeDrawerLayoutTouch();
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {

        return nestedScrollAxes == 1;

    }

    private void closeDrawerLayoutTouch(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        target.setOnTouchListener(null);
        long time = System.currentTimeMillis();
        //重置状态
        target.dispatchTouchEvent(MotionEvent.obtain(time,time,MotionEvent.ACTION_CANCEL,0,0,0));
        setTouchListener = false;
    }

    View target;
    boolean setTouchListener = false;




    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        //内部滑动结束调用
        if (dxUnconsumed < -10 && !setTouchListener) {
            //表示已经滑到左边边界了
            if (drawerLayout != null) {
                //开始拦截事件
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
                this.target = target;
                myTouchListener.reset();
                target.setOnTouchListener(myTouchListener);
                setTouchListener = true;
            }
        }
    }

    MyTouchListener myTouchListener = new MyTouchListener();

    class MyTouchListener implements OnTouchListener {
        boolean callDown = false;

        void reset() {
            callDown = false;

        }


        float lastX = 0;
        float lastY = 0;


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (!callDown) {
                MotionEvent obtain = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN, event.getRawX(),event.getRawY(), event.getMetaState());
                drawerLayout.onInterceptTouchEvent(obtain);
                lastX=event.getRawX();
                lastY=event.getRawY();
                callDown = true;
            }

            MotionEvent obtain = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),event.getAction(), event.getRawX(),event.getRawY(), event.getMetaState());
            drawerLayout.onTouchEvent(obtain);
            lastX=event.getRawX();
            lastY=event.getRawY();
            return true;
        }
    }

    ;
}
