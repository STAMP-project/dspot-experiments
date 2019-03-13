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


import SdbConstants.CONSISTENT_READ;
import SdbConstants.DOMAIN_NAME;
import SdbConstants.ITEM_NAME;
import SdbConstants.NEXT_TOKEN;
import SdbConstants.UPDATE_CONDITION;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class AbstractSdbCommandTest {
    private AbstractSdbCommand command;

    private AmazonSimpleDB sdbClient;

    private SdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void determineDomainName() {
        Assert.assertEquals("DOMAIN1", this.command.determineDomainName());
        exchange.getIn().setHeader(DOMAIN_NAME, "DOMAIN2");
        Assert.assertEquals("DOMAIN2", this.command.determineDomainName());
    }

    @Test
    public void determineItemName() {
        try {
            this.command.determineItemName();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("AWS SDB Item Name header is missing.", e.getMessage());
        }
        exchange.getIn().setHeader(ITEM_NAME, "ITEM1");
        Assert.assertEquals("ITEM1", this.command.determineItemName());
    }

    @Test
    public void determineConsistentRead() {
        Assert.assertEquals(Boolean.TRUE, this.command.determineConsistentRead());
        exchange.getIn().setHeader(CONSISTENT_READ, Boolean.FALSE);
        Assert.assertEquals(Boolean.FALSE, this.command.determineConsistentRead());
    }

    @Test
    public void determineUpdateCondition() {
        Assert.assertNull(this.command.determineUpdateCondition());
        UpdateCondition condition = new UpdateCondition("Key1", "Value1", true);
        exchange.getIn().setHeader(UPDATE_CONDITION, condition);
        Assert.assertSame(condition, this.command.determineUpdateCondition());
    }

    @Test
    public void determineNextToken() {
        Assert.assertNull(this.command.determineNextToken());
        exchange.getIn().setHeader(NEXT_TOKEN, "Token1");
        Assert.assertEquals("Token1", this.command.determineNextToken());
    }
}

