package com.example.logshowjava.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.logshowjava.R;
import com.example.logshowjava.view.DragLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FloatingLogViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;

    private WebView webView;
    private TextView textView;
    private Button loadBtn;
    private Handler loadWebViewHandler;
    private Runnable loadWebViewRunnable;

    private String path = "";
    private String fileName = "";
    private int TYPE_FLAG;

    private int old_width = 0;
    private int old_height = 0;
    private int old_x = 0;
    private int old_y = 0;

    private FileObserver fileObserver;

    private boolean isWatching = true;
    private boolean isMoving = false;
    WindowManager.LayoutParams params;
    public FloatingLogViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent != null && intent.getExtras() != null) {
            path = intent.getStringExtra("path");

        }
        Log.d("ZINGLOGSHOW", "get Path file "+ path);
        String[] pathArray = path.split("/");
        fileName = pathArray[pathArray.length - 1];
        String parentDic = TextUtils.join("/", Arrays.copyOfRange(pathArray,0,pathArray.length - 1)) + "/";
        Log.d("ZINGLOGSHOW", "get Path: "+ parentDic);

        fileObserver = new FileObserver(parentDic) {
            @Override
            public void onEvent(int event, String path) {
//                Log.d("ZINGLOGSHOW", "Event trigger: "+ path);

                if (path == null || !path.equals(fileName)){
                    return;
                }

                if (event == FileObserver.MODIFY){
                    Log.d("ZINGLOGSHOW", "Event Modify trigger: "+ event);

                    if (!isViewCollapsed() && webView != null){
                        loadWebViewHandler.post(loadWebViewRunnable);
                        Log.d("ZINGLOGSHOW", "File changed, load again");

                    }
                }
            }
        };
        fileObserver.startWatching();
        isWatching = true;
        return START_STICKY;
    }
    @Override
    public void onCreate() {

        loadWebViewHandler = new Handler();
        loadWebViewRunnable = new Runnable() {
            @Override
            public void run() {
                FileInputStream fis;
                String content = "";

                try {
                    fis = new FileInputStream(new File(path));

                    int size = fis.available();

                    byte[] buffer = new byte[size];
                    while (fis.read(buffer) != -1) {
                    }
                    content += new String(buffer);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        textView.setText(Html.fromHtml(content,Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        textView.setText(Html.fromHtml(content));
                    }

                } catch (IOException e) {
                    Log.d("ZINGLOGSHOW", "error reading file "+ e.getMessage());

                }
//                webView.loadUrl("file://" + path);
//
//                webView.scrollTo(0, webView.getContentHeight() * 3);
//                Log.d("ZINGLOGSHOW", "load file");

            }
        };
        super.onCreate();
        //Inflate the floating view layout we created
        Log.d("ZINGLOGSHOW", "layout inflate");

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_log_widget, null);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            TYPE_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            TYPE_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                TYPE_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;


        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        collapsedView.setVisibility(View.VISIBLE);
        //The root element of the expanded view layout
        final DragLayout expandedView = mFloatingView.findViewById(R.id.drag_layout);
        expandedView.setVisibility(View.GONE);

        expandedView.setScaleWindowListener(new DragLayout.ScaleWindowListener() {
            @Override
            public void OnScale(int dx, int dy) {

                params.width += dx;
                params.height -= dy;

                old_width = params.width;
                old_height = params.height;

                mWindowManager.updateViewLayout(mFloatingView,params);
            }
        });


        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });
        //Set the close button
        ImageView closeButtonDrag = (ImageView) mFloatingView.findViewById(R.id.close_button_at_drag);
        closeButtonDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                old_x = params.x;
                old_y = params.y;
                if (params.x > screenWidth / 2){
                    params.x = screenWidth - collapsedView.getWidth() / 2;
                } else {
                    params.x = 0;
                }
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                mWindowManager.updateViewLayout(mFloatingView,params);

            }
        });
        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;




            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return isViewCollapsed();
                    case MotionEvent.ACTION_MOVE:
//                        Log.d("ZINGLOGSHOW", "move ");

                        isMoving = true;
                        //Calculate the X and Y coordinates of the view.

                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);



                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (isMoving && collapsedView.getVisibility() == View.VISIBLE){
                            if (event.getRawX() > screenWidth / 2){
                                params.x = screenWidth - collapsedView.getWidth() / 2;
                            } else {
                                params.x = 0;
                            }
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            isMoving = false;
                            return true;
                        }
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                                webView = mFloatingView.findViewById(R.id.webview);
                                textView = mFloatingView.findViewById(R.id.tv);

//                                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                if (old_height == 0 || old_width == 0) {
                                    Log.d("ZINGLOGSHOW", "w "+ params.width + "h "+ params.height);

                                    params.width = 400;
                                    params.height = 450;
                                } else {
                                    params.width = old_width;
                                    params.height = old_height;
                                }
                                if (old_x != 0 || old_y != 0){
                                    params.x = old_x;
                                    params.y = old_y;
                                }
//
                                mWindowManager.updateViewLayout(mFloatingView,params);



                                loadBtn = mFloatingView.findViewById(R.id.load_btn);
//                                webView.loadUrl("file://" + path);
//                                webView.scrollBy(0, webView.getContentHeight() * 5);
                                loadBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (isWatching){
                                            Log.d("ZINGLOGSHOW", "unset listener");
                                            fileObserver.stopWatching();
                                            isWatching = false;
                                        } else {
                                            if (!path.equals("")){
                                                Log.d("ZINGLOGSHOW", "log file available, load html "+path);

                                                FileInputStream fis;
                                                String content = "";

                                                try {
                                                    fis = new FileInputStream(new File(path));
                                                    int size = fis.available();

                                                    byte[] buffer = new byte[size];
                                                    while (fis.read(buffer) != -1) {
                                                    }
                                                    content += new String(buffer);
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                        textView.setText(Html.fromHtml(content,Html.FROM_HTML_MODE_LEGACY));
                                                    } else {
                                                        textView.setText(Html.fromHtml(content));
                                                    }

                                                } catch (IOException e) {
                                                    Log.d("ZINGLOGSHOW", "error reading file "+ e.getMessage());

                                                }
//                                                webView.loadUrl("file://" + path);
//                                                webView.scrollTo(0, webView.getContentHeight() * 3);


                                                fileObserver.startWatching();
                                                isWatching = true;
                                            } else {
                                                Log.d("ZINGLOGSHOW", "log file null");
                                            }
                                        }
                                    }
                                });
//                                expandedView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                            }
                        }
//                        Log.d("ZINGLOGSHOW", "return "+isViewCollapsed());

                        return isViewCollapsed();
                }
                return false;
            }
        });

    }


    public void changePosition(int dx, int dy){
        params.x = params.x + dx;
        params.y = params.y + dy;

        mWindowManager.updateViewLayout(mFloatingView,params);

    }
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fileObserver.stopWatching();
        isWatching = false;
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
