package com.example.logshowjava.parser;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZingTVHtmlParser extends BaseHtmlParser {
    @Override
    public String read(String raw) {
        String[] listPtag = raw.split("</p>");
        String newP="";
        String priority = "";
        String textColor;
        for (int i = 0; i < listPtag.length; i++){
            newP = "<p ";

            Pattern priorityPattern = Pattern.compile("(priority=\")([0-9])(\")");
            Matcher matcher = priorityPattern.matcher(listPtag[i]);
            if (matcher.find()){
                priority = matcher.group(2);
            }
            newP += "priority=\""+priority+"\">";
            switch (Integer.parseInt(priority)){
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
            newP += "<font color=\""+textColor+"\">";

            priorityPattern = Pattern.compile("(<strong(.+)\">)(.*)");
            matcher = priorityPattern.matcher(listPtag[i]);
            if (matcher.find()){
                newP += "<strong>"+matcher.group(3).replace("&nbsp","&nbsp ");

            } else {
                Log.d("ZINGLOGSHOW", "not match");

            }

            newP += "</font>";
//            Log.d("ZINGLOGSHOW", newP);

            listPtag[i] = newP;

        }

        return TextUtils.join("</p>", listPtag);

    }
}
