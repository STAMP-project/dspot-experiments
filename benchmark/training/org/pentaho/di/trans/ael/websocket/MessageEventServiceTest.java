/**
 * ! ******************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.trans.ael.websocket;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Assert;
import org.pentaho.di.engine.api.events.LogEvent;
import org.pentaho.di.engine.api.events.MetricsEvent;
import org.pentaho.di.engine.api.events.StatusEvent;
import org.pentaho.di.engine.api.model.ModelType;
import org.pentaho.di.engine.api.remote.Message;
import org.pentaho.di.engine.api.remote.StopMessage;
import org.pentaho.di.engine.api.reporting.LogEntry;
import org.pentaho.di.trans.ael.websocket.exception.HandlerRegistrationException;
import org.pentaho.di.trans.ael.websocket.exception.MessageEventFireEventException;
import org.pentaho.di.trans.ael.websocket.handler.MessageEventHandler;


@RunWith(MockitoJUnitRunner.class)
public class MessageEventServiceTest {
    private LogEvent transformationMessageEvent;

    private LogEvent operationMessageEvent;

    private MetricsEvent otherOpMessageEvent;

    private StatusEvent otherTransMessageEvent;

    @Mock
    private MessageEventHandler messageEventHandler;

    @Mock
    private MessageEventHandler messageEventHandler2;

    @Mock
    private LogEntry logEntry;

    private MessageEventService messageEventService;

    @Test
    public void testTranformationAddHandler() throws KettleException {
        messageEventService.addHandler(transformationMessageEvent, messageEventHandler);
        Assert.assertTrue(((messageEventService.getHandlersFor(transformationMessageEvent).size()) == 1));
        Assert.assertTrue(messageEventHandler.getIdentifier().equals(messageEventService.getHandlersFor(transformationMessageEvent).get(0).getIdentifier()));
    }

    @Test(expected = HandlerRegistrationException.class)
    public void testTranformationDuplicateAddHandler() throws KettleException {
        messageEventService.addHandler(transformationMessageEvent, messageEventHandler);
        messageEventService.addHandler(transformationMessageEvent, messageEventHandler);
    }

    @Test
    public void testTranformationAddDiffHandlersForSameEvent() throws KettleException {
        testAddDiffHandlersForSameEvent(transformationMessageEvent, messageEventHandler, messageEventHandler2);
    }

    @Test
    public void testTransformationHasHandler() throws KettleException {
        addHandlers(transformationMessageEvent, messageEventHandler, messageEventHandler2);
        Assert.assertTrue(messageEventService.hasHandlers(transformationMessageEvent));
    }

    @Test
    public void testTransformationHasHandlerFalseTrans() throws KettleException {
        addHandlers(transformationMessageEvent, messageEventHandler, messageEventHandler2);
        Assert.assertFalse(messageEventService.hasHandlers(otherTransMessageEvent));
    }

    @Test
    public void testTransformationHasHandlerFalseOp() throws KettleException {
        addHandlers(transformationMessageEvent, messageEventHandler, messageEventHandler2);
        Assert.assertFalse(messageEventService.hasHandlers(otherOpMessageEvent));
    }

    @Test
    public void testOperationAddHandler() throws KettleException {
        messageEventService.addHandler(operationMessageEvent, messageEventHandler);
        Assert.assertTrue(((messageEventService.getHandlersFor(operationMessageEvent).size()) == 1));
        Assert.assertTrue(messageEventHandler.getIdentifier().equals(messageEventService.getHandlersFor(operationMessageEvent).get(0).getIdentifier()));
    }

    @Test(expected = HandlerRegistrationException.class)
    public void testOperationDuplicateAddHandler() throws KettleException {
        messageEventService.addHandler(operationMessageEvent, messageEventHandler);
        messageEventService.addHandler(operationMessageEvent, messageEventHandler);
    }

    @Test
    public void testOperationAddDiffHandlersForSameEvent() throws KettleException {
        testAddDiffHandlersForSameEvent(operationMessageEvent, messageEventHandler, messageEventHandler2);
    }

    @Test
    public void testOperationHasHandler() throws KettleException {
        addHandlers(operationMessageEvent, messageEventHandler, messageEventHandler2);
        Assert.assertTrue(messageEventService.hasHandlers(operationMessageEvent));
    }

    @Test
    public void testOperationHasHandlerFalseTrans() throws KettleException {
        addHandlers(operationMessageEvent, messageEventHandler, messageEventHandler2);
        Assert.assertFalse(messageEventService.hasHandlers(otherTransMessageEvent));
    }

    @Test
    public void testOperationHasHandlerFalseOp() throws KettleException {
        addHandlers(operationMessageEvent, messageEventHandler, messageEventHandler2);
        Assert.assertFalse(messageEventService.hasHandlers(otherOpMessageEvent));
    }

    @Test(expected = HandlerRegistrationException.class)
    public void testMsgEventTypeNull() throws KettleException {
        messageEventService.addHandler(null, messageEventHandler);
    }

    @Test(expected = HandlerRegistrationException.class)
    public void testMsgHandlerNull() throws KettleException {
        messageEventService.addHandler(operationMessageEvent, null);
    }

    @Test(expected = HandlerRegistrationException.class)
    public void testbothNull() throws KettleException {
        messageEventService.addHandler(null, null);
    }

    @Test(expected = MessageEventFireEventException.class)
    public void testFireEventNull() throws KettleException {
        messageEventService.fireEvent(null);
    }

    @Test
    public void testOperationFireEvent() throws KettleException {
        addHandlers(operationMessageEvent, messageEventHandler, messageEventHandler2);
        LogEvent logEvent = new LogEvent(new org.pentaho.di.engine.api.remote.RemoteSource(ModelType.OPERATION, "Operation_ID"), logEntry);
        messageEventService.fireEvent(logEvent);
        Mockito.verify(messageEventHandler).execute(logEvent);
        Mockito.verify(messageEventHandler2).execute(logEvent);
    }

    @Test(expected = MessageEventFireEventException.class)
    public void testOperationFireEventThrowException() throws KettleException {
        addHandlers(operationMessageEvent, messageEventHandler, messageEventHandler2);
        Mockito.doThrow(new RuntimeException("Test")).when(messageEventHandler).execute(ArgumentMatchers.any(Message.class));
        LogEvent logEvent = new LogEvent(new org.pentaho.di.engine.api.remote.RemoteSource(ModelType.OPERATION, "Operation_ID"), logEntry);
        messageEventService.fireEvent(logEvent);
        Mockito.verify(messageEventHandler, Mockito.never()).execute(logEvent);
        Mockito.verify(messageEventHandler2).execute(logEvent);
    }

    @Test
    public void testStopMessageFireEvent() throws KettleException {
        addHandlers(new StopMessage(""), messageEventHandler, messageEventHandler2);
        StopMessage msg = new StopMessage("User request");
        messageEventService.fireEvent(msg);
        Mockito.verify(messageEventHandler).execute(msg);
        Mockito.verify(messageEventHandler2).execute(msg);
    }

    @Test
    public void testTransformationFireEvent() throws Exception {
        addHandlers(transformationMessageEvent, messageEventHandler, messageEventHandler2);
        LogEvent logEvent = new LogEvent(new org.pentaho.di.engine.api.remote.RemoteSource(ModelType.TRANSFORMATION, "Operation_ID"), logEntry);
        messageEventService.fireEvent(logEvent);
        Mockito.verify(messageEventHandler).execute(logEvent);
        Mockito.verify(messageEventHandler2).execute(logEvent);
    }
}

