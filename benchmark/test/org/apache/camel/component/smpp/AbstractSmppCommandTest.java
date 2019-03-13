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


import OptionalParameter.Additional_status_info_text;
import OptionalParameter.Alert_on_message_delivery;
import OptionalParameter.Dest_addr_subunit;
import OptionalParameter.Dest_telematics_id;
import OptionalParameter.Qos_time_to_live;
import OptionalParameter.Source_subaddress;
import Tag.ADDITIONAL_STATUS_INFO_TEXT;
import Tag.ALERT_ON_MESSAGE_DELIVERY;
import Tag.DEST_ADDR_SUBUNIT;
import Tag.DEST_TELEMATICS_ID;
import Tag.QOS_TIME_TO_LIVE;
import Tag.SOURCE_SUBADDRESS;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.jsmpp.session.SMPPSession;
import org.junit.Assert;
import org.junit.Test;


public class AbstractSmppCommandTest {
    private SMPPSession session = new SMPPSession();

    private SmppConfiguration config = new SmppConfiguration();

    private AbstractSmppCommand command;

    @Test
    public void constructor() {
        Assert.assertSame(session, command.session);
        Assert.assertSame(config, command.config);
    }

    @Test
    public void getResponseMessage() {
        Exchange inOnlyExchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOnly);
        Exchange inOutExchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext(), ExchangePattern.InOut);
        Assert.assertSame(inOnlyExchange.getIn(), command.getResponseMessage(inOnlyExchange));
        Assert.assertSame(inOutExchange.getOut(), command.getResponseMessage(inOutExchange));
    }

    @Test
    public void determineTypeClass() throws Exception {
        Assert.assertSame(Source_subaddress.class, command.determineTypeClass(SOURCE_SUBADDRESS));
        Assert.assertSame(Additional_status_info_text.class, command.determineTypeClass(ADDITIONAL_STATUS_INFO_TEXT));
        Assert.assertSame(Dest_addr_subunit.class, command.determineTypeClass(DEST_ADDR_SUBUNIT));
        Assert.assertSame(Dest_telematics_id.class, command.determineTypeClass(DEST_TELEMATICS_ID));
        Assert.assertSame(Qos_time_to_live.class, command.determineTypeClass(QOS_TIME_TO_LIVE));
        Assert.assertSame(Alert_on_message_delivery.class, command.determineTypeClass(ALERT_ON_MESSAGE_DELIVERY));
    }
}

