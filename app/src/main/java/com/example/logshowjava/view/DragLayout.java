package com.example.logshowjava.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logshowjava.MainActivity;
import com.example.logshowjava.R;

public class DragLayout extends RelativeLayout {
    private LinearLayout mainView;
    private LinearLayout tvWrapper;
    private TextView tv;
    private ScrollView scrollView;
    private View scaleZone;
    private WebView webView;
    private Context context;
    private int dx = 0;
    private int dy = 0;

    private int x = 0;
    private int y = 0;
    private boolean isInitView = true;
    private boolean isScaling = false;

    private float x_previous;
    private float y_previous;

    private int currentScroll = 0;

    private ActionMode mActionMode;
    private ViewDragHelper mDragHelper;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;

    ScaleWindowListener listener;

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
        super.onFinishInflate();

        mainView = findViewById(R.id.main_view);
        scaleZone = findViewById(R.id.scale_zone);
        scrollView = findViewById(R.id.scroll_view);
        tvWrapper = findViewById(R.id.tv_wrapper);
        tv = findViewById(R.id.tv);
        tv.setTextIsSelectable(true);
        tv.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                int startIndex = tv.getSelectionStart();
                int endIndex = tv.getSelectionEnd();
                if (startIndex > 0 && endIndex > 0) {
                    String copyText = tv.getText().toString().substring(startIndex, endIndex);
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", copyText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copied "+ copyText, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Please select again", Toast.LENGTH_SHORT).show();

                }


//                Log.d("ZINGLOGSHOW", "Copied text");


                return true;
            }
        });
//        webView = findViewById(R.id.webview);

//        emulateShiftHeld(webView);

    }

    public class DragHelperCallback extends ViewDragHelper.Callback {


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

//            Log.d("ZINGLOGSHOW", "onviewposition "+ dx + " "+  dy);
//            Log.d("ZINGLOGSHOW", "left "+scaleZone.getLeft() + " top "+ (scaleZone.getTop()) + " right " + (scaleZone.getRight()) + " bottom " +scaleZone.getBottom());
            requestLayout();
        }
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
    public boolean isViewHit(View view, int x, int y) {
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


        if (isViewHit(scaleZone, (int)ev.getX(), (int)ev.getY())) {

            isScaling = true;
            if (scrollView.getScrollY() != 0) {
                currentScroll = scrollView.getScrollY();
            }
//            Log.d("ZINGLOGSHOW", "Current Scroll "+ currentScroll);

            tvWrapper.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

//            mDragHelper.shouldInterceptTouchEvent(ev);

//            if (dx != 0 || dy != 0){
//                requestLayout();
//            }
//            return true;
        }


        if (ev.getAction() == MotionEvent.ACTION_UP && isScaling){
            isScaling = false;
            scrollView.scrollTo(0, currentScroll);
//            Log.d("ZINGLOGSHOW", "Scroll to "+ currentScroll);

        }
        if (isScaling){
            if (ev.getAction() == MotionEvent.ACTION_DOWN){
                x_previous = ev.getRawX();
                y_previous = ev.getRawY();
            }
            if (ev.getAction() == MotionEvent.ACTION_MOVE){

                if (listener != null){
                    listener.OnScale((int)(ev.getRawX() - x_previous), (int)(ev.getRawY() - y_previous));

                }
                x_previous = ev.getRawX();
                y_previous = ev.getRawY();
            }
            mDragHelper.shouldInterceptTouchEvent(ev);


            dx = (int)mDragHelper.getdX(ev);
            dy = (int)mDragHelper.getdY(ev);
            x = (int)mDragHelper.getX(ev);
            y = (int)mDragHelper.getY(ev);

            requestLayout();

            return true;
        }


        boolean isHit = isViewHit(scrollView, (int) ev.getX(), (int)ev.getY());

//        Log.d("ZINGLOGSHOW","onTouch return " + isHit);

        return isHit;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//            Log.d("ZINGLOGSHOW","onIntercept called");

        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        if (isViewHit(scaleZone, (int)ev.getX(), (int)ev.getY())) {
            tvWrapper.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        }else {
            tvWrapper.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        }
        if (isViewHit(mainView, (int)ev.getX(), (int)ev.getY())) {
            return mDragHelper.shouldInterceptTouchEvent(ev);
        } else {
//            Log.d("ZINGLOGSHOW","onIntercept return false");

            return false;
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (isInitView){
            mainView.layout(
                    left,
                    top,
                    right - 100,
                    top + 100
            );


            scrollView.layout(
                    mainView.getLeft(),
                    mainView.getBottom(),
                    mainView.getRight() + 100,
                    mainView.getBottom() + 350
            );

            isInitView = false;
        }
        if (dx != 0 || dy != 0) {

//            mainView.layout(
//                    mainView.getLeft(),
//                    ((mainView.getTop() > webView.getBottom() - 100) && dy > 0) ? mainView.getTop(): y - 50 ,
//                    ((mainView.getRight() - 100 < mainView.getLeft()) && dx < 0)? mainView.getRight(): x - 50,
//                    mainView.getTop() + 100
//            );
//            webView.layout(
//                    mainView.getLeft() ,
//                    mainView.getBottom(),
//                    mainView.getRight() + 100,
//                    webView.getBottom()
//            );
            mainView.layout(
                    left,
                    top ,
                    ((mainView.getRight() - 100 < mainView.getLeft()) && dx < 0)? mainView.getRight(): right - 100,
                    top + 100
            );
            scrollView.layout(
                    mainView.getLeft() ,
                    mainView.getBottom(),
                    right,
                    bottom
            );
            dx = 0;
            dy = 0;
        }



        scrollView.layout(
                mainView.getLeft(),
                mainView.getBottom(),
                right,
                bottom
        );

        scaleZone.layout(
                right - 100,
                top,
                right,
                top + 100
        );
    }

    public interface ScaleWindowListener {
        void OnScale(int dx, int dy);
    }
    public void setScaleWindowListener(ScaleWindowListener listener){
        this.listener = listener;
    }
}
