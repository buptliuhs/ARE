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

package uoa.are.database;

import java.sql.Connection;
import org.apache.log4j.Logger;

/**
 * Database connection utility class.
 * 
 * @author hliu482
 *
 */
public class ConnectionEx {
    protected Logger logger = Logger.getLogger(ConnectionEx.class);
    protected Connection m_Connection = null;

    public Connection getConnection() {
        return m_Connection;
    }

    public ConnectionEx(String jndiName) throws Exception {

        Connection conn = null;
        try {
            conn = ConnectionFactory.getInstance(jndiName).getConnection();
            if (conn == null) {
                logger.error("getConnection returned NULL!");
            }
        } catch (Exception e) {
            logger.error("ConnectionEx() failed!", e);
        }
        synchronized (this) {
            m_Connection = conn;
        }
    }

    public void commit() throws Exception {
        if (null != m_Connection) {
            m_Connection.commit();
        }
    }

    public void rollback() throws Exception {
        if (null != m_Connection) {
            m_Connection.rollback();
        }
    }

    public void close() {
        try {
            if (null != m_Connection) {
                m_Connection.close();
                m_Connection = null;
            }
        } catch (Exception e) {
            logger.error("close() failed!", e);
        }
    }

    public boolean isClosed() throws Exception {
        if (null == m_Connection) {
            return true;
        } else {
            return m_Connection.isClosed();
        }
    }
}
