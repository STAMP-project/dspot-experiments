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
import java.util.Vector;
import java.util.logging.Logger;
import junit.framework.TestCase;
import tests.support.DatabaseCreator;
import tests.support.Support_SQL;
import tests.support.ThreadPool;


public class StressTest extends TestCase {
    Vector<Connection> vc = new Vector<Connection>();

    private static Connection conn;

    private static Statement statement;

    // /**
    // * @see junit.framework.TestCase#setUp()
    // */
    // @Override
    // protected void setUp() throws Exception {
    // super.setUp();
    // vc.clear();
    // }
    // 
    // /**
    // * @see junit.framework.TestCase#tearDown()
    // */
    // @Override
    // protected void tearDown() throws Exception {
    // closeConnections();
    // statement.execute("DELETE FROM " + DatabaseCreator.TEST_TABLE2);
    // super.tearDown();
    // }
    /**
     * StressTest#testManyConnectionsUsingOneThread(). Create many
     *        connections to the DataBase using one thread.
     */
    public void testManyConnectionsUsingOneThread() {
        try {
            int maxConnections = getConnectionNum();
            openConnections(maxConnections);
            TestCase.assertEquals("Incorrect number of created connections", maxConnections, vc.size());
        } catch (Exception e) {
            TestCase.fail(("Unexpected Exception " + (e.toString())));
        }
    }

    /**
     * StressTest#testManyConnectionsUsingManyThreads(). Create many
     *        connections to the DataBase using some threads.
     */
    public void testManyConnectionsUsingManyThreads() {
        int numTasks = getConnectionNum();
        ThreadPool threadPool = new ThreadPool(numTasks);
        // run example tasks
        for (int i = 0; i < numTasks; i++) {
            threadPool.runTask(createTask(i));
        }
        // close the pool and wait for all tasks to finish.
        threadPool.join();
        TestCase.assertEquals("Unable to create a connection", numTasks, vc.size());
        if (numTasks != (Support_SQL.sqlMaxConnections)) {
            try {
                // try to create connection n + 1
                Connection c = Support_SQL.getConnection();
                c.close();
                TestCase.fail((("It is possible to create more than " + numTasks) + "connections"));
            } catch (SQLException sql) {
                // expected
            }
        }
    }

    /**
     * StressTest#testInsertOfManyRowsUsingOneThread(). Insert a lot of
     *        records to the Database using a maximum number of connections.
     */
    public void testInsertOfManyRowsUsingOneThread() {
        Logger.global.info("java.sql stress test: single thread and many operations.");
        int maxConnections = getConnectionNum();
        Logger.global.info(((("Opening " + maxConnections) + " to database ") + (Support_SQL.getFilename())));
        openConnections(maxConnections);
        int tasksPerConnection = (Support_SQL.sqlMaxTasks) / maxConnections;
        Logger.global.info(((((("TasksPerConnection =  " + (Support_SQL.sqlMaxTasks)) + " by (maxConnections) ") + maxConnections) + " = ") + tasksPerConnection));
        int pk = 1;
        for (int i = 0; i < (vc.size()); ++i) {
            Logger.global.info((((" creating " + tasksPerConnection) + "tasks for Connection ") + i));
            Connection c = vc.elementAt(i);
            for (int j = 0; j < tasksPerConnection; ++j) {
                insertNewRecord(c, (pk++));
            }
        }
        try {
            ResultSet rs = StressTest.statement.executeQuery(("SELECT COUNT(*) as counter FROM " + (DatabaseCreator.TEST_TABLE2)));
            TestCase.assertTrue("RecordSet is empty", rs.next());
            TestCase.assertEquals("Incorrect number of records", (tasksPerConnection * maxConnections), rs.getInt("counter"));
            rs.close();
        } catch (SQLException sql) {
            TestCase.fail(("Unexpected SQLException " + (sql.toString())));
        }
    }

    /**
     *
     *
     * @unknown 
     */
    public void testInsertOfManyRowsUsingManyThreads() {
        Logger.global.info("java.sql stress test: multiple threads and many operations.");
        int numConnections = getConnectionNum();
        int tasksPerConnection = (Support_SQL.sqlMaxTasks) / numConnections;
        Logger.global.info(((("Opening " + numConnections) + " to database ") + (Support_SQL.getFilename())));
        ThreadPool threadPool = new ThreadPool(numConnections);
        for (int i = 0; i < numConnections; ++i) {
            Logger.global.info((((" creating " + tasksPerConnection) + " tasks for Connection ") + i));
            threadPool.runTask(insertTask(numConnections, i));
        }
        // close the pool and wait for all tasks to finish.
        threadPool.join();
        TestCase.assertEquals("Unable to create a connection", numConnections, vc.size());
        try {
            ResultSet rs = StressTest.statement.executeQuery(("SELECT COUNT(*) as counter FROM " + (DatabaseCreator.TEST_TABLE2)));
            TestCase.assertTrue("RecordSet is empty", rs.next());
            TestCase.assertEquals("Incorrect number of records", (tasksPerConnection * numConnections), rs.getInt("counter"));
            rs.close();
        } catch (SQLException sql) {
            TestCase.fail(("Unexpected SQLException " + (sql.toString())));
        }
    }
}

