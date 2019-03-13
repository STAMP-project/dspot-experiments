/**
 * Copyright (c) 2006-2019, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.runtime.core.internal.component;


import com.speedment.runtime.core.component.connectionpool.ConnectionDecorator;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author pemi
 */
final class ConnectionPoolComponentImplTest {
    private static final ConnectionDecorator CONNECTION_DECORATOR = ( connection) -> {
    };

    private ConnectionPoolComponentImpl instance;

    @Test
    public void testGetPoolSize() {
        final int result = instance.getMaxRetainSize();
        Assertions.assertTrue((result >= 0));
        instance.setMaxRetainSize(10);
        Assertions.assertEquals(10, instance.getMaxRetainSize());
    }

    private static final class DummyConnectionImpl implements Connection {
        final String uri;

        final String user;

        final char[] password;

        private boolean closed;

        public DummyConnectionImpl(String uri, String user, char[] password) {
            this.uri = uri;
            this.user = user;
            this.password = password;
        }

        @Override
        public Statement createStatement() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return false;
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void commit() throws SQLException {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void rollback() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void close() throws SQLException {
            if (isClosed()) {
                ConnectionPoolComponentImplTest.log("Closed twice");
            }
            closed = true;
        }

        @Override
        public boolean isClosed() throws SQLException {
            return closed;
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public boolean isReadOnly() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public String getCatalog() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void clearWarnings() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public int getHoldability() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Clob createClob() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Blob createBlob() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public NClob createNClob() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Properties getClientInfo() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setSchema(String schema) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public String getSchema() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void abort(Executor executor) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");// To change body of generated methods, choose Tools | Templates.

        }
    }
}

