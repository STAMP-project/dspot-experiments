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
package org.apache.camel.component.xchange.account;


import java.util.List;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.Wallet;


public class AccountProducerTest extends CamelTestSupport {
    @Test
    @SuppressWarnings("unchecked")
    public void testBalances() throws Exception {
        Assume.assumeTrue(hasAPICredentials());
        List<Balance> balances = template.requestBody("direct:balances", null, List.class);
        Assert.assertNotNull("Balances not null", balances);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWallets() throws Exception {
        Assume.assumeTrue(hasAPICredentials());
        List<Wallet> wallets = template.requestBody("direct:wallets", null, List.class);
        Assert.assertNotNull("Wallets not null", wallets);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFundingHistory() throws Exception {
        Assume.assumeTrue(hasAPICredentials());
        List<FundingRecord> records = template.requestBody("direct:fundingHistory", null, List.class);
        Assert.assertNotNull("Funding records not null", records);
    }
}

