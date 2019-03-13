/**
 * Copyright (c) 2017 YCSB contributors. All rights reserved.
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
package com.yahoo.ycsb.db.elasticsearch5;


import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.workloads.CoreWorkload;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import org.junit.Assert;
import org.junit.Test;


public abstract class ElasticsearchIntegTestBase {
    private DB db;

    private static final HashMap<String, ByteIterator> MOCK_DATA;

    private static final String MOCK_TABLE = "MOCK_TABLE";

    private static final String FIELD_PREFIX = CoreWorkload.FIELD_NAME_PREFIX_DEFAULT;

    static {
        MOCK_DATA = new HashMap(10);
        for (int i = 1; i <= 10; i++) {
            ElasticsearchIntegTestBase.MOCK_DATA.put(((ElasticsearchIntegTestBase.FIELD_PREFIX) + i), new StringByteIterator(("value" + i)));
        }
    }

    @Test
    public void testInsert() {
        final Status result = db.insert(ElasticsearchIntegTestBase.MOCK_TABLE, "0", ElasticsearchIntegTestBase.MOCK_DATA);
        Assert.assertEquals(OK, result);
    }

    /**
     * Test of delete method, of class ElasticsearchClient.
     */
    @Test
    public void testDelete() {
        final Status result = db.delete(ElasticsearchIntegTestBase.MOCK_TABLE, "1");
        Assert.assertEquals(OK, result);
    }

    /**
     * Test of read method, of class ElasticsearchClient.
     */
    @Test
    public void testRead() {
        final Set<String> fields = ElasticsearchIntegTestBase.MOCK_DATA.keySet();
        final HashMap<String, ByteIterator> resultParam = new HashMap<>(10);
        final Status result = db.read(ElasticsearchIntegTestBase.MOCK_TABLE, "1", fields, resultParam);
        Assert.assertEquals(OK, result);
    }

    /**
     * Test of update method, of class ElasticsearchClient.
     */
    @Test
    public void testUpdate() {
        final HashMap<String, ByteIterator> newValues = new HashMap<>(10);
        for (int i = 1; i <= 10; i++) {
            newValues.put(((ElasticsearchIntegTestBase.FIELD_PREFIX) + i), new StringByteIterator(("newvalue" + i)));
        }
        final Status updateResult = db.update(ElasticsearchIntegTestBase.MOCK_TABLE, "1", newValues);
        Assert.assertEquals(OK, updateResult);
        // validate that the values changed
        final HashMap<String, ByteIterator> resultParam = new HashMap<>(10);
        final Status readResult = db.read(ElasticsearchIntegTestBase.MOCK_TABLE, "1", ElasticsearchIntegTestBase.MOCK_DATA.keySet(), resultParam);
        Assert.assertEquals(OK, readResult);
        for (int i = 1; i <= 10; i++) {
            Assert.assertEquals(("newvalue" + i), resultParam.get(((ElasticsearchIntegTestBase.FIELD_PREFIX) + i)).toString());
        }
    }

    /**
     * Test of scan method, of class ElasticsearchClient.
     */
    @Test
    public void testScan() {
        final int recordcount = 10;
        final Set<String> fields = ElasticsearchIntegTestBase.MOCK_DATA.keySet();
        final Vector<HashMap<String, ByteIterator>> resultParam = new Vector<>(10);
        final Status result = db.scan(ElasticsearchIntegTestBase.MOCK_TABLE, "1", recordcount, fields, resultParam);
        Assert.assertEquals(OK, result);
        Assert.assertEquals(10, resultParam.size());
    }
}

