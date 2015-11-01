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

package uoa.are.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.*;

import org.apache.log4j.Logger;

import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;
import uoa.are.util.FileUtil;

/**
 * Configuration Loader (from file and database).
 * 
 * @author hliu482
 * 
 */
public class Configure {
    private Properties m_Prop = null;
    private String m_strFileName;
    private File m_file = null;

    private static Configure m_Instance = new Configure();

    protected Logger logger = Logger.getLogger(this.getClass());

    public static Configure getInstance() {
        return m_Instance;
    }

    synchronized private boolean loadConfigFile() throws Exception {
        logger.info("Configure::init " + m_strFileName);
        if (m_Prop == null) {
            m_Prop = new Properties();
            if (null == m_file) {
                m_file = new File(m_strFileName);
            }

            String m_strAbsPath = m_file.getAbsolutePath();
            if (!m_file.exists()) {
                throw new Exception("File not found:" + m_strFileName + ":"
                        + m_strAbsPath);
            }
            BufferedReader br = new BufferedReader(
                    new FileReader(m_strFileName));
            m_Prop.load(br);
            br.close();
        }

        return true;
    }

    public void setConfFileName(String str) {
        logger.info("setConfFileName: " + str);
        this.m_strFileName = str;
        try {
            loadConfigFile();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void init() {
        FileUtil.mkdirs(Env.SCRIPTS_PATH);
        FileUtil.mkdirs(Env.LOG_PATH);
        FileUtil.mkdirs(Env.TMP_PATH);

        try {
            String file = "ar.sci";
            logger.info("Copying " + file + " to " + Env.SCRIPTS_PATH);
            Files.copy(
                    this.getClass().getClassLoader().getResourceAsStream(file),
                    FileSystems.getDefault().getPath(Env.SCRIPTS_PATH, file),
                    REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Configure() {
        init();
    }

    @SuppressWarnings("unused")
    private String getPropertyFromDB(String strKey) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT value FROM sys_setting WHERE lower(name) = '"
                    + strKey.toLowerCase() + "'";
            // logger.debug(sql);
            sc.execute(sql);
            sc.next();
            return sc.getString("value");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public String getProperty(String strKey) {
        try {
            if (m_Prop == null) {
                loadConfigFile();
            }
            String str = m_Prop.getProperty(strKey);
            if (str != null) {
                str = str.trim();
            } else {
                // str = getPropertyFromDB(strKey);
            }
            logger.debug("getProperty: " + strKey + " -> " + str);
            return str;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
