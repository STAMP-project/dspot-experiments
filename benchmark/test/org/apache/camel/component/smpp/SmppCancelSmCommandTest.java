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
import SmppConstants.SERVICE_TYPE;
import SmppConstants.SOURCE_ADDR;
import SmppConstants.SOURCE_ADDR_NPI;
import SmppConstants.SOURCE_ADDR_TON;
import TypeOfNumber.INTERNATIONAL;
import TypeOfNumber.NATIONAL;
import TypeOfNumber.UNKNOWN;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.session.SMPPSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SmppCancelSmCommandTest {
    private SMPPSession session;

    private SmppConfiguration config;

    private SmppCancelSmCommand command;

    @Test
    public void executeWithConfigurationData() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "CancelSm");
        exchange.getIn().setHeader(ID, "1");
        command.execute(exchange);
        Mockito.verify(session).cancelShortMessage("", "1", UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1616", UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1717");
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
    }

    @Test
    public void execute() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "CancelSm");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SERVICE_TYPE, "XXX");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        exchange.getIn().setHeader(DEST_ADDR_TON, INTERNATIONAL.value());
        exchange.getIn().setHeader(DEST_ADDR_NPI, INTERNET.value());
        exchange.getIn().setHeader(DEST_ADDR, "1919");
        command.execute(exchange);
        Mockito.verify(session).cancelShortMessage("XXX", "1", NATIONAL, NumberingPlanIndicator.NATIONAL, "1818", INTERNATIONAL, INTERNET, "1919");
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
    }
}

