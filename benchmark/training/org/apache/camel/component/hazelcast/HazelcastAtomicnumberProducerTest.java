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


import HazelcastConstants.EXPECTED_VALUE;
import com.hazelcast.core.IAtomicLong;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelExecutionException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastAtomicnumberProducerTest extends HazelcastCamelTestSupport {
    @Mock
    private IAtomicLong atomicNumber;

    @Test(expected = CamelExecutionException.class)
    public void testWithInvalidOperationName() {
        template.sendBody("direct:setInvalid", 4711);
    }

    @Test
    public void testSet() {
        template.sendBody("direct:set", 4711);
        Mockito.verify(atomicNumber).set(4711);
    }

    @Test
    public void testGet() {
        Mockito.when(atomicNumber.get()).thenReturn(1234L);
        long body = template.requestBody("direct:get", null, Long.class);
        Mockito.verify(atomicNumber).get();
        assertEquals(1234, body);
    }

    @Test
    public void testIncrement() {
        Mockito.when(atomicNumber.incrementAndGet()).thenReturn(11L);
        long body = template.requestBody("direct:increment", null, Long.class);
        Mockito.verify(atomicNumber).incrementAndGet();
        assertEquals(11, body);
    }

    @Test
    public void testDecrement() {
        Mockito.when(atomicNumber.decrementAndGet()).thenReturn(9L);
        long body = template.requestBody("direct:decrement", null, Long.class);
        Mockito.verify(atomicNumber).decrementAndGet();
        assertEquals(9, body);
    }

    @Test
    public void testDestroy() throws InterruptedException {
        template.sendBody("direct:destroy", null);
        Mockito.verify(atomicNumber).destroy();
    }

    @Test
    public void testSetWithOperationNumber() {
        template.sendBody("direct:setWithOperationNumber", 5711);
        Mockito.verify(atomicNumber).set(5711);
    }

    @Test
    public void testSetWithOperationName() {
        template.sendBody("direct:setWithOperationName", 5711);
        Mockito.verify(atomicNumber).set(5711);
    }

    @Test
    public void testCompareAndSet() {
        Map<String, Object> headersOk = new HashMap();
        headersOk.put(EXPECTED_VALUE, 1234L);
        Mockito.when(atomicNumber.compareAndSet(1234L, 1235L)).thenReturn(true);
        Mockito.when(atomicNumber.compareAndSet(1233L, 1235L)).thenReturn(false);
        boolean result = template.requestBodyAndHeaders("direct:compareAndSet", 1235L, headersOk, Boolean.class);
        Mockito.verify(atomicNumber).compareAndSet(1234L, 1235L);
        assertEquals(true, result);
        Map<String, Object> headersKo = new HashMap();
        headersKo.put(EXPECTED_VALUE, 1233L);
        result = template.requestBodyAndHeaders("direct:compareAndSet", 1235L, headersKo, Boolean.class);
        Mockito.verify(atomicNumber).compareAndSet(1233L, 1235L);
        assertEquals(false, result);
    }

    @Test
    public void testGetAndAdd() {
        Mockito.when(atomicNumber.getAndAdd(12L)).thenReturn(13L);
        long result = template.requestBody("direct:getAndAdd", 12L, Long.class);
        Mockito.verify(atomicNumber).getAndAdd(12L);
        assertEquals(13L, result);
    }
}

