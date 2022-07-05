package com.fdf.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import me.jessyan.autosize.utils.ScreenUtils;

public class LayoutAutoResizeHelper {
    private int usableHeightPrevious;

    private LayoutAutoResizeHelper() {

    }

    public static LayoutAutoResizeHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final LayoutAutoResizeHelper INSTANCE = new LayoutAutoResizeHelper();
    }

    public void adjustResize(Activity activity) {
        setupAdjustResize(activity);
    }

    private void setupAdjustResize(Activity activity) {
        ViewGroup content = activity.findViewById(android.R.id.content);
        View contentView = content.getChildAt(0);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(() -> possiblyResizeChildOfContent(contentView));
    }

    private void possiblyResizeChildOfContent(View contentView) {
        ViewGroup.LayoutParams contentViewParams = contentView.getLayoutParams();
        //获取当前界面可视区域（注：可视区域指APP界面）
        Rect rect = computeUsableRect(contentView);
        //获取当前界面可视区域高度
        int currentUsableHeight = rect.bottom - rect.top;
        if (currentUsableHeight != usableHeightPrevious) {
            //获取根布局高度
            int rootViewHeight = contentView.getRootView().getHeight();
            //计算出根布局高度与可视区域高度差值
            int heightDiff = rootViewHeight - currentUsableHeight;
            //差值超过四分之一说明软键盘弹出
            if (heightDiff > (rootViewHeight / 4)) {
                //全屏模式需要加上状态栏高度，否则低于软键盘高度的输入框弹起时与软键盘顶部会有偏差
                contentViewParams.height = rootViewHeight - heightDiff + getStatusHeight(contentView.getContext());
            } else {
                contentViewParams.height = rootViewHeight ;
            }
            contentView.requestLayout();
            usableHeightPrevious = currentUsableHeight;
        }
    }

    private Rect computeUsableRect(View view) {
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);
        return r;
    }

    private int getStatusHeight(Context context) {
        return ScreenUtils.getStatusBarHeight();
    }
}

