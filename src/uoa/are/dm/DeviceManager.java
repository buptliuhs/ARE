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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import uoa.are.common.Code;
import uoa.are.common.Const;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;

/**
 * Utility class to query device type from database
 * 
 * @author hliu482
 * 
 */
public class DeviceManager {
    static Logger logger = Logger.getLogger(DeviceManager.class);

    static public List<Device> getIdelDevices(int userId) {
        List<Device> idle_list = new LinkedList<Device>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT d.id as device_id, d.name as device_name, dt.name as device_type"
                    + " FROM device d, sys_device_type dt" + " WHERE d.type = dt.id" + " AND d.user_id = " + userId
                    + " AND d.id NOT IN (SELECT device_id FROM subject)" + " ORDER BY d.type, d.id";
            sc.execute(sql);
            logger.debug(sql);
            while (sc.next()) {
                int device_id = sc.getInt("device_id");
                String device_name = sc.getString("device_name");
                String device_type = sc.getString("device_type");
                Device device = new Device();
                device.setId(device_id);
                device.setName(device_name);
                device.setType(device_type);
                idle_list.add(device);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return idle_list;
    }

    static public List<Device> getAllDevices(int userId, String deviceType) {
        List<Device> all_list = new LinkedList<Device>();
        List<Device> idle_list = new LinkedList<Device>();
        List<Device> list = new LinkedList<Device>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            // Query project and subject who assigned with each device (LEFT
            // JOIN: project_name/subject_name == null if devices are not
            // assigned to anyone)
            String sql = "SELECT d.id as device_id, d.name as device_name, dt.name as device_type, prj.name as project_name, sub.name as subject_name FROM device d"
                    + " LEFT JOIN sys_device_type dt ON d.type = dt.id"
                    + " LEFT JOIN subject sub ON sub.device_id = d.id"
                    + " LEFT JOIN project prj ON sub.project_id = prj.id"
                    + " WHERE d.type = "
                    + deviceType
                    + " AND d.user_id = " + userId + " ORDER BY d.id";
            sc.execute(sql);
            logger.debug(sql);
            while (sc.next()) {
                int device_id = sc.getInt("device_id");
                String device_name = sc.getString("device_name");
                String device_type = sc.getString("device_type");
                String allocatedTo = "";
                if (!StringUtils.isEmpty(sc.getString("project_name")))
                    allocatedTo = sc.getString("project_name") + " / " + sc.getString("subject_name");
                Device device = new Device();
                device.setId(device_id);
                device.setName(device_name);
                device.setType(device_type);
                device.setAllocatedTo(allocatedTo);
                if (StringUtils.isEmpty(allocatedTo))
                    idle_list.add(device);
                else
                    list.add(device);
            }
            all_list.addAll(idle_list);
            all_list.addAll(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return all_list;
    }

    public static boolean hasDevice(int userId, String type, String name) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) AS c FROM device WHERE user_id = " + userId + " AND type = " + type
                    + " AND name = '" + name + "'";
            sc.execute(sql);
            logger.debug(sql);
            sc.next();
            return sc.getInt("c") > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    public static int addDevice(int userId, String type, String name) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "INSERT INTO device (user_id, name, type) VALUES (" + userId + ", '" + name + "', " + type
                    + ")";
            sc.execute(sql);
            logger.debug(sql);
            return Code.SUCCESSFUL;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return Code.FAILED;
    }
}