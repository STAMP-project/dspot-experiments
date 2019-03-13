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
package org.apache.camel.component.aws.sdb;


import SdbConstants.DOMAIN_NAMES;
import SdbConstants.MAX_NUMBER_OF_DOMAINS;
import SdbConstants.NEXT_TOKEN;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class ListDomainsCommandTest {
    private ListDomainsCommand command;

    private AmazonSDBClientMock sdbClient;

    private SdbConfiguration configuration;

    private Exchange exchange;

    @SuppressWarnings({ "unchecked" })
    @Test
    public void execute() {
        exchange.getIn().setHeader(NEXT_TOKEN, "TOKEN1");
        command.execute();
        Assert.assertEquals(new Integer(5), sdbClient.listDomainsRequest.getMaxNumberOfDomains());
        Assert.assertEquals("TOKEN1", sdbClient.listDomainsRequest.getNextToken());
        List<String> domains = exchange.getIn().getHeader(DOMAIN_NAMES, List.class);
        Assert.assertEquals("TOKEN2", exchange.getIn().getHeader(NEXT_TOKEN));
        Assert.assertEquals(2, domains.size());
        Assert.assertTrue(domains.contains("DOMAIN1"));
        Assert.assertTrue(domains.contains("DOMAIN2"));
    }

    @Test
    public void determineMaxNumberOfDomains() {
        Assert.assertEquals(new Integer(5), this.command.determineMaxNumberOfDomains());
        exchange.getIn().setHeader(MAX_NUMBER_OF_DOMAINS, new Integer(10));
        Assert.assertEquals(new Integer(10), this.command.determineMaxNumberOfDomains());
    }
}

