/**
 * Copyright (c) 2014, Yahoo!, Inc. All rights reserved.
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


import Status.NOT_FOUND;
import Status.OK;
import com.yahoo.ycsb.ByteArrayByteIterator;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Status;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * MongoDbClientTest provides runs the basic DB test cases.
 * <p>
 * The tests will be skipped if MongoDB is not running on port 27017 on the
 * local machine. See the README.md for how to get MongoDB running.
 * </p>
 */
@SuppressWarnings("boxing")
public abstract class AbstractDBTestCases {
    /**
     * The default port for MongoDB.
     */
    private static final int MONGODB_DEFAULT_PORT = 27017;

    /**
     * Test method for {@link DB#insert}, {@link DB#read}, and {@link DB#delete} .
     */
    @Test
    public void testInsertReadDelete() {
        final DB client = getDB();
        final String table = getClass().getSimpleName();
        final String id = "delete";
        HashMap<String, ByteIterator> inserted = new HashMap<String, ByteIterator>();
        inserted.put("a", new ByteArrayByteIterator(new byte[]{ 1, 2, 3, 4 }));
        Status result = client.insert(table, id, inserted);
        Assert.assertThat("Insert did not return success (0).", result, CoreMatchers.is(OK));
        HashMap<String, ByteIterator> read = new HashMap<String, ByteIterator>();
        Set<String> keys = Collections.singleton("a");
        result = client.read(table, id, keys, read);
        Assert.assertThat("Read did not return success (0).", result, CoreMatchers.is(OK));
        for (String key : keys) {
            ByteIterator iter = read.get(key);
            Assert.assertThat(("Did not read the inserted field: " + key), iter, CoreMatchers.notNullValue());
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (1)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (2)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (3)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (4)))));
            Assert.assertFalse(iter.hasNext());
        }
        result = client.delete(table, id);
        Assert.assertThat("Delete did not return success (0).", result, CoreMatchers.is(OK));
        read.clear();
        result = client.read(table, id, null, read);
        Assert.assertThat("Read, after delete, did not return not found (1).", result, CoreMatchers.is(NOT_FOUND));
        Assert.assertThat("Found the deleted fields.", read.size(), CoreMatchers.is(0));
        result = client.delete(table, id);
        Assert.assertThat("Delete did not return not found (1).", result, CoreMatchers.is(NOT_FOUND));
    }

    /**
     * Test method for {@link DB#insert}, {@link DB#read}, and {@link DB#update} .
     */
    @Test
    public void testInsertReadUpdate() {
        DB client = getDB();
        final String table = getClass().getSimpleName();
        final String id = "update";
        HashMap<String, ByteIterator> inserted = new HashMap<String, ByteIterator>();
        inserted.put("a", new ByteArrayByteIterator(new byte[]{ 1, 2, 3, 4 }));
        Status result = client.insert(table, id, inserted);
        Assert.assertThat("Insert did not return success (0).", result, CoreMatchers.is(OK));
        HashMap<String, ByteIterator> read = new HashMap<String, ByteIterator>();
        Set<String> keys = Collections.singleton("a");
        result = client.read(table, id, keys, read);
        Assert.assertThat("Read did not return success (0).", result, CoreMatchers.is(OK));
        for (String key : keys) {
            ByteIterator iter = read.get(key);
            Assert.assertThat(("Did not read the inserted field: " + key), iter, CoreMatchers.notNullValue());
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (1)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (2)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (3)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (4)))));
            Assert.assertFalse(iter.hasNext());
        }
        HashMap<String, ByteIterator> updated = new HashMap<String, ByteIterator>();
        updated.put("a", new ByteArrayByteIterator(new byte[]{ 5, 6, 7, 8 }));
        result = client.update(table, id, updated);
        Assert.assertThat("Update did not return success (0).", result, CoreMatchers.is(OK));
        read.clear();
        result = client.read(table, id, null, read);
        Assert.assertThat("Read, after update, did not return success (0).", result, CoreMatchers.is(OK));
        for (String key : keys) {
            ByteIterator iter = read.get(key);
            Assert.assertThat(("Did not read the inserted field: " + key), iter, CoreMatchers.notNullValue());
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (5)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (6)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (7)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (8)))));
            Assert.assertFalse(iter.hasNext());
        }
    }

    /**
     * Test method for {@link DB#insert}, {@link DB#read}, and {@link DB#update} .
     */
    @Test
    public void testInsertReadUpdateWithUpsert() {
        Properties props = new Properties();
        props.setProperty("mongodb.upsert", "true");
        DB client = getDB(props);
        final String table = getClass().getSimpleName();
        final String id = "updateWithUpsert";
        HashMap<String, ByteIterator> inserted = new HashMap<String, ByteIterator>();
        inserted.put("a", new ByteArrayByteIterator(new byte[]{ 1, 2, 3, 4 }));
        Status result = client.insert(table, id, inserted);
        Assert.assertThat("Insert did not return success (0).", result, CoreMatchers.is(OK));
        HashMap<String, ByteIterator> read = new HashMap<String, ByteIterator>();
        Set<String> keys = Collections.singleton("a");
        result = client.read(table, id, keys, read);
        Assert.assertThat("Read did not return success (0).", result, CoreMatchers.is(OK));
        for (String key : keys) {
            ByteIterator iter = read.get(key);
            Assert.assertThat(("Did not read the inserted field: " + key), iter, CoreMatchers.notNullValue());
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (1)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (2)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (3)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (4)))));
            Assert.assertFalse(iter.hasNext());
        }
        HashMap<String, ByteIterator> updated = new HashMap<String, ByteIterator>();
        updated.put("a", new ByteArrayByteIterator(new byte[]{ 5, 6, 7, 8 }));
        result = client.update(table, id, updated);
        Assert.assertThat("Update did not return success (0).", result, CoreMatchers.is(OK));
        read.clear();
        result = client.read(table, id, null, read);
        Assert.assertThat("Read, after update, did not return success (0).", result, CoreMatchers.is(OK));
        for (String key : keys) {
            ByteIterator iter = read.get(key);
            Assert.assertThat(("Did not read the inserted field: " + key), iter, CoreMatchers.notNullValue());
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (5)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (6)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (7)))));
            Assert.assertTrue(iter.hasNext());
            Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (8)))));
            Assert.assertFalse(iter.hasNext());
        }
    }

    /**
     * Test method for {@link DB#scan}.
     */
    @Test
    public void testScan() {
        final DB client = getDB();
        final String table = getClass().getSimpleName();
        // Insert a bunch of documents.
        for (int i = 0; i < 100; ++i) {
            HashMap<String, ByteIterator> inserted = new HashMap<String, ByteIterator>();
            inserted.put("a", new ByteArrayByteIterator(new byte[]{ ((byte) (i & 255)), ((byte) ((i >> 8) & 255)), ((byte) ((i >> 16) & 255)), ((byte) ((i >> 24) & 255)) }));
            Status result = client.insert(table, padded(i), inserted);
            Assert.assertThat("Insert did not return success (0).", result, CoreMatchers.is(OK));
        }
        Set<String> keys = Collections.singleton("a");
        Vector<HashMap<String, ByteIterator>> results = new Vector<HashMap<String, ByteIterator>>();
        Status result = client.scan(table, "00050", 5, null, results);
        Assert.assertThat("Read did not return success (0).", result, CoreMatchers.is(OK));
        Assert.assertThat(results.size(), CoreMatchers.is(5));
        for (int i = 0; i < 5; ++i) {
            Map<String, ByteIterator> read = results.get(i);
            for (String key : keys) {
                ByteIterator iter = read.get(key);
                Assert.assertThat(("Did not read the inserted field: " + key), iter, CoreMatchers.notNullValue());
                Assert.assertTrue(iter.hasNext());
                Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) ((i + 50) & 255)))));
                Assert.assertTrue(iter.hasNext());
                Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (((i + 50) >> 8) & 255)))));
                Assert.assertTrue(iter.hasNext());
                Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (((i + 50) >> 16) & 255)))));
                Assert.assertTrue(iter.hasNext());
                Assert.assertThat(iter.nextByte(), CoreMatchers.is(Byte.valueOf(((byte) (((i + 50) >> 24) & 255)))));
                Assert.assertFalse(iter.hasNext());
            }
        }
    }
}

