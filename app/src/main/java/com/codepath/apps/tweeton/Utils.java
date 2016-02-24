package com.codepath.apps.tweeton;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by JaneChung on 2/16/16.
 */
public class Utils {

    public static String getTimeStamp(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
                String[] diff = relativeDate.split(" ");
                if (diff.length >= 2) {
                    relativeDate = diff[0] + diff[1].charAt(0);
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void showSnackBarForException(View v) {

        final Snackbar snackbar = Snackbar.make(v, "Error: Please try again",Snackbar.LENGTH_SHORT);

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }

    public static void showToastForException(Context context) {

        Toast.makeText(context, "Error: Please try again", Toast.LENGTH_SHORT).show();
    }
}
