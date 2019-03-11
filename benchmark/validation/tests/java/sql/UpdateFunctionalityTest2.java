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


public class UpdateFunctionalityTest2 extends TestCase {
    private static Connection conn = null;

    private static Statement statement = null;

    /**
     * UpdateFunctionalityTest2#testUpdate1(). Updates row with no
     *        referencing ones and RESTRICT action
     */
    public void testUpdate1() throws SQLException {
        DatabaseCreator.fillFKStrictTable(UpdateFunctionalityTest2.conn);
        UpdateFunctionalityTest2.statement.execute((("UPDATE " + (DatabaseCreator.PARENT_TABLE)) + " SET id = 4 WHERE id = 3"));
    }

    /**
     * UpdateFunctionalityTest2#testUpdate3(). Deletes all referencing
     *        rows and then updates referenced one
     */
    public void testUpdate3() throws SQLException {
        DatabaseCreator.fillFKStrictTable(UpdateFunctionalityTest2.conn);
        UpdateFunctionalityTest2.statement.execute((("DELETE FROM " + (DatabaseCreator.FKSTRICT_TABLE)) + " WHERE name_id = 1;"));
        UpdateFunctionalityTest2.statement.execute((("UPDATE " + (DatabaseCreator.PARENT_TABLE)) + " SET id = 5 WHERE id = 1;"));
    }

    /**
     * UpdateFunctionalityTest2#testUpdate5(). Updates row with
     *        referencing ones and CASCADE action - expecting that all
     *        referencing rows will also be updated
     */
    public void testUpdate5() throws SQLException {
        DatabaseCreator.fillFKCascadeTable(UpdateFunctionalityTest2.conn);
        UpdateFunctionalityTest2.statement.executeUpdate((("UPDATE " + (DatabaseCreator.PARENT_TABLE)) + " SET id = 5 WHERE id = 1;"));
        ResultSet r = UpdateFunctionalityTest2.statement.executeQuery(((("SELECT COUNT(*) " + "FROM ") + (DatabaseCreator.FKCASCADE_TABLE)) + " WHERE name_id = 1;"));
        r.next();
        TestCase.assertEquals("Should be 2 rows", 2, r.getInt(1));
        r = UpdateFunctionalityTest2.statement.executeQuery(((("SELECT COUNT(*) " + "FROM ") + (DatabaseCreator.FKCASCADE_TABLE)) + " WHERE name_id = 5;"));
        r.next();
        TestCase.assertEquals("Should be 0 rows", 0, r.getInt(1));
        r.close();
    }

    /**
     * UpdateFunctionalityTest2#testUpdate8(). Updates table using scalar
     *        subquery as new field value
     */
    public void testUpdate8() throws SQLException {
        UpdateFunctionalityTest2.statement.executeUpdate((((((((("UPDATE " + (DatabaseCreator.SIMPLE_TABLE3)) + " SET speed = (SELECT MAX(speed) FROM ") + (DatabaseCreator.SIMPLE_TABLE1)) + ") WHERE id = (SELECT id FROM ") + (DatabaseCreator.SIMPLE_TABLE1)) + " WHERE speed = (SELECT MAX(speed) FROM ") + (DatabaseCreator.SIMPLE_TABLE1)) + "))"));
        ResultSet r = UpdateFunctionalityTest2.statement.executeQuery((((("SELECT id FROM " + (DatabaseCreator.SIMPLE_TABLE3)) + " WHERE speed = (SELECT MAX(speed) FROM ") + (DatabaseCreator.SIMPLE_TABLE1)) + ");"));
        r.next();
        TestCase.assertEquals("Incorrect id updated", 1, r.getInt(1));
        r.close();
    }

    /**
     * UpdateFunctionalityTest2#testUpdate9(). Updates table using
     *        PreparedStatement
     */
    public void testUpdate9() throws SQLException {
        DatabaseCreator.fillTestTable5(UpdateFunctionalityTest2.conn);
        PreparedStatement stat = UpdateFunctionalityTest2.conn.prepareStatement((("UPDATE " + (DatabaseCreator.TEST_TABLE5)) + " SET testValue = ? WHERE testID = ?"));
        stat.setString(1, "1");
        stat.setInt(2, 1);
        stat.execute();
        stat.setString(1, "2");
        stat.setInt(2, 2);
        stat.execute();
        ResultSet r = UpdateFunctionalityTest2.statement.executeQuery((("SELECT testId, testValue FROM " + (DatabaseCreator.TEST_TABLE5)) + " WHERE testID < 3 ORDER BY testID"));
        while (r.next()) {
            TestCase.assertEquals("Incorrect value was returned", new Integer(r.getInt(1)).toString(), r.getString(2));
        } 
        r.close();
        stat.close();
    }
}

