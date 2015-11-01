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
 * Utility class to manage projects in database
 * 
 * @author hliu482
 * 
 */
public class ProjectManager {
    static Logger logger = Logger.getLogger(ProjectManager.class);

    static public List<Integer> getAllProjectIDs() {
        List<Integer> list = new LinkedList<Integer>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT id FROM project ORDER BY id";
            sc.execute(sql);
            logger.debug(sql);

            while (sc.next()) {
                list.add(sc.getInt("id"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return list;
    }

    static public List<Project> getAllProjects(int userId) {
        List<Project> list = new LinkedList<Project>();
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT id, name, description FROM project WHERE user_id = " + userId
                    + " AND enabled = 1 ORDER BY id";
            sc.execute(sql);
            logger.debug(sql);

            while (sc.next()) {
                Project p = new Project();
                p.setId(sc.getInt("id"));
                p.setName(sc.getString("name"));
                p.setDescription(sc.getString("description"));
                list.add(p);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
        return list;
    }

    static public int addProject(int userId, String name, String description) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "INSERT INTO project (user_id, name, description) VALUES (" + userId + ", '" + name + "', '"
                    + description + "')";
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

    static public int editProject(int userId, String prjId, String name, String description) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "UPDATE project SET name = '" + name + "', description = '" + description + "' WHERE id = "
                    + prjId + " AND user_id = " + userId;
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

    static public int deleteProject(int userId, String prjId) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "UPDATE project SET enabled = 0 WHERE id = " + prjId + " AND user_id = " + userId;
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