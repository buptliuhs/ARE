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

import org.apache.log4j.Logger;

import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;

public class DBUtil {
    static Logger logger = Logger.getLogger(DBUtil.class);

    private static void write(String jndiName, String sql) throws Exception {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(jndiName));
            logger.debug(sql);
            logger.debug("Changed rows: " + sc.execute(sql));
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private static void writeAll(String jndiName, String[] sqls) throws Exception {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(jndiName));
            for (String sql : sqls) {
                logger.debug(sql);
                sc.execute(sql);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                sc.closeAll();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public static void run(String jndiName, String[] sqls) throws Exception {
        writeAll(jndiName, sqls);
    }

    public static void insert(String jndiName, String sql) throws Exception {
        write(jndiName, sql);
    }

    public static void update(String jndiName, String sql) throws Exception {
        write(jndiName, sql);
    }

    public static void delete(String jndiName, String sql) throws Exception {
        write(jndiName, sql);
    }
}