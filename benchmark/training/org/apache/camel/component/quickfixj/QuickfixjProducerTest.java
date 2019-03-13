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


import ExchangePattern.InOut;
import QuickfixjEventCategory.AppMessageReceived;
import QuickfixjProducer.CORRELATION_CRITERIA_KEY;
import QuickfixjProducer.CORRELATION_TIMEOUT_KEY;
import SenderCompID.FIELD;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.camel.Exchange;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;
import quickfix.fix42.Email;
import quickfix.org.apache.camel.Message;


public class QuickfixjProducerTest {
    private Exchange mockExchange;

    private QuickfixjEndpoint mockEndpoint;

    private Message mockCamelMessage;

    private QuickfixjProducer producer;

    private SessionID sessionID;

    private Message inboundFixMessage;

    private QuickfixjEngine quickfixjEngine;

    public class TestException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    @Test
    public void setExceptionOnExchange() throws Exception {
        Session mockSession = Mockito.spy(TestSupport.createSession(sessionID));
        Mockito.doReturn(mockSession).when(producer).getSession(MessageUtils.getSessionID(inboundFixMessage));
        Mockito.doThrow(new QuickfixjProducerTest.TestException()).when(mockSession).send(ArgumentMatchers.isA(Message.class));
        producer.process(mockExchange);
        Mockito.verify(mockExchange).setException(ArgumentMatchers.isA(QuickfixjProducerTest.TestException.class));
    }

    @Test
    public void processInOnlyExchangeSuccess() throws Exception {
        Session mockSession = Mockito.spy(TestSupport.createSession(sessionID));
        Mockito.doReturn(mockSession).when(producer).getSession(MessageUtils.getSessionID(inboundFixMessage));
        Mockito.doReturn(true).when(mockSession).send(ArgumentMatchers.isA(Message.class));
        producer.process(mockExchange);
        Mockito.verify(mockExchange, Mockito.never()).setException(ArgumentMatchers.isA(IllegalStateException.class));
        Mockito.verify(mockSession).send(inboundFixMessage);
    }

    @Test
    public void processInOnlyExchangeSendUnsuccessful() throws Exception {
        Session mockSession = Mockito.spy(TestSupport.createSession(sessionID));
        Mockito.doReturn(mockSession).when(producer).getSession(MessageUtils.getSessionID(inboundFixMessage));
        Mockito.doReturn(false).when(mockSession).send(ArgumentMatchers.isA(Message.class));
        producer.process(mockExchange);
        Mockito.verify(mockSession).send(inboundFixMessage);
        Mockito.verify(mockExchange).setException(ArgumentMatchers.isA(CannotSendException.class));
    }

    @Test
    public void processInOutExchangeSuccess() throws Exception {
        Mockito.when(mockExchange.getPattern()).thenReturn(InOut);
        SessionID responseSessionID = new SessionID(sessionID.getBeginString(), sessionID.getTargetCompID(), sessionID.getSenderCompID());
        Mockito.when(mockExchange.getProperty(CORRELATION_CRITERIA_KEY)).thenReturn(new MessagePredicate(responseSessionID, MsgType.EMAIL));
        Mockito.when(mockExchange.getProperty(CORRELATION_TIMEOUT_KEY, 1000L, Long.class)).thenReturn(5000L);
        org.apache.camel.Message mockOutboundCamelMessage = Mockito.mock(Message.class);
        Mockito.when(mockExchange.getOut()).thenReturn(mockOutboundCamelMessage);
        final Message outboundFixMessage = new Email();
        outboundFixMessage.getHeader().setString(FIELD, "TARGET");
        outboundFixMessage.getHeader().setString(TargetCompID.FIELD, "SENDER");
        Session mockSession = Mockito.spy(TestSupport.createSession(sessionID));
        Mockito.doReturn(mockSession).when(producer).getSession(MessageUtils.getSessionID(inboundFixMessage));
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            quickfixjEngine.getMessageCorrelator().onEvent(AppMessageReceived, sessionID, outboundFixMessage);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 10);
                return true;
            }
        }).when(mockSession).send(ArgumentMatchers.isA(Message.class));
        producer.process(mockExchange);
        Mockito.verify(mockExchange, Mockito.never()).setException(ArgumentMatchers.isA(IllegalStateException.class));
        Mockito.verify(mockSession).send(inboundFixMessage);
        Mockito.verify(mockOutboundCamelMessage).getHeaders();
        Mockito.verify(mockOutboundCamelMessage).setBody(outboundFixMessage);
    }

    @Test
    public void processInOutExchangeSendUnsuccessful() throws Exception {
        Mockito.when(mockExchange.getPattern()).thenReturn(InOut);
        Mockito.when(mockExchange.getProperty(CORRELATION_CRITERIA_KEY)).thenReturn(new MessagePredicate(sessionID, MsgType.EMAIL));
        Mockito.when(mockExchange.getProperty(CORRELATION_TIMEOUT_KEY, 1000L, Long.class)).thenReturn(5000L);
        org.apache.camel.Message mockOutboundCamelMessage = Mockito.mock(Message.class);
        Mockito.when(mockExchange.getOut()).thenReturn(mockOutboundCamelMessage);
        final Message outboundFixMessage = new Email();
        outboundFixMessage.getHeader().setString(FIELD, "TARGET");
        outboundFixMessage.getHeader().setString(TargetCompID.FIELD, "SENDER");
        Session mockSession = Mockito.spy(TestSupport.createSession(sessionID));
        Mockito.doReturn(mockSession).when(producer).getSession(MessageUtils.getSessionID(inboundFixMessage));
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            quickfixjEngine.getMessageCorrelator().onEvent(AppMessageReceived, sessionID, outboundFixMessage);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 10);
                return false;
            }
        }).when(mockSession).send(ArgumentMatchers.isA(Message.class));
        producer.process(mockExchange);
        Mockito.verify(mockOutboundCamelMessage, Mockito.never()).setBody(ArgumentMatchers.isA(Message.class));
        Mockito.verify(mockSession).send(inboundFixMessage);
        Mockito.verify(mockExchange).setException(ArgumentMatchers.isA(CannotSendException.class));
    }
}

