/**
 * Copyright (c) 2018 YCSB contributors All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db.ignite;


import Status.NOT_FOUND;
import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Integration tests for the Ignite client
 */
public class IgniteClientTest extends IgniteClientTestBase {
    private static final String HOST = "127.0.0.1";

    private static final String PORTS = "47500..47509";

    private static final String SERVER_NODE_NAME = "YCSB Server Node";

    private static TcpDiscoveryIpFinder ipFinder = new TcpDiscoveryVmIpFinder(true);

    @Test
    public void testInsert() throws Exception {
        IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).clear();
        final String key = "key";
        final Map<String, String> input = new HashMap<>();
        input.put("field0", "value1");
        input.put("field1", "value2");
        final Status status = client.insert(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, StringByteIterator.getByteIteratorMap(input));
        MatcherAssert.assertThat(status, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(1));
    }

    @Test
    public void testDelete() throws Exception {
        IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).clear();
        final String key1 = "key1";
        final Map<String, String> input1 = new HashMap<>();
        input1.put("field0", "value1");
        input1.put("field1", "value2");
        final Status status1 = client.insert(IgniteClientTestBase.DEFAULT_CACHE_NAME, key1, StringByteIterator.getByteIteratorMap(input1));
        MatcherAssert.assertThat(status1, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(1));
        final String key2 = "key2";
        final Map<String, String> input2 = new HashMap<>();
        input2.put("field0", "value1");
        input2.put("field1", "value2");
        final Status status2 = client.insert(IgniteClientTestBase.DEFAULT_CACHE_NAME, key2, StringByteIterator.getByteIteratorMap(input2));
        MatcherAssert.assertThat(status2, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(2));
        final Status status3 = client.delete(IgniteClientTestBase.DEFAULT_CACHE_NAME, key2);
        MatcherAssert.assertThat(status3, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(1));
    }

    @Test
    public void testRead() throws Exception {
        IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).clear();
        final String key = "key";
        final Map<String, String> input = new HashMap<>();
        input.put("field0", "value1");
        input.put("field1", "value2A");
        input.put("field3", null);
        final Status sPut = client.insert(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, StringByteIterator.getByteIteratorMap(input));
        MatcherAssert.assertThat(sPut, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(1));
        final Set<String> fld = new TreeSet<>();
        fld.add("field0");
        fld.add("field1");
        fld.add("field3");
        final HashMap<String, ByteIterator> result = new HashMap<>();
        final Status sGet = client.read(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, fld, result);
        MatcherAssert.assertThat(sGet, Matchers.is(OK));
        final HashMap<String, String> strResult = new HashMap<String, String>();
        for (final Map.Entry<String, ByteIterator> e : result.entrySet()) {
            if ((e.getValue()) != null) {
                strResult.put(e.getKey(), e.getValue().toString());
            }
        }
        MatcherAssert.assertThat(strResult, Matchers.hasEntry("field0", "value1"));
        MatcherAssert.assertThat(strResult, Matchers.hasEntry("field1", "value2A"));
    }

    @Test
    public void testReadAllFields() throws Exception {
        IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).clear();
        final String key = "key";
        final Map<String, String> input = new HashMap<>();
        input.put("field0", "value1");
        input.put("field1", "value2A");
        input.put("field3", null);
        final Status sPut = client.insert(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, StringByteIterator.getByteIteratorMap(input));
        MatcherAssert.assertThat(sPut, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(1));
        final Set<String> fld = new TreeSet<>();
        final HashMap<String, ByteIterator> result1 = new HashMap<>();
        final Status sGet = client.read(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, fld, result1);
        MatcherAssert.assertThat(sGet, Matchers.is(OK));
        final HashMap<String, String> strResult = new HashMap<String, String>();
        for (final Map.Entry<String, ByteIterator> e : result1.entrySet()) {
            if ((e.getValue()) != null) {
                strResult.put(e.getKey(), e.getValue().toString());
            }
        }
        MatcherAssert.assertThat(strResult, Matchers.hasEntry("field0", "value1"));
        MatcherAssert.assertThat(strResult, Matchers.hasEntry("field1", "value2A"));
    }

    @Test
    public void testReadNotPresent() throws Exception {
        IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).clear();
        final String key = "key";
        final Map<String, String> input = new HashMap<>();
        input.put("field0", "value1");
        input.put("field1", "value2A");
        input.put("field3", null);
        final Status sPut = client.insert(IgniteClientTestBase.DEFAULT_CACHE_NAME, key, StringByteIterator.getByteIteratorMap(input));
        MatcherAssert.assertThat(sPut, Matchers.is(OK));
        MatcherAssert.assertThat(IgniteClientTestBase.cluster.cache(IgniteClientTestBase.DEFAULT_CACHE_NAME).size(), Matchers.is(1));
        final Set<String> fld = new TreeSet<>();
        final String newKey = "newKey";
        final HashMap<String, ByteIterator> result1 = new HashMap<>();
        final Status sGet = client.read(IgniteClientTestBase.DEFAULT_CACHE_NAME, newKey, fld, result1);
        MatcherAssert.assertThat(sGet, Matchers.is(NOT_FOUND));
    }
}

