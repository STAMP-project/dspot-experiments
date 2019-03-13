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


import Alphabet.ALPHA_8_BIT;
import Alphabet.ALPHA_DEFAULT;
import NumberingPlanIndicator.INTERNET;
import ReplaceIfPresentFlag.DEFAULT;
import ReplaceIfPresentFlag.REPLACE;
import SmppConstants.ALPHABET;
import SmppConstants.COMMAND;
import SmppConstants.DATA_CODING;
import SmppConstants.DEST_ADDR;
import SmppConstants.DEST_ADDR_NPI;
import SmppConstants.DEST_ADDR_TON;
import SmppConstants.ERROR;
import SmppConstants.ID;
import SmppConstants.OPTIONAL_PARAMETER;
import SmppConstants.OPTIONAL_PARAMETERS;
import SmppConstants.PRIORITY_FLAG;
import SmppConstants.PROTOCOL_ID;
import SmppConstants.REGISTERED_DELIVERY;
import SmppConstants.REPLACE_IF_PRESENT_FLAG;
import SmppConstants.SCHEDULE_DELIVERY_TIME;
import SmppConstants.SENT_MESSAGE_COUNT;
import SmppConstants.SOURCE_ADDR;
import SmppConstants.SOURCE_ADDR_NPI;
import SmppConstants.SOURCE_ADDR_TON;
import SmppConstants.VALIDITY_PERIOD;
import Tag.ADDITIONAL_STATUS_INFO_TEXT;
import Tag.DEST_TELEMATICS_ID;
import TypeOfNumber.INTERNATIONAL;
import TypeOfNumber.NATIONAL;
import TypeOfNumber.UNKNOWN;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.bean.Address;
import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.Tag;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.SubmitMultiResult;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.bean.UnsuccessDelivery;
import org.jsmpp.session.SMPPSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SmppSubmitMultiCommandTest {
    private static TimeZone defaultTimeZone;

    private SMPPSession session;

    private SmppConfiguration config;

    private SmppSubmitMultiCommand command;

    @Test
    public void executeWithConfigurationData() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setBody("short message body");
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(new Address[]{ new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717") }), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(((byte) (1))), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(DEFAULT), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("short message body".getBytes()))).thenReturn(new SubmitMultiResult("1", new UnsuccessDelivery(new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717"), 0)));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
        Assert.assertEquals(1, exchange.getOut().getHeader(SENT_MESSAGE_COUNT));
        Assert.assertNotNull(exchange.getOut().getHeader(ERROR));
    }

    @Test
    public void execute() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(DEST_ADDR_TON, INTERNATIONAL.value());
        exchange.getIn().setHeader(DEST_ADDR_NPI, INTERNET.value());
        exchange.getIn().setHeader(DEST_ADDR, Arrays.asList("1919"));
        exchange.getIn().setHeader(SCHEDULE_DELIVERY_TIME, new Date(1111111));
        exchange.getIn().setHeader(VALIDITY_PERIOD, new Date(2222222));
        exchange.getIn().setHeader(PROTOCOL_ID, ((byte) (1)));
        exchange.getIn().setHeader(PRIORITY_FLAG, ((byte) (2)));
        exchange.getIn().setHeader(REGISTERED_DELIVERY, value());
        exchange.getIn().setHeader(REPLACE_IF_PRESENT_FLAG, REPLACE.value());
        exchange.getIn().setBody("short message body");
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(NATIONAL), ArgumentMatchers.eq(NumberingPlanIndicator.NATIONAL), ArgumentMatchers.eq("1818"), ArgumentMatchers.eq(new Address[]{ new Address(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.INTERNET, "1919") }), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (1))), ArgumentMatchers.eq(((byte) (2))), ArgumentMatchers.eq("-300101001831100+"), ArgumentMatchers.eq("-300101003702200+"), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.FAILURE)), ArgumentMatchers.eq(REPLACE), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("short message body".getBytes()))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
        Assert.assertEquals(1, exchange.getOut().getHeader(SENT_MESSAGE_COUNT));
        Assert.assertNull(exchange.getOut().getHeader(ERROR));
    }

    @Test
    public void executeWithValidityPeriodAsString() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(DEST_ADDR_TON, INTERNATIONAL.value());
        exchange.getIn().setHeader(DEST_ADDR_NPI, INTERNET.value());
        exchange.getIn().setHeader(DEST_ADDR, Arrays.asList("1919"));
        exchange.getIn().setHeader(SCHEDULE_DELIVERY_TIME, new Date(1111111));
        exchange.getIn().setHeader(VALIDITY_PERIOD, "000003000000000R");// three days

        exchange.getIn().setHeader(PROTOCOL_ID, ((byte) (1)));
        exchange.getIn().setHeader(PRIORITY_FLAG, ((byte) (2)));
        exchange.getIn().setHeader(REGISTERED_DELIVERY, value());
        exchange.getIn().setHeader(REPLACE_IF_PRESENT_FLAG, REPLACE.value());
        exchange.getIn().setBody("short message body");
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(NATIONAL), ArgumentMatchers.eq(NumberingPlanIndicator.NATIONAL), ArgumentMatchers.eq("1818"), ArgumentMatchers.eq(new Address[]{ new Address(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.INTERNET, "1919") }), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (1))), ArgumentMatchers.eq(((byte) (2))), ArgumentMatchers.eq("-300101001831100+"), ArgumentMatchers.eq("000003000000000R"), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.FAILURE)), ArgumentMatchers.eq(REPLACE), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("short message body".getBytes()))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
        Assert.assertEquals(1, exchange.getOut().getHeader(SENT_MESSAGE_COUNT));
        Assert.assertNull(exchange.getOut().getHeader(ERROR));
    }

    @Test
    public void bodyWithSmscDefaultDataCodingNarrowedToCharset() throws Exception {
        final byte dataCoding = ((byte) (0));/* SMSC-default */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        byte[] bodyNarrowed = new byte[]{ '?', 'A', 'B', '\u0000', '?', ((byte) (127)), 'C', '?' };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        Address[] destAddrs = new Address[]{ new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717") };
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(destAddrs), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(((byte) (1))), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(DEFAULT), ArgumentMatchers.eq(DataCodings.newInstance(dataCoding)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(bodyNarrowed))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
    }

    @Test
    public void bodyWithLatin1DataCodingNarrowedToCharset() throws Exception {
        final byte dataCoding = ((byte) (3));/* ISO-8859-1 (Latin1) */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        byte[] bodyNarrowed = new byte[]{ '?', 'A', 'B', '\u0000', '?', ((byte) (127)), 'C', '?' };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        Address[] destAddrs = new Address[]{ new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717") };
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(destAddrs), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(((byte) (1))), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(DEFAULT), ArgumentMatchers.eq(DataCodings.newInstance(dataCoding)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(bodyNarrowed))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
    }

    @Test
    public void bodyWithSMPP8bitDataCodingNotModified() throws Exception {
        final byte dataCoding = ((byte) (4));/* SMPP 8-bit */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        Address[] destAddrs = new Address[]{ new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717") };
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(destAddrs), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(((byte) (1))), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(DEFAULT), ArgumentMatchers.eq(DataCodings.newInstance(dataCoding)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(body))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
    }

    @Test
    public void bodyWithGSM8bitDataCodingNotModified() throws Exception {
        final byte dataCoding = ((byte) (247));/* GSM 8-bit class 3 */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        Address[] destAddrs = new Address[]{ new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717") };
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(destAddrs), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(((byte) (1))), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(DEFAULT), ArgumentMatchers.eq(DataCodings.newInstance(dataCoding)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(body))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
    }

    @Test
    public void eightBitDataCodingOverridesDefaultAlphabet() throws Exception {
        final byte binDataCoding = ((byte) (4));/* SMPP 8-bit */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(ALPHABET, ALPHA_DEFAULT.value());
        exchange.getIn().setHeader(DATA_CODING, binDataCoding);
        exchange.getIn().setBody(body);
        Address[] destAddrs = new Address[]{ new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717") };
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(destAddrs), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(((byte) (1))), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(DEFAULT), ArgumentMatchers.eq(DataCodings.newInstance(binDataCoding)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(body))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
    }

    @Test
    public void latin1DataCodingOverridesEightBitAlphabet() throws Exception {
        final byte latin1DataCoding = ((byte) (3));/* ISO-8859-1 (Latin1) */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        byte[] bodyNarrowed = new byte[]{ '?', 'A', 'B', '\u0000', '?', ((byte) (127)), 'C', '?' };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(ALPHABET, ALPHA_8_BIT.value());
        exchange.getIn().setHeader(DATA_CODING, latin1DataCoding);
        exchange.getIn().setBody(body);
        Address[] destAddrs = new Address[]{ new Address(TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717") };
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(destAddrs), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(((byte) (1))), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(DEFAULT), ArgumentMatchers.eq(DataCodings.newInstance(latin1DataCoding)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(bodyNarrowed))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
    }

    @Test
    public void executeWithOptionalParameter() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(DEST_ADDR_TON, INTERNATIONAL.value());
        exchange.getIn().setHeader(DEST_ADDR_NPI, INTERNET.value());
        exchange.getIn().setHeader(DEST_ADDR, Arrays.asList("1919"));
        exchange.getIn().setHeader(SCHEDULE_DELIVERY_TIME, new Date(1111111));
        exchange.getIn().setHeader(VALIDITY_PERIOD, new Date(2222222));
        exchange.getIn().setHeader(PROTOCOL_ID, ((byte) (1)));
        exchange.getIn().setHeader(PRIORITY_FLAG, ((byte) (2)));
        exchange.getIn().setHeader(REGISTERED_DELIVERY, value());
        exchange.getIn().setHeader(REPLACE_IF_PRESENT_FLAG, REPLACE.value());
        Map<String, String> optionalParameters = new LinkedHashMap<>();
        optionalParameters.put("SOURCE_SUBADDRESS", "1292");
        optionalParameters.put("ADDITIONAL_STATUS_INFO_TEXT", "urgent");
        optionalParameters.put("DEST_ADDR_SUBUNIT", "4");
        optionalParameters.put("DEST_TELEMATICS_ID", "2");
        optionalParameters.put("QOS_TIME_TO_LIVE", "3600000");
        optionalParameters.put("ALERT_ON_MESSAGE_DELIVERY", null);
        exchange.getIn().setHeader(OPTIONAL_PARAMETERS, optionalParameters);
        exchange.getIn().setBody("short message body");
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(NATIONAL), ArgumentMatchers.eq(NumberingPlanIndicator.NATIONAL), ArgumentMatchers.eq("1818"), ArgumentMatchers.eq(new Address[]{ new Address(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.INTERNET, "1919") }), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (1))), ArgumentMatchers.eq(((byte) (2))), ArgumentMatchers.eq("-300101001831100+"), ArgumentMatchers.eq("-300101003702200+"), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.FAILURE)), ArgumentMatchers.eq(REPLACE), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("short message body".getBytes()), ArgumentMatchers.eq(new OptionalParameter.Source_subaddress("1292".getBytes())), ArgumentMatchers.eq(new OptionalParameter.Additional_status_info_text("urgent".getBytes())), ArgumentMatchers.eq(new OptionalParameter.Dest_addr_subunit(((byte) (4)))), ArgumentMatchers.eq(new OptionalParameter.Dest_telematics_id(((short) (2)))), ArgumentMatchers.eq(new OptionalParameter.Qos_time_to_live(3600000)), ArgumentMatchers.eq(new OptionalParameter.Alert_on_message_delivery("O".getBytes())))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
        Assert.assertEquals(1, exchange.getOut().getHeader(SENT_MESSAGE_COUNT));
        Assert.assertNull(exchange.getOut().getHeader(ERROR));
    }

    @Test
    public void executeWithOptionalParameterNewStyle() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(DEST_ADDR_TON, INTERNATIONAL.value());
        exchange.getIn().setHeader(DEST_ADDR_NPI, INTERNET.value());
        exchange.getIn().setHeader(DEST_ADDR, Arrays.asList("1919"));
        exchange.getIn().setHeader(SCHEDULE_DELIVERY_TIME, new Date(1111111));
        exchange.getIn().setHeader(VALIDITY_PERIOD, new Date(2222222));
        exchange.getIn().setHeader(PROTOCOL_ID, ((byte) (1)));
        exchange.getIn().setHeader(PRIORITY_FLAG, ((byte) (2)));
        exchange.getIn().setHeader(REGISTERED_DELIVERY, value());
        exchange.getIn().setHeader(REPLACE_IF_PRESENT_FLAG, REPLACE.value());
        Map<Short, Object> optionalParameters = new LinkedHashMap<>();
        // standard optional parameter
        optionalParameters.put(Short.valueOf(((short) (514))), "1292".getBytes("UTF-8"));
        optionalParameters.put(Short.valueOf(((short) (29))), "urgent");
        optionalParameters.put(Short.valueOf(((short) (5))), Byte.valueOf("4"));
        optionalParameters.put(Short.valueOf(((short) (8))), Short.valueOf(((short) (2))));
        optionalParameters.put(Short.valueOf(((short) (23))), Integer.valueOf(3600000));
        optionalParameters.put(Short.valueOf(((short) (4876))), null);
        // vendor specific optional parameter
        optionalParameters.put(Short.valueOf(((short) (8528))), "0815".getBytes("UTF-8"));
        optionalParameters.put(Short.valueOf(((short) (8529))), "0816");
        optionalParameters.put(Short.valueOf(((short) (8530))), Byte.valueOf("6"));
        optionalParameters.put(Short.valueOf(((short) (8531))), Short.valueOf(((short) (9))));
        optionalParameters.put(Short.valueOf(((short) (8532))), Integer.valueOf(7400000));
        optionalParameters.put(Short.valueOf(((short) (8533))), null);
        exchange.getIn().setHeader(OPTIONAL_PARAMETER, optionalParameters);
        exchange.getIn().setBody("short message body");
        Mockito.when(session.submitMultiple(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(NATIONAL), ArgumentMatchers.eq(NumberingPlanIndicator.NATIONAL), ArgumentMatchers.eq("1818"), ArgumentMatchers.eq(new Address[]{ new Address(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.INTERNET, "1919") }), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(((byte) (1))), ArgumentMatchers.eq(((byte) (2))), ArgumentMatchers.eq("-300101001831100+"), ArgumentMatchers.eq("-300101003702200+"), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.FAILURE)), ArgumentMatchers.eq(REPLACE), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("short message body".getBytes()), ArgumentMatchers.eq(new OptionalParameter.OctetString(Tag.SOURCE_SUBADDRESS, "1292")), ArgumentMatchers.eq(new OptionalParameter.COctetString(ADDITIONAL_STATUS_INFO_TEXT.code(), "urgent")), ArgumentMatchers.eq(new OptionalParameter.Byte(Tag.DEST_ADDR_SUBUNIT, ((byte) (4)))), ArgumentMatchers.eq(new OptionalParameter.Short(DEST_TELEMATICS_ID.code(), ((short) (2)))), ArgumentMatchers.eq(new OptionalParameter.Int(Tag.QOS_TIME_TO_LIVE, 3600000)), ArgumentMatchers.eq(new OptionalParameter.Null(Tag.ALERT_ON_MESSAGE_DELIVERY)), ArgumentMatchers.eq(new OptionalParameter.OctetString(((short) (8528)), "1292", "UTF-8")), ArgumentMatchers.eq(new OptionalParameter.COctetString(((short) (8529)), "0816")), ArgumentMatchers.eq(new OptionalParameter.Byte(((short) (8530)), ((byte) (6)))), ArgumentMatchers.eq(new OptionalParameter.Short(((short) (8531)), ((short) (9)))), ArgumentMatchers.eq(new OptionalParameter.Int(((short) (8532)), 7400000)), ArgumentMatchers.eq(new OptionalParameter.Null(((short) (8533)))))).thenReturn(new SubmitMultiResult("1"));
        command.execute(exchange);
        Assert.assertEquals(Arrays.asList("1"), exchange.getOut().getHeader(ID));
        Assert.assertEquals(1, exchange.getOut().getHeader(SENT_MESSAGE_COUNT));
        Assert.assertNull(exchange.getOut().getHeader(ERROR));
    }
}

