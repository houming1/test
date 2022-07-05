package com.fdf.base.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 万能分割线
 * <p>
 * 横向列表分割线 {@link #HORIZONTAL_DIV}
 * 纵向列表分割线 {@link #VERTICAL_DIV}
 * 表格列表分割线 {@link #GRID_DIV}
 * </P>
 */
public class ItemDecorationPowerful extends RecyclerView.ItemDecoration {
    private static final String TAG = "ItemDecorationPowerful";
    //横向布局分割线
    public static final int HORIZONTAL_DIV = 0;
    //纵向布局分割线
    public static final int VERTICAL_DIV = 1;
    //表格布局分割线
    public static final int GRID_DIV = 2;
    private int mOrientation;
    private int mDividerWidth = 0;
    private Paint mPaint;

    /**
     * 默认纵向布分割线
     */
    public ItemDecorationPowerful() {
        this(VERTICAL_DIV);
    }

    /**
     * @param orientation 方向类型
     */
    public ItemDecorationPowerful(int orientation) {
        this(orientation, Color.parseColor("#808080"), 2);
    }

    /**
     * @param orientation 方向类型
     * @param color       分割线颜色
     * @param divWidth    分割线宽度
     */
    public ItemDecorationPowerful(int orientation, int color, int divWidth) {
        this.setOrientation(orientation);
        mDividerWidth = divWidth;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        switch (mOrientation) {
            case HORIZONTAL_DIV:
                //横向布局分割线
                drawHorizontal(c, parent);
                break;
            case VERTICAL_DIV:
                //纵向布局分割线
                drawVertical(c, parent);
                break;
            case GRID_DIV:
                //表格格局分割线
                drawGrid(c, parent);
                break;
            default:
                //纵向布局分割线
                drawVertical(c, parent);
                break;
        }
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int itemPosition = parent.getChildAdapterPosition(view);
        RecyclerView.Adapter mAdapter = parent.getAdapter();
        if (mAdapter != null) {
            int mChildCount = mAdapter.getItemCount();
            switch (mOrientation) {
                case HORIZONTAL_DIV:
                    /**
                     * 横向布局分割线
                     * <p>
                     *     如果是第一个Item，则不需要分割线
                     * </p>
                     */
                    if (itemPosition != 0) {
                        outRect.set(mDividerWidth, 0, 0, 0);
                    }
                    break;
                case VERTICAL_DIV:
                    /**
                     * 纵向布局分割线
                     * <p>
                     *     如果是第一个Item，则不需要分割线
                     * </p>
                     */
                    if (itemPosition != 0) {
                        outRect.set(0, mDividerWidth, 0, 0);
                    }
                    break;
                case GRID_DIV:
                    /**
                     * 表格格局分割线
                     * <p>
                     *      1：当是第一个Item的时候，四周全部需要分割线
                     *      2：当是第一行Item的时候，需要额外添加顶部的分割线
                     *      3：当是第一列Item的时候，需要额外添加左侧的分割线
                     *      4：默认情况全部添加底部和右侧的分割线
                     * </p>
                     */
//                    RecyclerView.LayoutManager mLayoutManager = parent.getLayoutManager();
//                    if (mLayoutManager instanceof GridLayoutManager) {
//                        GridLayoutManager mGridLayoutManager = (GridLayoutManager) mLayoutManager;
//                        int mSpanCount = mGridLayoutManager.getSpanCount();
//                        if (itemPosition == 0) {//1
//                            outRect.set(mDividerWidth, mDividerWidth, mDividerWidth, mDividerWidth);
//                        } else if ((itemPosition + 1) <= mSpanCount) {//2
//                            outRect.set(0, mDividerWidth, mDividerWidth, mDividerWidth);
//                        } else if (((itemPosition + mSpanCount) % mSpanCount) == 0) {//3
//                            outRect.set(mDividerWidth, 0, mDividerWidth, mDividerWidth);
//                        } else {//4
//                            outRect.set(0, 0, mDividerWidth, mDividerWidth);
//                        }
//                    }
                    RecyclerView.LayoutManager mLayoutManager = parent.getLayoutManager();
                    itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
                    GridLayoutManager mGridLayoutManager = (GridLayoutManager) mLayoutManager;
                    int spanCount = mGridLayoutManager.getSpanCount();
                    int childCount = parent.getAdapter().getItemCount();

                    boolean isLastRow = isLastRow(parent, itemPosition, spanCount, childCount);

                    int top = 0;
                    int left;
                    int right;
                    int bottom;
                    int eachWidth = (spanCount - 1) * mDividerWidth / spanCount;
                    int dl = mDividerWidth - eachWidth;

                    left = itemPosition % spanCount * dl;
                    right = eachWidth - left;
                    bottom = mDividerWidth;
                    if (isLastRow) {
                        bottom = 0;
                    }
                    outRect.set(left, top, right, bottom);
                    break;
                default:
                    //纵向布局分割线
                    if (itemPosition != (mChildCount - 1)) {
                        outRect.set(0, 0, 0, mDividerWidth);
                    }
                    break;
            }
        }
    }


    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            return lines == pos / spanCount + 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 绘制横向列表分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int mChildCount = parent.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            if (i == 0) continue;
            View mChild = parent.getChildAt(i);
            drawLeft(c, mChild, parent);
        }
    }

    /**
     * 绘制纵向列表分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        int mChildCount = parent.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            if (i == 0) continue;
            View mChild = parent.getChildAt(i);
            drawTop(c, mChild, parent);
        }
    }

    /**
     * 绘制表格类型分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private void drawGrid(Canvas c, RecyclerView parent) {
        //绘制item分割线
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            boolean isLastRow = isLastRow(parent, i, getSpanCount(parent), childCount);
            //画水平分隔线
            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDividerWidth;
            if (isLastRow) bottom = top;
            if (mPaint != null) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
            //画垂直分割线
            top = child.getTop();
            bottom = child.getBottom() + mDividerWidth;
            left = child.getRight() + layoutParams.rightMargin;
            right = left + mDividerWidth;
            if (isLastRow) bottom = child.getBottom();
            if (mPaint != null) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }

//        int mChildCount = parent.getChildCount();
//        for (int i = 0; i < mChildCount; i++) {
//            View mChild = parent.getChildAt(i);
//            RecyclerView.LayoutManager mLayoutManager = parent.getLayoutManager();
//            if (mLayoutManager instanceof GridLayoutManager) {
//                GridLayoutManager mGridLayoutManager = (GridLayoutManager) mLayoutManager;
//                int mSpanCount = mGridLayoutManager.getSpanCount();
//                if (i == 0) {
//                    drawTop(c, mChild, parent);
//                    drawLeft(c, mChild, parent);
//                }
//                if ((i + 1) <= mSpanCount) {
//                    drawTop(c, mChild, parent);
//                }
//                if (((i + mSpanCount) % mSpanCount) == 0) {
//                    drawLeft(c, mChild, parent);
//                }
//                drawRight(c, mChild, parent);
//                drawBottom(c, mChild, parent);
//            }
//        }
    }

    //获取列数
    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    /**
     * 绘制右边分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawLeft(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left = mChild.getLeft() - mDividerWidth - mChildLayoutParams.leftMargin;
        int top = mChild.getTop() - mChildLayoutParams.topMargin;
        int right = mChild.getLeft() - mChildLayoutParams.leftMargin;
        int bottom;
        if (isGridLayoutManager(recyclerView)) {
            bottom = mChild.getBottom() + mChildLayoutParams.bottomMargin + mDividerWidth;
        } else {
            bottom = mChild.getBottom() + mChildLayoutParams.bottomMargin;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 绘制顶部分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawTop(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left;
        int top = mChild.getTop() - mChildLayoutParams.topMargin - mDividerWidth;
        int right = mChild.getRight() + mChildLayoutParams.rightMargin;
        int bottom = mChild.getTop() - mChildLayoutParams.topMargin;
        if (isGridLayoutManager(recyclerView)) {
            left = mChild.getLeft() - mChildLayoutParams.leftMargin - mDividerWidth;
        } else {
            left = mChild.getLeft() - mChildLayoutParams.leftMargin;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 绘制右边分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawRight(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left = mChild.getRight() + mChildLayoutParams.rightMargin;
        int top;
        int right = left + mDividerWidth;
        int bottom = mChild.getBottom() + mChildLayoutParams.bottomMargin;
        if (isGridLayoutManager(recyclerView)) {
            top = mChild.getTop() - mChildLayoutParams.topMargin - mDividerWidth;
        } else {
            top = mChild.getTop() - mChildLayoutParams.topMargin;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 绘制底部分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawBottom(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left = mChild.getLeft() - mChildLayoutParams.leftMargin;
        int top = mChild.getBottom() + mChildLayoutParams.bottomMargin;
        int bottom = top + mDividerWidth;
        int right;
        if (isGridLayoutManager(recyclerView)) {
            right = mChild.getRight() + mChildLayoutParams.rightMargin + mDividerWidth;
        } else {
            right = mChild.getRight() + mChildLayoutParams.rightMargin;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 判断RecyclerView所加载LayoutManager是否为GridLayoutManager
     *
     * @param recyclerView RecyclerView
     * @return 是GridLayoutManager返回true，否则返回false
     */
    private boolean isGridLayoutManager(RecyclerView recyclerView) {
        RecyclerView.LayoutManager mLayoutManager = recyclerView.getLayoutManager();
        return (mLayoutManager instanceof GridLayoutManager);
    }

    /**
     * 初始化分割线类型
     *
     * @param orientation 分割线类型
     */
    public void setOrientation(int orientation) {
        if (mOrientation != HORIZONTAL_DIV && mOrientation != VERTICAL_DIV && mOrientation != GRID_DIV) {
            throw new IllegalArgumentException("ItemDecorationPowerful：分割线类型设置异常");
        } else {
            this.mOrientation = orientation;
        }
    }
}