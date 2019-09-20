package com.example.logshowjava;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.logshowjava.fragment.WebviewFragment;

import java.io.File;
import java.io.FileWriter;

import timber.log.Timber;

import static com.example.logshowjava.LogHelper.isExternalStorageAvailable;

public class MainActivity extends AppCompatActivity {
    Button button;

    WebView webView;
//    FileLoggingTree fileLoggingTree;
    int counter = 0;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
//        webView = findViewById(R.id.webview);

        if (isExternalStorageAvailable()){
            // Check whether this app has write external storage permission or not.
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // If do not grant write external storage permission.
            if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            {
                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }else {
//
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

                WebviewFragment webviewFragment = WebviewFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.webview_fragment, webviewFragment);
                fragmentTransaction.commit();
                Toast.makeText(getApplicationContext(), "granted permission", Toast.LENGTH_LONG).show();
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


}
