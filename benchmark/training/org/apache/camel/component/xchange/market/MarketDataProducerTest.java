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
package org.apache.camel.component.xchange.market;


import CurrencyPair.EOS_ETH;
import org.apache.camel.component.xchange.XChangeConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.dto.marketdata.Ticker;


public class MarketDataProducerTest extends CamelTestSupport {
    @Test
    public void testTicker() throws Exception {
        Ticker ticker = template.requestBody("direct:ticker", EOS_ETH, Ticker.class);
        Assert.assertNotNull("Ticker not null", ticker);
        ticker = template.requestBodyAndHeader("direct:ticker", null, XChangeConfiguration.HEADER_CURRENCY_PAIR, EOS_ETH, Ticker.class);
        Assert.assertNotNull("Ticker not null", ticker);
    }

    @Test
    public void testTickerBTCUSDT() throws Exception {
        Ticker ticker = template.requestBody("direct:tickerBTCUSDT", null, Ticker.class);
        Assert.assertNotNull("Ticker not null", ticker);
    }
}

