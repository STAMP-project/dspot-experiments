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
package org.apache.camel.component.aws.ddb;


import ComparisonOperator.GT;
import DdbConstants.CONSUMED_CAPACITY;
import DdbConstants.COUNT;
import DdbConstants.ITEMS;
import DdbConstants.LAST_EVALUATED_KEY;
import DdbConstants.SCANNED_COUNT;
import DdbConstants.SCAN_FILTER;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConsumedCapacity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class ScanCommandTest {
    private ScanCommand command;

    private AmazonDDBClientMock ddbClient;

    private DdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void execute() {
        Map<String, Condition> scanFilter = new HashMap<>();
        Condition condition = new Condition().withComparisonOperator(GT.toString()).withAttributeValueList(new AttributeValue().withN("1985"));
        scanFilter.put("year", condition);
        exchange.getIn().setHeader(SCAN_FILTER, scanFilter);
        command.execute();
        Map<String, AttributeValue> mapAssert = new HashMap<>();
        mapAssert.put("1", new AttributeValue("LAST_KEY"));
        ConsumedCapacity consumed = ((ConsumedCapacity) (exchange.getIn().getHeader(CONSUMED_CAPACITY)));
        Assert.assertEquals(scanFilter, ddbClient.scanRequest.getScanFilter());
        Assert.assertEquals(Integer.valueOf(10), exchange.getIn().getHeader(SCANNED_COUNT, Integer.class));
        Assert.assertEquals(Integer.valueOf(1), exchange.getIn().getHeader(COUNT, Integer.class));
        Assert.assertEquals(Double.valueOf(1.0), consumed.getCapacityUnits());
        Assert.assertEquals(mapAssert, exchange.getIn().getHeader(LAST_EVALUATED_KEY, Map.class));
        Map<?, ?> items = ((Map<?, ?>) (exchange.getIn().getHeader(ITEMS, List.class).get(0)));
        Assert.assertEquals(new AttributeValue("attrValue"), items.get("attrName"));
    }
}

