package com.example.logshowjava;

import android.content.Context;
import android.util.Log;

import com.example.logshowjava.utility.LogHelper;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {
    final Context context;
    public FileLoggingTree(Context context){
        super();
        this.context = context;
    }
    private static final String LOG_TAG = FileLoggingTree.class.getSimpleName();

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            String logTimeStamp = new SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                    Locale.getDefault()).format(new Date());

            // Create file
            File file = LogHelper.getLogsFile(context);

            // If file created or exists save logs
            if (file != null) {
                FileWriter writer = new FileWriter(file, true);
                String textColor;
                switch (priority){
                    case Log.ERROR:
                        textColor = "#E74C3C";
                        break;
                    case Log.DEBUG:
                        textColor = "#B7950B";
                        break;
                    default:
                        textColor = "#212F3D";
                        break;
                }

                writer.append("<p priority=\"").append(String.valueOf(priority))
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
            }
            LogHelper.writeNewLogTrigger(file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while logging into file : " + e);
        }
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }


}
