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
package org.apache.camel.component.quickfixj;


import org.apache.camel.Exchange;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import quickfix.Message;
import quickfix.SessionID;


@Ignore("Fails on CI server sometimes")
public class QuickfixjLazyProducerTest {
    private Exchange mockExchange;

    private QuickfixjEndpoint endpoint;

    private Message mockCamelMessage;

    private QuickfixjProducer producer;

    private SessionID sessionID;

    private Message inboundFixMessage;

    private QuickfixjEngine quickfixjEngine;

    @Test
    public void processWithLazyEngine() throws Exception {
        QuickfixjEngine engine = ((QuickfixjEngine) (ReflectionTestUtils.getField(endpoint, "engine")));
        Assert.assertThat(engine.isInitialized(), CoreMatchers.is(false));
        Assert.assertThat(engine.isStarted(), CoreMatchers.is(false));
        // Session mockSession = Mockito.spy(TestSupport.createSession(sessionID));
        // Mockito.doReturn(mockSession).when(producer).getSession(MessageUtils.getSessionID(inboundFixMessage));
        // Mockito.doReturn(true).when(mockSession).send(Matchers.isA(Message.class));
        producer.process(mockExchange);
        Assert.assertThat(engine.isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(engine.isStarted(), CoreMatchers.is(true));
        // 
        // Mockito.verify(mockExchange, Mockito.never()).setException(Matchers.isA(IllegalStateException.class));
        // Mockito.verify(mockSession).send(inboundFixMessage);
    }
}

