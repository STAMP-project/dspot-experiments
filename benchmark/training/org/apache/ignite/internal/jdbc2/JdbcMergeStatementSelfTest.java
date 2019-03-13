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


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import org.junit.Test;


/**
 * MERGE statement test.
 */
public class JdbcMergeStatementSelfTest extends JdbcAbstractDmlStatementSelfTest {
    /**
     * SQL query.
     */
    private static final String SQL = "merge into Person(_key, id, firstName, lastName, age, data) values " + (("('p1', 1, 'John', 'White', 25, RAWTOHEX('White')), " + "('p2', 2, 'Joe', 'Black', 35, RAWTOHEX('Black')), ") + "('p3', 3, 'Mike', 'Green', 40, RAWTOHEX('Green'))");

    /**
     * SQL query.
     */
    protected static final String SQL_PREPARED = "merge into Person(_key, id, firstName, lastName, age, data) values " + "(?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?)";

    /**
     * Statement.
     */
    protected Statement stmt;

    /**
     * Prepared statement.
     */
    protected PreparedStatement prepStmt;

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testExecuteUpdate() throws SQLException {
        int res = stmt.executeUpdate(JdbcMergeStatementSelfTest.SQL);
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
        boolean res = stmt.execute(JdbcMergeStatementSelfTest.SQL);
        assertEquals(false, res);
    }

    /**
     *
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testBatch() throws SQLException {
        prepStmt.setString(1, "p1");
        prepStmt.setInt(2, 1);
        prepStmt.setString(3, "John");
        prepStmt.setString(4, "White");
        prepStmt.setInt(5, 25);
        prepStmt.setBytes(6, JdbcAbstractDmlStatementSelfTest.getBytes("White"));
        prepStmt.setString(7, "p2");
        prepStmt.setInt(8, 2);
        prepStmt.setString(9, "Joe");
        prepStmt.setString(10, "Black");
        prepStmt.setInt(11, 35);
        prepStmt.setBytes(12, JdbcAbstractDmlStatementSelfTest.getBytes("Black"));
        prepStmt.addBatch();
        prepStmt.setString(1, "p3");
        prepStmt.setInt(2, 3);
        prepStmt.setString(3, "Mike");
        prepStmt.setString(4, "Green");
        prepStmt.setInt(5, 40);
        prepStmt.setBytes(6, JdbcAbstractDmlStatementSelfTest.getBytes("Green"));
        prepStmt.setString(7, "p4");
        prepStmt.setInt(8, 4);
        prepStmt.setString(9, "Leah");
        prepStmt.setString(10, "Grey");
        prepStmt.setInt(11, 22);
        prepStmt.setBytes(12, JdbcAbstractDmlStatementSelfTest.getBytes("Grey"));
        prepStmt.addBatch();
        int[] res = prepStmt.executeBatch();
        assertTrue(Arrays.equals(new int[]{ 2, 2 }, res));
    }
}

