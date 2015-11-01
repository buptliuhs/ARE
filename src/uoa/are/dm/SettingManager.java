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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import uoa.are.common.Code;
import uoa.are.common.Const;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;

/**
 * Utility class to manage settings in database
 * 
 * @author hliu482
 * 
 */
public class SettingManager {
    static Logger logger = Logger.getLogger(SettingManager.class);

    static final int DEFAULT_USER_ID = 1;

    static public void newSettings(int user_id) {
        logger.info("newSettings for: " + user_id);
        List<Setting> list = getSettings(DEFAULT_USER_ID);

        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            for (int i = 0; i < list.size(); ++i) {
                Setting s = list.get(i);
                String sql = "INSERT INTO sys_setting (user_id, name, description, value) VALUES (" + user_id + ", '"
                        + s.getName() + "', '" + s.getDescription() + "', '" + s.getValue() + "')";
                sc.execute(sql);
                logger.debug(sql);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    static public List<Setting> getSettings(int user_id) {
        logger.info("getSettings (" + user_id + ")");
        List<Setting> list = new ArrayList<Setting>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT id, name, description, value FROM sys_setting WHERE user_id = " + user_id
                    + " ORDER BY id";
            sc.execute(sql);
            logger.debug(sql);

            while (sc.next()) {
                Setting s = new Setting();
                s.setId(sc.getInt("id"));
                s.setName(sc.getString("name"));
                s.setDescription(sc.getString("description"));
                s.setValue(sc.getString("value"));
                list.add(s);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return list;
    }

    static public int editSetting(int user_id, Setting s) {
        logger.info("editSettings (" + user_id + "): " + s);
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "UPDATE sys_setting SET value = '" + s.getValue() + "' WHERE id = " + s.getId()
                    + " AND user_id = " + user_id;
            sc.execute(sql);
            logger.debug(sql);
            return Code.SUCCESSFUL;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Code.FAILED;
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

    static public String getValue(int user_id, String name) {
        logger.info("getValue (" + user_id + ", " + name + ")");
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT COUNT(*) as c, value FROM sys_setting WHERE UPPER(name) = '" + name
                    + "' AND user_id = " + user_id;
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            if (sc.getInt("c") == 0)
                return null;
            String value = sc.getString("value");
            logger.info(value);
            return value;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }
}