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


import SmppCommandType.CANCEL_SM;
import SmppCommandType.DATA_SHORT_MESSAGE;
import SmppCommandType.QUERY_SM;
import SmppCommandType.REPLACE_SM;
import SmppCommandType.SUBMIT_MULTI;
import SmppCommandType.SUBMIT_SM;
import SmppConstants.COMMAND;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class SmppCommandTypeTest {
    private Exchange exchange;

    @Test
    public void createSmppSubmitSmCommand() {
        Assert.assertSame(SUBMIT_SM, SmppCommandType.fromExchange(exchange));
    }

    @Test
    public void createSmppSubmitMultiCommand() {
        exchange.getIn().setHeader(COMMAND, "SubmitMulti");
        Assert.assertSame(SUBMIT_MULTI, SmppCommandType.fromExchange(exchange));
    }

    @Test
    public void createSmppDataSmCommand() {
        exchange.getIn().setHeader(COMMAND, "DataSm");
        Assert.assertSame(DATA_SHORT_MESSAGE, SmppCommandType.fromExchange(exchange));
    }

    @Test
    public void createSmppReplaceSmCommand() {
        exchange.getIn().setHeader(COMMAND, "ReplaceSm");
        Assert.assertSame(REPLACE_SM, SmppCommandType.fromExchange(exchange));
    }

    @Test
    public void createSmppQuerySmCommand() {
        exchange.getIn().setHeader(COMMAND, "QuerySm");
        Assert.assertSame(QUERY_SM, SmppCommandType.fromExchange(exchange));
    }

    @Test
    public void createSmppCancelSmCommand() {
        exchange.getIn().setHeader(COMMAND, "CancelSm");
        Assert.assertSame(CANCEL_SM, SmppCommandType.fromExchange(exchange));
    }
}

