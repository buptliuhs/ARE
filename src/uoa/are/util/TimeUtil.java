// Copyright 2015 Tony (Huansheng) Liu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package uoa.are.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ibm.icu.math.BigDecimal;

/**
 * Utility class to manipulate time.
 * 
 * @author hliu482
 * 
 */
public class TimeUtil {

    public static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DATABASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat DISPLAY_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /**
     * Get time with specific format.
     * 
     * @param date
     * @param format
     * @return
     */
    public static String getTime(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * Get current time with specific format.
     * 
     * @param format
     * @return
     */
    public static String getTime(String format) {
        return getTime(Calendar.getInstance().getTime(), format);
    }

    /**
     * Format second value to hour value (modular).
     * 
     * @param v
     * @return
     */
    public static int getHour(long v) {
        int h = (int) (v / 3600);
        return h;
    }

    /**
     * Format second value to minute value (modular).
     * 
     * @param v
     * @return
     */
    public static int getMinute(int v) {
        int h = v / 3600;
        int m = (v - h * 3600) / 60;
        return m;
    }

    /**
     * Format second value to second value (modular).
     * 
     * @param v
     * @return
     */
    public static int getSecond(int v) {
        int h = v / 3600;
        int m = (v - h * 3600) / 60;
        int s = v - h * 3600 - m * 60;
        return s;
    }

    /**
     * Format second value to string (00:00:00)
     * 
     * @param v
     * @return
     */
    public static String formatSecond2(long v) {
        long h = v / 3600;
        long m = (v - h * 3600) / 60;
        long s = v - h * 3600 - m * 60;
        return ((h < 10) ? "0" : "") + h + ":" + ((m < 10) ? "0" : "") + m + ":" + ((s < 10) ? "0" : "") + s;
    }

    /**
     * Format second value to string (00:00)
     * 
     * @param v
     * @return
     */
    public static String formatSecond3(long v) {
        long m = v / 60;
        long s = v - m * 60;
        return ((m < 10) ? "0" : "") + m + ":" + ((s < 10) ? "0" : "") + s;
    }

    /**
     * Format second value to string (2h, 3m, 15s)
     * 
     * @param v
     * @return
     */
    public static String formatSecond(long v) {
        long h = v / 3600;
        long m = (v - h * 3600) / 60;
        long s = v - (h * 3600) - (m * 60);
        return h + "h, " + m + "m, " + s + "s";
    }

    /**
     * Format second value to string (2h, 3m, 15s) on 24 hours basis.
     * 
     * @param s
     * @param t
     * @return
     */
    public static String formatSecond(int s, int t) {
        int d = (int) (((double) s / t) * 3600 * 24);
        return formatSecond(d);
    }

    /**
     * Seconds to minutes.
     * 
     * @param s
     * @return
     */
    public static double secondsToMinutes(int s) {
        return secondsToMinutes(s, 1);
    }

    public static double secondsToMinutes(int s, int d) {
        return NumberUtil.getDouble((double) s / 60, BigDecimal.ROUND_UP, d);
    }

    /**
     * Seconds to hours.
     * 
     * @param s
     * @return
     */
    public static double secondsToHours(int s) {
        return secondsToHours(s, 2);
    }

    public static double secondsToHours(int s, int d) {
        return NumberUtil.getDouble((double) s / 3600, BigDecimal.ROUND_UP, d);
    }

    /**
     * Get list of day before date.
     * 
     * @param date
     * @param num
     * @return
     */
    public static List<String> getListOfPreviousDays(String date, int num) {
        // date: 20150116
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        c.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        List<String> ret = new LinkedList<String>();
        int i = num;
        do {
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH) + 1;
            int d = c.get(Calendar.DAY_OF_MONTH);
            String s = "" + y + (m < 10 ? "0" : "") + m + (d < 10 ? "0" : "") + d;
            ret.add(0, s);
            i--;
            c.add(Calendar.DAY_OF_MONTH, -1);
        } while (i > 0);
        return ret;
    }

    public static void main(String args[]) {
        System.out.println(TimeUtil.getTime("yyyyMMddHHmmss"));
        System.out.println(TimeUtil.getTime("yyMMddHHmmss"));
        System.out.println(Calendar.getInstance().getTime());

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2015);
        c2.set(Calendar.MONTH, 5);
        c2.set(Calendar.DAY_OF_MONTH, 29);
        c2.set(Calendar.HOUR, 9);
        c2.set(Calendar.MINUTE, 30);
        c2.set(Calendar.SECOND, 30);
        System.out.println("c2 = " + c2.getTime());

    }
}