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

package uoa.are.algorithm;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uoa.are.common.Const;
import uoa.are.common.Env;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;
import uoa.are.dm.RecElement;
import uoa.are.dm.ReportManager;
import uoa.are.util.FileUtil;
import uoa.are.util.TaskManager;
import uoa.are.util.TimeUtil;

/**
 * This is a base abstract class of Activity Recognition Processor. Every new
 * Activity Recognition Algorithm need a new processor that extends from this
 * class.
 * 
 * @author hliu482
 * 
 */
public abstract class ARProcessor extends Observable implements Runnable {
    public enum Mode {
        RECOGNITION, CORRECTION
    }

    public static final String MODE = "__MODE__";
    public static final String USER_ID = "__USER_ID__";
    public static final String RAW_FILE = "__RAW_FILE__";
    public static final String SRC_FILE = "__SRC_FILE__";
    public static final String DST_FILE = "__DST_FILE__";
    public static final String PRJ_ID = "__PRJ_ID__";
    public static final String SUB_ID = "__SUB_ID__";
    public static final String OBSERVER = "__OBSERVER__";
    public static final String OBSERVERABLE_ID = "__OBSERVERABLE_ID__";
    public static final String ALGORITHM = "__ALGORITHM__";
    public static final String DURATION = "__DURATION__";
    public static final String STARTTIME = "__STARTTIME__";

    private static final String TASK_CATEGORY = ARProcessor.class.getSimpleName();

    protected Logger logger = Logger.getLogger(this.getClass());
    protected Map<String, Object> _conf;

    protected Calendar start_time = null;
    private int duration = 0;
    protected RecElement[] activities = null;
    private Mode mode;

    private String task_name;

    public ARProcessor(Map<String, Object> conf) {
        _conf = conf;
        for (String key : _conf.keySet()) {
            logger.info(key + ": " + conf.get(key));
        }
        mode = (Mode) _conf.get(MODE);
        switch (mode) {
        case RECOGNITION:
            addObserver((Observer) _conf.get(OBSERVER));
            task_name = FileUtil.getName((String) _conf.get(RAW_FILE)) + "|" + FileUtil.getName((String) _conf.get(SRC_FILE)) + "|" + this.getClass().getName();
            TaskManager.getInstance().addTask(TASK_CATEGORY, (String) _conf.get(USER_ID), task_name);
            start_time = parseDateTime((String) _conf.get(SRC_FILE), Env.UNIFIED_SIGNAL_FILE_NAME_PATTERN);
            break;
        case CORRECTION:
            task_name = FileUtil.getName((String) _conf.get(RAW_FILE)) + "|ALL|" + this.getClass().getName();
            TaskManager.getInstance().addTask(TASK_CATEGORY, (String) _conf.get(USER_ID), task_name);
            start_time = parseDateTime((String) _conf.get(STARTTIME), Env.UNIFIED_DATETIME_PATTERN);
            duration = (Integer) _conf.get(DURATION);
            break;
        default:
            throw new IllegalArgumentException("Configuration \"" + MODE + "\" not specified!");
        }
        logger.info("start_time: " + start_time.getTime());
    }

    /**
     * Clean temporary file.
     */
    protected abstract void cleanUp();

    /**
     * Do activity recognition.
     * 
     * @return
     */
    protected abstract boolean doAR();

    /**
     * Do correction
     * 
     * @param result
     */
    protected abstract void doCorrection();

    /**
     * save the analysis result to database
     */
    private void saveResultToDB() {
        logger.info("saveResultToDB: " + start_time.getTime().toString() + " [num: " + activities.length + "]");

        if (activities == null || activities.length == 0) {
            logger.warn("No activities to be saved");
            return;
        }
        if (start_time == null) {
            logger.error("start_time is NULL");
            return;
        }
        String sql = "INSERT INTO data (project_id, subject_id, ts, sma, angle, num, num_filt) VALUES (" + _conf.get(PRJ_ID) + ", " + _conf.get(SUB_ID)
                + ", '__TS__', __SMA__, __ANGLE__, __NUM__, __NUM_FILT__)" + " ON DUPLICATE KEY UPDATE sma = __SMA__, angle = __ANGLE__, num = __NUM__, num_filt = __NUM_FILT__";
        SC sc = null;
        Calendar time = (Calendar) start_time.clone();
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            sc.setAutoCommit(false);
            int i = 0;
            int BATCH_SIZE = 120;
            for (RecElement p : activities) {
                String t = TimeUtil.getTime(time.getTime(), "yyyyMMddHHmmss");
                String s = sql.replace("__TS__", t).replace("__NUM__", "" + p.num).replace("__NUM_FILT__", "" + p.num_filt).replace("__ANGLE__", "" + p.angle)
                        .replace("__SMA__", "" + p.sma);
                // logger.debug(s);
                sc.execute(s);
                time.add(Calendar.SECOND, 1);
                i++;
                if (i % BATCH_SIZE == 0)
                    sc.commit();
            }
            sc.commit();
            logger.info("saveResultToDB done");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    /**
     * Run method.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        logger.debug(this.getClass().getName() + " is running ...");
        boolean successful = false;
        do {
            switch (mode) {
            case RECOGNITION:
                logger.info("Analyzing " + _conf.get(SRC_FILE));
                successful = doAR();
                break;
            case CORRECTION:
                logger.info("Correcting " + _conf.get(RAW_FILE));
                if (successful = loadActivities())
                    doCorrection();
                break;
            }
            if (successful) {
                saveResultToDB();
                cleanUp();
                TaskManager.getInstance().finishTask(TASK_CATEGORY, (String) _conf.get(USER_ID), task_name);
            } else {
                logger.error("======== doAR failed ========");
                logger.error("======== Run it again ========");
            }
        } while (!successful);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", _conf.get(OBSERVERABLE_ID));
        data.put("length", activities.length);

        // Notify observer
        setChanged();
        notifyObservers(data);
    }

    private boolean loadActivities() {
        logger.info("loadActivities for " + start_time.getTime() + " [" + duration + "]");
        String prjId = (String) _conf.get(PRJ_ID);
        String subId = (String) _conf.get(SUB_ID);
        String sTime = TimeUtil.getTime(start_time.getTime(), "yyyyMMddHHmmss");
        activities = ReportManager.getActForRange(prjId, subId, sTime, duration);
        return true;
    }

    /**
     * Parse signal start time from file name. The file name is unified by raw
     * data converter.
     * 
     * @param fileName
     *            (2015_01_19_11_34.csv) or (201501191134)
     * @return null if fileName does not match the pattern.
     */
    private Calendar parseDateTime(String fileName, String pattern) {
        String name = new File(fileName).getName();
        Pattern datePatt = Pattern.compile(pattern);
        Matcher m = datePatt.matcher(name);
        // logger.debug(name + " vs. " + datePatt);
        if (m.matches()) {
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hour = Integer.parseInt(m.group(4));
            int minute = Integer.parseInt(m.group(5));

            return new GregorianCalendar(year, month - 1, day, hour, minute);
        } else {
            logger.error("NOT match: " + name + ":" + datePatt);
        }
        return null;
    }

}