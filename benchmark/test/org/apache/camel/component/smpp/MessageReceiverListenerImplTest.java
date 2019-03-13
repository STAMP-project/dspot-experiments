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


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.ExceptionHandler;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.SMPPSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MessageReceiverListenerImplTest {
    private MessageReceiverListenerImpl listener;

    private SmppEndpoint endpoint;

    private Processor processor;

    private ExceptionHandler exceptionHandler;

    @Test
    public void onAcceptAlertNotificationSuccess() throws Exception {
        AlertNotification alertNotification = Mockito.mock(AlertNotification.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Mockito.when(endpoint.createOnAcceptAlertNotificationExchange(alertNotification)).thenReturn(exchange);
        Mockito.when(exchange.getException()).thenReturn(null);
        listener.onAcceptAlertNotification(alertNotification);
        Mockito.verify(endpoint).createOnAcceptAlertNotificationExchange(alertNotification);
        Mockito.verify(processor).process(exchange);
    }

    @Test
    public void onAcceptDeliverSmException() throws Exception {
        DeliverSm deliverSm = Mockito.mock(DeliverSm.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Mockito.when(endpoint.createOnAcceptDeliverSmExchange(deliverSm)).thenReturn(exchange);
        Mockito.when(exchange.getException()).thenReturn(null);
        listener.onAcceptDeliverSm(deliverSm);
        Mockito.verify(endpoint).createOnAcceptDeliverSmExchange(deliverSm);
        Mockito.verify(processor).process(exchange);
    }

    @Test
    public void onAcceptDataSmSuccess() throws Exception {
        SMPPSession session = Mockito.mock(SMPPSession.class);
        DataSm dataSm = Mockito.mock(DataSm.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        OptionalParameter[] optionalParameters = new OptionalParameter[]{  };
        Mockito.when(endpoint.createOnAcceptDataSm(dataSm, "1")).thenReturn(exchange);
        Mockito.when(exchange.getException()).thenReturn(null);
        Mockito.when(dataSm.getOptionalParameters()).thenReturn(optionalParameters);
        DataSmResult result = listener.onAcceptDataSm(dataSm, session);
        Mockito.verify(endpoint).createOnAcceptDataSm(dataSm, "1");
        Mockito.verify(processor).process(exchange);
        Assert.assertEquals("1", result.getMessageId());
        Assert.assertSame(optionalParameters, result.getOptionalParameters());
    }
}

