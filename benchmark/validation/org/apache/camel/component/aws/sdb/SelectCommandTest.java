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


import SdbConstants.ITEMS;
import SdbConstants.NEXT_TOKEN;
import SdbConstants.SELECT_EXPRESSION;
import com.amazonaws.services.simpledb.model.Item;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class SelectCommandTest {
    private SelectCommand command;

    private AmazonSDBClientMock sdbClient;

    private SdbConfiguration configuration;

    private Exchange exchange;

    @SuppressWarnings("unchecked")
    @Test
    public void execute() {
        exchange.getIn().setHeader(NEXT_TOKEN, "TOKEN1");
        exchange.getIn().setHeader(SELECT_EXPRESSION, "SELECT NAME1 FROM DOMAIN1 WHERE NAME1 LIKE 'VALUE1'");
        command.execute();
        Assert.assertEquals(Boolean.TRUE, sdbClient.selectRequest.getConsistentRead());
        Assert.assertEquals("TOKEN1", sdbClient.selectRequest.getNextToken());
        Assert.assertEquals("SELECT NAME1 FROM DOMAIN1 WHERE NAME1 LIKE 'VALUE1'", sdbClient.selectRequest.getSelectExpression());
        List<Item> items = exchange.getIn().getHeader(ITEMS, List.class);
        Assert.assertEquals("TOKEN2", exchange.getIn().getHeader(NEXT_TOKEN));
        Assert.assertEquals(2, items.size());
        Assert.assertEquals("ITEM1", items.get(0).getName());
        Assert.assertEquals("ITEM2", items.get(1).getName());
    }

    @Test
    public void determineSelectExpression() {
        Assert.assertNull(this.command.determineSelectExpression());
        exchange.getIn().setHeader(SELECT_EXPRESSION, "SELECT NAME1 FROM DOMAIN1 WHERE NAME1 LIKE 'VALUE1'");
        Assert.assertEquals("SELECT NAME1 FROM DOMAIN1 WHERE NAME1 LIKE 'VALUE1'", this.command.determineSelectExpression());
    }
}

