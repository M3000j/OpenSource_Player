package com.example.cor.mjplayer;

import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by M3000j on 12/19/2017.
 */

public class LyricPruning {

    ////get string in form of [03:22.09] and resturn the time
    public static int ExtractTime(String s) {
        String result = null;
        int time = 0;
        result = s;
        result = result.replace("[", "");
        result = result.replace("]", "");
        time = ParseTime(result);
        return time;
    }

    private static int ParseTime(String s) {
        int min = 0;
        int sec = 0;
        String[] minsec;
        minsec = s.split(":");/////split and get the second and minute in string format

            sec = (int) Double.parseDouble(minsec[1]);
            min = Integer.parseInt(minsec[0]) * 60;
        return sec + min;
    }
}
