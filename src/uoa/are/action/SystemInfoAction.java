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

package uoa.are.action;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uoa.are.common.Configure;
import uoa.are.common.Const;
import uoa.are.common.Env;
import uoa.are.common.ResponseUtil;
import uoa.are.common.UserRole;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;
import uoa.are.dm.DeviceType;
import uoa.are.dm.DeviceTypeManager;
import uoa.are.dm.ReportManager;
import uoa.are.util.FileUtil;
import uoa.are.util.NumberUtil;
import uoa.are.util.TimeUtil;

/**
 * This is action for system information.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class SystemInfoAction extends AuthorizedAction {

    private int numberOfFile(String dir, String pattern, boolean recursively) {
        // logger.debug("numberOfFile: " + dir);
        File file = new File(dir);
        if (!file.exists()) {
            logger.error("FATAL ERROR, folder not existed: " + file.getAbsolutePath());
            return 0;
        }

        int count = 0;
        File[] sub_dirs_and_files = file.listFiles();
        for (File df : sub_dirs_and_files) {
            if (df.isFile()) {
                if (df.getName().matches(pattern))
                    count++;
            } else if (recursively) {
                count += numberOfFile(df.getAbsolutePath(), pattern, recursively);
            }
        }
        if (count > 0)
            logger.debug(dir + ", pattern: " + pattern + ", count: " + count);
        return count;
    }

    private Map<Object, Object> getNumberOfFile() {
        // Do not calculate McRoberts raw files
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        try {
            List<DeviceType> list = DeviceTypeManager.getAllDeviceType();
            int count = 0;
            for (DeviceType dt : list) {
                count += numberOfFile(Env.RAW_PATH, dt.getPattern(), true);
            }
            map.put("name", "Number of uploads");
            map.put("description", count);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }

    private Map<Object, Object> getSignalVolume(int user_id) {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        int hours = 0;
        try {
            hours = TimeUtil.getHour(ReportManager.getDataVolume(user_id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        map.put("name", "Volume of signal");
        map.put("description", hours + " Hours (" + NumberUtil.getDouble(((double) hours / 24 / 7)) + " person * week)");
        return map;
    }

    private Map<Object, Object> getSizeOfFiles() {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        File d = new File(Env.BASE_PATH);
        long total_space = d.getTotalSpace();
        long data_space = FileUtil.folderSize(new File(Env.BASE_PATH));

        map.put("name", "Size of files");
        map.put("description",
                NumberUtil.getDouble((double) data_space / 1024 / 1024 / 1024) + " GB ("
                        + NumberUtil.getDouble(((double) data_space * 100) / total_space) + " %)");
        return map;
    }

    private Map<Object, Object> getDiskSpace() {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        File d = new File(Env.BASE_PATH);
        long total_space = d.getTotalSpace();
        long free_space = d.getFreeSpace();

        map.put("name", "Free disk space");
        map.put("description",
                NumberUtil.getDouble((double) free_space / 1024 / 1024 / 1024) + " GB ("
                        + NumberUtil.getDouble(((double) free_space * 100) / total_space) + " %)");
        return map;
    }

    private Map<Object, Object> getTotalSpace() {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        File d = new File(Env.BASE_PATH);
        long total_space = d.getTotalSpace();

        map.put("name", "Total disk space");
        map.put("description", NumberUtil.getDouble((double) total_space / 1024 / 1024 / 1024) + " GB");
        return map;
    }

    private Map<Object, Object> getProjectInfo(int user_id) {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put("name", "Number of project");

        long count = 0;
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS c FROM project WHERE enabled = 1";
            if (user_id != 0)
                sql += " AND user_id = " + user_id;
            sc.execute(sql);
            logger.debug(sql);
            sc.next();
            count = sc.getLong("c");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        map.put("description", count);
        return map;
    }

    private Map<Object, Object> getSubjectInfo(int user_id) {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put("name", "Number of subject");

        long count = 0;
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS c FROM subject WHERE enabled = 1";
            if (user_id != 0)
                sql += " AND project_id IN (SELECT id FROM project WHERE user_id = " + user_id + ")";
            sc.execute(sql);
            logger.debug(sql);
            sc.next();
            count = sc.getLong("c");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        map.put("description", count);
        return map;
    }

    private Map<Object, Object> getDeviceInfo(int user_id) {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put("name", "Number of device");

        long count = 0;
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS c FROM device";
            if (user_id != 0)
                sql += " WHERE user_id = " + user_id;
            sc.execute(sql);
            logger.debug(sql);
            sc.next();
            count = sc.getLong("c");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        map.put("description", count);
        return map;
    }

    private Map<Object, Object> getIdleDeviceInfo(int user_id) {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put("name", "Number of idle device");

        long count = 0;
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS c FROM device";
            if (user_id != 0)
                sql += " WHERE user_id = " + user_id + " AND id NOT IN (SELECT device_id FROM subject WHERE user_id = "
                        + user_id + ")";
            else
                sql += " WHERE id NOT IN (SELECT device_id FROM subject)";
            sc.execute(sql);
            logger.debug(sql);
            sc.next();
            count = sc.getLong("c");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        map.put("description", count);
        return map;
    }

    private Map<Object, Object> getVersion() {
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put("name", "Version");
        map.put("description", Configure.getInstance().getProperty("app.version"));
        return map;
    }

    /**
     * Initialize system information list.
     * 
     * @throws IOException
     */
    public void initSysInfoList() throws IOException {
        if (!isAuthorized())
            return;
        int user_id = getSessionUserID();
        int role_id = getSessionRoleID();
        logger.info("initSysInfoList (" + user_id + ")");

        try {
            int index = 1;
            Map<Object, Object> data = new LinkedHashMap<Object, Object>();

            if (role_id == UserRole.ADMIN) {
                // Projects & subjects & devices
                data.put((index++), getProjectInfo(0));
                data.put((index++), getSubjectInfo(0));
                data.put((index++), getDeviceInfo(0));
                data.put((index++), getIdleDeviceInfo(0));

                // Data volume
                data.put((index++), getSignalVolume(0));

                // Number of uploads
                data.put((index++), getNumberOfFile());

                // Disk space info
                data.put((index++), getSizeOfFiles());
                data.put((index++), getDiskSpace());
                data.put((index++), getTotalSpace());
                data.put((index++), getVersion());
            } else {
                // Projects & subjects & devices
                data.put((index++), getProjectInfo(user_id));
                data.put((index++), getSubjectInfo(user_id));
                data.put((index++), getDeviceInfo(user_id));
                data.put((index++), getIdleDeviceInfo(user_id));
                // Data volume
                data.put((index++), getSignalVolume(user_id));
            }

            ResponseUtil.setResponseMap(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
