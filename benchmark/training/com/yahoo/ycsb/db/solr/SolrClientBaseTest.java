/**
 * Copyright (c) 2016 YCSB contributors. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db.solr;


import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.workloads.CoreWorkload;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import org.apache.solr.cloud.MiniSolrCloudCluster;
import org.junit.Assert;
import org.junit.Test;


public abstract class SolrClientBaseTest {
    protected static MiniSolrCloudCluster miniSolrCloudCluster;

    private DB instance;

    private static final HashMap<String, ByteIterator> MOCK_DATA;

    protected static final String MOCK_TABLE = "ycsb";

    private static final String MOCK_KEY0 = "0";

    private static final String MOCK_KEY1 = "1";

    private static final int NUM_RECORDS = 10;

    private static final String FIELD_PREFIX = CoreWorkload.FIELD_NAME_PREFIX_DEFAULT;

    static {
        MOCK_DATA = new HashMap(SolrClientBaseTest.NUM_RECORDS);
        for (int i = 0; i < (SolrClientBaseTest.NUM_RECORDS); i++) {
            SolrClientBaseTest.MOCK_DATA.put(((SolrClientBaseTest.FIELD_PREFIX) + i), new StringByteIterator(("value" + i)));
        }
    }

    @Test
    public void testInsert() throws Exception {
        Status result = instance.insert(SolrClientBaseTest.MOCK_TABLE, SolrClientBaseTest.MOCK_KEY0, SolrClientBaseTest.MOCK_DATA);
        Assert.assertEquals(OK, result);
    }

    @Test
    public void testDelete() throws Exception {
        Status result = instance.delete(SolrClientBaseTest.MOCK_TABLE, SolrClientBaseTest.MOCK_KEY1);
        Assert.assertEquals(OK, result);
    }

    @Test
    public void testRead() throws Exception {
        Set<String> fields = SolrClientBaseTest.MOCK_DATA.keySet();
        HashMap<String, ByteIterator> resultParam = new HashMap<>(SolrClientBaseTest.NUM_RECORDS);
        Status result = instance.read(SolrClientBaseTest.MOCK_TABLE, SolrClientBaseTest.MOCK_KEY1, fields, resultParam);
        Assert.assertEquals(OK, result);
    }

    @Test
    public void testUpdate() throws Exception {
        HashMap<String, ByteIterator> newValues = new HashMap<>(SolrClientBaseTest.NUM_RECORDS);
        for (int i = 0; i < (SolrClientBaseTest.NUM_RECORDS); i++) {
            newValues.put(((SolrClientBaseTest.FIELD_PREFIX) + i), new StringByteIterator(("newvalue" + i)));
        }
        Status result = instance.update(SolrClientBaseTest.MOCK_TABLE, SolrClientBaseTest.MOCK_KEY1, newValues);
        Assert.assertEquals(OK, result);
        // validate that the values changed
        HashMap<String, ByteIterator> resultParam = new HashMap<>(SolrClientBaseTest.NUM_RECORDS);
        instance.read(SolrClientBaseTest.MOCK_TABLE, SolrClientBaseTest.MOCK_KEY1, SolrClientBaseTest.MOCK_DATA.keySet(), resultParam);
        for (int i = 0; i < (SolrClientBaseTest.NUM_RECORDS); i++) {
            Assert.assertEquals(("newvalue" + i), resultParam.get(((SolrClientBaseTest.FIELD_PREFIX) + i)).toString());
        }
    }

    @Test
    public void testScan() throws Exception {
        Set<String> fields = SolrClientBaseTest.MOCK_DATA.keySet();
        Vector<HashMap<String, ByteIterator>> resultParam = new Vector<>(SolrClientBaseTest.NUM_RECORDS);
        Status result = instance.scan(SolrClientBaseTest.MOCK_TABLE, SolrClientBaseTest.MOCK_KEY1, SolrClientBaseTest.NUM_RECORDS, fields, resultParam);
        Assert.assertEquals(OK, result);
    }
}

