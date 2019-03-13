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


import NumberingPlanIndicator.INTERNET;
import SmppConstants.COMMAND;
import SmppConstants.DEST_ADDR;
import SmppConstants.DEST_ADDR_NPI;
import SmppConstants.DEST_ADDR_TON;
import SmppConstants.ID;
import SmppConstants.OPTIONAL_PARAMETER;
import SmppConstants.OPTIONAL_PARAMETERS;
import SmppConstants.REGISTERED_DELIVERY;
import SmppConstants.SERVICE_TYPE;
import SmppConstants.SOURCE_ADDR;
import SmppConstants.SOURCE_ADDR_NPI;
import SmppConstants.SOURCE_ADDR_TON;
import Tag.ADDITIONAL_STATUS_INFO_TEXT;
import Tag.DEST_TELEMATICS_ID;
import TypeOfNumber.INTERNATIONAL;
import TypeOfNumber.NATIONAL;
import TypeOfNumber.UNKNOWN;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.Tag;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.MessageId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SmppDataSmCommandTest {
    private static TimeZone defaultTimeZone;

    private SMPPSession session;

    private SmppConfiguration config;

    private SmppDataSmCommand command;

    @Test
    public void executeWithConfigurationData() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "DataSm");
        Mockito.when(session.dataShortMessage(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1717"), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(new RegisteredDelivery(((byte) (1)))), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))))).thenReturn(new org.jsmpp.session.DataSmResult(new MessageId("1"), null));
        command.execute(exchange);
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
        Assert.assertNull(exchange.getOut().getHeader(OPTIONAL_PARAMETERS));
    }

    @Test
    public void execute() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "DataSm");
        exchange.getIn().setHeader(SERVICE_TYPE, "XXX");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(DEST_ADDR_TON, INTERNATIONAL.value());
        exchange.getIn().setHeader(DEST_ADDR_NPI, INTERNET.value());
        exchange.getIn().setHeader(DEST_ADDR, "1919");
        exchange.getIn().setHeader(REGISTERED_DELIVERY, value());
        Mockito.when(session.dataShortMessage(ArgumentMatchers.eq("XXX"), ArgumentMatchers.eq(NATIONAL), ArgumentMatchers.eq(NumberingPlanIndicator.NATIONAL), ArgumentMatchers.eq("1818"), ArgumentMatchers.eq(INTERNATIONAL), ArgumentMatchers.eq(INTERNET), ArgumentMatchers.eq("1919"), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(new RegisteredDelivery(((byte) (2)))), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))))).thenReturn(new org.jsmpp.session.DataSmResult(new MessageId("1"), null));
        command.execute(exchange);
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
        Assert.assertNull(exchange.getOut().getHeader(OPTIONAL_PARAMETERS));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void executeWithOptionalParameter() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "DataSm");
        Map<String, String> optionalParameters = new LinkedHashMap<>();
        optionalParameters.put("SOURCE_SUBADDRESS", "1292");
        optionalParameters.put("ADDITIONAL_STATUS_INFO_TEXT", "urgent");
        optionalParameters.put("DEST_ADDR_SUBUNIT", "4");
        optionalParameters.put("DEST_TELEMATICS_ID", "2");
        optionalParameters.put("QOS_TIME_TO_LIVE", "3600000");
        optionalParameters.put("ALERT_ON_MESSAGE_DELIVERY", null);
        // fall back test for vendor specific optional parameter
        optionalParameters.put("0x2150", "0815");
        optionalParameters.put("0x2151", "0816");
        optionalParameters.put("0x2152", "6");
        optionalParameters.put("0x2153", "9");
        optionalParameters.put("0x2154", "7400000");
        optionalParameters.put("0x2155", null);
        exchange.getIn().setHeader(OPTIONAL_PARAMETERS, optionalParameters);
        Mockito.when(session.dataShortMessage(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1717"), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(new RegisteredDelivery(((byte) (1)))), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))), ArgumentMatchers.eq(new OptionalParameter.Source_subaddress("1292".getBytes())), ArgumentMatchers.eq(new OptionalParameter.Additional_status_info_text("urgent")), ArgumentMatchers.eq(new OptionalParameter.Dest_addr_subunit(((byte) (4)))), ArgumentMatchers.eq(new OptionalParameter.Dest_telematics_id(((short) (2)))), ArgumentMatchers.eq(new OptionalParameter.Qos_time_to_live(3600000)), ArgumentMatchers.eq(new OptionalParameter.Alert_on_message_delivery(((byte) (0)))))).thenReturn(new org.jsmpp.session.DataSmResult(new MessageId("1"), new OptionalParameter[]{ new OptionalParameter.Source_subaddress("1292".getBytes()), new OptionalParameter.Additional_status_info_text("urgent"), new OptionalParameter.Dest_addr_subunit(((byte) (4))), new OptionalParameter.Dest_telematics_id(((short) (2))), new OptionalParameter.Qos_time_to_live(3600000), new OptionalParameter.Alert_on_message_delivery(((byte) (0))) }));
        command.execute(exchange);
        Assert.assertEquals(3, exchange.getOut().getHeaders().size());
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
        Map<String, String> optParamMap = exchange.getOut().getHeader(OPTIONAL_PARAMETERS, Map.class);
        Assert.assertEquals(6, optParamMap.size());
        Assert.assertEquals("1292", optParamMap.get("SOURCE_SUBADDRESS"));
        Assert.assertEquals("urgent", optParamMap.get("ADDITIONAL_STATUS_INFO_TEXT"));
        Assert.assertEquals("4", optParamMap.get("DEST_ADDR_SUBUNIT"));
        Assert.assertEquals("2", optParamMap.get("DEST_TELEMATICS_ID"));
        Assert.assertEquals("3600000", optParamMap.get("QOS_TIME_TO_LIVE"));
        Assert.assertEquals("0", optParamMap.get("ALERT_ON_MESSAGE_DELIVERY"));
        Map<Short, Object> optionalResultParameter = exchange.getOut().getHeader(OPTIONAL_PARAMETER, Map.class);
        Assert.assertEquals(6, optionalResultParameter.size());
        Assert.assertArrayEquals("1292".getBytes("UTF-8"), ((byte[]) (optionalResultParameter.get(Short.valueOf(((short) (514)))))));
        Assert.assertEquals("urgent", optionalResultParameter.get(Short.valueOf(((short) (29)))));
        Assert.assertEquals(Byte.valueOf(((byte) (4))), optionalResultParameter.get(Short.valueOf(((short) (5)))));
        Assert.assertEquals(Short.valueOf(((short) (2))), optionalResultParameter.get(Short.valueOf(((short) (8)))));
        Assert.assertEquals(Integer.valueOf(3600000), optionalResultParameter.get(Short.valueOf(((short) (23)))));
        Assert.assertEquals(((byte) (0)), optionalResultParameter.get(Short.valueOf(((short) (4876)))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void executeWithOptionalParameterNewStyle() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "DataSm");
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
        Mockito.when(session.dataShortMessage(ArgumentMatchers.eq("CMT"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1717"), ArgumentMatchers.eq(new ESMClass()), ArgumentMatchers.eq(new RegisteredDelivery(((byte) (1)))), ArgumentMatchers.eq(DataCodings.newInstance(((byte) (0)))), ArgumentMatchers.eq(new OptionalParameter.OctetString(Tag.SOURCE_SUBADDRESS, "1292")), ArgumentMatchers.eq(new OptionalParameter.COctetString(ADDITIONAL_STATUS_INFO_TEXT.code(), "urgent")), ArgumentMatchers.eq(new OptionalParameter.Byte(Tag.DEST_ADDR_SUBUNIT, ((byte) (4)))), ArgumentMatchers.eq(new OptionalParameter.Short(DEST_TELEMATICS_ID.code(), ((short) (2)))), ArgumentMatchers.eq(new OptionalParameter.Int(Tag.QOS_TIME_TO_LIVE, 3600000)), ArgumentMatchers.eq(new OptionalParameter.Null(Tag.ALERT_ON_MESSAGE_DELIVERY)), ArgumentMatchers.eq(new OptionalParameter.OctetString(((short) (8528)), "1292", "UTF-8")), ArgumentMatchers.eq(new OptionalParameter.COctetString(((short) (8529)), "0816")), ArgumentMatchers.eq(new OptionalParameter.Byte(((short) (8530)), ((byte) (6)))), ArgumentMatchers.eq(new OptionalParameter.Short(((short) (8531)), ((short) (9)))), ArgumentMatchers.eq(new OptionalParameter.Int(((short) (8532)), 7400000)), ArgumentMatchers.eq(new OptionalParameter.Null(((short) (8533)))))).thenReturn(new org.jsmpp.session.DataSmResult(new MessageId("1"), new OptionalParameter[]{ new OptionalParameter.Source_subaddress("1292".getBytes()), new OptionalParameter.Additional_status_info_text("urgent"), new OptionalParameter.Dest_addr_subunit(((byte) (4))), new OptionalParameter.Dest_telematics_id(((short) (2))), new OptionalParameter.Qos_time_to_live(3600000), new OptionalParameter.Alert_on_message_delivery(((byte) (0))) }));
        command.execute(exchange);
        Assert.assertEquals(3, exchange.getOut().getHeaders().size());
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
        Map<String, String> optParamMap = exchange.getOut().getHeader(OPTIONAL_PARAMETERS, Map.class);
        Assert.assertEquals(6, optParamMap.size());
        Assert.assertEquals("1292", optParamMap.get("SOURCE_SUBADDRESS"));
        Assert.assertEquals("urgent", optParamMap.get("ADDITIONAL_STATUS_INFO_TEXT"));
        Assert.assertEquals("4", optParamMap.get("DEST_ADDR_SUBUNIT"));
        Assert.assertEquals("2", optParamMap.get("DEST_TELEMATICS_ID"));
        Assert.assertEquals("3600000", optParamMap.get("QOS_TIME_TO_LIVE"));
        Assert.assertEquals("0", optParamMap.get("ALERT_ON_MESSAGE_DELIVERY"));
        Map<Short, Object> optionalResultParameter = exchange.getOut().getHeader(OPTIONAL_PARAMETER, Map.class);
        Assert.assertEquals(6, optionalResultParameter.size());
        Assert.assertArrayEquals("1292".getBytes("UTF-8"), ((byte[]) (optionalResultParameter.get(Short.valueOf(((short) (514)))))));
        Assert.assertEquals("urgent", optionalResultParameter.get(Short.valueOf(((short) (29)))));
        Assert.assertEquals(Byte.valueOf(((byte) (4))), optionalResultParameter.get(Short.valueOf(((short) (5)))));
        Assert.assertEquals(Short.valueOf(((short) (2))), optionalResultParameter.get(Short.valueOf(((short) (8)))));
        Assert.assertEquals(Integer.valueOf(3600000), optionalResultParameter.get(Short.valueOf(((short) (23)))));
        Assert.assertEquals(((byte) (0)), optionalResultParameter.get(Short.valueOf(((short) (4876)))));
    }
}

