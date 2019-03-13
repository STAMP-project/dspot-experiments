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


import com.hazelcast.ringbuffer.Ringbuffer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastRingbufferProducerForSpringTest extends HazelcastCamelSpringTestSupport {
    @Mock
    private Ringbuffer<Object> ringbuffer;

    @Test
    public void testReadTail() throws InterruptedException {
        Mockito.when(ringbuffer.readOne(ArgumentMatchers.anyLong())).thenReturn("pippo");
        Object result = template.requestBody("direct:readonceTail", 12L, String.class);
        assertEquals("pippo", result);
    }

    @Test
    public void testAdd() throws InterruptedException {
        Mockito.when(ringbuffer.add(ArgumentMatchers.anyLong())).thenReturn(13L);
        Object result = template.requestBody("direct:add", 12L, Long.class);
        assertEquals(13L, result);
    }

    @Test
    public void testCapacity() throws InterruptedException {
        Mockito.when(ringbuffer.capacity()).thenReturn(13L);
        Object result = template.requestBody("direct:capacity", 12L, Long.class);
        assertEquals(13L, result);
    }

    @Test
    public void testRemainingCapacity() throws InterruptedException {
        Mockito.when(ringbuffer.remainingCapacity()).thenReturn(2L);
        Object result = template.requestBody("direct:remainingCapacity", "", Long.class);
        assertEquals(2L, result);
    }
}

