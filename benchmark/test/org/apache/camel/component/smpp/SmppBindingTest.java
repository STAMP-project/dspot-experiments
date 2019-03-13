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


import DeliveryReceiptState.DELIVRD;
import NumberingPlanIndicator.INTERNET;
import NumberingPlanIndicator.NATIONAL;
import SmppConstants.COMMAND;
import SmppConstants.COMMAND_ID;
import SmppConstants.COMMAND_STATUS;
import SmppConstants.DATA_CODING;
import SmppConstants.DELIVERED;
import SmppConstants.DEST_ADDR;
import SmppConstants.DEST_ADDR_NPI;
import SmppConstants.DEST_ADDR_TON;
import SmppConstants.ERROR;
import SmppConstants.ESME_ADDR;
import SmppConstants.ESME_ADDR_NPI;
import SmppConstants.ESME_ADDR_TON;
import SmppConstants.FINAL_STATUS;
import SmppConstants.ID;
import SmppConstants.MESSAGE_TYPE;
import SmppConstants.OPTIONAL_PARAMETER;
import SmppConstants.OPTIONAL_PARAMETERS;
import SmppConstants.REGISTERED_DELIVERY;
import SmppConstants.SCHEDULE_DELIVERY_TIME;
import SmppConstants.SEQUENCE_NUMBER;
import SmppConstants.SERVICE_TYPE;
import SmppConstants.SOURCE_ADDR;
import SmppConstants.SOURCE_ADDR_NPI;
import SmppConstants.SOURCE_ADDR_TON;
import SmppConstants.SUBMITTED;
import SmppConstants.VALIDITY_PERIOD;
import SmppMessageType.DeliveryReceipt;
import TypeOfNumber.NETWORK_SPECIFIC;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.Tag;
import org.jsmpp.session.SMPPSession;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test class for <code>org.apache.camel.component.smpp.SmppBinding</code>
 */
public class SmppBindingTest {
    private SmppBinding binding;

    private CamelContext camelContext;

    @Test
    public void emptyConstructorShouldSetTheSmppConfiguration() {
        Assert.assertNotNull(binding.getConfiguration());
    }

    @Test
    public void constructorSmppConfigurationShouldSetTheSmppConfiguration() {
        SmppConfiguration configuration = new SmppConfiguration();
        binding = new SmppBinding(configuration);
        Assert.assertSame(configuration, binding.getConfiguration());
    }

    @Test
    public void createSmppMessageFromAlertNotificationShouldReturnASmppMessage() {
        AlertNotification alertNotification = new AlertNotification();
        alertNotification.setCommandId(1);
        alertNotification.setSequenceNumber(1);
        alertNotification.setSourceAddr("1616");
        alertNotification.setSourceAddrNpi(NATIONAL.value());
        alertNotification.setSourceAddrTon(TypeOfNumber.NATIONAL.value());
        alertNotification.setEsmeAddr("1717");
        alertNotification.setEsmeAddrNpi(NATIONAL.value());
        alertNotification.setEsmeAddrTon(TypeOfNumber.NATIONAL.value());
        SmppMessage smppMessage = binding.createSmppMessage(camelContext, alertNotification);
        Assert.assertNull(smppMessage.getBody());
        Assert.assertEquals(10, smppMessage.getHeaders().size());
        Assert.assertEquals(1, smppMessage.getHeader(SEQUENCE_NUMBER));
        Assert.assertEquals(1, smppMessage.getHeader(COMMAND_ID));
        Assert.assertEquals(0, smppMessage.getHeader(COMMAND_STATUS));
        Assert.assertEquals("1616", smppMessage.getHeader(SOURCE_ADDR));
        Assert.assertEquals(((byte) (8)), smppMessage.getHeader(SOURCE_ADDR_NPI));
        Assert.assertEquals(((byte) (2)), smppMessage.getHeader(SOURCE_ADDR_TON));
        Assert.assertEquals("1717", smppMessage.getHeader(ESME_ADDR));
        Assert.assertEquals(((byte) (8)), smppMessage.getHeader(ESME_ADDR_NPI));
        Assert.assertEquals(((byte) (2)), smppMessage.getHeader(ESME_ADDR_TON));
        Assert.assertEquals(SmppMessageType.AlertNotification.toString(), smppMessage.getHeader(MESSAGE_TYPE));
    }

