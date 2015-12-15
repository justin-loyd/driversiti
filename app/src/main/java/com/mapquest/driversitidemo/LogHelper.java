package com.mapquest.driversitidemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class LogHelper {
    private static final String LOG_TAG = LogHelper.class.getSimpleName();

    private static void appendLog(String text) {
        File logFile = new File("sdcard/driversiti-log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(Calendar.getInstance().getTime() + ": " + text);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void emailLogFile(Activity activity){
        File logFile = new File("sdcard/driversiti-log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Driversiti Log");

        Uri uri = Uri.fromFile(logFile);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        activity.startActivity(Intent.createChooser(emailIntent, "Send Email:"));
    }

    public static void updateStatus(final TextView textView, final String status){
        Log.i(LOG_TAG, Html.fromHtml(status).toString());
        appendLog(Html.fromHtml(status).toString());

        ((Activity)textView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(Html.fromHtml("<br>" + DateFormat.format("hh:mm:ss:", Calendar.getInstance().getTime()) + " " + status));
            }
        });
    }
}
