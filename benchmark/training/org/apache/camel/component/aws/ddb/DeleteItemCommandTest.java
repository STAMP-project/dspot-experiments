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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class DeleteItemCommandTest {
    private DeleteItemCommand command;

    private AmazonDDBClientMock ddbClient;

    private DdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void execute() {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("1", new AttributeValue("Key_1"));
        exchange.getIn().setHeader(KEY, key);
        Map<String, ExpectedAttributeValue> updateCondition = new HashMap<>();
        updateCondition.put("name", new ExpectedAttributeValue(new AttributeValue("expected value")));
        exchange.getIn().setHeader(UPDATE_CONDITION, updateCondition);
        exchange.getIn().setHeader(RETURN_VALUES, "ALL_OLD");
        command.execute();
        Assert.assertEquals("DOMAIN1", ddbClient.deleteItemRequest.getTableName());
        Assert.assertEquals(key, ddbClient.deleteItemRequest.getKey());
        Assert.assertEquals(updateCondition, ddbClient.deleteItemRequest.getExpected());
        Assert.assertEquals("ALL_OLD", ddbClient.deleteItemRequest.getReturnValues());
        Assert.assertEquals(new AttributeValue("attrValue"), exchange.getIn().getHeader(ATTRIBUTES, Map.class).get("attrName"));
    }
}

