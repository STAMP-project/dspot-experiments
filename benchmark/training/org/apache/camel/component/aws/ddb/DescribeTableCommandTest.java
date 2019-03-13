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


import DdbConstants.CREATION_DATE;
import DdbConstants.ITEM_COUNT;
import DdbConstants.KEY_SCHEMA;
import DdbConstants.READ_CAPACITY;
import DdbConstants.TABLE_NAME;
import DdbConstants.TABLE_SIZE;
import DdbConstants.TABLE_STATUS;
import DdbConstants.WRITE_CAPACITY;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class DescribeTableCommandTest {
    private DescribeTableCommand command;

    private AmazonDDBClientMock ddbClient;

    private DdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void testExecute() {
        command.execute();
        List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement().withAttributeName("name"));
        Assert.assertEquals("FULL_DESCRIBE_TABLE", ddbClient.describeTableRequest.getTableName());
        Assert.assertEquals("FULL_DESCRIBE_TABLE", exchange.getIn().getHeader(TABLE_NAME));
        Assert.assertEquals("ACTIVE", exchange.getIn().getHeader(TABLE_STATUS));
        Assert.assertEquals(new Date(AmazonDDBClientMock.NOW), exchange.getIn().getHeader(CREATION_DATE));
        Assert.assertEquals(100L, exchange.getIn().getHeader(ITEM_COUNT));
        Assert.assertEquals(keySchema, exchange.getIn().getHeader(KEY_SCHEMA));
        Assert.assertEquals(20L, exchange.getIn().getHeader(READ_CAPACITY));
        Assert.assertEquals(10L, exchange.getIn().getHeader(WRITE_CAPACITY));
        Assert.assertEquals(1000L, exchange.getIn().getHeader(TABLE_SIZE));
    }
}

