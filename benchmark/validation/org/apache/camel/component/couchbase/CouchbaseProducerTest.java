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
package org.apache.camel.component.couchbase;


import com.couchbase.client.CouchbaseClient;
import java.util.HashMap;
import java.util.Map;
import net.spy.memcached.internal.OperationFuture;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class CouchbaseProducerTest {
    @Mock
    private CouchbaseClient client;

    @Mock
    private CouchbaseEndpoint endpoint;

    @Mock
    private Exchange exchange;

    @Mock
    private Message msg;

    @Mock
    private OperationFuture<?> response;

    @Mock
    private OperationFuture<Boolean> of;

    private CouchbaseProducer producer;

    @Test(expected = CouchbaseException.class)
    public void testBodyMandatory() throws Exception {
        producer.process(exchange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersistToLowerThanSupported() throws Exception {
        producer = new CouchbaseProducer(endpoint, client, (-1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersistToHigherThanSupported() throws Exception {
        producer = new CouchbaseProducer(endpoint, client, 5, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplicateToLowerThanSupported() throws Exception {
        producer = new CouchbaseProducer(endpoint, client, 0, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplicateToHigherThanSupported() throws Exception {
        producer = new CouchbaseProducer(endpoint, client, 0, 4);
    }

    @Test
    public void testMaximumValuesForPersistToAndRepicateTo() throws Exception {
        try {
            producer = new CouchbaseProducer(endpoint, client, 4, 3);
        } catch (IllegalArgumentException e) {
            Assert.fail(("Exception was thrown while testing maximum values for persistTo and replicateTo parameters " + (e.getMessage())));
        }
    }

    @Test
    public void testExpiryTimeIsSet() throws Exception {
        Mockito.when(of.get()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                return true;
            }
        });
        Mockito.when(client.set(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(of);
        // Mock out some headers so we can set an expiry
        int expiry = 5000;
        Map<String, Object> testHeaders = new HashMap<>();
        testHeaders.put("CCB_TTL", Integer.toString(expiry));
        Mockito.when(msg.getHeaders()).thenReturn(testHeaders);
        Mockito.when(msg.getHeader(CouchbaseConstants.HEADER_TTL, String.class)).thenReturn(Integer.toString(expiry));
        Mockito.when(endpoint.getId()).thenReturn("123");
        Mockito.when(endpoint.getOperation()).thenReturn("CCB_PUT");
        Mockito.when(exchange.getOut()).thenReturn(msg);
        producer.process(exchange);
        Mockito.verify(client).set(ArgumentMatchers.anyString(), ArgumentMatchers.eq(expiry), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testTimeOutRetryToException() throws Exception {
        Mockito.when(of.get()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                throw new RuntimeException("Timed out waiting for operation");
            }
        });
        Mockito.when(client.set(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(of);
        Mockito.when(endpoint.getId()).thenReturn("123");
        Mockito.when(endpoint.getOperation()).thenReturn("CCB_PUT");
        try {
            producer.process(exchange);
        } catch (Exception e) {
            // do nothing
            Mockito.verify(of, Mockito.times(3)).get();
        }
    }

    @Test
    public void testTimeOutRetryThenSuccess() throws Exception {
        Mockito.when(of.get()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                throw new RuntimeException("Timed out waiting for operation");
            }
        }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                return true;
            }
        });
        Mockito.when(client.set(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(of);
        Mockito.when(endpoint.getId()).thenReturn("123");
        Mockito.when(endpoint.getOperation()).thenReturn("CCB_PUT");
        Mockito.when(exchange.getOut()).thenReturn(msg);
        producer.process(exchange);
        Mockito.verify(of, Mockito.times(2)).get();
        Mockito.verify(msg).setBody(true);
    }

    @Test
    public void testTimeOutRetryTwiceThenSuccess() throws Exception {
        Mockito.when(of.get()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                throw new RuntimeException("Timed out waiting for operation");
            }
        }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                throw new RuntimeException("Timed out waiting for operation");
            }
        }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                return true;
            }
        });
        Mockito.when(client.set(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(of);
        Mockito.when(endpoint.getId()).thenReturn("123");
        Mockito.when(endpoint.getOperation()).thenReturn("CCB_PUT");
        Mockito.when(exchange.getOut()).thenReturn(msg);
        producer.process(exchange);
        Mockito.verify(of, Mockito.times(3)).get();
        Mockito.verify(msg).setBody(true);
    }
}

