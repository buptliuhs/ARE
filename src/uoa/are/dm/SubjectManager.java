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

import java.io.File;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import uoa.are.common.Code;
import uoa.are.common.Const;
import uoa.are.common.Env;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;
import uoa.are.util.FileUtil;
import uoa.are.util.NumberUtil;
import uoa.are.util.TimeUtil;

/**
 * Utility class to manage projects in database
 * 
 * @author hliu482
 * 
 */
public class SubjectManager {
    static Logger logger = Logger.getLogger(SubjectManager.class);

    static public List<Subject> getAllSubjects(int userId, String prjId) {
        List<Subject> list = new LinkedList<Subject>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT s.id as id"
                    + ", s.name as name"
                    + ", s.dob as dob"
                    + ", s.height as height"
                    + ", s.weight as weight"
                    + ", s.gender as gender"
                    + ", s.device_id as device_id"
                    + ", dt.name as device_type"
                    + ", d.name as device_name"
                    + ", p.name as project_name FROM subject s, project p, device d, sys_device_type dt WHERE s.project_id = "
                    + prjId + " AND s.project_id = p.id" + " AND s.device_id = d.id" + " AND dt.id = d.type"
                    + " AND p.user_id = " + userId + " AND s.enabled = 1" + " ORDER BY s.id";
            sc.execute(sql);
            logger.debug(sql);

            while (sc.next()) {
                int id = sc.getInt("id");
                String project_name = sc.getString("project_name");
                Date dob = sc.getDate("dob");
                String name = sc.getString("name");
                int height = NumberUtil.getInt(sc.getDouble("height"));
                double weight = NumberUtil.getDouble(sc.getDouble("weight"));
                int gender = sc.getInt("gender");
                int device_id = sc.getInt("device_id");
                String device_name = sc.getString("device_name");
                String device_type = sc.getString("device_type");
                Subject subject = new Subject();
                subject.setId(id);
                subject.setName(name);
                subject.setDob(TimeUtil.DISPLAY_DATE_FORMAT.format(dob));
                subject.setGender(gender);
                subject.setHeight(height);
                subject.setWeight(weight);
                subject.setDevice_id(device_id);
                subject.setDevice_name(device_type + " - " + device_name);
                subject.setProject_name(project_name);
                list.add(subject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return list;
    }

    static private void createDataFolder(String p_id, String s_id) {
        String dir = Env.RAW_PATH + p_id + File.separator + s_id;
        FileUtil.mkdirs(dir);

        // dir = Env.REPORT_PATH + p_id + File.separator + s_id;
        // FileUtil.mkdirs(dir);
    }

    static public int addSubject(String prjId, String name, String dob, String height, String weight, String gender,
            String deviceId) {
        // TODO: Do not validate if project belongs to current logged in user
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "INSERT INTO subject (project_id, name, dob, height, weight, gender, device_id) VALUES ("
                    + prjId + ", '" + name + "', '"
                    + TimeUtil.DATABASE_DATE_FORMAT.format(TimeUtil.DISPLAY_DATE_FORMAT.parse(dob)) + "', " + height
                    + ", " + weight + ", " + gender + ", " + deviceId + ")";
            sc.execute(sql);
            logger.debug(sql);

            sc.next();
            long new_sub_id = sc.getLong(1);
            logger.info("New subject ID: " + new_sub_id);

            // Create reports folder for the subject
            createDataFolder(prjId, "" + new_sub_id);
            return Code.SUCCESSFUL;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return Code.FAILED;
    }

    static public int editSubject(String id, String name, String dob, String height, String weight, String gender,
            String deviceId) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "UPDATE subject SET name = '" + name + "', dob = '"
                    + TimeUtil.DATABASE_DATE_FORMAT.format(TimeUtil.DISPLAY_DATE_FORMAT.parse(dob)) + "', gender = "
                    + gender + ", weight = " + weight + ", height = " + height;
            // NOTE: do not support changing device
            // sql += ", device_id = '" + deviceId + "'";
            sql += " WHERE id = " + id;
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

    static public int deleteSubject(String id) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "UPDATE subject SET enabled = 0 WHERE id = " + id;
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