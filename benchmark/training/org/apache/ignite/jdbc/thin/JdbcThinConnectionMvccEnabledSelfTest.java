/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.jdbc.thin;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.Callable;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Connection test.
 */
@SuppressWarnings("ThrowableNotThrown")
public class JdbcThinConnectionMvccEnabledSelfTest extends JdbcThinAbstractSelfTest {
    /**
     *
     */
    private static final String URL = "jdbc:ignite:thin://127.0.0.1";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMetadataDefaults() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMvccEnabledSelfTest.URL)) {
            DatabaseMetaData meta = conn.getMetaData();
            assertEquals(Connection.TRANSACTION_REPEATABLE_READ, meta.getDefaultTransactionIsolation());
            assertTrue(meta.supportsTransactions());
            assertFalse(meta.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE));
            assertFalse(meta.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED));
            assertFalse(meta.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
            assertTrue(meta.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ));
            assertFalse(meta.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetAutoCommit() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMvccEnabledSelfTest.URL)) {
            assertTrue(conn.getMetaData().supportsTransactions());
            assertTrue(conn.getAutoCommit());
            conn.setAutoCommit(false);
            assertFalse(conn.getAutoCommit());
            conn.setAutoCommit(true);
            assertTrue(conn.getAutoCommit());
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setAutoCommit(true);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCommit() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMvccEnabledSelfTest.URL)) {
            assertTrue(conn.getMetaData().supportsTransactions());
            // Should not be called in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.commit();
                    return null;
                }
            }, SQLException.class, "Transaction cannot be committed explicitly in auto-commit mode");
            conn.setAutoCommit(false);
            conn.commit();
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.commit();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRollback() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMvccEnabledSelfTest.URL)) {
            assertTrue(conn.getMetaData().supportsTransactions());
            // Should not be called in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.rollback();
                    return null;
                }
            }, SQLException.class, "Transaction cannot be rolled back explicitly in auto-commit mode.");
            conn.setAutoCommit(false);
            conn.rollback();
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.rollback();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetSavepoint() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMvccEnabledSelfTest.URL)) {
            assert !(conn.getMetaData().supportsSavepoints());
            // Disallowed in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setSavepoint();
                    return null;
                }
            }, SQLException.class, "Savepoint cannot be set in auto-commit mode");
            conn.setAutoCommit(false);
            // Unsupported
            checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setSavepoint();
                }
            });
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setSavepoint();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetSavepointName() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMvccEnabledSelfTest.URL)) {
            assert !(conn.getMetaData().supportsSavepoints());
            // Invalid arg
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setSavepoint(null);
                    return null;
                }
            }, SQLException.class, "Savepoint name cannot be null");
            final String name = "savepoint";
            // Disallowed in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setSavepoint(name);
                    return null;
                }
            }, SQLException.class, "Savepoint cannot be set in auto-commit mode");
            conn.setAutoCommit(false);
            // Unsupported
            checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setSavepoint(name);
                }
            });
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setSavepoint(name);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRollbackSavePoint() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMvccEnabledSelfTest.URL)) {
            assert !(conn.getMetaData().supportsSavepoints());
            // Invalid arg
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.rollback(null);
                    return null;
                }
            }, SQLException.class, "Invalid savepoint");
            final Savepoint savepoint = getFakeSavepoint();
            // Disallowed in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.rollback(savepoint);
                    return null;
                }
            }, SQLException.class, "Auto-commit mode");
            conn.setAutoCommit(false);
            // Unsupported
            checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.rollback(savepoint);
                }
            });
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.rollback(savepoint);
                }
            });
        }
    }
}

