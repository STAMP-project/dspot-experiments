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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;
import tests.support.DatabaseCreator;
import tests.support.ThreadPool;


public class MultiThreadAccessTest extends TestCase {
    private static Connection conn;

    private static Statement statement;

    private static final int numThreads = 10;

    private static final int numOfRecords = 20;

    private ThreadPool threadPool;

    /**
     * A few threads execute select operation in the same time for one table in
     * the database. Number of threads is defined by numThreads variable
     *
     * @throws SQLException
     * 		
     */
    public void test_MultipleAccessToOneTable() throws SQLException {
        for (int i = 0; i < (MultiThreadAccessTest.numThreads); i++) {
            threadPool.runTask(MultiThreadAccessTest.createTask1(i));
        }
    }

    /**
     * A few threads execute select operation in the same time for different
     * tables in the database. Number of threads is defined by numThreads
     * variable
     *
     * @throws SQLException
     * 		
     */
    public void test_MultipleAccessToSeveralTables() throws SQLException {
        threadPool.runTask(MultiThreadAccessTest.createTask1(1));
        threadPool.runTask(MultiThreadAccessTest.createTask2(2));
        threadPool.runTask(MultiThreadAccessTest.createTask3(3));
    }

    /**
     * A few threads execute update, insert and delete operations in the same
     * time for one table in the database. Number of threads is defined by
     * numThreads variable
     *
     * @throws SQLException
     * 		
     */
    public void test_MultipleOperationsInSeveralTables() throws SQLException {
        int id1 = (MultiThreadAccessTest.numOfRecords) - 1;
        threadPool.runTask(MultiThreadAccessTest.createTask4(id1));
        int id2 = (MultiThreadAccessTest.numOfRecords) + 1;
        threadPool.runTask(MultiThreadAccessTest.createTask5(id2));
        int oldID = 5;
        int newID = 100;
        threadPool.runTask(MultiThreadAccessTest.createTask6(oldID, newID));
        threadPool.join();
        Statement statement = MultiThreadAccessTest.conn.createStatement();
        String selectQuery = ("SELECT * FROM " + (DatabaseCreator.TEST_TABLE1)) + " WHERE id=";
        ResultSet result = statement.executeQuery((selectQuery + id1));
        TestCase.assertFalse("The record was not deleted", result.next());
        result = statement.executeQuery((selectQuery + id2));
        TestCase.assertTrue("The record was not inserted", result.next());
        TestCase.assertEquals("Wrong value of field1", ((DatabaseCreator.defaultString) + id2), result.getString("field1"));
        // TODO getBigDecimal is not supported
        TestCase.assertEquals("Wrong value of field2", Integer.valueOf(id2).intValue(), result.getInt("field2"));
        TestCase.assertEquals("Wrong value of field3", Integer.valueOf(id2).intValue(), result.getInt("field3"));
        result.close();
        result = statement.executeQuery((selectQuery + oldID));
        TestCase.assertFalse("The record was not deleted", result.next());
        result.close();
        result = statement.executeQuery((selectQuery + newID));
        TestCase.assertTrue("The record was not updated", result.next());
        TestCase.assertEquals("Wrong value of field1", ((DatabaseCreator.defaultString) + newID), result.getString("field1"));
        // TODO getBigDecimal is not supported
        TestCase.assertEquals("Wrong value of field2", Integer.valueOf(newID).intValue(), result.getInt("field2"));
        TestCase.assertEquals("Wrong value of field3", Integer.valueOf(newID).intValue(), result.getInt("field3"));
        result.close();
    }

    /**
     * A few threads execute update operation in the same time for one tables in
     * the database. Number of threads is defined by numThreads variable
     *
     * @throws SQLException
     * 		
     */
    public void test_MultipleUpdatesInOneTables() throws SQLException {
        int id = 1;
        String field = "field3";
        String selectQuery = (("SELECT * FROM " + (DatabaseCreator.TEST_TABLE1)) + " WHERE id=") + id;
        Statement statement = MultiThreadAccessTest.conn.createStatement();
        ResultSet result = statement.executeQuery(selectQuery);
        TestCase.assertTrue(("There is no records with id = " + id), result.next());
        // TODO getBigDecimal is not supported
        // assertEquals("Wrong value of field " + field, BigDecimal.valueOf(id),
        // result.getBigDecimal(field));
        result.close();
        for (int i = 0; i < (MultiThreadAccessTest.numThreads); i++) {
            threadPool.runTask(MultiThreadAccessTest.createTask7(id, field));
        }
        threadPool.join();
        double expectedVal = id + (MultiThreadAccessTest.numThreads);
        result = statement.executeQuery(selectQuery);
        TestCase.assertTrue(("There is no records with id = " + id), result.next());
        // TODO getBigDecimal is not supported ->
        // assertEquals("Wrong value of field " + field, expectedVal, result
        // .getBigDecimal(field).doubleValue());
        result.close();
    }
}