    @Test
    public void createSmppMessageFromDeliveryReceiptShouldReturnASmppMessage() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setShortMessage("id:2 sub:001 dlvrd:001 submit date:0908312310 done date:0908312311 stat:DELIVRD err:xxx Text:Hello SMPP world!".getBytes());
        SmppMessage smppMessage = binding.createSmppMessage(camelContext, deliverSm);
        Assert.assertEquals("Hello SMPP world!", smppMessage.getBody());
        Assert.assertEquals(8, smppMessage.getHeaders().size());
        Assert.assertEquals("2", smppMessage.getHeader(ID));
        Assert.assertEquals(1, smppMessage.getHeader(DELIVERED));
        // To avoid the test failure when running in different TimeZone
        // assertEquals(new Date(1251753060000L), smppMessage.getHeader(SmppConstants.DONE_DATE));
        Assert.assertEquals("xxx", smppMessage.getHeader(ERROR));
        // assertEquals(new Date(1251753000000L), smppMessage.getHeader(SmppConstants.SUBMIT_DATE));
        Assert.assertEquals(1, smppMessage.getHeader(SUBMITTED));
        Assert.assertEquals(DELIVRD, smppMessage.getHeader(FINAL_STATUS));
        Assert.assertEquals(DeliveryReceipt.toString(), smppMessage.getHeader(MESSAGE_TYPE));
        Assert.assertNull(smppMessage.getHeader(OPTIONAL_PARAMETERS));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createSmppMessageFromDeliveryReceiptWithOptionalParametersShouldReturnASmppMessage() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setShortMessage("id:2 sub:001 dlvrd:001 submit date:0908312310 done date:0908312311 stat:DELIVRD err:xxx Text:Hello SMPP world!".getBytes());
        deliverSm.setOptionalParameters(new OptionalParameter.OctetString(Tag.SOURCE_SUBADDRESS, "OctetString"), new OptionalParameter.COctetString(((short) (29)), "COctetString"), new OptionalParameter.Byte(Tag.DEST_ADDR_SUBUNIT, ((byte) (1))), new OptionalParameter.Short(Tag.DEST_TELEMATICS_ID, ((short) (1))), new OptionalParameter.Int(Tag.QOS_TIME_TO_LIVE, 1), new OptionalParameter.Null(Tag.ALERT_ON_MESSAGE_DELIVERY));
        SmppMessage smppMessage = binding.createSmppMessage(camelContext, deliverSm);
        Assert.assertEquals("Hello SMPP world!", smppMessage.getBody());
        Assert.assertEquals(10, smppMessage.getHeaders().size());
        Assert.assertEquals("2", smppMessage.getHeader(ID));
        Assert.assertEquals(1, smppMessage.getHeader(DELIVERED));
        // To avoid the test failure when running in different TimeZone
        // assertEquals(new Date(1251753060000L), smppMessage.getHeader(SmppConstants.DONE_DATE));
        Assert.assertEquals("xxx", smppMessage.getHeader(ERROR));
        // assertEquals(new Date(1251753000000L), smppMessage.getHeader(SmppConstants.SUBMIT_DATE));
        Assert.assertEquals(1, smppMessage.getHeader(SUBMITTED));
        Assert.assertEquals(DELIVRD, smppMessage.getHeader(FINAL_STATUS));
        Assert.assertEquals(DeliveryReceipt.toString(), smppMessage.getHeader(MESSAGE_TYPE));
        Map<String, Object> optionalParameters = smppMessage.getHeader(OPTIONAL_PARAMETERS, Map.class);
        Assert.assertEquals(6, optionalParameters.size());
        Assert.assertEquals("OctetString", optionalParameters.get("SOURCE_SUBADDRESS"));
        Assert.assertEquals("COctetString", optionalParameters.get("ADDITIONAL_STATUS_INFO_TEXT"));
        Assert.assertEquals(Byte.valueOf(((byte) (1))), optionalParameters.get("DEST_ADDR_SUBUNIT"));
        Assert.assertEquals(Short.valueOf(((short) (1))), optionalParameters.get("DEST_TELEMATICS_ID"));
        Assert.assertEquals(Integer.valueOf(1), optionalParameters.get("QOS_TIME_TO_LIVE"));
        Assert.assertNull("0x00", optionalParameters.get("ALERT_ON_MESSAGE_DELIVERY"));
        Map<Short, Object> optionalParameter = smppMessage.getHeader(OPTIONAL_PARAMETER, Map.class);
        Assert.assertEquals(6, optionalParameter.size());
        Assert.assertArrayEquals("OctetString".getBytes("UTF-8"), ((byte[]) (optionalParameter.get(Short.valueOf(((short) (514)))))));
        Assert.assertEquals("COctetString", optionalParameter.get(Short.valueOf(((short) (29)))));
        Assert.assertEquals(Byte.valueOf(((byte) (1))), optionalParameter.get(Short.valueOf(((short) (5)))));
        Assert.assertEquals(Short.valueOf(((short) (1))), optionalParameter.get(Short.valueOf(((short) (8)))));
        Assert.assertEquals(Integer.valueOf(1), optionalParameter.get(Short.valueOf(((short) (23)))));
        Assert.assertNull("0x00", optionalParameter.get(Short.valueOf(((short) (4876)))));
    }

    @Test
    public void createSmppMessageFromDeliveryReceiptWithPayloadInOptionalParameterShouldReturnASmppMessage() {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setOptionalParameters(new org.jsmpp.bean.OptionalParameter.OctetString(Tag.MESSAGE_PAYLOAD, "id:2 sub:001 dlvrd:001 submit date:0908312310 done date:0908312311 stat:DELIVRD err:xxx Text:Hello SMPP world!"));
        try {
            SmppMessage smppMessage = binding.createSmppMessage(camelContext, deliverSm);
            Assert.assertEquals("Hello SMPP world!", smppMessage.getBody());
            Assert.assertEquals(10, smppMessage.getHeaders().size());
            Assert.assertEquals("2", smppMessage.getHeader(ID));
            Assert.assertEquals(1, smppMessage.getHeader(DELIVERED));
            Assert.assertEquals("xxx", smppMessage.getHeader(ERROR));
            Assert.assertEquals(1, smppMessage.getHeader(SUBMITTED));
            Assert.assertEquals(DELIVRD, smppMessage.getHeader(FINAL_STATUS));
            Assert.assertEquals(DeliveryReceipt.toString(), smppMessage.getHeader(MESSAGE_TYPE));
        } catch (Exception e) {
            Assert.fail("Should not throw exception while creating smppMessage.");
        }
    }

    @Test
    public void createSmppMessageFromDeliveryReceiptWithoutShortMessageShouldNotThrowException() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setOptionalParameters(new OptionalParameter.Short(((short) (8531)), ((short) (0))));
        try {
            SmppMessage smppMessage = binding.createSmppMessage(camelContext, deliverSm);
            Map<Short, Object> optionalParameter = smppMessage.getHeader(OPTIONAL_PARAMETER, Map.class);
            Assert.assertEquals(Short.valueOf(((short) (0))), optionalParameter.get(Short.valueOf(((short) (8531)))));
        } catch (Exception e) {
            Assert.fail("Should not throw exception while creating smppMessage in absence of shortMessage");
        }
    }

    @Test
    public void createSmppMessageFromDeliverSmShouldReturnASmppMessage() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setShortMessage("Hello SMPP world!".getBytes());
        deliverSm.setSequenceNumber(1);
        deliverSm.setCommandId(1);
        deliverSm.setSourceAddr("1818");
        deliverSm.setSourceAddrNpi(NATIONAL.value());
        deliverSm.setSourceAddrTon(TypeOfNumber.NATIONAL.value());
        deliverSm.setDestAddress("1919");
        deliverSm.setDestAddrNpi(INTERNET.value());
        deliverSm.setDestAddrTon(NETWORK_SPECIFIC.value());
        deliverSm.setScheduleDeliveryTime("090831230627004+");
        deliverSm.setValidityPeriod("090901230627004+");
        deliverSm.setServiceType("WAP");
        SmppMessage smppMessage = binding.createSmppMessage(camelContext, deliverSm);
        Assert.assertEquals("Hello SMPP world!", smppMessage.getBody());
        Assert.assertEquals(13, smppMessage.getHeaders().size());
        Assert.assertEquals(1, smppMessage.getHeader(SEQUENCE_NUMBER));
        Assert.assertEquals(1, smppMessage.getHeader(COMMAND_ID));
        Assert.assertEquals("1818", smppMessage.getHeader(SOURCE_ADDR));
        Assert.assertEquals(((byte) (8)), smppMessage.getHeader(SOURCE_ADDR_NPI));
        Assert.assertEquals(((byte) (2)), smppMessage.getHeader(SOURCE_ADDR_TON));
        Assert.assertEquals("1919", smppMessage.getHeader(DEST_ADDR));
        Assert.assertEquals(((byte) (14)), smppMessage.getHeader(DEST_ADDR_NPI));
        Assert.assertEquals(((byte) (3)), smppMessage.getHeader(DEST_ADDR_TON));
        Assert.assertEquals("090831230627004+", smppMessage.getHeader(SCHEDULE_DELIVERY_TIME));
        Assert.assertEquals("090901230627004+", smppMessage.getHeader(VALIDITY_PERIOD));
        Assert.assertEquals("WAP", smppMessage.getHeader(SERVICE_TYPE));
        Assert.assertEquals(SmppMessageType.DeliverSm.toString(), smppMessage.getHeader(MESSAGE_TYPE));
    }

    @Test
    public void createSmppMessageFromDeliverSmWithPayloadInOptionalParameterShouldReturnASmppMessage() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSequenceNumber(1);
        deliverSm.setCommandId(1);
        deliverSm.setSourceAddr("1818");
        deliverSm.setSourceAddrNpi(NATIONAL.value());
        deliverSm.setSourceAddrTon(TypeOfNumber.NATIONAL.value());
        deliverSm.setDestAddress("1919");
        deliverSm.setDestAddrNpi(INTERNET.value());
        deliverSm.setDestAddrTon(NETWORK_SPECIFIC.value());
        deliverSm.setScheduleDeliveryTime("090831230627004+");
        deliverSm.setValidityPeriod("090901230627004+");
        deliverSm.setServiceType("WAP");
        deliverSm.setOptionalParameters(new org.jsmpp.bean.OptionalParameter.OctetString(Tag.MESSAGE_PAYLOAD, "Hello SMPP world!"));
        SmppMessage smppMessage = binding.createSmppMessage(camelContext, deliverSm);
        Assert.assertEquals("Hello SMPP world!", smppMessage.getBody());
        Assert.assertEquals(13, smppMessage.getHeaders().size());
        Assert.assertEquals(1, smppMessage.getHeader(SEQUENCE_NUMBER));
        Assert.assertEquals(1, smppMessage.getHeader(COMMAND_ID));
        Assert.assertEquals("1818", smppMessage.getHeader(SOURCE_ADDR));
        Assert.assertEquals(((byte) (8)), smppMessage.getHeader(SOURCE_ADDR_NPI));
        Assert.assertEquals(((byte) (2)), smppMessage.getHeader(SOURCE_ADDR_TON));
        Assert.assertEquals("1919", smppMessage.getHeader(DEST_ADDR));
        Assert.assertEquals(((byte) (14)), smppMessage.getHeader(DEST_ADDR_NPI));
        Assert.assertEquals(((byte) (3)), smppMessage.getHeader(DEST_ADDR_TON));
        Assert.assertEquals("090831230627004+", smppMessage.getHeader(SCHEDULE_DELIVERY_TIME));
        Assert.assertEquals("090901230627004+", smppMessage.getHeader(VALIDITY_PERIOD));
        Assert.assertEquals("WAP", smppMessage.getHeader(SERVICE_TYPE));
        Assert.assertEquals(SmppMessageType.DeliverSm.toString(), smppMessage.getHeader(MESSAGE_TYPE));
    }

    @Test
    public void createSmppMessageFromDataSmShouldReturnASmppMessage() throws Exception {
        DataSm dataSm = new DataSm();
        dataSm.setSequenceNumber(1);
        dataSm.setCommandId(1);
        dataSm.setCommandStatus(0);
        dataSm.setSourceAddr("1818");
        dataSm.setSourceAddrNpi(NATIONAL.value());
        dataSm.setSourceAddrTon(TypeOfNumber.NATIONAL.value());
        dataSm.setDestAddress("1919");
        dataSm.setDestAddrNpi(NATIONAL.value());
        dataSm.setDestAddrTon(TypeOfNumber.NATIONAL.value());
        dataSm.setServiceType("WAP");
        dataSm.setRegisteredDelivery(((byte) (0)));
        SmppMessage smppMessage = binding.createSmppMessage(camelContext, dataSm, "1");
        Assert.assertNull(smppMessage.getBody());
        Assert.assertEquals(14, smppMessage.getHeaders().size());
        Assert.assertEquals("1", smppMessage.getHeader(ID));
        Assert.assertEquals(1, smppMessage.getHeader(SEQUENCE_NUMBER));
        Assert.assertEquals(1, smppMessage.getHeader(COMMAND_ID));
        Assert.assertEquals(0, smppMessage.getHeader(COMMAND_STATUS));
        Assert.assertEquals("1818", smppMessage.getHeader(SOURCE_ADDR));
        Assert.assertEquals(((byte) (8)), smppMessage.getHeader(SOURCE_ADDR_NPI));
        Assert.assertEquals(((byte) (2)), smppMessage.getHeader(SOURCE_ADDR_TON));
        Assert.assertEquals("1919", smppMessage.getHeader(DEST_ADDR));
        Assert.assertEquals(((byte) (8)), smppMessage.getHeader(DEST_ADDR_NPI));
        Assert.assertEquals(((byte) (2)), smppMessage.getHeader(DEST_ADDR_TON));
        Assert.assertEquals("WAP", smppMessage.getHeader(SERVICE_TYPE));
        Assert.assertEquals(((byte) (0)), smppMessage.getHeader(REGISTERED_DELIVERY));
        Assert.assertEquals(((byte) (0)), smppMessage.getHeader(DATA_CODING));
        Assert.assertEquals(SmppMessageType.DataSm.toString(), smppMessage.getHeader(MESSAGE_TYPE));
    }

    @Test
    public void createSmppMessageFrom8bitDataCodingDeliverSmShouldNotModifyBody() throws Exception {
        final Set<String> encodings = Charset.availableCharsets().keySet();
        final byte[] dataCodings = new byte[]{ ((byte) (2)), ((byte) (4)), ((byte) (246)), ((byte) (244)) };
        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        DeliverSm deliverSm = new DeliverSm();
        for (byte dataCoding : dataCodings) {
            deliverSm.setDataCoding(dataCoding);
            deliverSm.setShortMessage(body);
            for (String encoding : encodings) {
                binding.getConfiguration().setEncoding(encoding);
                SmppMessage smppMessage = binding.createSmppMessage(camelContext, deliverSm);
                Assert.assertArrayEquals(String.format("data coding=0x%02X; encoding=%s", dataCoding, encoding), body, smppMessage.getBody(byte[].class));
            }
        }
    }

    @Test
    public void getterShouldReturnTheSetValues() {
        SmppConfiguration configuration = new SmppConfiguration();
        binding.setConfiguration(configuration);
        Assert.assertSame(configuration, binding.getConfiguration());
    }

    @Test
    public void createSmppSubmitSmCommand() {
        SMPPSession session = new SMPPSession();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        SmppCommand command = binding.createSmppCommand(session, exchange);
        Assert.assertTrue((command instanceof SmppSubmitSmCommand));
    }

    @Test
    public void createSmppSubmitMultiCommand() {
        SMPPSession session = new SMPPSession();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        SmppCommand command = binding.createSmppCommand(session, exchange);
        Assert.assertTrue((command instanceof SmppSubmitMultiCommand));
    }

    @Test
    public void createSmppDataSmCommand() {
        SMPPSession session = new SMPPSession();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(COMMAND, "DataSm");
        SmppCommand command = binding.createSmppCommand(session, exchange);
        Assert.assertTrue((command instanceof SmppDataSmCommand));
    }

    @Test
    public void createSmppReplaceSmCommand() {
        SMPPSession session = new SMPPSession();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        SmppCommand command = binding.createSmppCommand(session, exchange);
        Assert.assertTrue((command instanceof SmppReplaceSmCommand));
    }

    @Test
    public void createSmppQuerySmCommand() {
        SMPPSession session = new SMPPSession();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(COMMAND, "QuerySm");
        SmppCommand command = binding.createSmppCommand(session, exchange);
        Assert.assertTrue((command instanceof SmppQuerySmCommand));
    }

    @Test
    public void createSmppCancelSmCommand() {
        SMPPSession session = new SMPPSession();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(COMMAND, "CancelSm");
        SmppCommand command = binding.createSmppCommand(session, exchange);
        Assert.assertTrue((command instanceof SmppCancelSmCommand));
    }
}

