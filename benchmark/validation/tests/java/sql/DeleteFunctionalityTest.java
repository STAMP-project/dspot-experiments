/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.java.sql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;
import tests.support.DatabaseCreator;


public class DeleteFunctionalityTest extends TestCase {
    private static Connection conn = null;

    private static Statement statement = null;

    /**
     * DeleteFunctionalityTest#testDelete1(). Deletes row with no
     *        referencing ones and RESTRICT action
     */
    public void testDelete1() throws SQLException {
        DatabaseCreator.fillFKStrictTable(DeleteFunctionalityTest.conn);
        DeleteFunctionalityTest.statement.execute((("DELETE FROM " + (DatabaseCreator.PARENT_TABLE)) + " WHERE id = 3;"));
    }

    /**
     * DeleteFunctionalityTest#testDelete2(). Attempts to delete row with
     *        referencing ones and RESTRICT action - expecting SQLException
     *  TODO foreign key functionality is not supported
     */
    /* public void testDelete2() throws SQLException {
    DatabaseCreator.fillFKStrictTable(conn);
    try {
    statement.execute("DELETE FROM " + DatabaseCreator.PARENT_TABLE
    + " WHERE id = 1;");
    fail("expecting SQLException");
    } catch (SQLException ex) {
    // expected
    }
    }
     */
    /**
     * DeleteFunctionalityTest#testDelete3(). Deletes all referencing
     *        rows and then deletes referenced one
     */
    public void testDelete3() throws SQLException {
        DeleteFunctionalityTest.statement.execute((("DELETE FROM " + (DatabaseCreator.FKSTRICT_TABLE)) + " WHERE name_id = 1;"));
        DeleteFunctionalityTest.statement.execute((("DELETE FROM " + (DatabaseCreator.FKSTRICT_TABLE)) + " WHERE id = 1;"));
    }

    /**
     * DeleteFunctionalityTest#testDelete4(). Deletes row with no
     *        referencing ones and CASCADE action
     */
    public void testDelete4() throws SQLException {
        DatabaseCreator.fillFKCascadeTable(DeleteFunctionalityTest.conn);
        DeleteFunctionalityTest.statement.execute((("DELETE FROM " + (DatabaseCreator.PARENT_TABLE)) + " WHERE id = 3;"));
    }

    /**
     * DeleteFunctionalityTest#testDelete5(). Attempts to delete row with
     *        referencing ones and CASCADE action - expecting all referencing
     *        rows will also be deleted
     */
    public void testDelete5() throws SQLException {
        DeleteFunctionalityTest.statement.execute((("DELETE FROM " + (DatabaseCreator.PARENT_TABLE)) + " WHERE id = 1;"));
        ResultSet r = DeleteFunctionalityTest.statement.executeQuery((("SELECT COUNT(*) FROM " + (DatabaseCreator.FKCASCADE_TABLE)) + " WHERE name_id = 1;"));
        r.next();
        TestCase.assertEquals("Should be no rows", 0, r.getInt(1));
        r.close();
    }

    /**
     * DeleteFunctionalityTest#testDelete7(). Deletes rows using
     *        PreparedStatement
     */
    public void testDelete7() throws SQLException {
        DatabaseCreator.fillTestTable5(DeleteFunctionalityTest.conn);
        PreparedStatement stat = DeleteFunctionalityTest.conn.prepareStatement((("DELETE FROM " + (DatabaseCreator.TEST_TABLE5)) + " WHERE testID = ?"));
        stat.setInt(1, 1);
        stat.execute();
        stat.setInt(1, 2);
        stat.execute();
        ResultSet r = DeleteFunctionalityTest.statement.executeQuery((("SELECT COUNT(*) FROM " + (DatabaseCreator.TEST_TABLE5)) + " WHERE testID < 3 "));
        r.next();
        TestCase.assertEquals(0, r.getInt(1));
        r.close();
        stat.close();
    }
}

