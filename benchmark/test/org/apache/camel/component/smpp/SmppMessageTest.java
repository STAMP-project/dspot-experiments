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


import java.nio.charset.Charset;
import java.util.Set;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test class for <code>org.apache.camel.component.smpp.SmppMessage</code>
 */
public class SmppMessageTest {
    private SmppMessage message;

    private CamelContext camelContext = new DefaultCamelContext();

    @Test
    public void emptyConstructorShouldReturnAnInstanceWithoutACommand() {
        message = new SmppMessage(camelContext, null, new SmppConfiguration());
        Assert.assertNull(message.getCommand());
        Assert.assertTrue(message.getHeaders().isEmpty());
    }

    @Test
    public void alertNotificationConstructorShouldReturnAnInstanceWithACommandAndHeaderAttributes() {
        AlertNotification command = new AlertNotification();
        message = new SmppMessage(camelContext, command, new SmppConfiguration());
        Assert.assertTrue(((message.getCommand()) instanceof AlertNotification));
        Assert.assertTrue(message.getHeaders().isEmpty());
        Assert.assertTrue(message.isAlertNotification());
    }

    @Test
    public void testSmppMessageDataSm() {
        DataSm command = new DataSm();
        message = new SmppMessage(camelContext, command, new SmppConfiguration());
        Assert.assertTrue(((message.getCommand()) instanceof DataSm));
        Assert.assertTrue(message.getHeaders().isEmpty());
        Assert.assertTrue(message.isDataSm());
    }

    @Test
    public void testSmppMessageDeliverSm() {
        DeliverSm command = new DeliverSm();
        message = new SmppMessage(camelContext, command, new SmppConfiguration());
        Assert.assertTrue(((message.getCommand()) instanceof DeliverSm));
        Assert.assertTrue(message.getHeaders().isEmpty());
        Assert.assertTrue(message.isDeliverSm());
    }

    @Test
    public void testSmppMessageDeliverReceipt() {
        DeliverSm command = new DeliverSm();
        command.setSmscDeliveryReceipt();
        command.setShortMessage("id:2 sub:001 dlvrd:001 submit date:0908312310 done date:0908312311 stat:DELIVRD err:xxx Text:Hello SMPP world!".getBytes());
        message = new SmppMessage(camelContext, command, new SmppConfiguration());
        Assert.assertTrue(((message.getCommand()) instanceof DeliverSm));
        Assert.assertTrue(message.getHeaders().isEmpty());
        Assert.assertTrue(message.isDeliveryReceipt());
    }

    @Test
    public void newInstanceShouldReturnAnInstanceWithoutACommand() {
        message = new SmppMessage(camelContext, null, new SmppConfiguration());
        SmppMessage msg = message.newInstance();
        Assert.assertNotNull(msg);
        Assert.assertNull(msg.getCommand());
        Assert.assertTrue(msg.getHeaders().isEmpty());
    }

    @Test
    public void createBodyShouldNotMangle8bitDataCodingShortMessage() {
        final Set<String> encodings = Charset.availableCharsets().keySet();
        final byte[] dataCodings = new byte[]{ ((byte) (2)), ((byte) (4)), ((byte) (246)), ((byte) (244)) };
        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        DeliverSm command = new DeliverSm();
        SmppConfiguration config = new SmppConfiguration();
        for (byte dataCoding : dataCodings) {
            command.setDataCoding(dataCoding);
            command.setShortMessage(body);
            for (String encoding : encodings) {
                config.setEncoding(encoding);
                message = new SmppMessage(camelContext, command, config);
                Assert.assertArrayEquals(String.format("data coding=0x%02X; encoding=%s", dataCoding, encoding), body, ((byte[]) (message.createBody())));
            }
        }
    }

    @Test
    public void createBodyShouldReturnNullIfTheCommandIsNull() {
        message = new SmppMessage(camelContext, null, new SmppConfiguration());
        Assert.assertNull(message.createBody());
    }

    @Test
    public void createBodyShouldReturnNullIfTheCommandIsNotAMessageRequest() {
        AlertNotification command = new AlertNotification();
        message = new SmppMessage(camelContext, command, new SmppConfiguration());
        Assert.assertNull(message.createBody());
    }

    @Test
    public void createBodyShouldReturnTheShortMessageIfTheCommandIsAMessageRequest() {
        DeliverSm command = new DeliverSm();
        command.setShortMessage("Hello SMPP world!".getBytes());
        message = new SmppMessage(camelContext, command, new SmppConfiguration());
        Assert.assertEquals("Hello SMPP world!", message.createBody());
    }

    @Test
    public void toStringShouldReturnTheBodyIfTheCommandIsNull() {
        message = new SmppMessage(camelContext, null, new SmppConfiguration());
        Assert.assertEquals("SmppMessage: null", message.toString());
    }

    @Test
    public void toStringShouldReturnTheShortMessageIfTheCommandIsNotNull() {
        DeliverSm command = new DeliverSm();
        command.setShortMessage("Hello SMPP world!".getBytes());
        message = new SmppMessage(camelContext, command, new SmppConfiguration());
        Assert.assertEquals("SmppMessage: PDUHeader(0, 00000000, 00000000, 0)", message.toString());
    }
}

