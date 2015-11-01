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

import uoa.are.common.Code;
import uoa.are.common.Const;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;

/**
 * Utility class to manage users in database
 * 
 * @author hliu482
 * 
 */
public class UserManager {
    static Logger logger = Logger.getLogger(UserManager.class);

    static public List<User> getAllUsers() {
        List<User> list = new LinkedList<User>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT u.id as id, u.username as userName, u.password as password, u.enabled as enabled, u.role as role, r.role as roleName FROM sys_user u, sys_role r WHERE u.role = r.id"
                    + " ORDER BY u.id";
            sc.execute(sql);
            logger.debug(sql);

            while (sc.next()) {
                User user = new User();
                user.setId(sc.getInt("id"));
                user.setName(sc.getString("userName"));
                user.setRole(sc.getInt("role"));
                user.setRoleName(sc.getString("roleName"));
                user.setPassword(sc.getString("password"));
                user.setEnabled(sc.getInt("enabled"));
                list.add(user);
            }
            return list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return list;
    }

    static public int getNumberOfUsers() {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "select count(*) as d from sys_user";
            sc.execute(sql);
            sc.next();
            return sc.getInt("d");
        } catch (Exception e) {
            logger.error("doTask failed", e);
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error("doTask failed", e);
            }
        }
        return 0;
    }

    static public User getUserByName(String userName) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT count(*) AS count" + ", id" + ", role" + ", password" + ", enabled"
                    + " FROM sys_user WHERE username = '" + userName + "'";
            logger.debug(sql);
            sc.execute(sql);
            sc.next();
            int count = sc.getInt("count");
            if (count == 0)
                return null;
            User user = new User();
            user.setId(sc.getInt("id"));
            user.setName(userName);
            user.setRole(sc.getInt("role"));
            user.setPassword(sc.getString("password"));
            user.setEnabled(sc.getInt("enabled"));
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return null;
    }

    static public User addUser(String userName, String password) {
        User user = null;
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "INSERT INTO sys_user (username, password) VALUES ('" + userName + "', '" + password + "')";
            sc.execute(sql);
            logger.debug(sql);
            sc.next();
            int new_user_id = sc.getInt(1);
            logger.info("New User ID: " + new_user_id);
            user = new User();
            user.setId(new_user_id);
            user.setName(userName);

            // New Settings for the user
            SettingManager.newSettings(new_user_id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return user;
    }

    static public int editUser(String userName, String password) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "UPDATE sys_user SET password = '" + password + "'" + " WHERE username = '" + userName + "'";
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

    static private int changeUserStatus(String userName, int s) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "UPDATE sys_user SET enabled = " + s + "" + " WHERE username = '" + userName + "'";
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

    static public int enableUser(String userName) {
        return changeUserStatus(userName, 1);
    }

    static public int disableUser(String userName) {
        return changeUserStatus(userName, 0);
    }
}