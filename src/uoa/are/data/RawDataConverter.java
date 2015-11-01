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

package uoa.are.data;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import uoa.are.algorithm.ARProcessor;
import uoa.are.common.Configure;
import uoa.are.util.ARProcessExecutor;
import uoa.are.util.FileUtil;
import uoa.are.util.TaskManager;

/**
 * Base / Abstract class for raw data converter.
 * 
 * @author hliu482
 * 
 */
public abstract class RawDataConverter implements Runnable, Observer {
    protected Logger logger = Logger.getLogger(this.getClass());

    protected File _srcFile;
    protected File _dstFile;
    private String _dst_dir;
    private String _project_id;
    private String _subject_id;
    private String _user_id;
    private String _algorithm;

    private int _year;
    private int _month;
    private int _day;
    private int _hour;
    private int _minute;
    private String _startTime;
    private Calendar _startCalendar;

    private long _duration = 0; // duration of signal in second

    private static final String DEFAULT_ALGORITHM = Configure.getInstance().getProperty("DEFAULT_ALGORITHM");

    private static final String TASK_CATEGORY = RawDataConverter.class.getSimpleName();

    private static final int BLOCK_SIZE_HOUR = 2;
    protected static final long BLOCK_SIZE_100HZ = BLOCK_SIZE_HOUR * 60 * 60 * 100;

    public RawDataConverter(File src, String dst_dir, String project_id, String subject_id, String user_id, String algorithm) {
        _srcFile = src;
        _dst_dir = dst_dir;
        _project_id = project_id;
        _subject_id = subject_id;
        _user_id = user_id;
        _algorithm = algorithm;

        TaskManager.getInstance().removeTask(TASK_CATEGORY, _user_id, _srcFile.getName());
        TaskManager.getInstance().addTask(TASK_CATEGORY, _user_id, _srcFile.getName());
    }

