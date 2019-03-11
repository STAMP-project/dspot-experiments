/**
 * Copyright (C) 2007 The Android Open Source Project
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
package libcore.sqlite;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;


/**
 * This class provides SQL unit test, which can be used by subclasses eg. to
 * test JDBC drivers.
 */
abstract class AbstractSqlTest extends TestCase {
    /**
     * The first connection.
     */
    private Connection firstConnection;

    /**
     * The second connection.
     */
    private Connection secondConnection;

    /**
     * The statement from the first connection.
     */
    private Statement firstStmt;

    /**
     * The statement from the second connection.
     */
    private Statement secondStmt;

    /**
     * The values of the first column "one".
     */
    private final String[] ones = new String[]{ "hello!", "goodbye" };

    /**
     * The values of the second column "two".
     */
    private final short[] twos = new short[]{ 10, 20 };

    /**
     * The updated values of the first column "one".
     */
    private final String[] ones_updated;

    /**
     * Creates a new instance of this class
     */
    public AbstractSqlTest() {
        super();
        ones_updated = new String[ones.length];
        for (int i = 0; i < (ones.length); i++) {
            ones_updated[i] = (ones[i]) + (twos[i]);
        }
    }

    public void testAutoCommitInsertSelect() throws SQLException {
        autoCommitInsertSelect();
    }

    /**
     * Tests the following sequence after successful insertion of some test
     * data:
     * - update data from connection one
     * - select data from connection two (-> should have the old values)
     * - commit data from connection one
     * - select data from connection two (-> should have the new values)
     *
     * @throws SQLException
     * 		if there is a problem accessing the database
     */
    public void testUpdateSelectCommitSelect() throws SQLException {
        autoCommitInsertSelect();
        firstStmt.getConnection().setAutoCommit(false);
        updateOnes(firstStmt, ones_updated, twos);
        assertAllFromTbl1(secondStmt, ones, twos);
        firstStmt.getConnection().commit();
        assertAllFromTbl1(secondStmt, ones_updated, twos);
    }

    /**
     * Tests the following sequence after successful insertion of some test
     * data:
     * - update data from connection one
     * - select data from connection two (-> should have the old values)
     * - rollback data from connection one
     * - select data from connection two (-> should still have the old values)
     *
     * @throws SQLException
     * 		if there is a problem accessing the database
     */
    public void testUpdateSelectRollbackSelect() throws SQLException {
        autoCommitInsertSelect();
        firstStmt.getConnection().setAutoCommit(false);
        updateOnes(firstStmt, ones_updated, twos);
        assertAllFromTbl1(secondStmt, ones, twos);
        firstStmt.getConnection().rollback();
        assertAllFromTbl1(secondStmt, ones, twos);
    }
}

