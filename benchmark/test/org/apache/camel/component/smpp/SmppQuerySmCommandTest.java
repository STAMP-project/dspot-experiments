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


import SmppConstants.COMMAND;
import SmppConstants.ERROR;
import SmppConstants.FINAL_DATE;
import SmppConstants.ID;
import SmppConstants.MESSAGE_STATE;
import SmppConstants.SOURCE_ADDR;
import SmppConstants.SOURCE_ADDR_NPI;
import SmppConstants.SOURCE_ADDR_TON;
import TypeOfNumber.NATIONAL;
import TypeOfNumber.UNKNOWN;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.bean.MessageState;
import org.jsmpp.session.SMPPSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SmppQuerySmCommandTest {
    private SMPPSession session;

    private SmppConfiguration config;

    private SmppQuerySmCommand command;

    @Test
    public void executeWithConfigurationData() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "QuerySm");
        exchange.getIn().setHeader(ID, "1");
        Mockito.when(session.queryShortMessage("1", UNKNOWN, NumberingPlanIndicator.UNKNOWN, "1616")).thenReturn(new org.jsmpp.session.QuerySmResult("-300101010000004+", MessageState.DELIVERED, ((byte) (0))));
        command.execute(exchange);
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
        Assert.assertEquals("DELIVERED", exchange.getOut().getHeader(MESSAGE_STATE));
        Assert.assertEquals(((byte) (0)), exchange.getOut().getHeader(ERROR));
        Assert.assertNotNull(exchange.getOut().getHeader(FINAL_DATE));
    }

    @Test
    public void execute() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        exchange.getIn().setHeader(COMMAND, "QuerySm");
        exchange.getIn().setHeader(ID, "1");
        exchange.getIn().setHeader(SOURCE_ADDR_TON, NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR_NPI, NumberingPlanIndicator.NATIONAL.value());
        exchange.getIn().setHeader(SOURCE_ADDR, "1818");
        Mockito.when(session.queryShortMessage("1", NATIONAL, NumberingPlanIndicator.NATIONAL, "1818")).thenReturn(new org.jsmpp.session.QuerySmResult("-300101010000004+", MessageState.DELIVERED, ((byte) (0))));
        command.execute(exchange);
        Assert.assertEquals("1", exchange.getOut().getHeader(ID));
        Assert.assertEquals("DELIVERED", exchange.getOut().getHeader(MESSAGE_STATE));
        Assert.assertEquals(((byte) (0)), exchange.getOut().getHeader(ERROR));
        Assert.assertNotNull(exchange.getOut().getHeader(FINAL_DATE));
    }
}

