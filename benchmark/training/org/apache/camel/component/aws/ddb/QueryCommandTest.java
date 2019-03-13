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
import DdbConstants.ATTRIBUTE_NAMES;
import DdbConstants.CONSISTENT_READ;
import DdbConstants.CONSUMED_CAPACITY;
import DdbConstants.COUNT;
import DdbConstants.ITEMS;
import DdbConstants.KEY_CONDITIONS;
import DdbConstants.LAST_EVALUATED_KEY;
import DdbConstants.LIMIT;
import DdbConstants.SCAN_INDEX_FORWARD;
import DdbConstants.START_KEY;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConsumedCapacity;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class QueryCommandTest {
    private QueryCommand command;

    private AmazonDDBClientMock ddbClient;

    private DdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void execute() {
        Map<String, AttributeValue> startKey = new HashMap<>();
        startKey.put("1", new AttributeValue("startKey"));
        List<String> attributeNames = Arrays.asList("attrNameOne", "attrNameTwo");
        exchange.getIn().setHeader(ATTRIBUTE_NAMES, attributeNames);
        exchange.getIn().setHeader(CONSISTENT_READ, true);
        exchange.getIn().setHeader(START_KEY, startKey);
        exchange.getIn().setHeader(LIMIT, 10);
        exchange.getIn().setHeader(SCAN_INDEX_FORWARD, true);
        Map<String, Condition> keyConditions = new HashMap<>();
        Condition condition = new Condition().withComparisonOperator(GT.toString()).withAttributeValueList(new AttributeValue().withN("1985"));
        keyConditions.put("1", condition);
        exchange.getIn().setHeader(KEY_CONDITIONS, keyConditions);
        command.execute();
        Map<String, AttributeValue> mapAssert = new HashMap<>();
        mapAssert.put("1", new AttributeValue("LAST_KEY"));
        ConsumedCapacity consumed = ((ConsumedCapacity) (exchange.getIn().getHeader(CONSUMED_CAPACITY)));
        Assert.assertEquals(Integer.valueOf(1), exchange.getIn().getHeader(COUNT, Integer.class));
        Assert.assertEquals(Double.valueOf(1.0), consumed.getCapacityUnits());
        Assert.assertEquals(mapAssert, exchange.getIn().getHeader(LAST_EVALUATED_KEY, Map.class));
        Assert.assertEquals(keyConditions, exchange.getIn().getHeader(KEY_CONDITIONS, Map.class));
        Map<?, ?> items = ((Map<?, ?>) (exchange.getIn().getHeader(ITEMS, List.class).get(0)));
        Assert.assertEquals(new AttributeValue("attrValue"), items.get("attrName"));
    }
}

