package com.example.logshowjava;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.logshowjava.service.FloatingLogViewService;
import com.example.logshowjava.utility.LogHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

import static com.example.logshowjava.utility.LogHelper.isExternalStorageAvailable;

public class MainActivity extends AppCompatActivity {
    Button button;

//    CustomWebView webView;
//    FileLoggingTree fileLoggingTree;


    int counter = 0;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
//        webView = findViewById(R.id.webview);

        file = LogHelper.getLogsFile(getApplicationContext());


        if (isExternalStorageAvailable()){

            // Check whether this app has write external storage permission or not.
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // If do not grant write external storage permission.
            if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            {

                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }else {
//                Log.d("ZINGLOGSHOW", "HERE");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));

                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {

                    Intent intent = new Intent(MainActivity.this, FloatingLogViewService.class);
                    /* Send path, for example: /storage/emulated/0/Android/data/com.example.logshowjava/files/Documents/showlog/30-09-2019.html */
                    intent.putExtra("path",LogHelper.getLogsFile(this).getAbsolutePath());


                    startService(intent);
                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {

//                            handler.postDelayed(this, 1000);

                            Log.d("ZINGLOGSHOW", "write to file " + counter);
                            String textColor = "#E74C3C";
                            String logTimeStamp = new SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                                    Locale.getDefault()).format(new Date());
                            String tag = "TEST";
                            String message = "test"+counter;

                            final FileWriter writer;
                            try {
                                writer = new FileWriter(file, true);
                                writer.append("<p priority=\"").append(String.valueOf(2))
                                        .append("\" style=\"background:lightgray;overflow-wrap: break-word;\"><strong ").append("style=\"color:#145A32;\">&nbsp&nbsp")
                                        .append(logTimeStamp)
                                        .append(" :&nbsp&nbsp</strong><strong style=\"color:").append(textColor).append(";\">&nbsp&nbsp")
                                        .append(tag)
                                        .append("</strong> - ")
                                        .append("<span style=\"color:").append(textColor).append(";\">")
                                        .append(message)
                                        .append("</span>")
                                        .append("</p>");
                                writer.flush();
                                writer.close();
                                counter++;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            handler.postDelayed(this, 2000);



                        }
                    };

//                    handler.postDelayed(runnable, 2000);

                }
//                // Save email_public.txt file to /storage/emulated/0/DCIM folder
//                String publicDcimDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DCIM);
//
//                File newFile = new File(publicDcimDirPath, "email_public.txt");
//
//                FileWriter fw = new FileWriter(newFile);
//
//                fw.write(emailEditor.getText().toString());
//
//                fw.flush();
//
//                fw.close();
//                File filePath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//                Log.d("ZINGLOGSHOW", filePath.getAbsolutePath());

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Timber.e("Log time %d", counter);
                        counter++;
                    }
                });
//                generateFile("test.txt");
//                fileLoggingTree = new FileLoggingTree(getApplicationContext());
//                LogHelper.setWriteNewLogListener(new LogHelper.WriteNewLogListener() {
//                    @Override
//                    public void onWriteNewLog(String path) {
//                        Log.d("ZINGLOGSHOW", "WRITE  "+ path);
//                        try {
//                            webView.loadUrl("file://"+path);
//
//                        } catch (Exception e){
//                            Log.d("ZINGLOGSHOW", e.getMessage());
//                        }
//                    }
//                });

//                WebviewFragment webviewFragment = WebviewFragment.newInstance();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.add(R.id.webview_fragment, webviewFragment);
//                fragmentTransaction.commit();
//                Toast.makeText(getApplicationContext(), "granted permission", Toast.LENGTH_LONG).show();
            }
        }


    }

    // This method is invoked after user click buttons in permission grant popup dialog.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION)
        {
            int grantResultsLength = grantResults.length;
            if(grantResultsLength > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
//            //Check if the permission is granted or not.
//            if (resultCode == RESULT_OK) {
//                Intent intent = new Intent(MainActivity.this, FloatingLogViewService.class);
//                intent.putExtra("path",LogHelper.getLogsFile(this).getAbsolutePath());
//                Log.d("ZINGLOGSHOW", "HERE");
//
//                startService(intent);
//                Toast.makeText(this,
//                        "Service started",
//                        Toast.LENGTH_SHORT).show();
//            } else { //Permission is not available
//                Toast.makeText(this,
//                        "Draw over other app permission not available. Closing the application",
//                        Toast.LENGTH_SHORT).show();
//
//                finish();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

}
