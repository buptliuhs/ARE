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

package uoa.are.task;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import uoa.are.common.Const;
import uoa.are.common.Env;
import uoa.are.dm.ProjectManager;
import uoa.are.dm.UserManager;
import uoa.are.util.DBUtil;
import uoa.are.util.FileUtil;

/**
 * This is a clean task that can be used as a template.
 * 
 * @author hliu482
 * 
 */
public class CleanTask extends AbstractTask {

    private void cleanProject() throws Exception {
        DBUtil.delete(Const.ARE, "delete from project where user_id not in (select id from sys_user)");
    }

    private void cleanSubject() throws Exception {
        DBUtil.delete(Const.ARE, "delete from subject where project_id not in (select id from project)");
    }

    private void cleanData() throws Exception {
        DBUtil.delete(Const.ARE, "delete from data where subject_id not in (select id from subject)");
    }

    private void cleanDevice() throws Exception {
        DBUtil.delete(Const.ARE, "delete from device where user_id not in (select id from sys_user)");
    }

    private void cleanTask() throws Exception {
        DBUtil.delete(Const.ARE, "delete from task where k not in (select id from sys_user)");
    }

    private void cleanSetting() throws Exception {
        DBUtil.delete(Const.ARE, "delete from sys_setting where user_id not in (select id from sys_user)");
    }

    private void cleanFile() {
        List<Integer> list = ProjectManager.getAllProjectIDs();

        File rawDir = new File(Env.RAW_PATH);
        String[] directories = rawDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        for (String d : directories) {
            try {
                int i = Integer.parseInt(d);
                if (!list.contains(i)) {
                    File tbd = new File(Env.RAW_PATH + i);
                    logger.info("Delete folder: " + tbd.getAbsolutePath());
                    FileUtil.deleteDir(tbd.getAbsolutePath());
                }
            } catch (NumberFormatException e) {
                // do nothing and skip
                logger.warn("Unexpected folder: " + (Env.RAW_PATH + d));
            }
        }
    }

    /**
     * Actual job of the task.
     * 
     * @see uoa.are.task.AbstractTask#doTask()
     */
    @Override
    public void doTask() {
        int num = UserManager.getNumberOfUsers();
        logger.info("# of user: " + num);

        try {
            cleanProject();
            cleanSubject();
            cleanData();
            cleanDevice();
            cleanTask();
            cleanSetting();
            cleanFile();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
