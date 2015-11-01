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

import org.apache.log4j.Logger;

import uoa.are.common.Const;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;

/**
 * Utility class to query device type from database
 * 
 * @author hliu482
 * 
 */
public class DeviceTypeManager {
    static Logger logger = Logger.getLogger(DeviceTypeManager.class);

    /**
     * Get all device type
     * 
     * @return list of device type
     * @throws Exception
     */
    public static List<DeviceType> getAllDeviceType() throws Exception {
        List<DeviceType> list = new LinkedList<DeviceType>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT id, name, file_name_pattern, file_name_sample, device_name_index, time_index, converter_class, description FROM sys_device_type WHERE enabled = 1 ORDER BY id";
            sc.execute(sql);
            logger.debug(sql);

            while (sc.next()) {
                DeviceType dt = new DeviceType();
                dt.setId(sc.getInt("id"));
                dt.setName(sc.getString("name"));
                dt.setDescription(sc.getString("description"));
                dt.setPattern(sc.getString("file_name_pattern"));
                dt.setSample(sc.getString("file_name_sample"));
                dt.setDeviceNameIndex(sc.getInt("device_name_index"));
                dt.setTimeIndex(sc.getString("time_index"));
                dt.setConverter(sc.getString("converter_class"));
                list.add(dt);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return list;
    }

    public static DeviceType getDeviceTypeBySubjectId(String subject_id) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT count(*) as c, sdt.id as id, sdt.name as name, sdt.file_name_pattern as file_name_pattern, sdt.device_name_index as device_name_index, sdt.time_index as time_index, sdt.converter_class as converter_class, sdt.description as description"
                    + " FROM sys_device_type sdt, device d, subject s"
                    + " WHERE sdt.id = d.type AND d.id = s.device_id AND s.id = "
                    + subject_id;
            sc.execute(sql);
            logger.debug(sql);
            sc.next();
            if (sc.getInt("c") > 0) {
                DeviceType dt = new DeviceType();
                dt.setId(sc.getInt("id"));
                dt.setName(sc.getString("name"));
                dt.setDescription(sc.getString("description"));
                dt.setPattern(sc.getString("file_name_pattern"));
                dt.setDeviceNameIndex(sc.getInt("device_name_index"));
                dt.setTimeIndex(sc.getString("time_index"));
                dt.setConverter(sc.getString("converter_class"));
                return dt;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        logger.error("FATAL ERROR!! getDeviceTypeBySubjectId failed!!");
        return null;
    }

}