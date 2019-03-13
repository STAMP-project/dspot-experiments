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
package org.apache.camel.component.xchange.metadata;


import Currency.ETH;
import CurrencyPair.EOS_ETH;
import java.util.List;
import org.apache.camel.component.xchange.XChangeConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;


public class MetaDataProducerTest extends CamelTestSupport {
    @Test
    @SuppressWarnings("unchecked")
    public void testCurrencies() throws Exception {
        List<Currency> currencies = template.requestBody("direct:currencies", null, List.class);
        Assert.assertNotNull("Currencies not null", currencies);
        Assert.assertTrue("Contains ETH", currencies.contains(ETH));
    }

    @Test
    public void testCurrencyMetaData() throws Exception {
        CurrencyMetaData metadata = template.requestBody("direct:currencyMetaData", ETH, CurrencyMetaData.class);
        Assert.assertNotNull("CurrencyMetaData not null", metadata);
        metadata = template.requestBodyAndHeader("direct:currencyMetaData", null, XChangeConfiguration.HEADER_CURRENCY, ETH, CurrencyMetaData.class);
        Assert.assertNotNull("CurrencyMetaData not null", metadata);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCurrencyPairs() throws Exception {
        List<CurrencyPair> pairs = template.requestBody("direct:currencyPairs", null, List.class);
        Assert.assertNotNull("Pairs not null", pairs);
        Assert.assertTrue("Contains EOS/ETH", pairs.contains(EOS_ETH));
    }

    @Test
    public void testCurrencyPairMetaData() throws Exception {
        CurrencyPairMetaData metadata = template.requestBody("direct:currencyPairMetaData", EOS_ETH, CurrencyPairMetaData.class);
        Assert.assertNotNull("CurrencyPairMetaData not null", metadata);
        metadata = template.requestBodyAndHeader("direct:currencyPairMetaData", null, XChangeConfiguration.HEADER_CURRENCY_PAIR, EOS_ETH, CurrencyPairMetaData.class);
        Assert.assertNotNull("CurrencyPairMetaData not null", metadata);
    }
}

