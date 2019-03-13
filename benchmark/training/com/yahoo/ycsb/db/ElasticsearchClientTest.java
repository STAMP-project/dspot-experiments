/**
 * Copyright (c) 2012-2017 YCSB contributors. All rights reserved.
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
/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yahoo.ycsb.db;


import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.workloads.CoreWorkload;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ElasticsearchClientTest {
    @ClassRule
    public static final TemporaryFolder temp = new TemporaryFolder();

    private static final ElasticsearchClient instance = new ElasticsearchClient();

    private static final HashMap<String, ByteIterator> MOCK_DATA;

    private static final String MOCK_TABLE = "MOCK_TABLE";

    private static final String MOCK_KEY0 = "0";

    private static final String MOCK_KEY1 = "1";

    private static final String MOCK_KEY2 = "2";

    private static final String FIELD_PREFIX = CoreWorkload.FIELD_NAME_PREFIX_DEFAULT;

    static {
        MOCK_DATA = new HashMap(10);
        for (int i = 1; i <= 10; i++) {
            ElasticsearchClientTest.MOCK_DATA.put(((ElasticsearchClientTest.FIELD_PREFIX) + i), new StringByteIterator(("value" + i)));
        }
    }

    /**
     * Test of insert method, of class ElasticsearchClient.
     */
    @Test
    public void testInsert() {
        Status result = ElasticsearchClientTest.instance.insert(ElasticsearchClientTest.MOCK_TABLE, ElasticsearchClientTest.MOCK_KEY0, ElasticsearchClientTest.MOCK_DATA);
        Assert.assertEquals(OK, result);
    }

    /**
     * Test of delete method, of class ElasticsearchClient.
     */
    @Test
    public void testDelete() {
        Status result = ElasticsearchClientTest.instance.delete(ElasticsearchClientTest.MOCK_TABLE, ElasticsearchClientTest.MOCK_KEY1);
        Assert.assertEquals(OK, result);
    }

    /**
     * Test of read method, of class ElasticsearchClient.
     */
    @Test
    public void testRead() {
        Set<String> fields = ElasticsearchClientTest.MOCK_DATA.keySet();
        HashMap<String, ByteIterator> resultParam = new HashMap<>(10);
        Status result = ElasticsearchClientTest.instance.read(ElasticsearchClientTest.MOCK_TABLE, ElasticsearchClientTest.MOCK_KEY1, fields, resultParam);
        Assert.assertEquals(OK, result);
    }

    /**
     * Test of update method, of class ElasticsearchClient.
     */
    @Test
    public void testUpdate() {
        int i;
        HashMap<String, ByteIterator> newValues = new HashMap<>(10);
        for (i = 1; i <= 10; i++) {
            newValues.put(((ElasticsearchClientTest.FIELD_PREFIX) + i), new StringByteIterator(("newvalue" + i)));
        }
        Status result = ElasticsearchClientTest.instance.update(ElasticsearchClientTest.MOCK_TABLE, ElasticsearchClientTest.MOCK_KEY1, newValues);
        Assert.assertEquals(OK, result);
        // validate that the values changed
        HashMap<String, ByteIterator> resultParam = new HashMap<>(10);
        ElasticsearchClientTest.instance.read(ElasticsearchClientTest.MOCK_TABLE, ElasticsearchClientTest.MOCK_KEY1, ElasticsearchClientTest.MOCK_DATA.keySet(), resultParam);
        for (i = 1; i <= 10; i++) {
            Assert.assertEquals(("newvalue" + i), resultParam.get(((ElasticsearchClientTest.FIELD_PREFIX) + i)).toString());
        }
    }

    /**
     * Test of scan method, of class ElasticsearchClient.
     */
    @Test
    public void testScan() {
        int recordcount = 10;
        Set<String> fields = ElasticsearchClientTest.MOCK_DATA.keySet();
        Vector<HashMap<String, ByteIterator>> resultParam = new Vector<>(10);
        Status result = ElasticsearchClientTest.instance.scan(ElasticsearchClientTest.MOCK_TABLE, ElasticsearchClientTest.MOCK_KEY1, recordcount, fields, resultParam);
        Assert.assertEquals(OK, result);
    }
}

