package com.codepath.apps.tweeton;

import android.text.format.DateUtils;

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
}
