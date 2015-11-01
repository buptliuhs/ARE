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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Hashtable;

/**
 * Database connection factory class.
 * 
 * @author hliu482
 *
 */
public class ConnectionFactory {
    static protected Logger logger = Logger.getLogger(ConnectionFactory.class);

    static private Hashtable<String, ConnectionFactory> dss = new Hashtable<String, ConnectionFactory>();

    static final String DATABASE_JNDINAME = "jdbc/are";
    private DataSource ds;

    public static ConnectionFactory getInstance() {
        return getInstance(DATABASE_JNDINAME);
    }

    public static ConnectionFactory getInstance(String jndi) {
        if (!dss.containsKey(jndi))
            addDataSource(jndi);
        return (ConnectionFactory) dss.get(jndi);
    }

    ConnectionFactory(DataSource ds) {
        this.ds = ds;
    }

    private static void addDataSource(String jndi) {
        try {
            Context ctx = new InitialContext();
            Context envContext = (Context) ctx.lookup("java:comp/env");

            if (envContext == null)
                throw new Exception("FATAL ERROR - No Context");

            if (jndi != null) {
                DataSource ds = (DataSource) envContext.lookup(jndi);
                logger.debug("DataSource of " + jndi + ": " + ds);
                if (ds != null)
                    dss.put(jndi, new ConnectionFactory(ds));
                else
                    throw new Exception("Failed to find datasource!");
            }
        } catch (Exception e) {
            logger.error("addDataSource failed: ", e);
        }
    }

    public Connection getConnection() {
        try {
            return (ds != null) ? ds.getConnection() : null;
        } catch (SQLException ex) {
            logger.error("getConnection failed: ", ex);
            return null;
        }
    }

    public void destory() {
        dss.clear();
    }

}
