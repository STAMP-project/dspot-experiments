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
import com.hazelcast.core.MultiMap;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastMultimapProducerForSpringTest extends HazelcastCamelSpringTestSupport {
    @Mock
    private MultiMap<Object, Object> map;

    @Test
    public void testPut() throws InterruptedException {
        template.sendBodyAndHeader("direct:put", "my-foo", OBJECT_ID, "4711");
        Mockito.verify(map).put("4711", "my-foo");
    }

    @Test
    public void testRemoveValue() {
        template.sendBodyAndHeader("direct:removevalue", "my-foo", OBJECT_ID, "4711");
        Mockito.verify(map).remove("4711", "my-foo");
    }

    @Test
    public void testGet() {
        Mockito.when(map.get("4711")).thenReturn(Arrays.<Object>asList("my-foo"));
        template.sendBodyAndHeader("direct:get", null, OBJECT_ID, "4711");
        Mockito.verify(map).get("4711");
        Collection<?> body = consumer.receiveBody("seda:out", 5000, Collection.class);
        assertTrue(body.contains("my-foo"));
    }

    @Test
    public void testDelete() {
        template.sendBodyAndHeader("direct:delete", null, OBJECT_ID, 4711);
        Mockito.verify(map).remove(4711);
    }

    @Test
    public void testValueCount() {
        template.sendBodyAndHeader("direct:valueCount", "test", OBJECT_ID, "4711");
        Mockito.verify(map).valueCount("4711");
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

