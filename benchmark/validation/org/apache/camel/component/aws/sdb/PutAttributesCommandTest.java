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


import SdbConstants.ITEM_NAME;
import SdbConstants.REPLACEABLE_ATTRIBUTES;
import SdbConstants.UPDATE_CONDITION;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class PutAttributesCommandTest {
    private PutAttributesCommand command;

    private AmazonSDBClientMock sdbClient;

    private SdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void execute() {
        List<ReplaceableAttribute> replaceableAttributes = new ArrayList<>();
        replaceableAttributes.add(new ReplaceableAttribute("NAME1", "VALUE1", true));
        exchange.getIn().setHeader(REPLACEABLE_ATTRIBUTES, replaceableAttributes);
        exchange.getIn().setHeader(ITEM_NAME, "ITEM1");
        UpdateCondition updateCondition = new UpdateCondition("NAME1", "VALUE1", true);
        exchange.getIn().setHeader(UPDATE_CONDITION, updateCondition);
        command.execute();
        Assert.assertEquals("DOMAIN1", sdbClient.putAttributesRequest.getDomainName());
        Assert.assertEquals("ITEM1", sdbClient.putAttributesRequest.getItemName());
        Assert.assertEquals(updateCondition, sdbClient.putAttributesRequest.getExpected());
        Assert.assertEquals(replaceableAttributes, sdbClient.putAttributesRequest.getAttributes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeWithoutItemName() {
        List<ReplaceableAttribute> replaceableAttributes = new ArrayList<>();
        replaceableAttributes.add(new ReplaceableAttribute("NAME1", "VALUE1", true));
        exchange.getIn().setHeader(REPLACEABLE_ATTRIBUTES, replaceableAttributes);
        UpdateCondition updateCondition = new UpdateCondition("NAME1", "VALUE1", true);
        exchange.getIn().setHeader(UPDATE_CONDITION, updateCondition);
        command.execute();
    }

    @Test
    public void determineReplaceableAttributes() {
        Assert.assertNull(this.command.determineReplaceableAttributes());
        List<ReplaceableAttribute> replaceableAttributes = new ArrayList<>();
        replaceableAttributes.add(new ReplaceableAttribute("NAME1", "VALUE1", true));
        exchange.getIn().setHeader(REPLACEABLE_ATTRIBUTES, replaceableAttributes);
        Assert.assertEquals(replaceableAttributes, this.command.determineReplaceableAttributes());
    }
}

