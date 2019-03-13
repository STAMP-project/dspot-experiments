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
package org.apache.camel.component.smpp;


import org.apache.camel.Processor;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SessionStateListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * JUnit test class for <code>org.apache.camel.component.smpp.SmppConsumer</code>
 */
public class SmppConsumerTest {
    private SmppConsumer consumer;

    private SmppEndpoint endpoint;

    private SmppConfiguration configuration;

    private Processor processor;

    private SMPPSession session;

    @Test
    public void doStartShouldStartANewSmppSession() throws Exception {
        Mockito.when(endpoint.getConnectionString()).thenReturn("smpp://smppclient@localhost:2775");
        BindParameter expectedBindParameter = new BindParameter(BindType.BIND_RX, "smppclient", "password", "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "");
        Mockito.when(session.connectAndBind("localhost", new Integer(2775), expectedBindParameter)).thenReturn("1");
        consumer.doStart();
        Mockito.verify(session).setEnquireLinkTimer(5000);
        Mockito.verify(session).setTransactionTimer(10000);
        Mockito.verify(session).addSessionStateListener(ArgumentMatchers.isA(SessionStateListener.class));
        Mockito.verify(session).setMessageReceiverListener(ArgumentMatchers.isA(MessageReceiverListener.class));
        Mockito.verify(session).connectAndBind("localhost", new Integer(2775), expectedBindParameter);
    }

    @Test
    public void doStopShouldNotCloseTheSMPPSessionIfItIsNull() throws Exception {
        Mockito.when(endpoint.getConnectionString()).thenReturn("smpp://smppclient@localhost:2775");
        consumer.doStop();
    }

    @Test
    public void doStopShouldCloseTheSMPPSession() throws Exception {
        doStartShouldStartANewSmppSession();
        Mockito.reset(endpoint, processor, session);
        Mockito.when(endpoint.getConnectionString()).thenReturn("smpp://smppclient@localhost:2775");
        consumer.doStop();
        Mockito.verify(session).removeSessionStateListener(ArgumentMatchers.isA(SessionStateListener.class));
        Mockito.verify(session).unbindAndClose();
    }

    @Test
    public void addressRangeFromConfigurationIsUsed() throws Exception {
        configuration.setAddressRange("(111*|222*|333*)");
        BindParameter expectedBindParameter = new BindParameter(BindType.BIND_RX, "smppclient", "password", "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "(111*|222*|333*)");
        Mockito.when(session.connectAndBind("localhost", new Integer(2775), expectedBindParameter)).thenReturn("1");
        consumer.doStart();
        Mockito.verify(session).connectAndBind("localhost", new Integer(2775), expectedBindParameter);
    }

    @Test
    public void getterShouldReturnTheSetValues() {
        Assert.assertSame(endpoint, consumer.getEndpoint());
        Assert.assertSame(configuration, consumer.getConfiguration());
    }
}

