/**
 * Copyright (c) 2016 YCSB contributors. All rights reserved.
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


import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the binding of <a href="http://ceph.org/">RADOS of Ceph</a>.
 *
 * See {@code rados/README.md} for details.
 */
public class RadosClientTest {
    private static RadosClient radosclient;

    public static final String POOL_PROPERTY = "rados.pool";

    public static final String POOL_TEST = "rbd";

    private static final String TABLE_NAME = "table0";

    private static final String KEY0 = "key0";

    private static final String KEY1 = "key1";

    private static final String KEY2 = "key2";

    private static final HashMap<String, ByteIterator> DATA;

    private static final HashMap<String, ByteIterator> DATA_UPDATED;

    static {
        DATA = new HashMap<String, ByteIterator>(10);
        DATA_UPDATED = new HashMap<String, ByteIterator>(10);
        for (int i = 0; i < 10; i++) {
            String key = "key" + (UUID.randomUUID());
            RadosClientTest.DATA.put(key, new StringByteIterator(("data" + (UUID.randomUUID()))));
            RadosClientTest.DATA_UPDATED.put(key, new StringByteIterator(("data" + (UUID.randomUUID()))));
        }
    }

    @Test
    public void insertTest() {
        Status result = RadosClientTest.radosclient.insert(RadosClientTest.TABLE_NAME, RadosClientTest.KEY1, RadosClientTest.DATA);
        Assert.assertEquals(OK, result);
    }

    @Test
    public void updateTest() {
        RadosClientTest.radosclient.insert(RadosClientTest.TABLE_NAME, RadosClientTest.KEY2, RadosClientTest.DATA);
        Status result = RadosClientTest.radosclient.update(RadosClientTest.TABLE_NAME, RadosClientTest.KEY2, RadosClientTest.DATA_UPDATED);
        Assert.assertEquals(OK, result);
        HashMap<String, ByteIterator> ret = new HashMap<String, ByteIterator>(10);
        RadosClientTest.radosclient.read(RadosClientTest.TABLE_NAME, RadosClientTest.KEY2, RadosClientTest.DATA.keySet(), ret);
        compareMap(RadosClientTest.DATA_UPDATED, ret);
        RadosClientTest.radosclient.delete(RadosClientTest.TABLE_NAME, RadosClientTest.KEY2);
    }

    @Test
    public void readTest() {
        HashMap<String, ByteIterator> ret = new HashMap<String, ByteIterator>(10);
        Status result = RadosClientTest.radosclient.read(RadosClientTest.TABLE_NAME, RadosClientTest.KEY0, RadosClientTest.DATA.keySet(), ret);
        Assert.assertEquals(OK, result);
        compareMap(RadosClientTest.DATA, ret);
    }

    @Test
    public void deleteTest() {
        Status result = RadosClientTest.radosclient.delete(RadosClientTest.TABLE_NAME, RadosClientTest.KEY0);
        Assert.assertEquals(OK, result);
    }
}

