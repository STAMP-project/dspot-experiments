/**
 * Copyright 2009-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.database;


import TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;


public class ExtendedConnectionDataSourceProxyTests {
    @Test
    public void testOperationWithDataSourceUtils() throws SQLException {
        Connection con = Mockito.mock(Connection.class);
        DataSource ds = Mockito.mock(DataSource.class);
        Mockito.when(ds.getConnection()).thenReturn(con);// con1

        con.close();
        Mockito.when(ds.getConnection()).thenReturn(con);// con2

        con.close();
        Mockito.when(ds.getConnection()).thenReturn(con);// con3

        con.close();// con3

        Mockito.when(ds.getConnection()).thenReturn(con);// con4

        con.close();// con4

        final ExtendedConnectionDataSourceProxy csds = new ExtendedConnectionDataSourceProxy(ds);
        Connection con1 = csds.getConnection();
        Connection con2 = csds.getConnection();
        Assert.assertNotSame("shouldn't be the same connection", con1, con2);
        Assert.assertTrue("should be able to close connection", csds.shouldClose(con1));
        con1.close();
        Assert.assertTrue("should be able to close connection", csds.shouldClose(con2));
        con2.close();
        Connection con3 = csds.getConnection();
        csds.startCloseSuppression(con3);
        Connection con3_1 = csds.getConnection();
        Assert.assertSame("should be same connection", con3_1, con3);
        Assert.assertFalse("should not be able to close connection", csds.shouldClose(con3));
        con3_1.close();// no mock call for this - should be suppressed

        Connection con3_2 = csds.getConnection();
        Assert.assertSame("should be same connection", con3_2, con3);
        Connection con4 = csds.getConnection();
        Assert.assertNotSame("shouldn't be same connection", con4, con3);
        csds.stopCloseSuppression(con3);
        Assert.assertTrue("should be able to close connection", csds.shouldClose(con3));
        con3_1 = null;
        con3_2 = null;
        con3.close();
        Assert.assertTrue("should be able to close connection", csds.shouldClose(con4));
        con4.close();
    }

    @Test
    public void testOperationWithDirectCloseCall() throws SQLException {
        Connection con = Mockito.mock(Connection.class);
        DataSource ds = Mockito.mock(DataSource.class);
        Mockito.when(ds.getConnection()).thenReturn(con);// con1

        con.close();
        Mockito.when(ds.getConnection()).thenReturn(con);// con2

        con.close();
        final ExtendedConnectionDataSourceProxy csds = new ExtendedConnectionDataSourceProxy(ds);
        Connection con1 = csds.getConnection();
        csds.startCloseSuppression(con1);
        Connection con1_1 = csds.getConnection();
        Assert.assertSame("should be same connection", con1_1, con1);
        con1_1.close();// no mock call for this - should be suppressed

        Connection con1_2 = csds.getConnection();
        Assert.assertSame("should be same connection", con1_2, con1);
        Connection con2 = csds.getConnection();
        Assert.assertNotSame("shouldn't be same connection", con2, con1);
        csds.stopCloseSuppression(con1);
        Assert.assertTrue("should be able to close connection", csds.shouldClose(con1));
        con1_1 = null;
        con1_2 = null;
        con1.close();
        Assert.assertTrue("should be able to close connection", csds.shouldClose(con2));
        con2.close();
    }

    @Test
    public void testSuppressOfCloseWithJdbcTemplate() throws Exception {
        Connection con = Mockito.mock(Connection.class);
        DataSource ds = Mockito.mock(DataSource.class);
        Statement stmt = Mockito.mock(Statement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        // open and start suppressing close
        Mockito.when(ds.getConnection()).thenReturn(con);
        // transaction 1
        Mockito.when(con.getAutoCommit()).thenReturn(false);
        Mockito.when(con.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery("select baz from bar")).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(false);
        Mockito.when(con.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery("select foo from bar")).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(false);
        con.commit();
        // transaction 2
        Mockito.when(con.getAutoCommit()).thenReturn(false);
        Mockito.when(con.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery("select ham from foo")).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(false);
        // REQUIRES_NEW transaction
        Mockito.when(ds.getConnection()).thenReturn(con);
        Mockito.when(con.getAutoCommit()).thenReturn(false);
        Mockito.when(con.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery("select 1 from eggs")).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(false);
        con.commit();
        con.close();
        // resume transaction 2
        Mockito.when(con.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery("select more, ham from foo")).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(false);
        con.commit();
        // transaction 3
        Mockito.when(con.getAutoCommit()).thenReturn(false);
        Mockito.when(con.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery("select spam from ham")).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(false);
        con.commit();
        // stop suppressing close and close
        con.close();
        // standalone query
        Mockito.when(ds.getConnection()).thenReturn(con);
        Mockito.when(con.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery("select egg from bar")).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(false);
        con.close();
        final ExtendedConnectionDataSourceProxy csds = new ExtendedConnectionDataSourceProxy();
        csds.setDataSource(ds);
        PlatformTransactionManager tm = new org.springframework.jdbc.datasource.DataSourceTransactionManager(csds);
        TransactionTemplate tt = new TransactionTemplate(tm);
        final TransactionTemplate tt2 = new TransactionTemplate(tm);
        tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        final JdbcTemplate template = new JdbcTemplate(csds);
        Connection connection = DataSourceUtils.getConnection(csds);
        csds.startCloseSuppression(connection);
        tt.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                template.queryForList("select baz from bar");
                template.queryForList("select foo from bar");
                return null;
            }
        });
        tt.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                template.queryForList("select ham from foo");
                tt2.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                    @Override
                    public Void doInTransaction(TransactionStatus status) {
                        template.queryForList("select 1 from eggs");
                        return null;
                    }
                });
                template.queryForList("select more, ham from foo");
                return null;
            }
        });
        tt.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                template.queryForList("select spam from ham");
                return null;
            }
        });
        csds.stopCloseSuppression(connection);
        DataSourceUtils.releaseConnection(connection, csds);
        template.queryForList("select egg from bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void delegateIsRequired() throws Exception {
        ExtendedConnectionDataSourceProxy tested = new ExtendedConnectionDataSourceProxy(null);
        tested.afterPropertiesSet();
    }

    @Test
    public void unwrapForUnsupportedInterface() throws Exception {
        ExtendedConnectionDataSourceProxy tested = new ExtendedConnectionDataSourceProxy(new ExtendedConnectionDataSourceProxyTests.DataSourceStub());
        Assert.assertFalse(tested.isWrapperFor(ExtendedConnectionDataSourceProxyTests.Unsupported.class));
        try {
            tested.unwrap(ExtendedConnectionDataSourceProxyTests.Unsupported.class);
            Assert.fail();
        } catch (SQLException expected) {
            // this would be the correct behavior in a Java6-only recursive implementation
            // assertEquals(DataSourceStub.UNWRAP_ERROR_MESSAGE, expected.getMessage());
            Assert.assertEquals(("Unsupported class " + (ExtendedConnectionDataSourceProxyTests.Unsupported.class.getSimpleName())), expected.getMessage());
        }
    }

    @Test
    public void unwrapForSupportedInterface() throws Exception {
        ExtendedConnectionDataSourceProxyTests.DataSourceStub ds = new ExtendedConnectionDataSourceProxyTests.DataSourceStub();
        ExtendedConnectionDataSourceProxy tested = new ExtendedConnectionDataSourceProxy(ds);
        Assert.assertTrue(tested.isWrapperFor(ExtendedConnectionDataSourceProxyTests.Supported.class));
        Assert.assertEquals(ds, tested.unwrap(ExtendedConnectionDataSourceProxyTests.Supported.class));
    }

    @Test
    public void unwrapForSmartDataSource() throws Exception {
        ExtendedConnectionDataSourceProxy tested = new ExtendedConnectionDataSourceProxy(new ExtendedConnectionDataSourceProxyTests.DataSourceStub());
        Assert.assertTrue(tested.isWrapperFor(DataSource.class));
        Assert.assertEquals(tested, tested.unwrap(DataSource.class));
        Assert.assertTrue(tested.isWrapperFor(SmartDataSource.class));
        Assert.assertEquals(tested, tested.unwrap(SmartDataSource.class));
    }

    /**
     * Interface implemented by the wrapped DataSource
     */
    private static interface Supported {}

    /**
     * Interface *not* implemented by the wrapped DataSource
     */
    private static interface Unsupported {}

    /**
     * Stub for a wrapped DataSource that implements additional interface. Its
     * purpose is testing of {@link DataSource#isWrapperFor(Class)} and
     * {@link DataSource#unwrap(Class)} methods.
     */
    private static class DataSourceStub implements DataSource , ExtendedConnectionDataSourceProxyTests.Supported {
        private static final String UNWRAP_ERROR_MESSAGE = "supplied type is not implemented by this class";

        @Override
        public Connection getConnection() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            if ((iface.equals(ExtendedConnectionDataSourceProxyTests.Supported.class)) || (iface.equals(DataSource.class))) {
                return true;
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if ((iface.equals(ExtendedConnectionDataSourceProxyTests.Supported.class)) || (iface.equals(DataSource.class))) {
                return ((T) (this));
            }
            throw new SQLException(ExtendedConnectionDataSourceProxyTests.DataSourceStub.UNWRAP_ERROR_MESSAGE);
        }

        /**
         * Added due to JDK 7.
         */
        @SuppressWarnings("unused")
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException();
        }
    }
}

