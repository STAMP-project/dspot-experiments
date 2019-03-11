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


import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.GridTestUtils;


/**
 * Statement test.
 */
@SuppressWarnings({ "ThrowableNotThrown" })
public class JdbcThinStatementSelfTest extends JdbcThinAbstractSelfTest {
    /**
     * URL.
     */
    private static final String URL = "jdbc:ignite:thin://127.0.0.1/";

    /**
     * SQL query.
     */
    private static final String SQL = "select * from Person where age > 30";

    /**
     * Connection.
     */
    private Connection conn;

    /**
     * Statement.
     */
    private Statement stmt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecuteQuery0() throws Exception {
        ResultSet rs = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        assert rs != null;
        int cnt = 0;
        while (rs.next()) {
            int id = rs.getInt("id");
            if (id == 2) {
                assert "Joe".equals(rs.getString("firstName"));
                assert "Black".equals(rs.getString("lastName"));
                assert (rs.getInt("age")) == 35;
            } else
                if (id == 3) {
                    assert "Mike".equals(rs.getString("firstName"));
                    assert "Green".equals(rs.getString("lastName"));
                    assert (rs.getInt("age")) == 40;
                } else
                    assert false : "Wrong ID: " + id;


            cnt++;
        } 
        assert cnt == 2;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecuteQuery1() throws Exception {
        final String sqlText = "select val from test";
        try (ResultSet rs = stmt.executeQuery(sqlText)) {
            assertNotNull(rs);
            assertTrue(rs.next());
            int val = rs.getInt(1);
            assertTrue(("Invalid val: " + val), ((val >= 1) && (val <= 10)));
        }
        stmt.close();
        // Call on a closed statement
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.executeQuery(sqlText);
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecute() throws Exception {
        assert stmt.execute(JdbcThinStatementSelfTest.SQL);
        assert (stmt.getUpdateCount()) == (-1) : "Update count must be -1 for SELECT query";
        ResultSet rs = stmt.getResultSet();
        assert rs != null;
        assert (stmt.getResultSet()) == null;
        int cnt = 0;
        while (rs.next()) {
            int id = rs.getInt("id");
            if (id == 2) {
                assert "Joe".equals(rs.getString("firstName"));
                assert "Black".equals(rs.getString("lastName"));
                assert (rs.getInt("age")) == 35;
            } else
                if (id == 3) {
                    assert "Mike".equals(rs.getString("firstName"));
                    assert "Green".equals(rs.getString("lastName"));
                    assert (rs.getInt("age")) == 40;
                } else
                    assert false : "Wrong ID: " + id;


            cnt++;
        } 
        assert cnt == 2;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testMaxRows() throws Exception {
        stmt.setMaxRows(1);
        assert (stmt.getMaxRows()) == 1;
        ResultSet rs = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        assert rs != null;
        int cnt = 0;
        while (rs.next()) {
            int id = rs.getInt("id");
            if (id == 2) {
                assert "Joe".equals(rs.getString("firstName"));
                assert "Black".equals(rs.getString("lastName"));
                assert (rs.getInt("age")) == 35;
            } else
                if (id == 3) {
                    assert "Mike".equals(rs.getString("firstName"));
                    assert "Green".equals(rs.getString("lastName"));
                    assert (rs.getInt("age")) == 40;
                } else
                    assert false : "Wrong ID: " + id;


            cnt++;
        } 
        assert cnt == 1;
        stmt.setMaxRows(0);
        rs = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        assert rs != null;
        cnt = 0;
        while (rs.next()) {
            int id = rs.getInt("id");
            if (id == 2) {
                assert "Joe".equals(rs.getString("firstName"));
                assert "Black".equals(rs.getString("lastName"));
                assert (rs.getInt("age")) == 35;
            } else
                if (id == 3) {
                    assert "Mike".equals(rs.getString("firstName"));
                    assert "Green".equals(rs.getString("lastName"));
                    assert (rs.getInt("age")) == 40;
                } else
                    assert false : "Wrong ID: " + id;


            cnt++;
        } 
        assert cnt == 2;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testCloseResultSet0() throws Exception {
        ResultSet rs0 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        ResultSet rs1 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        ResultSet rs2 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        assert rs0.isClosed() : "ResultSet must be implicitly closed after re-execute statement";
        assert rs1.isClosed() : "ResultSet must be implicitly closed after re-execute statement";
        assert !(rs2.isClosed()) : "Last result set must be available";
        stmt.close();
        assert rs2.isClosed() : "ResultSet must be explicitly closed after close statement";
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testCloseResultSet1() throws Exception {
        stmt.execute(JdbcThinStatementSelfTest.SQL);
        ResultSet rs = stmt.getResultSet();
        stmt.close();
        assert rs.isClosed() : "ResultSet must be explicitly closed after close statement";
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testCloseResultSetByConnectionClose() throws Exception {
        ResultSet rs = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        conn.close();
        assert stmt.isClosed() : "Statement must be implicitly closed after close connection";
        assert rs.isClosed() : "ResultSet must be implicitly closed after close connection";
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testCloseOnCompletionAfterQuery() throws Exception {
        assert !(stmt.isCloseOnCompletion()) : "Invalid default closeOnCompletion";
        ResultSet rs0 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        ResultSet rs1 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        assert rs0.isClosed() : "Result set must be closed implicitly";
        assert !(stmt.isClosed()) : "Statement must not be closed";
        rs1.close();
        assert !(stmt.isClosed()) : "Statement must not be closed";
        ResultSet rs2 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        stmt.closeOnCompletion();
        assert stmt.isCloseOnCompletion() : "Invalid closeOnCompletion";
        rs2.close();
        assert stmt.isClosed() : "Statement must be closed";
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testCloseOnCompletionBeforeQuery() throws Exception {
        assert !(stmt.isCloseOnCompletion()) : "Invalid default closeOnCompletion";
        ResultSet rs0 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        ResultSet rs1 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        assert rs0.isClosed() : "Result set must be closed implicitly";
        assert !(stmt.isClosed()) : "Statement must not be closed";
        rs1.close();
        assert !(stmt.isClosed()) : "Statement must not be closed";
        stmt.closeOnCompletion();
        ResultSet rs2 = stmt.executeQuery(JdbcThinStatementSelfTest.SQL);
        assert stmt.isCloseOnCompletion() : "Invalid closeOnCompletion";
        rs2.close();
        assert stmt.isClosed() : "Statement must be closed";
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecuteQueryMultipleOnlyResultSets() throws Exception {
        assert conn.getMetaData().supportsMultipleResultSets();
        int stmtCnt = 10;
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < stmtCnt; ++i)
            sql.append("select ").append(i).append("; ");

        assert stmt.execute(sql.toString());
        for (int i = 0; i < stmtCnt; ++i) {
            assert stmt.getMoreResults();
            ResultSet rs = stmt.getResultSet();
            assert rs.next();
            assert (rs.getInt(1)) == i;
            assert !(rs.next());
        }
        assert !(stmt.getMoreResults());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecuteQueryMultipleOnlyDml() throws Exception {
        conn.setSchema(null);
        Statement stmt0 = conn.createStatement();
        int stmtCnt = 10;
        StringBuilder sql = new StringBuilder("drop table if exists test; create table test(ID int primary key, NAME varchar(20)); ");
        for (int i = 0; i < stmtCnt; ++i)
            sql.append((((("insert into test (ID, NAME) values (" + i) + ", 'name_") + i) + "'); "));

        assert !(stmt0.execute(sql.toString()));
        // DROP TABLE statement
        assert (stmt0.getResultSet()) == null;
        assert (stmt0.getUpdateCount()) == 0;
        // CREATE TABLE statement
        assert (stmt0.getResultSet()) == null;
        assert (stmt0.getUpdateCount()) == 0;
        for (int i = 0; i < stmtCnt; ++i) {
            assert stmt0.getMoreResults();
            assert (stmt0.getResultSet()) == null;
            assert (stmt0.getUpdateCount()) == 1;
        }
        assert !(stmt0.getMoreResults());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecuteQueryMultipleMixed() throws Exception {
        conn.setSchema(null);
        Statement stmt0 = conn.createStatement();
        int stmtCnt = 10;
        StringBuilder sql = new StringBuilder("drop table if exists test; create table test(ID int primary key, NAME varchar(20)); ");
        for (int i = 0; i < stmtCnt; ++i) {
            if ((i % 2) == 0)
                sql.append(((((" insert into test (ID, NAME) values (" + i) + ", 'name_") + i) + "'); "));
            else
                sql.append(((" select * from test where id < " + i) + "; "));

        }
        assert !(stmt0.execute(sql.toString()));
        // DROP TABLE statement
        assert (stmt0.getResultSet()) == null;
        assert (stmt0.getUpdateCount()) == 0;
        // CREATE TABLE statement
        assert (stmt0.getResultSet()) == null;
        assert (stmt0.getUpdateCount()) == 0;
        boolean notEmptyResult = false;
        for (int i = 0; i < stmtCnt; ++i) {
            assert stmt0.getMoreResults();
            if ((i % 2) == 0) {
                assert (stmt0.getResultSet()) == null;
                assert (stmt0.getUpdateCount()) == 1;
            } else {
                assert (stmt0.getUpdateCount()) == (-1);
                ResultSet rs = stmt0.getResultSet();
                int rowsCnt = 0;
                while (rs.next())
                    rowsCnt++;

                assert rowsCnt <= ((i + 1) / 2);
                if (rowsCnt == ((i + 1) / 2))
                    notEmptyResult = true;

            }
        }
        assert notEmptyResult;
        assert !(stmt0.getMoreResults());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecuteUpdate() throws Exception {
        final String sqlText = "update test set val=1 where _key=1";
        assertEquals(1, stmt.executeUpdate(sqlText));
        stmt.close();
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.executeUpdate(sqlText);
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testExecuteUpdateProducesResultSet() throws Exception {
        final String sqlText = "select * from test";
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return stmt.executeUpdate(sqlText);
            }
        }, SQLException.class, "Given statement type does not match that declared by JDBC driver");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testClose() throws Exception {
        String sqlText = "select * from test";
        ResultSet rs = stmt.executeQuery(sqlText);
        assertTrue(rs.next());
        assertFalse(rs.isClosed());
        assertFalse(stmt.isClosed());
        stmt.close();
        stmt.close();// Closing closed is ok

        assertTrue(stmt.isClosed());
        // Current result set must be closed
        assertTrue(rs.isClosed());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testGetSetMaxFieldSizeUnsupported() throws Exception {
        assertEquals(0, stmt.getMaxFieldSize());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setMaxFieldSize(100);
                return null;
            }
        }, SQLFeatureNotSupportedException.class, "Field size limitation is not supported");
        assertEquals(0, stmt.getMaxFieldSize());
        stmt.close();
        // Call on a closed statement
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getMaxFieldSize();
            }
        });
        // Call on a closed statement
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setMaxFieldSize(100);
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testGetSetMaxRows() throws Exception {
        assertEquals(0, stmt.getMaxRows());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setMaxRows((-1));
                return null;
            }
        }, SQLException.class, "Invalid max rows value");
        assertEquals(0, stmt.getMaxRows());
        final int maxRows = 1;
        stmt.setMaxRows(maxRows);
        assertEquals(maxRows, stmt.getMaxRows());
        String sqlText = "select * from test";
        ResultSet rs = stmt.executeQuery(sqlText);
        assertTrue(rs.next());
        assertFalse(rs.next());// max rows reached

        stmt.close();
        // Call on a closed statement
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getMaxRows();
            }
        });
        // Call on a closed statement
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setMaxRows(maxRows);
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testGetSetQueryTimeout() throws Exception {
        assertEquals(0, stmt.getQueryTimeout());
        // Invalid argument
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setQueryTimeout((-1));
                return null;
            }
        }, SQLException.class, "Invalid timeout value");
        assertEquals(0, stmt.getQueryTimeout());
        final int timeout = 3;
        stmt.setQueryTimeout(timeout);
        assertEquals(timeout, stmt.getQueryTimeout());
        stmt.close();
        // Call on a closed statement
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getQueryTimeout();
            }
        });
        // Call on a closed statement
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setQueryTimeout(timeout);
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testMaxFieldSize() throws Exception {
        assert (stmt.getMaxFieldSize()) >= 0;
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setMaxFieldSize((-1));
                return null;
            }
        }, SQLException.class, "Invalid field limit");
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setMaxFieldSize(100);
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testQueryTimeout() throws Exception {
        assert (stmt.getQueryTimeout()) == 0 : "Default timeout invalid: " + (stmt.getQueryTimeout());
        stmt.setQueryTimeout(10);
        assert (stmt.getQueryTimeout()) == 10;
        stmt.close();
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getQueryTimeout();
            }
        });
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setQueryTimeout(10);
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testWarningsOnClosedStatement() throws Exception {
        stmt.clearWarnings();
        assert (stmt.getWarnings()) == null;
        stmt.close();
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getWarnings();
            }
        });
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.clearWarnings();
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testCursorName() throws Exception {
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setCursorName("test");
            }
        });
        stmt.close();
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setCursorName("test");
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testGetMoreResults() throws Exception {
        assert !(stmt.getMoreResults());
        stmt.execute("select 1; ");
        ResultSet rs = stmt.getResultSet();
        assert !(stmt.getMoreResults());
        assert (stmt.getResultSet()) == null;
        assert rs.isClosed();
        stmt.close();
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getMoreResults();
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testGetMoreResults1() throws Exception {
        assert !(stmt.getMoreResults(Statement.CLOSE_CURRENT_RESULT));
        assert !(stmt.getMoreResults(Statement.KEEP_CURRENT_RESULT));
        assert !(stmt.getMoreResults(Statement.CLOSE_ALL_RESULTS));
        stmt.execute("select 1; ");
        ResultSet rs = stmt.getResultSet();
        assert !(stmt.getMoreResults(Statement.KEEP_CURRENT_RESULT));
        assert !(rs.isClosed());
        assert !(stmt.getMoreResults(Statement.CLOSE_ALL_RESULTS));
        assert rs.isClosed();
        stmt.close();
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getMoreResults(Statement.KEEP_CURRENT_RESULT);
            }
        });
    }

    /**
     * Verifies that emty batch can be performed.
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testBatchEmpty() throws Exception {
        assert conn.getMetaData().supportsBatchUpdates();
        stmt.addBatch("");
        stmt.clearBatch();
        // Just verify that no exception have been thrown.
        stmt.executeBatch();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testFetchDirection() throws Exception {
        assert (stmt.getFetchDirection()) == (ResultSet.FETCH_FORWARD);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setFetchDirection(ResultSet.FETCH_REVERSE);
                return null;
            }
        }, SQLFeatureNotSupportedException.class, "Only forward direction is supported.");
        stmt.close();
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setFetchDirection((-1));
            }
        });
        checkStatementClosed(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getFetchDirection();
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testAutogenerated() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate("select 1", (-1));
                return null;
            }
        }, SQLException.class, "Invalid autoGeneratedKeys value");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.execute("select 1", (-1));
                return null;
            }
        }, SQLException.class, "Invalid autoGeneratedKeys value");
        assert !(conn.getMetaData().supportsGetGeneratedKeys());
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.getGeneratedKeys();
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.executeUpdate("select 1", Statement.RETURN_GENERATED_KEYS);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.executeUpdate("select 1", new int[]{ 1, 2 });
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.executeUpdate("select 1", new String[]{ "a", "b" });
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.execute("select 1", Statement.RETURN_GENERATED_KEYS);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.execute("select 1", new int[]{ 1, 2 });
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.execute("select 1", new String[]{ "a", "b" });
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testStatementTypeMismatchSelectForCachedQuery() throws Exception {
        // Put query to cache.
        stmt.executeQuery("select 1;");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeUpdate("select 1;");
                return null;
            }
        }, SQLException.class, "Given statement type does not match that declared by JDBC driver");
        assert (stmt.getResultSet()) == null : "Not results expected. Last statement is executed with exception";
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @org.junit.Test
    public void testStatementTypeMismatchUpdate() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.executeQuery("update test set val=28 where _key=1");
                return null;
            }
        }, SQLException.class, "Given statement type does not match that declared by JDBC driver");
        ResultSet rs = stmt.executeQuery("select val from test where _key=1");
        boolean next = rs.next();
        assert next;
        assert (rs.getInt(1)) == 1 : (("The data must not be updated. " + ("Because update statement is executed via 'executeQuery' method." + " Data [val=")) + (rs.getInt(1))) + ']';
    }

    /**
     *
     */
    @SuppressWarnings("unused")
    public static class Test {
        @QuerySqlField
        private int val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        public Test(int val) {
            this.val = val;
        }
    }

    /**
     * Person.
     */
    private static class Person implements Serializable {
        /**
         * ID.
         */
        @QuerySqlField
        private final int id;

        /**
         * First name.
         */
        @QuerySqlField
        private final String firstName;

        /**
         * Last name.
         */
        @QuerySqlField
        private final String lastName;

        /**
         * Age.
         */
        @QuerySqlField
        private final int age;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param firstName
         * 		First name.
         * @param lastName
         * 		Last name.
         * @param age
         * 		Age.
         */
        private Person(int id, String firstName, String lastName, int age) {
            assert !(F.isEmpty(firstName));
            assert !(F.isEmpty(lastName));
            assert age > 0;
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }
    }
}

