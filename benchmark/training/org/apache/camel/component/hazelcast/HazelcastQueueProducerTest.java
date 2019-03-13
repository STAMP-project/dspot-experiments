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


import HazelcastConstants.DRAIN_TO_COLLECTION;
import com.hazelcast.core.IQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.camel.CamelExecutionException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;


public class HazelcastQueueProducerTest extends HazelcastCamelTestSupport {
    @Mock
    private IQueue<String> queue;

    @Test(expected = CamelExecutionException.class)
    public void testWithInvalidOperation() {
        template.sendBody("direct:putInvalid", "foo");
    }

    @Test
    public void put() throws InterruptedException {
        template.sendBody("direct:put", "foo");
        verify(queue).put("foo");
    }

    @Test
    public void putWithOperationNumber() throws InterruptedException {
        template.sendBody("direct:putWithOperationNumber", "foo");
        verify(queue).put("foo");
    }

    @Test
    public void putWithOperationName() throws InterruptedException {
        template.sendBody("direct:putWithOperationName", "foo");
        verify(queue).put("foo");
    }

    @Test
    public void noOperation() {
        template.sendBody("direct:no-operation", "bar");
        verify(queue).add("bar");
    }

    @Test
    public void add() {
        template.sendBody("direct:add", "bar");
        verify(queue).add("bar");
    }

    @Test
    public void offer() {
        template.sendBody("direct:offer", "foobar");
        verify(queue).offer("foobar");
    }

    @Test
    public void removeSpecifiedValue() throws InterruptedException {
        template.sendBody("direct:removeValue", "foo2");
        verify(queue).remove("foo2");
    }

    @Test
    public void removeValue() {
        template.sendBody("direct:removeValue", null);
        verify(queue).remove();
    }

    @Test
    public void poll() throws InterruptedException {
        Mockito.when(queue.poll()).thenReturn("foo");
        String answer = template.requestBody("direct:poll", null, String.class);
        verify(queue).poll();
        assertEquals("foo", answer);
    }

    @Test
    public void peek() throws InterruptedException {
        Mockito.when(queue.peek()).thenReturn("foo");
        String answer = template.requestBody("direct:peek", null, String.class);
        verify(queue).peek();
        assertEquals("foo", answer);
    }

    @Test
    public void remainingCapacity() throws InterruptedException {
        Mockito.when(queue.remainingCapacity()).thenReturn(10);
        int answer = template.requestBody("direct:REMAINING_CAPACITY", null, Integer.class);
        verify(queue).remainingCapacity();
        assertEquals(10, answer);
    }

    @Test
    public void removeAll() throws InterruptedException {
        Collection c = new HashSet<>();
        c.add("foo2");
        template.sendBody("direct:removeAll", c);
        verify(queue).removeAll(c);
    }

    @Test
    public void removeIf() throws InterruptedException {
        Predicate<String> i = ( s) -> (s.length()) > 5;
        template.sendBody("direct:removeIf", i);
        verify(queue).removeIf(i);
    }

    @Test
    public void take() throws InterruptedException {
        template.sendBody("direct:take", "foo");
        verify(queue).take();
    }

    @Test
    public void retainAll() throws InterruptedException {
        Collection c = new HashSet<>();
        c.add("foo2");
        template.sendBody("direct:retainAll", c);
        verify(queue).retainAll(c);
    }

    @Test
    public void drainTo() throws InterruptedException {
        Map<String, Object> headers = new HashMap<>();
        Collection l = new ArrayList<>();
        headers.put(DRAIN_TO_COLLECTION, l);
        Mockito.when(queue.drainTo(l)).thenReturn(10);
        int answer = template.requestBodyAndHeaders("direct:drainTo", "test", headers, Integer.class);
        verify(queue).drainTo(l);
        assertEquals(10, answer);
    }
}

