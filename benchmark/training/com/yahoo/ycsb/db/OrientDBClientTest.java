/**
 * Copyright (c) 2015 - 2016 YCSB contributors. All rights reserved.
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


import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.dictionary.ODictionary;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.StringByteIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by kruthar on 12/29/15.
 */
public class OrientDBClientTest {
    // TODO: This must be copied because it is private in OrientDBClient, but this should defer to table property.
    private static final String CLASS = "usertable";

    private static final int FIELD_LENGTH = 32;

    private static final String FIELD_PREFIX = "FIELD";

    private static final String KEY_PREFIX = "user";

    private static final int NUM_FIELDS = 3;

    private static final String TEST_DB_URL = "memory:test";

    private static OrientDBClient orientDBClient = null;

    @Test
    public void insertTest() {
        String insertKey = "user0";
        Map<String, ByteIterator> insertMap = insertRow(insertKey);
        OPartitionedDatabasePool pool = OrientDBClientTest.orientDBClient.getDatabasePool();
        try (ODatabaseDocumentTx db = pool.acquire()) {
            ODictionary<ORecord> dictionary = db.getDictionary();
            ODocument result = dictionary.get(insertKey);
            Assert.assertTrue("Assert a row was inserted.", (result != null));
            for (int i = 0; i < (OrientDBClientTest.NUM_FIELDS); i++) {
                Assert.assertEquals("Assert all inserted columns have correct values.", result.field(((OrientDBClientTest.FIELD_PREFIX) + i)), insertMap.get(((OrientDBClientTest.FIELD_PREFIX) + i)).toString());
            }
        }
    }

    @Test
    public void updateTest() {
        String preupdateString = "preupdate";
        String user0 = "user0";
        String user1 = "user1";
        String user2 = "user2";
        OPartitionedDatabasePool pool = OrientDBClientTest.orientDBClient.getDatabasePool();
        try (ODatabaseDocumentTx db = pool.acquire()) {
            // Manually insert three documents
            for (String key : Arrays.asList(user0, user1, user2)) {
                ODocument doc = new ODocument(OrientDBClientTest.CLASS);
                for (int i = 0; i < (OrientDBClientTest.NUM_FIELDS); i++) {
                    doc.field(((OrientDBClientTest.FIELD_PREFIX) + i), preupdateString);
                }
                doc.save();
                ODictionary<ORecord> dictionary = db.getDictionary();
                dictionary.put(key, doc);
            }
        }
        HashMap<String, ByteIterator> updateMap = new HashMap<>();
        for (int i = 0; i < (OrientDBClientTest.NUM_FIELDS); i++) {
            updateMap.put(((OrientDBClientTest.FIELD_PREFIX) + i), new StringByteIterator(buildDeterministicValue(user1, ((OrientDBClientTest.FIELD_PREFIX) + i))));
        }
        OrientDBClientTest.orientDBClient.update(OrientDBClientTest.CLASS, user1, updateMap);
        try (ODatabaseDocumentTx db = pool.acquire()) {
            ODictionary<ORecord> dictionary = db.getDictionary();
            // Ensure that user0 record was not changed
            ODocument result = dictionary.get(user0);
            for (int i = 0; i < (OrientDBClientTest.NUM_FIELDS); i++) {
                Assert.assertEquals("Assert first row fields contain preupdateString", result.field(((OrientDBClientTest.FIELD_PREFIX) + i)), preupdateString);
            }
            // Check that all the columns have expected values for user1 record
            result = dictionary.get(user1);
            for (int i = 0; i < (OrientDBClientTest.NUM_FIELDS); i++) {
                Assert.assertEquals("Assert updated row fields are correct", result.field(((OrientDBClientTest.FIELD_PREFIX) + i)), updateMap.get(((OrientDBClientTest.FIELD_PREFIX) + i)).toString());
            }
            // Ensure that user2 record was not changed
            result = dictionary.get(user2);
            for (int i = 0; i < (OrientDBClientTest.NUM_FIELDS); i++) {
                Assert.assertEquals("Assert third row fields contain preupdateString", result.field(((OrientDBClientTest.FIELD_PREFIX) + i)), preupdateString);
            }
        }
    }

    @Test
    public void readTest() {
        String insertKey = "user0";
        Map<String, ByteIterator> insertMap = insertRow(insertKey);
        HashSet<String> readFields = new HashSet<>();
        HashMap<String, ByteIterator> readResultMap = new HashMap<>();
        // Test reading a single field
        readFields.add("FIELD0");
        OrientDBClientTest.orientDBClient.read(OrientDBClientTest.CLASS, insertKey, readFields, readResultMap);
        Assert.assertEquals("Assert that result has correct number of fields", readFields.size(), readResultMap.size());
        for (String field : readFields) {
            Assert.assertEquals((("Assert " + field) + " was read correctly"), insertMap.get(field).toString(), readResultMap.get(field).toString());
        }
        readResultMap = new HashMap();
        // Test reading all fields
        readFields.add("FIELD1");
        readFields.add("FIELD2");
        OrientDBClientTest.orientDBClient.read(OrientDBClientTest.CLASS, insertKey, readFields, readResultMap);
        Assert.assertEquals("Assert that result has correct number of fields", readFields.size(), readResultMap.size());
        for (String field : readFields) {
            Assert.assertEquals((("Assert " + field) + " was read correctly"), insertMap.get(field).toString(), readResultMap.get(field).toString());
        }
    }

    @Test
    public void deleteTest() {
        String user0 = "user0";
        String user1 = "user1";
        String user2 = "user2";
        insertRow(user0);
        insertRow(user1);
        insertRow(user2);
        OrientDBClientTest.orientDBClient.delete(OrientDBClientTest.CLASS, user1);
        OPartitionedDatabasePool pool = OrientDBClientTest.orientDBClient.getDatabasePool();
        try (ODatabaseDocumentTx db = pool.acquire()) {
            ODictionary<ORecord> dictionary = db.getDictionary();
            Assert.assertNotNull("Assert user0 still exists", dictionary.get(user0));
            Assert.assertNull("Assert user1 does not exist", dictionary.get(user1));
            Assert.assertNotNull("Assert user2 still exists", dictionary.get(user2));
        }
    }

    @Test
    public void scanTest() {
        Map<String, Map<String, ByteIterator>> keyMap = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            String insertKey = (OrientDBClientTest.KEY_PREFIX) + i;
            keyMap.put(insertKey, insertRow(insertKey));
        }
        Set<String> fieldSet = new HashSet<>();
        fieldSet.add("FIELD0");
        fieldSet.add("FIELD1");
        int startIndex = 0;
        int resultRows = 3;
        Vector<HashMap<String, ByteIterator>> resultVector = new Vector<>();
        OrientDBClientTest.orientDBClient.scan(OrientDBClientTest.CLASS, ((OrientDBClientTest.KEY_PREFIX) + startIndex), resultRows, fieldSet, resultVector);
        // Check the resultVector is the correct size
        Assert.assertEquals("Assert the correct number of results rows were returned", resultRows, resultVector.size());
        int testIndex = startIndex;
        // Check each vector row to make sure we have the correct fields
        for (HashMap<String, ByteIterator> result : resultVector) {
            Assert.assertEquals("Assert that this row has the correct number of fields", fieldSet.size(), result.size());
            for (String field : fieldSet) {
                Assert.assertEquals("Assert this field is correct in this row", keyMap.get(((OrientDBClientTest.KEY_PREFIX) + testIndex)).get(field).toString(), result.get(field).toString());
            }
            testIndex++;
        }
    }
}

