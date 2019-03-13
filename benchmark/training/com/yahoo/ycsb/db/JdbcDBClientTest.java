/**
 * Copyright (c) 2015 - 2016 Yahoo! Inc., 2016 YCSB contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db;


import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.StringByteIterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.junit.Assert;
import org.junit.Test;


public class JdbcDBClientTest {
    private static final String TEST_DB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    private static final String TEST_DB_URL = "jdbc:hsqldb:mem:ycsb";

    private static final String TEST_DB_USER = "sa";

    private static final String TABLE_NAME = "USERTABLE";

    private static final int FIELD_LENGTH = 32;

    private static final String FIELD_PREFIX = "FIELD";

    private static final String KEY_PREFIX = "user";

    private static final String KEY_FIELD = "YCSB_KEY";

    private static final int NUM_FIELDS = 3;

    private static Connection jdbcConnection = null;

    private static JdbcDBClient jdbcDBClient = null;

    @Test
    public void insertTest() {
        try {
            String insertKey = "user0";
            HashMap<String, ByteIterator> insertMap = insertRow(insertKey);
            ResultSet resultSet = JdbcDBClientTest.jdbcConnection.prepareStatement(String.format("SELECT * FROM %s", JdbcDBClientTest.TABLE_NAME)).executeQuery();
            // Check we have a result Row
            Assert.assertTrue(resultSet.next());
            // Check that all the columns have expected values
            Assert.assertEquals(resultSet.getString(JdbcDBClientTest.KEY_FIELD), insertKey);
            for (int i = 0; i < 3; i++) {
                Assert.assertEquals(resultSet.getString(((JdbcDBClientTest.FIELD_PREFIX) + i)), insertMap.get(((JdbcDBClientTest.FIELD_PREFIX) + i)).toString());
            }
            // Check that we do not have any more rows
            Assert.assertFalse(resultSet.next());
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail("Failed insertTest");
        }
    }

    @Test
    public void updateTest() {
        try {
            String preupdateString = "preupdate";
            StringBuilder fauxInsertString = new StringBuilder(String.format("INSERT INTO %s VALUES(?", JdbcDBClientTest.TABLE_NAME));
            for (int i = 0; i < (JdbcDBClientTest.NUM_FIELDS); i++) {
                fauxInsertString.append(",?");
            }
            fauxInsertString.append(")");
            PreparedStatement fauxInsertStatement = JdbcDBClientTest.jdbcConnection.prepareStatement(fauxInsertString.toString());
            for (int i = 2; i < ((JdbcDBClientTest.NUM_FIELDS) + 2); i++) {
                fauxInsertStatement.setString(i, preupdateString);
            }
            fauxInsertStatement.setString(1, "user0");
            fauxInsertStatement.execute();
            fauxInsertStatement.setString(1, "user1");
            fauxInsertStatement.execute();
            fauxInsertStatement.setString(1, "user2");
            fauxInsertStatement.execute();
            HashMap<String, ByteIterator> updateMap = new HashMap<String, ByteIterator>();
            for (int i = 0; i < 3; i++) {
                updateMap.put(((JdbcDBClientTest.FIELD_PREFIX) + i), new StringByteIterator(buildDeterministicValue("user1", ((JdbcDBClientTest.FIELD_PREFIX) + i))));
            }
            JdbcDBClientTest.jdbcDBClient.update(JdbcDBClientTest.TABLE_NAME, "user1", updateMap);
            ResultSet resultSet = JdbcDBClientTest.jdbcConnection.prepareStatement(String.format("SELECT * FROM %s ORDER BY %s", JdbcDBClientTest.TABLE_NAME, JdbcDBClientTest.KEY_FIELD)).executeQuery();
            // Ensure that user0 record was not changed
            resultSet.next();
            Assert.assertEquals("Assert first row key is user0", resultSet.getString(JdbcDBClientTest.KEY_FIELD), "user0");
            for (int i = 0; i < 3; i++) {
                Assert.assertEquals("Assert first row fields contain preupdateString", resultSet.getString(((JdbcDBClientTest.FIELD_PREFIX) + i)), preupdateString);
            }
            // Check that all the columns have expected values for user1 record
            resultSet.next();
            Assert.assertEquals(resultSet.getString(JdbcDBClientTest.KEY_FIELD), "user1");
            for (int i = 0; i < 3; i++) {
                Assert.assertEquals(resultSet.getString(((JdbcDBClientTest.FIELD_PREFIX) + i)), updateMap.get(((JdbcDBClientTest.FIELD_PREFIX) + i)).toString());
            }
            // Ensure that user2 record was not changed
            resultSet.next();
            Assert.assertEquals("Assert third row key is user2", resultSet.getString(JdbcDBClientTest.KEY_FIELD), "user2");
            for (int i = 0; i < 3; i++) {
                Assert.assertEquals("Assert third row fields contain preupdateString", resultSet.getString(((JdbcDBClientTest.FIELD_PREFIX) + i)), preupdateString);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail("Failed updateTest");
        }
    }

    @Test
    public void readTest() {
        String insertKey = "user0";
        HashMap<String, ByteIterator> insertMap = insertRow(insertKey);
        Set<String> readFields = new HashSet<String>();
        HashMap<String, ByteIterator> readResultMap = new HashMap<String, ByteIterator>();
        // Test reading a single field
        readFields.add("FIELD0");
        JdbcDBClientTest.jdbcDBClient.read(JdbcDBClientTest.TABLE_NAME, insertKey, readFields, readResultMap);
        Assert.assertEquals("Assert that result has correct number of fields", readFields.size(), readResultMap.size());
        for (String field : readFields) {
            Assert.assertEquals((("Assert " + field) + " was read correctly"), insertMap.get(field).toString(), readResultMap.get(field).toString());
        }
        readResultMap = new HashMap<String, ByteIterator>();
        // Test reading all fields
        readFields.add("FIELD1");
        readFields.add("FIELD2");
        JdbcDBClientTest.jdbcDBClient.read(JdbcDBClientTest.TABLE_NAME, insertKey, readFields, readResultMap);
        Assert.assertEquals("Assert that result has correct number of fields", readFields.size(), readResultMap.size());
        for (String field : readFields) {
            Assert.assertEquals((("Assert " + field) + " was read correctly"), insertMap.get(field).toString(), readResultMap.get(field).toString());
        }
    }

    @Test
    public void deleteTest() {
        try {
            insertRow("user0");
            String deleteKey = "user1";
            insertRow(deleteKey);
            insertRow("user2");
            JdbcDBClientTest.jdbcDBClient.delete(JdbcDBClientTest.TABLE_NAME, deleteKey);
            ResultSet resultSet = JdbcDBClientTest.jdbcConnection.prepareStatement(String.format("SELECT * FROM %s", JdbcDBClientTest.TABLE_NAME)).executeQuery();
            int totalRows = 0;
            while (resultSet.next()) {
                Assert.assertNotEquals("Assert this is not the deleted row key", deleteKey, resultSet.getString(JdbcDBClientTest.KEY_FIELD));
                totalRows++;
            } 
            // Check we do not have a result Row
            Assert.assertEquals("Assert we ended with the correct number of rows", totalRows, 2);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail("Failed deleteTest");
        }
    }

    @Test
    public void scanTest() throws SQLException {
        Map<String, HashMap<String, ByteIterator>> keyMap = new HashMap<String, HashMap<String, ByteIterator>>();
        for (int i = 0; i < 5; i++) {
            String insertKey = (JdbcDBClientTest.KEY_PREFIX) + i;
            keyMap.put(insertKey, insertRow(insertKey));
        }
        Set<String> fieldSet = new HashSet<String>();
        fieldSet.add("FIELD0");
        fieldSet.add("FIELD1");
        int startIndex = 1;
        int resultRows = 3;
        Vector<HashMap<String, ByteIterator>> resultVector = new Vector<HashMap<String, ByteIterator>>();
        JdbcDBClientTest.jdbcDBClient.scan(JdbcDBClientTest.TABLE_NAME, ((JdbcDBClientTest.KEY_PREFIX) + startIndex), resultRows, fieldSet, resultVector);
        // Check the resultVector is the correct size
        Assert.assertEquals("Assert the correct number of results rows were returned", resultRows, resultVector.size());
        // Check each vector row to make sure we have the correct fields
        int testIndex = startIndex;
        for (Map<String, ByteIterator> result : resultVector) {
            Assert.assertEquals("Assert that this row has the correct number of fields", fieldSet.size(), result.size());
            for (String field : fieldSet) {
                Assert.assertEquals("Assert this field is correct in this row", keyMap.get(((JdbcDBClientTest.KEY_PREFIX) + testIndex)).get(field).toString(), result.get(field).toString());
            }
            testIndex++;
        }
    }

    @Test
    public void insertBatchTest() throws DBException {
        insertBatchTest(20);
    }

    @Test
    public void insertPartialBatchTest() throws DBException {
        insertBatchTest(19);
    }
}

