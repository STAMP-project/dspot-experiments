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
package org.apache.ignite.loadtests.client;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.client.marshaller.GridClientMarshaller;
import org.apache.ignite.internal.client.marshaller.optimized.GridClientOptimizedMarshaller;
import org.apache.ignite.internal.processors.rest.client.message.GridClientCacheRequest;
import org.apache.ignite.marshaller.MarshallerUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests basic performance of marshallers.
 */
public class ClientMarshallerBenchmarkTest extends GridCommonAbstractTest {
    /**
     * Marshallers to test.
     */
    private GridClientMarshaller[] marshallers;

    /**
     *
     */
    public ClientMarshallerBenchmarkTest() {
        try {
            marshallers = new GridClientMarshaller[]{ new org.apache.ignite.internal.client.marshaller.jdk.GridClientJdkMarshaller(MarshallerUtils.classNameFilter(this.getClass().getClassLoader())), new GridClientOptimizedMarshaller() };
        } catch (IgniteCheckedException e) {
            throw new org.apache.ignite.IgniteException(e);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCacheRequestTime() throws Exception {
        GridClientCacheRequest req = new GridClientCacheRequest(CAS);
        req.clientId(UUID.randomUUID());
        req.cacheName("CacheName");
        req.requestId(1024);
        req.key("key");
        req.value(1L);
        req.value2(2L);
        Map<Object, Object> additional = new HashMap<>();
        for (int i = 0; i < 1000; i++)
            additional.put(("key" + i), ((long) (i)));

        req.values(additional);
        // Warm up.
        for (GridClientMarshaller marshaller : marshallers) {
            GridClientCacheRequest res = runMarshallUnmarshalLoop(req, 1, marshaller);
            assertEquals(req.operation(), res.operation());
            assertEquals(0, res.requestId());// requestId is not marshalled.

            assertEquals(null, res.clientId());// clientId is not marshalled.

            assertEquals(null, res.destinationId());// destinationId is not marshalled.

            assertEquals(req.cacheName(), res.cacheName());
            assertEquals(req.key(), res.key());
            assertEquals(req.value(), res.value());
            assertEquals(req.value2(), res.value2());
            for (Map.Entry<Object, Object> e : req.values().entrySet())
                assertEquals(e.getValue(), res.values().get(e.getKey()));

        }
        // Now real test.
        for (GridClientMarshaller marshaller : marshallers)
            runMarshallUnmarshalLoop(req, 1000, marshaller);

    }
}

