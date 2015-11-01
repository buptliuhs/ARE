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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

public class SC {
    private Logger logger = Logger.getLogger(SC.class);

    private final String EMPTY_STRING = "";

    private int _errorCode = 0;
    private String _errorMsg = null;
    private int _rows = -1;
    private Connection _connection = null;
    private ConnectionEx _connectionEx = null;
    private ResultSet _resultSet = null;
    private boolean _autoCommit = false;
    private boolean _autoRollback = false;
    private String _sqlType = "UNKNOWN";
    private String _sql = null;
    private boolean _isLastNull = false;
    private Statement _statement = null;
    private Statement _batchStatement = null;

    public SC() {
        _errorCode = 0;
        _resultSet = null;
        _errorMsg = null;
        _autoRollback = false;
        _autoCommit = true;
        _rows = 0;
    }

    public SC(ConnectionEx conn) throws Exception {
        this();
        _connection = conn.getConnection();
        _connectionEx = conn;

        if (_connection != null)
            setAutoCommit(_autoCommit);
        if (_connection != null)
            _connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }

    public void setAutoCommit(boolean bAuto) throws Exception {
        _autoCommit = bAuto;
        if (_connection == null) {
            Failure("NO connection!", 0);
        }
        try {
            _connection.setAutoCommit(_autoCommit);
        } catch (SQLException e) {
            sqlFailure("Failed to set auto commit!", e, 0);
        }
    }

    private void setSql(String sqlStatement) throws Exception {
        if (_connection == null) {
            Failure("NO connection!", 0);
        }
        _sql = sqlStatement;
        _errorCode = 0;
        _rows = 0;
        _errorMsg = null;

        sqlStatement = sqlStatement.trim();
        _sqlType = sqlStatement.substring(0, 4).toUpperCase();
        if (_sqlType.equals("SELE")) {
            _sqlType = "SELECT";
        } else if (_sqlType.equals("WITH")) {
            _sqlType = "SELECT";
        } else if (_sqlType.equals("DELE")) {
            _sqlType = "DELETE";
        } else if (_sqlType.equals("UPDA")) {
            _sqlType = "UPDATE";
        } else if (_sqlType.equals("INSE")) {
            _sqlType = "INSERT";
        } else {
            _sqlType = "UNKNOWN";
        }

        close();

        _statement = _connection.createStatement();
    }

    private int execute() throws Exception {
        _rows = 0;
        if (_statement == null) {
            _rows = -1;
            Failure("NO executable SQL!", 0);
        }
        try {
            if (_sqlType.equals("SELECT")) {
                _resultSet = _statement.executeQuery(_sql);
            } else if (_sqlType.equals("INSERT")) {
                // set to return ID
                _statement.executeUpdate(_sql, Statement.RETURN_GENERATED_KEYS);
                _resultSet = _statement.getGeneratedKeys();
            } else {
                _rows = _statement.executeUpdate(_sql);
            }
        } catch (SQLException e) {
            _rows = -1;
            sqlFailure("Execute SQL:\n  " + _sql + " failed!", e, 0);
            return -1;
        }
        return _rows;
    }

    public int execute(String strSQL) throws Exception {
        setSql(strSQL.replaceAll("\\n", " "));
        return execute();
    }

    public Date getDate(String columnName) throws Exception {
        Date retValue = null;
        if (_resultSet == null) {
            throw new Exception("Get " + columnName + " failed. resultset is NULL.");
        }
        try {
            retValue = _resultSet.getDate(columnName);
            _isLastNull = _resultSet.wasNull();
        } catch (SQLException e) {
            sqlFailure("Get " + columnName + " failed!", e, 0);
        }
        return retValue;
    }

    public Timestamp getTimestamp(String columnName) throws Exception {
        Timestamp retValue = null;
        if (_resultSet == null) {
            throw new Exception("Get " + columnName + " failed. resultset is NULL.");
        }
        try {
            retValue = _resultSet.getTimestamp(columnName);
            _isLastNull = _resultSet.wasNull();
        } catch (SQLException e) {
            sqlFailure("Get " + columnName + " failed!", e, 0);
        }
        return retValue;
    }

