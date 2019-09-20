package com.example.logshowjava.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.logshowjava.R;

public class DragLayout extends RelativeLayout {
    private LinearLayout mainView;
    private Context context;

    private ViewDragHelper mDragHelper;

    public DragLayout(Context context) {
        super(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());

    }

    @Override
    protected void onFinishInflate() {
        mainView = findViewById(R.id.main_view);
        super.onFinishInflate();
    }
    public class DragHelperCallback extends ViewDragHelper.Callback {
//        @Override
//        public void onViewDragStateChanged(int state) {
//            if (state == mDraggingState) { // no change
//                return;
//            }
//            if (isMoving() &&
//                    state == ViewDragHelper.STATE_IDLE) {
//                // the view stopped from moving.
//
//                if (mDraggingBorder == 0) {
//                    onStopDraggingToClosed();
//                } else if (mDraggingBorder == mVerticalRange) {
//                    mIsOpen = true;
//                }
//            }
//            if (state == ViewDragHelper.STATE_DRAGGING) {
//                onStartDragging();
//            }
//            mDraggingState = state;
//        }



//        public int getViewVerticalDragRange(View child) {
//            return mVerticalRange;
//        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == R.id.main_view);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mainView.getHeight() - mainView.getPaddingBottom();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {


            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mainView.getWidth();

            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

            return newLeft;
        }
    }
    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0]
                && screenX < viewLocation[0] + view.getWidth()
                && screenY >= viewLocation[1]
                && screenY < viewLocation[1] + view.getHeight();
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);

        return isViewHit(mainView, (int) ev.getX(), (int) ev.getY());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        return mDragHelper.shouldInterceptTouchEvent(ev);

    }
}
