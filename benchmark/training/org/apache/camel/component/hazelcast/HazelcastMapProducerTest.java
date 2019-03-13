/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.hazelcast;


import HazelcastConstants.OBJECT_ID;
import HazelcastConstants.OBJECT_VALUE;
import HazelcastConstants.QUERY;
import HazelcastConstants.TTL_UNIT;
import HazelcastConstants.TTL_VALUE;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.hazelcast.testutil.Dummy;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastMapProducerTest extends HazelcastCamelTestSupport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Mock
    private IMap<Object, Object> map;

    @Test(expected = CamelExecutionException.class)
    public void testWithInvalidOperation() {
        template.sendBody("direct:putInvalid", "my-foo");
    }

    @Test
    public void testPut() throws InterruptedException {
        template.sendBodyAndHeader("direct:put", "my-foo", OBJECT_ID, "4711");
        Mockito.verify(map).put("4711", "my-foo");
    }

    @Test
    public void testPutWithOperationNumber() throws InterruptedException {
        template.sendBodyAndHeader("direct:putWithOperationNumber", "my-foo", OBJECT_ID, "4711");
        Mockito.verify(map).put("4711", "my-foo");
    }

    @Test
    public void testPutWithOperationName() throws InterruptedException {
        template.sendBodyAndHeader("direct:putWithOperationName", "my-foo", OBJECT_ID, "4711");
        Mockito.verify(map).put("4711", "my-foo");
    }

    @Test
    public void testPutWithTTL() throws InterruptedException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(OBJECT_ID, "4711");
        headers.put(TTL_VALUE, new Long(1));
        headers.put(TTL_UNIT, TimeUnit.MINUTES);
        template.sendBodyAndHeaders("direct:put", "test", headers);
        Mockito.verify(map).put("4711", "test", 1, TimeUnit.MINUTES);
    }

    @Test
    public void testUpdate() {
        template.sendBodyAndHeader("direct:update", "my-fooo", OBJECT_ID, "4711");
        Mockito.verify(map).lock("4711");
        Mockito.verify(map).replace("4711", "my-fooo");
        Mockito.verify(map).unlock("4711");
    }

    @Test
    public void testGet() {
        Mockito.when(map.get("4711")).thenReturn("my-foo");
        template.sendBodyAndHeader("direct:get", null, OBJECT_ID, "4711");
        String body = consumer.receiveBody("seda:out", 5000, String.class);
        Mockito.verify(map).get("4711");
        assertEquals("my-foo", body);
    }

    @Test
    public void testGetAllEmptySet() {
        Set<Object> l = new HashSet<>();
        Map t = new HashMap();
        t.put("key1", "value1");
        t.put("key2", "value2");
        t.put("key3", "value3");
        Mockito.when(map.getAll(ArgumentMatchers.anySet())).thenReturn(t);
        template.sendBodyAndHeader("direct:getAll", null, OBJECT_ID, l);
        String body = consumer.receiveBody("seda:out", 5000, String.class);
        Mockito.verify(map).getAll(l);
        assertTrue(body.contains("key1=value1"));
        assertTrue(body.contains("key2=value2"));
        assertTrue(body.contains("key3=value3"));
    }

    @Test
    public void testGetAllOnlyOneKey() {
        Set<Object> l = new HashSet<>();
        l.add("key1");
        Map t = new HashMap();
        t.put("key1", "value1");
        Mockito.when(map.getAll(l)).thenReturn(t);
        template.sendBodyAndHeader("direct:getAll", null, OBJECT_ID, l);
        String body = consumer.receiveBody("seda:out", 5000, String.class);
        Mockito.verify(map).getAll(l);
        assertEquals("{key1=value1}", body);
    }

    @Test
    public void testDelete() {
        template.sendBodyAndHeader("direct:delete", null, OBJECT_ID, 4711);
        Mockito.verify(map).remove(4711);
    }

    @Test
    public void testQuery() {
        String sql = "bar > 1000";
        Mockito.when(map.values(ArgumentMatchers.any(SqlPredicate.class))).thenReturn(Arrays.<Object>asList(new Dummy("beta", 2000), new Dummy("gamma", 3000)));
        template.sendBodyAndHeader("direct:queue", null, QUERY, sql);
        Mockito.verify(map).values(ArgumentMatchers.any(SqlPredicate.class));
        Collection<?> b1 = consumer.receiveBody("seda:out", 5000, Collection.class);
        assertNotNull(b1);
        assertEquals(2, b1.size());
    }

    @Test
    public void testEmptyQuery() {
        Mockito.when(map.values()).thenReturn(Arrays.<Object>asList(new Dummy("beta", 2000), new Dummy("gamma", 3000), new Dummy("delta", 4000)));
        template.sendBody("direct:queue", null);
        Mockito.verify(map).values();
        Collection<?> b1 = consumer.receiveBody("seda:out", 5000, Collection.class);
        assertNotNull(b1);
        assertEquals(3, b1.size());
    }

    @Test
    public void testUpdateOldValue() throws InterruptedException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(OBJECT_ID, "4711");
        headers.put(OBJECT_VALUE, "my-foo");
        template.sendBodyAndHeaders("direct:update", "replaced", headers);
        Mockito.verify(map).lock("4711");
        Mockito.verify(map).replace("4711", "my-foo", "replaced");
        Mockito.verify(map).unlock("4711");
    }

    @Test
    public void testPutIfAbsent() throws InterruptedException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(OBJECT_ID, "4711");
        template.sendBodyAndHeaders("direct:putIfAbsent", "replaced", headers);
        Mockito.verify(map).putIfAbsent("4711", "replaced");
    }

    @Test
    public void testPutIfAbsentWithTtl() throws InterruptedException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(OBJECT_ID, "4711");
        headers.put(TTL_VALUE, new Long(1));
        headers.put(TTL_UNIT, TimeUnit.MINUTES);
        template.sendBodyAndHeaders("direct:putIfAbsent", "replaced", headers);
        Mockito.verify(map).putIfAbsent("4711", "replaced", new Long(1), TimeUnit.MINUTES);
    }

    @Test
    public void testEvict() throws InterruptedException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(OBJECT_ID, "4711");
        template.sendBodyAndHeaders("direct:evict", "", headers);
        Mockito.verify(map).evict("4711");
    }

    @Test
    public void testEvictAll() throws InterruptedException {
        Map<String, Object> headers = new HashMap<>();
        template.sendBodyAndHeaders("direct:evictAll", "", headers);
        Mockito.verify(map).evictAll();
    }

    @Test
    public void testClear() throws InterruptedException {
        template.sendBody("direct:clear", "test");
        Mockito.verify(map).clear();
    }

    @Test
    public void testContainsKey() {
        Mockito.when(map.containsKey("testOk")).thenReturn(true);
        Mockito.when(map.containsKey("testKo")).thenReturn(false);
        template.sendBodyAndHeader("direct:containsKey", null, OBJECT_ID, "testOk");
        Boolean body = consumer.receiveBody("seda:out", 5000, Boolean.class);
        Mockito.verify(map).containsKey("testOk");
        assertEquals(true, body);
        template.sendBodyAndHeader("direct:containsKey", null, OBJECT_ID, "testKo");
        body = consumer.receiveBody("seda:out", 5000, Boolean.class);
        Mockito.verify(map).containsKey("testKo");
        assertEquals(false, body);
    }

    @Test
    public void testContainsValue() {
        Mockito.when(map.containsValue("testOk")).thenReturn(true);
        Mockito.when(map.containsValue("testKo")).thenReturn(false);
        template.sendBody("direct:containsValue", "testOk");
        Boolean body = consumer.receiveBody("seda:out", 5000, Boolean.class);
        Mockito.verify(map).containsValue("testOk");
        assertEquals(true, body);
        template.sendBody("direct:containsValue", "testKo");
        body = consumer.receiveBody("seda:out", 5000, Boolean.class);
        Mockito.verify(map).containsValue("testKo");
        assertEquals(false, body);
    }
}

