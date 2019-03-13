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
import SdbConstants.ITEM_NAME;
import SdbConstants.UPDATE_CONDITION;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class DeleteAttributesCommandTest {
    private DeleteAttributesCommand command;

    private AmazonSDBClientMock sdbClient;

    private SdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void execute() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("NAME1", "VALUE1"));
        exchange.getIn().setHeader(ATTRIBUTES, attributes);
        exchange.getIn().setHeader(ITEM_NAME, "ITEM1");
        UpdateCondition condition = new UpdateCondition("Key1", "Value1", true);
        exchange.getIn().setHeader(UPDATE_CONDITION, condition);
        command.execute();
        Assert.assertEquals("DOMAIN1", sdbClient.deleteAttributesRequest.getDomainName());
        Assert.assertEquals("ITEM1", sdbClient.deleteAttributesRequest.getItemName());
        Assert.assertEquals(condition, sdbClient.deleteAttributesRequest.getExpected());
        Assert.assertEquals(attributes, sdbClient.deleteAttributesRequest.getAttributes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeWithoutItemName() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("NAME1", "VALUE1"));
        exchange.getIn().setHeader(ATTRIBUTES, attributes);
        UpdateCondition condition = new UpdateCondition("Key1", "Value1", true);
        exchange.getIn().setHeader(UPDATE_CONDITION, condition);
        command.execute();
    }

    @Test
    public void determineAttributes() {
        Assert.assertNull(this.command.determineAttributes());
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("NAME1", "VALUE1"));
        exchange.getIn().setHeader(ATTRIBUTES, attributes);
        Assert.assertEquals(attributes, this.command.determineAttributes());
    }
}