    public String getString(String columnName) throws Exception {
        String retValue = null;
        if (_resultSet == null) {
            throw new Exception("Get " + columnName + " failed. resultset is NULL.");
        }
        try {
            retValue = _resultSet.getString(columnName);
            _isLastNull = _resultSet.wasNull();
            if (_isLastNull || null == retValue)
                retValue = EMPTY_STRING;
            else
                retValue = retValue.trim();
        } catch (SQLException e) {
            sqlFailure("Get " + columnName + " failed!", e, 0);
        }
        return retValue;
    }

    public int getInt(int columnIndex) throws Exception {
        int retValue = 0;
        if (_resultSet == null) {
            throw new Exception("Get " + columnIndex + " failed. Resultset is NULL.");
        }
        try {
            retValue = _resultSet.getInt(columnIndex);
            _isLastNull = _resultSet.wasNull();
        } catch (SQLException e) {
            sqlFailure("Get " + columnIndex + " failed!", e, 0);
            return 0;
        }
        return retValue;
    }

    public int getInt(String columnName) throws Exception {
        int retValue;
        if (_resultSet == null) {
            throw new Exception("Get " + columnName + " failed. resultset is NULL.");
        }
        try {
            retValue = _resultSet.getInt(columnName);
            _isLastNull = _resultSet.wasNull();
        } catch (SQLException e) {
            sqlFailure("Get " + columnName + " failed!", e, 0);
            return 0;
        }
        return retValue;
    }

    public long getLong(int columnIndex) throws Exception {
        long retValue = 0;
        if (_resultSet == null) {
            throw new Exception("Get " + columnIndex + " failed. Resultset is NULL.");
        }
        try {
            retValue = _resultSet.getLong(columnIndex);
            _isLastNull = _resultSet.wasNull();
        } catch (SQLException e) {
            sqlFailure("Get " + columnIndex + " failed!", e, 0);
            return 0;
        }
        return retValue;
    }

    public long getLong(String columnName) throws Exception {
        long retValue;
        if (_resultSet == null) {
            throw new Exception("Get " + columnName + " failed. Resultset is NULL.");
        }
        try {
            retValue = _resultSet.getLong(columnName);
            _isLastNull = _resultSet.wasNull();
        } catch (SQLException e) {
            sqlFailure("Get " + columnName + " failed!", e, 0);
            return 0;
        }
        return retValue;
    }

    public double getDouble(String columnName) throws Exception {
        double retValue;
        if (_resultSet == null) {
            throw new Exception("Get " + columnName + " failed. Resultset is NULL.");
        }

        try {
            retValue = _resultSet.getDouble(columnName);
            _isLastNull = _resultSet.wasNull();
        } catch (SQLException e) {
            sqlFailure("Get " + columnName + " failed!", e, 0);
            return 0;
        }
        return retValue;
    }

    public boolean next() throws Exception {
        return _resultSet.next();
    }

    public void commit() throws Exception {
        try {
            _connection.commit();
        } catch (Exception e) {
            logger.error("commit failed!", e);
        }
    }

    private void sqlFailure(String notice, SQLException e, int flag) throws Exception {
        _errorCode = -e.getErrorCode();
        _errorMsg = "SQL:" + _sql + "\n" + notice + "\n" + "  ErrorCode:" + (new Integer(_errorCode)).toString()
                + ";\n" + "  ErrorMsg:" + e.getMessage() + "\n";
        throw new Exception("SQL Error:" + _errorMsg + notice + e.getMessage());
    }

    public void closeAll() {
        try {
            close();
            if (this._batchStatement != null) {
                this._batchStatement.close();
                this._batchStatement = null;
            }
            if (null != _connectionEx) {
                _connectionEx.close();
            } else if (null != _connection) {
                _connection.close();
                _connection = null;
            }
            _connectionEx = null;
        } catch (Exception e) {
            logger.error("closeAll failed!", e);
        }
    }

    private void close() {
        _errorMsg = null;
        try {
            if (_resultSet != null) {
                _resultSet.close();
                _resultSet = null;
            }
            if (_statement != null) {
                _statement.close();
                _statement = null;
            }
        } catch (Exception e) {
            logger.error("close failed!", e);
        }
    }

    private void Failure(String notice, int flag) throws Exception {
        _errorCode = 50;
        _errorMsg = notice;
        if (flag == 1 && _autoRollback && (!_sqlType.equals("SELECT"))) {
            try {
                _connection.rollback();
            } catch (SQLException e1) {
            }
        }
        throw new Exception(_errorMsg);
    }
}
