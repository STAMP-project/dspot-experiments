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
package org.apache.ignite.jdbc;


import java.sql.Connection;
import java.sql.SQLException;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * JDBC version mismatch test.
 */
public class JdbcVersionMismatchSelfTest extends GridCommonAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testVersionMismatchJdbc() throws Exception {
        try (Connection conn1 = connect();Connection conn2 = connect()) {
            conn1.setAutoCommit(false);
            conn2.setAutoCommit(false);
            // Start first transaction and observe some values.
            assertEquals(1, JdbcVersionMismatchSelfTest.executeQuery(conn1, "SELECT * FROM test").size());
            // Change values while first transaction is still in progress.
            JdbcVersionMismatchSelfTest.executeUpdate(conn2, "INSERT INTO test VALUES (2, 2, 'test_2')");
            JdbcVersionMismatchSelfTest.executeUpdate(conn2, "COMMIT");
            assertEquals(2, JdbcVersionMismatchSelfTest.executeQuery(conn2, "SELECT * FROM test").size());
            // Force version mismatch.
            try {
                JdbcVersionMismatchSelfTest.executeUpdate(conn1, "INSERT INTO test VALUES (2, 2, 'test_2')");
                fail();
            } catch (SQLException e) {
                assertEquals(SqlStateCode.SERIALIZATION_FAILURE, e.getSQLState());
                assertEquals(IgniteQueryErrorCode.TRANSACTION_SERIALIZATION_ERROR, e.getErrorCode());
                assertNotNull(e.getMessage());
                assertTrue(e.getMessage().contains("Cannot serialize transaction due to write conflict"));
            }
            // Subsequent call should cause exception due to TX being rolled back.
            try {
                JdbcVersionMismatchSelfTest.executeQuery(conn1, "SELECT * FROM test").size();
                fail();
            } catch (SQLException e) {
                assertEquals(SqlStateCode.TRANSACTION_STATE_EXCEPTION, e.getSQLState());
                assertEquals(IgniteQueryErrorCode.TRANSACTION_COMPLETED, e.getErrorCode());
                assertNotNull(e.getMessage());
                assertTrue(e.getMessage().contains("Transaction is already completed"));
            }
            // Commit should fail.
            try {
                conn1.commit();
                fail();
            } catch (SQLException e) {
                // Cannot pass proper error codes for now
                assertEquals(SqlStateCode.INTERNAL_ERROR, e.getSQLState());
                assertEquals(IgniteQueryErrorCode.UNKNOWN, e.getErrorCode());
                assertNotNull(e.getMessage());
                assertTrue(e.getMessage().contains("Failed to finish transaction because it has been rolled back"));
            }
            // Rollback should work.
            conn1.rollback();
            // Subsequent calls should work fine.
            assertEquals(2, JdbcVersionMismatchSelfTest.executeQuery(conn2, "SELECT * FROM test").size());
        }
    }
}

