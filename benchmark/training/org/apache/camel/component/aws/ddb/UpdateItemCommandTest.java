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


import DdbConstants.ATTRIBUTES;
import DdbConstants.KEY;
import DdbConstants.RETURN_VALUES;
import DdbConstants.UPDATE_CONDITION;
import DdbConstants.UPDATE_VALUES;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class UpdateItemCommandTest {
    private UpdateItemCommand command;

    private AmazonDDBClientMock ddbClient;

    private DdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void execute() {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("1", new AttributeValue("Key_1"));
        exchange.getIn().setHeader(KEY, key);
        Map<String, AttributeValueUpdate> attributeMap = new HashMap<>();
        AttributeValueUpdate attributeValue = new AttributeValueUpdate(new AttributeValue("new value"), AttributeAction.ADD);
        attributeMap.put("name", attributeValue);
        exchange.getIn().setHeader(UPDATE_VALUES, attributeMap);
        Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        expectedAttributeValueMap.put("name", new ExpectedAttributeValue(new AttributeValue("expected value")));
        exchange.getIn().setHeader(UPDATE_CONDITION, expectedAttributeValueMap);
        exchange.getIn().setHeader(RETURN_VALUES, "ALL_OLD");
        command.execute();
        Assert.assertEquals("DOMAIN1", ddbClient.updateItemRequest.getTableName());
        Assert.assertEquals(attributeMap, ddbClient.updateItemRequest.getAttributeUpdates());
        Assert.assertEquals(key, ddbClient.updateItemRequest.getKey());
        Assert.assertEquals(expectedAttributeValueMap, ddbClient.updateItemRequest.getExpected());
        Assert.assertEquals("ALL_OLD", ddbClient.updateItemRequest.getReturnValues());
        Assert.assertEquals(new AttributeValue("attrValue"), exchange.getIn().getHeader(ATTRIBUTES, Map.class).get("attrName"));
    }
}

