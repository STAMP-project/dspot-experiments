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


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Callable;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Statement test.
 */
public class JdbcThinInsertStatementSelfTest extends JdbcThinAbstractDmlStatementSelfTest {
    /**
     * SQL query.
     */
    private static final String SQL = "insert into Person(_key, id, firstName, lastName, age) values " + (("('p1', 1, 'John', 'White', 25), " + "('p2', 2, 'Joe', 'Black', 35), ") + "('p3', 3, 'Mike', 'Green', 40)");

    /**
     * SQL query.
     */
    private static final String SQL_PREPARED = "insert into Person(_key, id, firstName, lastName, age) values " + "(?, ?, ?, ?, ?), (?, ?, ?, ?, ?), (?, ?, ?, ?, ?)";

    /**
     * Arguments for prepared statement.
     */
    private final Object[][] args = new Object[][]{ new Object[]{ "p1", 1, "John", "White", 25 }, new Object[]{ "p3", 3, "Mike", "Green", 40 }, new Object[]{ "p2", 2, "Joe", "Black", 35 } };

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
        assertEquals(3, stmt.executeUpdate(JdbcThinInsertStatementSelfTest.SQL));
    }

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testPreparedExecuteUpdate() throws SQLException {
        assertEquals(3, prepStmt.executeUpdate());
    }

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecute() throws SQLException {
        assertFalse(stmt.execute(JdbcThinInsertStatementSelfTest.SQL));
    }

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testPreparedExecute() throws SQLException {
        assertFalse(prepStmt.execute());
    }

    /**
     *
     */
    @Test
    public void testDuplicateKeys() {
        jcache(0).put("p2", new JdbcThinAbstractDmlStatementSelfTest.Person(2, "Joe", "Black", 35));
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Object>() {
            /**
             * {@inheritDoc }
             */
            @Override
            public Object call() throws Exception {
                return stmt.execute(JdbcThinInsertStatementSelfTest.SQL);
            }
        }, SQLException.class, "Failed to INSERT some keys because they are already in cache [keys=[p2]]");
        assertEquals(3, jcache(0).withKeepBinary().getAll(new HashSet(Arrays.asList("p1", "p2", "p3"))).size());
    }
}

