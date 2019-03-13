/**
 * Copyright (c) 2018 YCSB contributors. All rights reserved.
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
package com.yahoo.ycsb.db.rocksdb;


import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.workloads.CoreWorkload;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RocksDBClientTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private static final String MOCK_TABLE = "ycsb";

    private static final String MOCK_KEY0 = "0";

    private static final String MOCK_KEY1 = "1";

    private static final String MOCK_KEY2 = "2";

    private static final String MOCK_KEY3 = "3";

    private static final int NUM_RECORDS = 10;

    private static final String FIELD_PREFIX = CoreWorkload.FIELD_NAME_PREFIX_DEFAULT;

    private static final Map<String, ByteIterator> MOCK_DATA;

    static {
        MOCK_DATA = new HashMap(RocksDBClientTest.NUM_RECORDS);
        for (int i = 0; i < (RocksDBClientTest.NUM_RECORDS); i++) {
            RocksDBClientTest.MOCK_DATA.put(((RocksDBClientTest.FIELD_PREFIX) + i), new StringByteIterator(("value" + i)));
        }
    }

    private RocksDBClient instance;

    @Test
    public void insertAndRead() throws Exception {
        final Status insertResult = instance.insert(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY0, RocksDBClientTest.MOCK_DATA);
        Assert.assertEquals(OK, insertResult);
        final Set<String> fields = RocksDBClientTest.MOCK_DATA.keySet();
        final Map<String, ByteIterator> resultParam = new HashMap<>(RocksDBClientTest.NUM_RECORDS);
        final Status readResult = instance.read(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY0, fields, resultParam);
        Assert.assertEquals(OK, readResult);
    }

    @Test
    public void insertAndDelete() throws Exception {
        final Status insertResult = instance.insert(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY1, RocksDBClientTest.MOCK_DATA);
        Assert.assertEquals(OK, insertResult);
        final Status result = instance.delete(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY1);
        Assert.assertEquals(OK, result);
    }

    @Test
    public void insertUpdateAndRead() throws Exception {
        final Map<String, ByteIterator> newValues = new HashMap<>(RocksDBClientTest.NUM_RECORDS);
        final Status insertResult = instance.insert(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY2, RocksDBClientTest.MOCK_DATA);
        Assert.assertEquals(OK, insertResult);
        for (int i = 0; i < (RocksDBClientTest.NUM_RECORDS); i++) {
            newValues.put(((RocksDBClientTest.FIELD_PREFIX) + i), new StringByteIterator(("newvalue" + i)));
        }
        final Status result = instance.update(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY2, newValues);
        Assert.assertEquals(OK, result);
        // validate that the values changed
        final Map<String, ByteIterator> resultParam = new HashMap<>(RocksDBClientTest.NUM_RECORDS);
        instance.read(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY2, RocksDBClientTest.MOCK_DATA.keySet(), resultParam);
        for (int i = 0; i < (RocksDBClientTest.NUM_RECORDS); i++) {
            Assert.assertEquals(("newvalue" + i), resultParam.get(((RocksDBClientTest.FIELD_PREFIX) + i)).toString());
        }
    }

    @Test
    public void insertAndScan() throws Exception {
        final Status insertResult = instance.insert(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY3, RocksDBClientTest.MOCK_DATA);
        Assert.assertEquals(OK, insertResult);
        final Set<String> fields = RocksDBClientTest.MOCK_DATA.keySet();
        final Vector<HashMap<String, ByteIterator>> resultParam = new Vector<>(RocksDBClientTest.NUM_RECORDS);
        final Status result = instance.scan(RocksDBClientTest.MOCK_TABLE, RocksDBClientTest.MOCK_KEY3, RocksDBClientTest.NUM_RECORDS, fields, resultParam);
        Assert.assertEquals(OK, result);
    }
}

