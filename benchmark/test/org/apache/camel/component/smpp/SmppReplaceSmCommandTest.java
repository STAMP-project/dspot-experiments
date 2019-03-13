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
import SmppConstants.ALPHABET;
import SmppConstants.COMMAND;
import SmppConstants.DATA_CODING;
import SmppConstants.ID;
import SmppConstants.REGISTERED_DELIVERY;
import SmppConstants.SCHEDULE_DELIVERY_TIME;
import SmppConstants.SOURCE_ADDR;
import SmppConstants.SOURCE_ADDR_NPI;
import SmppConstants.SOURCE_ADDR_TON;
import SmppConstants.VALIDITY_PERIOD;
import TypeOfNumber.NATIONAL;
import TypeOfNumber.UNKNOWN;
import java.util.Date;
import java.util.TimeZone;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.session.SMPPSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SmppReplaceSmCommandTest {
    private static TimeZone defaultTimeZone;

    private SMPPSession session;

    private SmppConfiguration config;

    private SmppReplaceSmCommand command;

    @Test
    public void executeWithConfigurationData() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setBody("new short message body");
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("new short message body".getBytes()));
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
    }

    @Test
    public void execute() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(SCHEDULE_DELIVERY_TIME, new Date(1111111));
        exchange.getIn().setHeader(VALIDITY_PERIOD, new Date(2222222));
        exchange.getIn().setHeader(REGISTERED_DELIVERY, value());
        exchange.getIn().setBody("new short message body");
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(NATIONAL), ArgumentMatchers.eq(NumberingPlanIndicator.NATIONAL), ArgumentMatchers.eq("1818"), ArgumentMatchers.eq("-300101001831100+"), ArgumentMatchers.eq("-300101003702200+"), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("new short message body".getBytes()));
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
    }

    @Test
    public void executeWithValidityPeriodAsString() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(SCHEDULE_DELIVERY_TIME, new Date(1111111));
        exchange.getIn().setHeader(VALIDITY_PERIOD, "000003000000000R");// three days

        exchange.getIn().setHeader(REGISTERED_DELIVERY, value());
        exchange.getIn().setBody("new short message body");
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(NATIONAL), ArgumentMatchers.eq(NumberingPlanIndicator.NATIONAL), ArgumentMatchers.eq("1818"), ArgumentMatchers.eq("-300101001831100+"), ArgumentMatchers.eq("000003000000000R"), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq("new short message body".getBytes()));
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
    }

    @Test
    public void bodyWithSmscDefaultDataCodingNarrowedToCharset() throws Exception {
        final int dataCoding = 0;/* SMSC-default */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        byte[] bodyNarrowed = new byte[]{ '?', 'A', 'B', '\u0000', '?', ((byte) (127)), 'C', '?' };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(bodyNarrowed));
    }

    @Test
    public void bodyWithLatin1DataCodingNarrowedToCharset() throws Exception {
        final int dataCoding = 3;/* ISO-8859-1 (Latin1) */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        byte[] bodyNarrowed = new byte[]{ '?', 'A', 'B', '\u0000', '?', ((byte) (127)), 'C', '?' };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(bodyNarrowed));
    }

    @Test
    public void bodyWithSMPP8bitDataCodingNotModified() throws Exception {
        final int dataCoding = 4;/* SMPP 8-bit */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(body));
    }

    @Test
    public void bodyWithGSM8bitDataCodingNotModified() throws Exception {
        final int dataCoding = 247;/* GSM 8-bit class 3 */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(DATA_CODING, dataCoding);
        exchange.getIn().setBody(body);
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(body));
    }

    @Test
    public void eightBitDataCodingOverridesDefaultAlphabet() throws Exception {
        final int binDataCoding = 247;/* GSM 8-bit class 3 */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(ALPHABET, ALPHA_DEFAULT.value());
        exchange.getIn().setHeader(DATA_CODING, binDataCoding);
        exchange.getIn().setBody(body);
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(body));
    }

    @Test
    public void latin1DataCodingOverridesEightBitAlphabet() throws Exception {
        final int latin1DataCoding = 3;/* ISO-8859-1 (Latin1) */

        byte[] body = new byte[]{ ((byte) (255)), 'A', 'B', ((byte) (0)), ((byte) (255)), ((byte) (127)), 'C', ((byte) (255)) };
        byte[] bodyNarrowed = new byte[]{ '?', 'A', 'B', '\u0000', '?', ((byte) (127)), 'C', '?' };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        exchange.getIn().setHeader(ALPHABET, ALPHA_8_BIT.value());
        exchange.getIn().setHeader(DATA_CODING, latin1DataCoding);
        exchange.getIn().setBody(body);
        command.execute(exchange);
        Mockito.verify(session).replaceShortMessage(((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(UNKNOWN), ArgumentMatchers.eq(NumberingPlanIndicator.UNKNOWN), ArgumentMatchers.eq("1616"), ((String) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(new org.jsmpp.bean.RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE)), ArgumentMatchers.eq(((byte) (0))), ArgumentMatchers.eq(bodyNarrowed));
    }
}