    /**
     * Calling this function will affect the destination file name.
     * 
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    protected void setStartTime(int year, int month, int day, int hour, int minute) {
        _year = year;
        _month = month;
        _day = day;
        _hour = hour;
        _minute = minute;

        _startCalendar = new GregorianCalendar(_year, _month - 1, _day, _hour, _minute, 0);

        _startTime = year + (((month < 10) ? "0" : "") + month) + (((day < 10) ? "0" : "") + day) + (((hour < 10) ? "0" : "") + hour) + (((minute < 10) ? "0" : "") + minute);
        String dst_file_name = year + "_" + (((month < 10) ? "0" : "") + month) + "_" + (((day < 10) ? "0" : "") + day) + "_" + (((hour < 10) ? "0" : "") + hour) + "_"
                + (((minute < 10) ? "0" : "") + minute) + ".csv";
        _dstFile = new File(_dst_dir + dst_file_name);
    }

    private Map<String, Integer> observableMap = new HashMap<String, Integer>();
    private List<String> observables = new LinkedList<String>();
    private int durationOfActivityInSecond = 0;

    private Object obj = new Object();

    @Override
    synchronized public void update(Observable o, Object arg) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) arg;
        if (data.containsKey("id")) {
            String id = (String) data.get("id");
            observables.remove(id);
            observableMap.put(id, 1);
            logger.info("One observable is completed: " + id);
        }
        if (data.containsKey("length")) {
            int length = (Integer) data.get("length");
            durationOfActivityInSecond += length;
        }
        logger.info("Obserables: " + observables.size() + " / " + observableMap.size());
        synchronized (obj) {
            obj.notify();
            logger.debug("obj.notify");
        }
    }

    synchronized private void addObservable(String arg) {
        observableMap.put(arg, 0);
        observables.add(arg);
        logger.info("Obserables: " + observables.size() + " / " + observableMap.size());
    }

    private void postProcess() {
        logger.info("Start post process: " + _startTime + " -> " + durationOfActivityInSecond);
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(ARProcessor.MODE, ARProcessor.Mode.CORRECTION);
        conf.put(ARProcessor.USER_ID, _user_id);
        conf.put(ARProcessor.RAW_FILE, _srcFile.getName());
        conf.put(ARProcessor.PRJ_ID, _project_id);
        conf.put(ARProcessor.SUB_ID, _subject_id);
        conf.put(ARProcessor.DURATION, durationOfActivityInSecond);
        conf.put(ARProcessor.STARTTIME, _startTime);

        // Kick off next step of processing
        launchARProcessor(conf);
    }

    private void waitForComplete() {
        while (observables.size() > 0) {
            synchronized (obj) {
                try {
                    logger.info("Waiting " + observables.size() + " observable to complete ");
                    obj.wait(60000);
                    logger.debug("obj is woken up...");
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        }
        // all done
        logger.info("ALL DONE: " + durationOfActivityInSecond);

        // now, it is time to do overall correction
        postProcess();
    }

    protected void processCurrentBlock() {
        // Start activity recognition thread
        String newSrcFile = _dstFile.getAbsolutePath();
        // add to observables
        addObservable(_dstFile.getName());

        logger.info("Start processing converted file: " + _dstFile);
        String newDstFile = FileUtil.removeSuffix(_dstFile.getAbsolutePath());

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(ARProcessor.MODE, ARProcessor.Mode.RECOGNITION);
        conf.put(ARProcessor.USER_ID, _user_id);
        conf.put(ARProcessor.RAW_FILE, _srcFile.getName());
        conf.put(ARProcessor.SRC_FILE, newSrcFile);
        conf.put(ARProcessor.DST_FILE, newDstFile);
        conf.put(ARProcessor.PRJ_ID, _project_id);
        conf.put(ARProcessor.SUB_ID, _subject_id);
        conf.put(ARProcessor.OBSERVER, this);
        conf.put(ARProcessor.OBSERVERABLE_ID, _dstFile.getName());

        // Kick off next step of processing
        launchARProcessor(conf);

        // create a new file
        _startCalendar.add(Calendar.HOUR, BLOCK_SIZE_HOUR);
        int year = _startCalendar.get(Calendar.YEAR);
        int month = _startCalendar.get(Calendar.MONTH) + 1;
        int day = _startCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = _startCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = _startCalendar.get(Calendar.MINUTE);
        String dst_file_name = year + "_" + (((month < 10) ? "0" : "") + month) + "_" + (((day < 10) ? "0" : "") + day) + "_" + (((hour < 10) ? "0" : "") + hour) + "_"
                + (((minute < 10) ? "0" : "") + minute) + ".csv";
        _dstFile = new File(_dst_dir + dst_file_name);
        logger.info("Create a new file: " + _dstFile.getName());
    }

    /**
     * What needs to be done after converting. It will invoke a following
     * process doing the activity analysis based on the converted unified
     * accelerometer data.
     * 
     * @param conf
     */
    private void launchARProcessor(Map<String, Object> conf) {
        try {
            Class<?> c = Class.forName((_algorithm == null) ? DEFAULT_ALGORITHM : _algorithm);
            logger.info("postProcess by using: " + c.getName());
            ARProcessor ar = (ARProcessor) c.getConstructor(Map.class).newInstance(conf);

            logger.info("Add new ActivityRecognition task to thread executor");
            ARProcessExecutor.getInstance().execute(ar);
        } catch (Exception e) {
            logger.error("postProcess failed", e);
        }
    }

    protected abstract void doConvert();

    protected void addDuration(long d) {
        _duration += d;
    }

    @Override
    public void run() {
        logger.debug(this.getClass().getName() + " is running ...");
        logger.info("Converting " + _srcFile.getAbsolutePath() + " to " + _dstFile.getAbsolutePath() + " [project_id = " + this._project_id + ", subject_id = " + this._subject_id
                + "]");

        // clean old AR tasks
        TaskManager.getInstance().removeTask(ARProcessor.class.getSimpleName(), _user_id, _srcFile.getName());

        doConvert();

        waitForComplete();

        TaskManager.getInstance().finishTask(TASK_CATEGORY, _user_id, _srcFile.getName());
        TaskManager.getInstance().updateTask(TASK_CATEGORY, _user_id, _srcFile.getName(), _srcFile.getName() + "|" + _duration);
    }

}
