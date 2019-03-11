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
package org.apache.ignite.internal.jdbc2;


import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Callable;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Statement test.
 */
public class JdbcInsertStatementSelfTest extends JdbcAbstractDmlStatementSelfTest {
    /**
     * SQL query.
     */
    private static final String SQL = "insert into Person(_key, id, firstName, lastName, age, data) values " + (("('p1', 1, 'John', 'White', 25, RAWTOHEX('White')), " + "('p2', 2, 'Joe', 'Black', 35, RAWTOHEX('Black')), ") + "('p3', 3, 'Mike', 'Green', 40, RAWTOHEX('Green'))");

    /**
     * SQL query.
     */
    private static final String SQL_PREPARED = "insert into Person(_key, id, firstName, lastName, age, data) values " + "(?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?)";

    /**
     * Statement.
     */
    private Statement stmt;

    /**
     * Prepared statement.
     */
    private PreparedStatement prepStmt;

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecuteUpdate() throws SQLException {
        int res = stmt.executeUpdate(JdbcInsertStatementSelfTest.SQL);
        assertEquals(3, res);
    }

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecute() throws SQLException {
        boolean res = stmt.execute(JdbcInsertStatementSelfTest.SQL);
        assertEquals(false, res);
    }

    /**
     *
     */
    @Test
    public void testDuplicateKeys() {
        jcache(0).put("p2", new JdbcAbstractDmlStatementSelfTest.Person(2, "Joe", "Black", 35));
        Throwable reason = GridTestUtils.assertThrows(log, new Callable<Object>() {
            /**
             * {@inheritDoc }
             */
            @Override
            public Object call() throws Exception {
                return stmt.execute(JdbcInsertStatementSelfTest.SQL);
            }
        }, SQLException.class, null);
        reason = reason.getCause();
        assertNotNull(reason);
        assertTrue(reason.getMessage().contains("Failed to INSERT some keys because they are already in cache [keys=[p2]]"));
        assertEquals(3, jcache(0).withKeepBinary().getAll(new HashSet(Arrays.asList("p1", "p2", "p3"))).size());
    }

    /**
     *
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testBatch() throws SQLException {
        formBatch(1, 2);
        formBatch(3, 4);
        int[] res = prepStmt.executeBatch();
        assertTrue(Arrays.equals(new int[]{ 2, 2 }, res));
    }

    /**
     *
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testSingleItemBatch() throws SQLException {
        formBatch(1, 2);
        int[] res = prepStmt.executeBatch();
        assertTrue(Arrays.equals(new int[]{ 2 }, res));
    }

    /**
     *
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testSingleItemBatchError() throws SQLException {
        formBatch(1, 2);
        prepStmt.executeBatch();
        formBatch(1, 2);// Duplicate key

        BatchUpdateException reason = ((BatchUpdateException) (GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return prepStmt.executeBatch();
            }
        }, BatchUpdateException.class, "Failed to INSERT some keys because they are already in cache")));
        // Check update counts in the exception.
        assertTrue(F.isEmpty(reason.getUpdateCounts()));
    }

    /**
     *
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testErrorAmidstBatch() throws SQLException {
        formBatch(1, 2);
        formBatch(3, 1);// Duplicate key

        BatchUpdateException reason = ((BatchUpdateException) (GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return prepStmt.executeBatch();
            }
        }, BatchUpdateException.class, "Failed to INSERT some keys because they are already in cache")));
        // Check update counts in the exception.
        int[] counts = reason.getUpdateCounts();
        assertNotNull(counts);
        assertEquals(1, counts.length);
        assertEquals(2, counts[0]);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClearBatch() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws SQLException {
                return prepStmt.executeBatch();
            }
        }, SQLException.class, "Batch is empty");
        formBatch(1, 2);
        prepStmt.clearBatch();
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws SQLException {
                return prepStmt.executeBatch();
            }
        }, SQLException.class, "Batch is empty");
    }
}

