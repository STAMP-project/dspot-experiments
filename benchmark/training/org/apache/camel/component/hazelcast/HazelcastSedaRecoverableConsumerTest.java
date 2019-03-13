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


import com.hazelcast.core.IQueue;
import com.hazelcast.core.TransactionalQueue;
import java.util.concurrent.TimeUnit;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public abstract class HazelcastSedaRecoverableConsumerTest extends HazelcastCamelTestSupport {
    @Mock
    protected IQueue<Object> queue;

    @Mock
    protected TransactionalQueue<Object> tqueue;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mock;

    @Test
    public void testRecovery() throws InterruptedException {
        Mockito.when(queue.poll(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(TimeUnit.class))).thenReturn("bar").thenReturn(null);
        Mockito.when(tqueue.poll(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(TimeUnit.class))).thenReturn("bar").thenReturn(null);
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied(5000, TimeUnit.MILLISECONDS);
    }
}

