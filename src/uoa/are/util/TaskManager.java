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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import uoa.are.algorithm.ARProcessor;
import uoa.are.common.Const;
import uoa.are.data.RawDataConverter;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;
import uoa.are.dm.Algorithm;
import uoa.are.dm.AlgorithmManager;

public class TaskManager {
    private Logger logger = Logger.getLogger(this.getClass());

    private static TaskManager _instance = null;

    private TaskManager() {
        cleanUnfinishedTasks();
    }

    synchronized public static TaskManager getInstance() {
        if (_instance == null) {
            _instance = new TaskManager();
        }
        return _instance;
    }

    private void cleanUnfinishedTasks() {
        logger.info("cleanUnfinishedTasks");
        String sql = "DELETE FROM task WHERE et IS NULL";
        runSql(sql);
    }

    public Map<String, Integer> getRunningTaskSummary(String key) {
        Map<String, Integer> running_task = new HashMap<String, Integer>();
        SC sc = null;
        try {
            String sql = "SELECT c, COUNT(*) as d FROM task WHERE k = '" + key + "' AND et is NULL GROUP BY c";
            sc = new SC(new ConnectionEx(Const.ARE));
            logger.debug(sql);
            sc.execute(sql);
            while (sc.next()) {
                running_task.put(sc.getString("c"), sc.getInt("d"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }

        return running_task;
    }

    private void runSql(String sql) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            sc.execute(sql);
            logger.debug(sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    synchronized public void addTask(String category, String key, String value) {
        logger.info("addTask: " + category + ":" + key + ":" + value);
        String sql = "INSERT INTO task (c, k, v, st) VALUES ('" + category + "', '" + key + "', '" + value
                + "', NOW())" + " ON DUPLICATE KEY UPDATE st = NOW(), et = NULL";
        runSql(sql);
    }

    synchronized public void finishTask(String category, String key, String value) {
        logger.info("finishTask: " + category + ":" + key + ":" + value);
        String sql = "UPDATE task SET et = NOW() WHERE c = '" + category + "' AND k = '" + key + "' AND v = '" + value
                + "'";
        runSql(sql);
    }

    synchronized public void updateTask(String category, String key, String value, String uValue) {
        logger.info("updateTask: " + category + ":" + key + ":" + uValue);
        String sql = "UPDATE task SET v = '" + uValue + "' WHERE c = '" + category + "' AND k = '" + key
                + "' AND v = '" + value + "'";
        runSql(sql);
    }

    synchronized public void removeTask(String category, String key, String value) {
        logger.info("removeTask: " + category + ":" + key + ":" + value);
        String sql = "DELETE FROM task WHERE c = '" + category + "' AND k = '" + key + "' AND v LIKE '" + value + "%'";
        runSql(sql);
    }

    public boolean isRunning(String key, String value) {
        SC sc = null;
        try {
            String sql = "SELECT COUNT(*) as d FROM task WHERE k = '" + key + "' AND v LIKE '" + value
                    + "%' AND et is NULL";
            sc = new SC(new ConnectionEx(Const.ARE));
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            return sc.getInt("d") != 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return false;
    }

    public String getAlgorithmName(String key, String value) {
        String c = ARProcessor.class.getSimpleName();
        SC sc = null;
        try {
            String sql = "SELECT v FROM task WHERE c = '" + c + "' AND k = '" + key + "' AND v LIKE '" + value
                    + "%' ORDER BY st DESC LIMIT 1";
            sc = new SC(new ConnectionEx(Const.ARE));
            logger.debug(sql);
            sc.execute(sql);
            while (sc.next()) {
                String v = sc.getString("v");
                String className = v.substring(v.lastIndexOf('|') + 1);
                Algorithm a = AlgorithmManager.getAlgorithmByClassName(className);
                if (a == null)
                    return "";
                return a.getName();
            }
            return "";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    public String getProcessingTime(String key, String value) {
        SC sc = null;
        try {
            String sql = "SELECT st, et FROM task WHERE k = '" + key + "' AND v LIKE '" + value
                    + "%' AND et is NOT NULL";
            sc = new SC(new ConnectionEx(Const.ARE));
            logger.debug(sql);
            sc.execute(sql);
            Timestamp st = null;
            Timestamp et = null;
            while (sc.next()) {
                Timestamp st1 = sc.getTimestamp("st");
                if (st == null || st1.compareTo(st) < 0) {
                    st = st1;
                }
                Timestamp et1 = sc.getTimestamp("et");
                if (et == null || et1.compareTo(et) > 0) {
                    et = et1;
                }
            }
            if (st == null || et == null)
                return "";
            else
                return TimeUtil.formatSecond3((et.getTime() - st.getTime()) / 1000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    public String getProcessedAt(String key, String value) {
        String c = RawDataConverter.class.getSimpleName();
        SC sc = null;
        try {
            String sql = "SELECT COUNT(*) AS d, st FROM task WHERE c = '" + c + "' AND k = '" + key + "' AND v LIKE '"
                    + value + "%' LIMIT 1";
            sc = new SC(new ConnectionEx(Const.ARE));
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            int d = sc.getInt("d");
            if (d == 0)
                return "";
            Timestamp st = sc.getTimestamp("st");
            return TimeUtil.DISPLAY_TIME_FORMAT.format(new Date(st.getTime()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    public int getNumberOfJobs(String key, String value) {
        SC sc = null;
        try {
            String sql = "SELECT COUNT(*) AS d FROM task WHERE k = '" + key + "' AND v LIKE '" + value + "%'";
            sc = new SC(new ConnectionEx(Const.ARE));
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            return sc.getInt("d");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    public String getDuration(String key, String value) {
        String c = RawDataConverter.class.getSimpleName();
        String NA = "";
        SC sc = null;
        try {
            String sql = "SELECT COUNT(*) AS d, v FROM task WHERE c = '" + c + "' AND k = '" + key + "' AND v LIKE '"
                    + value + "%' LIMIT 1";
            sc = new SC(new ConnectionEx(Const.ARE));
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            int d = sc.getInt("d");
            if (d == 0)
                return NA;
            String v = sc.getString("v");
            if (v.indexOf('|') < 0)
                return NA;
            int t = Integer.parseInt(v.substring(v.indexOf('|') + 1));
            return TimeUtil.formatSecond2(t);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return NA;
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }
}
