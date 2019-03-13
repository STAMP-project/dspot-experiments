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


import SdbConstants.ATTRIBUTES;
import SdbConstants.ATTRIBUTE_NAMES;
import SdbConstants.ITEM_NAME;
import com.amazonaws.services.simpledb.model.Attribute;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class GetAttributesCommandTest {
    private GetAttributesCommand command;

    private AmazonSDBClientMock sdbClient;

    private SdbConfiguration configuration;

    private Exchange exchange;

    @SuppressWarnings("unchecked")
    @Test
    public void execute() {
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add("ATTRIBUTE1");
        exchange.getIn().setHeader(ATTRIBUTE_NAMES, attributeNames);
        exchange.getIn().setHeader(ITEM_NAME, "ITEM1");
        command.execute();
        Assert.assertEquals("DOMAIN1", sdbClient.getAttributesRequest.getDomainName());
        Assert.assertEquals("ITEM1", sdbClient.getAttributesRequest.getItemName());
        Assert.assertEquals(Boolean.TRUE, sdbClient.getAttributesRequest.getConsistentRead());
        Assert.assertEquals(attributeNames, sdbClient.getAttributesRequest.getAttributeNames());
        List<Attribute> attributes = exchange.getIn().getHeader(ATTRIBUTES, List.class);
        Assert.assertEquals(2, attributes.size());
        Assert.assertEquals("AttributeOne", attributes.get(0).getName());
        Assert.assertEquals("Value One", attributes.get(0).getValue());
        Assert.assertEquals("AttributeTwo", attributes.get(1).getName());
        Assert.assertEquals("Value Two", attributes.get(1).getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeWithoutItemName() {
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add("ATTRIBUTE1");
        exchange.getIn().setHeader(ATTRIBUTE_NAMES, attributeNames);
        command.execute();
    }

    @Test
    public void determineAttributeNames() {
        Assert.assertNull(this.command.determineAttributeNames());
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add("ATTRIBUTE1");
        exchange.getIn().setHeader(ATTRIBUTE_NAMES, attributeNames);
        Assert.assertEquals(attributeNames, this.command.determineAttributeNames());
    }
}

