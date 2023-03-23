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
    private MotionEvent downEvent;

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
                if (newState == 0 && isClose) {
                    //设置为这个状态LOCK_MODE_LOCKED_CLOSED
                    //避免drawerLayout拦截childView 的事件
                    closeDrawerLayoutTouch();
                }
            }
        });
    }

    long downTime = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downTime = System.currentTimeMillis();
            downEvent = MotionEvent.obtain(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {

        return nestedScrollAxes == 1;

    }

    private void closeDrawerLayoutTouch() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        target.setOnTouchListener(null);

        //重置状态
        cancelTargetView();
        setTouchListener = false;
    }

    View target;
    boolean setTouchListener = false;

    private void cancelTargetView(){
        long time = System.currentTimeMillis();
        target.dispatchTouchEvent(MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL, 0, 0, 0));
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        //内部滑动结束调用
        if (dxUnconsumed < 0 && !setTouchListener) {
            float useTime = (System.currentTimeMillis() - downTime) * 1.0f / 1000;
            float velocityX = Math.abs(dxUnconsumed) / useTime;

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            setTouchListener = true;
            this.target = target;
            if (velocityX > 300) {
                //快速滑动
                //直接打开
                drawerLayout.openDrawer(Gravity.LEFT);
                cancelTargetView();

            } else {
                //慢慢滑动
                //开始拦截事件
                myTouchListener.reset();
                target.setOnTouchListener(myTouchListener);

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
                // MotionEvent obtain = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN, event.getRawX(),event.getRawY(), event.getMetaState());
                drawerLayout.onInterceptTouchEvent(downEvent);
                lastX = event.getRawX();
                lastY = event.getRawY();
                callDown = true;
                downEvent.recycle();
            }

            MotionEvent obtain = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getRawX(), event.getRawY(), event.getMetaState());
            drawerLayout.onTouchEvent(obtain);
            lastX = event.getRawX();
            lastY = event.getRawY();
            obtain.recycle();
            return true;
        }
    }

    ;
}
