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

package uoa.are.dm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import uoa.are.common.ActivityType;
import uoa.are.common.Configure;
import uoa.are.common.Const;
import uoa.are.common.PhaseType;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;
import uoa.are.util.TimeUtil;

/**
 * Utility class to query data from database and produce some reports.
 * 
 * @author hliu482
 * 
 */
public class ReportManager {
    static Logger logger = Logger.getLogger(ReportManager.class);

    /**
     * Get volume of data. How many data in total in the system.
     * 
     * @return seconds
     * @throws Exception
     */
    public static long getDataVolume(int user_id) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS d FROM data";
            if (user_id != 0)
                sql += " WHERE project_id IN (SELECT id FROM project WHERE user_id = " + user_id + ")";
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            return sc.getLong("d");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get total seconds of data during the day
     * 
     * @param prj_id
     * @param sub_id
     * @param date
     * @return seconds
     */
    public static int getDuration(String prj_id, String sub_id, String date) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS d FROM data WHERE project_id = " + prj_id + " AND subject_id = " + sub_id
                    + " AND ts LIKE '" + date + "%'";
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            return sc.getInt("d");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get total seconds of data for subject.
     * 
     * @param prj_id
     * @param sub_id
     * @return seconds
     */
    public static int getDuration(String prj_id, String sub_id) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS d FROM data WHERE project_id = " + prj_id + " AND subject_id = " + sub_id;
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            return sc.getInt("d");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get activity result for the day.
     * 
     * @param prj_id
     * @param sub_id
     * @param date
     * @return
     */
    public static RecElement[] getActForDay(String prj_id, String sub_id, String date) {
        RecElement[] ret = new RecElement[60 * 60 * 24];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = new RecElement();

        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT ts, num, num_filt, sma, angle FROM data WHERE project_id = " + prj_id
                    + " AND subject_id = " + sub_id + " AND ts LIKE '" + date + "%'" + " ORDER BY ts";
            logger.debug(sql);
            sc.execute(sql);

            while (sc.next()) {
                String ts = sc.getString("ts");
                int s = Integer.parseInt(ts.substring(8, 10)) * 3600 + Integer.parseInt(ts.substring(10, 12)) * 60
                        + Integer.parseInt(ts.substring(12));
                ret[s].num_filt = sc.getInt("num_filt");
                ret[s].num = sc.getInt("num");
                ret[s].sma = sc.getDouble("sma");
                ret[s].angle = sc.getDouble("angle");
            }
            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get activity result.
     * 
     * @param prj_id
     * @param sub_id
     * @param start_time
     * @return
     */
    public static RecElement[] getActForRange(String prj_id, String sub_id, String start_time, int count) {
        RecElement[] ret = new RecElement[count];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = new RecElement();

        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT ts, num, num_filt, sma, angle FROM data WHERE project_id = " + prj_id
                    + " AND subject_id = " + sub_id + " AND ts >= '" + start_time + "'" + " ORDER BY ts LIMIT " + count;
            logger.debug(sql);
            sc.execute(sql);

            int index = 0;
            while (sc.next()) {
                ret[index].num_filt = sc.getInt("num_filt");
                ret[index].num = sc.getInt("num");
                ret[index].sma = sc.getDouble("sma");
                ret[index].angle = sc.getDouble("angle");
                index++;
            }
            logger.info("index: " + index);
            logger.info("start sma of range: " + ret[0].sma);
            logger.info("end sma of range: " + ret[index - 1].sma);

            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static final int SIZE_OF_WINDOW = Integer.parseInt(Configure.getInstance().getProperty("SIZE_OF_WINDOW"));
    private static final int range = SIZE_OF_WINDOW * 60;
    private static final int seconds_of_day = 24 * 60 * 60;

    /**
     * Get activity around a point of time.
     * 
     * @param prj_id
     * @param sub_id
     * @param date
     * @param time
     * @return second-by-second activity result
     * @throws Exception
     */
    public static ActData getAct(String prj_id, String sub_id, String date, int time) throws Exception {

        logger.debug("time: " + time + "(" + TimeUtil.formatSecond2(time) + ")");
        RecElement[] act = ReportManager.getActForDay(prj_id, sub_id, date);

        // calculate real start/end second
        int start_second, end_second;
        int index = time;
        if (act[index].num_filt == ActivityType.UNDEFINED) {
            logger.error("FATAL ERROR!!! index = " + index);
            return null;
        }

        // calculate temporary start/end second
        int tmp_start = (time - range / 2) < 0 ? 0 : (time - range / 2);
        int tmp_end = (time + range / 2 - 1) > (seconds_of_day - 1) ? (seconds_of_day - 1) : (time + range / 2 - 1);
        // int tmp_end = (time + range / 2) > (seconds_of_day) ?
        // (seconds_of_day)
        // : (time + range / 2);

        logger.debug("[tmp_start, tmp_end] = [" + tmp_start + ", " + tmp_end + "]");
        logger.debug("[tmp_start, tmp_end] = [" + TimeUtil.formatSecond2(tmp_start) + ", "
                + TimeUtil.formatSecond2(tmp_end) + "]");

        // get start point
        // Only look for continuous result, stop at boundary
        for (; index >= tmp_start; --index) {
            if (act[index].num_filt == ActivityType.UNDEFINED)
                break;
        }
        if (index < tmp_start)
            start_second = tmp_start;
        else
            start_second = index + 1;

        // get end point
        // Only look for continuous result, stop at boundary
        index = time;
        for (; index <= tmp_end; ++index) {
            if (act[index].num_filt == ActivityType.UNDEFINED)
                break;
        }
        if (index > tmp_end)
            end_second = tmp_end;
        else
            end_second = index - 1;

        // now, [start_second, end_second] < [tmp_start, tmp_end] < [time -
        // range/2, time + range/2]
        logger.debug("[start_second, end_second] = [" + start_second + ", " + end_second + "]");
        logger.debug("[start_second, end_second] = [" + TimeUtil.formatSecond2(start_second) + ", "
                + TimeUtil.formatSecond2(end_second) + "]");

        ActData ad = new ActData();
        ad.setStart_second(start_second);
        ad.setEnd_second(end_second);
        ad.setAct(Arrays.copyOfRange(act, start_second, end_second + 1));
        return ad;
    }

    /**
     * Get time string for a given phase name of a day.
     * <p>
     * MORNING: 6 ~ 12;
     * <p>
     * AFTERNOON: 12 ~ 18;
     * <p>
     * EVENING: 18 ~ 24;
     * <p>
     * NIGHT: 0 ~ 6;
     * <p>
     * 
     * @param date
     * @param phase
     * @return
     */
    private static String buildPhaseWhereClause(String date, int phase) {
        if (phase == PhaseType.MORNING) {
            return "ts >= '" + date + "050000' AND ts <= '" + date + "115959'";
        } else if (phase == PhaseType.AFTERNOON) {
            return "ts >= '" + date + "120000' AND ts <= '" + date + "165959'";
        } else if (phase == PhaseType.EVENING) {
            return "ts >= '" + date + "170000' AND ts <= '" + date + "205959'";
        } else if (phase == PhaseType.NIGHT) {
            return "(ts >= '" + date + "210000' AND ts <= '" + date + "235959')" + " OR " + "(ts >= '" + date
                    + "000000' AND ts <= '" + date + "045959')";
        }
        throw new IllegalArgumentException("buildPhaseWhereClause." + phase);
    }

    /**
     * Get total seconds of data during the day for a specific phase.
     * 
     * @param prj_id
     * @param sub_id
     * @param date
     * @param act_type
     * @param phase
     * @return
     */
    public static int getDurationOfActForPhase(String prj_id, String sub_id, String date, int act_type, int phase) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS d FROM data WHERE project_id = " + prj_id + " AND subject_id = " + sub_id
                    + " AND ts LIKE '" + date + "%'" + " AND num_filt = " + act_type + " AND ("
                    + buildPhaseWhereClause(date, phase) + ")";
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            int d = sc.getInt("d");
            // logger.info(act_type + ": " + d);
            return d;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static int getDurationOfAct(String prj_id, String sub_id, String start_time, String end_time, int act_type) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS d FROM data WHERE project_id = " + prj_id + " AND subject_id = " + sub_id
                    + " AND ts >= '" + start_time + "' AND ts <= '" + end_time + "' AND num_filt = " + act_type;
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            int d = sc.getInt("d");
            return d;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get total seconds of data during the day for a specific hour.
     * 
     * @param prj_id
     * @param sub_id
     * @param date
     * @param act_type
     * @param hour
     * @return
     */
    public static int getDurationOfActForHour(String prj_id, String sub_id, String date, int act_type, int hour) {
        String strHour = ((hour < 10) ? "0" : "") + hour;
        String start_time = date + strHour + "0000";
        String end_time = date + strHour + "5959";
        return getDurationOfAct(prj_id, sub_id, start_time, end_time, act_type);
    }

    /**
     * Get total seconds of data during the day.
     * 
     * @param prj_id
     * @param sub_id
     * @param date
     * @param act_type
     * @return
     */
    public static int getDurationOfActForDay(String prj_id, String sub_id, String date, int act_type) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS d FROM data WHERE project_id = " + prj_id + " AND subject_id = " + sub_id
                    + " AND ts LIKE '" + date + "%'" + " AND num_filt = " + act_type;
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            int d = sc.getInt("d");
            // logger.info(act_type + ": " + d);
            return d;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Get available report list for a specific subject.
     * 
     * @param prj_id
     * @param sub_id
     * @return
     * @throws Exception
     */
    public static List<DailyReport> getDailyReportList(String prj_id, String sub_id) throws Exception {
        List<DailyReport> result = new LinkedList<DailyReport>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT DISTINCT(SUBSTRING(ts,1,8)) AS dl FROM data WHERE project_id = " + prj_id
                    + " AND subject_id = " + sub_id + " ORDER BY dl DESC";
            logger.debug(sql);
            sc.execute(sql);

            while (sc.next()) {
                DailyReport dr = new DailyReport();
                dr.setDate(sc.getString("dl"));
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, Integer.parseInt(dr.getDate().substring(0, 4)));
                c.set(Calendar.MONTH, Integer.parseInt(dr.getDate().substring(4, 6)) - 1);
                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dr.getDate().substring(6, 8)));
                dr.setText(TimeUtil.DISPLAY_DATE_FORMAT.format(c.getTime()));
                result.add(dr);

                dr.setDuration(TimeUtil.formatSecond(getDuration(prj_id, sub_id, dr.getDate())));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

}