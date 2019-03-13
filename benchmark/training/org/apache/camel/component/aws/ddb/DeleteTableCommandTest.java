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
import DdbConstants.PROVISIONED_THROUGHPUT;
import DdbConstants.TABLE_SIZE;
import DdbConstants.TABLE_STATUS;
import TableStatus.ACTIVE;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import java.util.ArrayList;
import java.util.Date;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class DeleteTableCommandTest {
    private DeleteTableCommand command;

    private AmazonDDBClientMock ddbClient;

    private DdbConfiguration configuration;

    private Exchange exchange;

    @Test
    public void testExecute() {
        command.execute();
        Assert.assertEquals("DOMAIN1", ddbClient.deleteTableRequest.getTableName());
        Assert.assertEquals(new ProvisionedThroughputDescription(), exchange.getIn().getHeader(PROVISIONED_THROUGHPUT));
        Assert.assertEquals(new Date(AmazonDDBClientMock.NOW), exchange.getIn().getHeader(CREATION_DATE, Date.class));
        Assert.assertEquals(Long.valueOf(10L), exchange.getIn().getHeader(ITEM_COUNT, Long.class));
        Assert.assertEquals(new ArrayList<com.amazonaws.services.dynamodbv2.model.KeySchemaElement>(), exchange.getIn().getHeader(KEY_SCHEMA, ArrayList.class));
        Assert.assertEquals(Long.valueOf(20L), exchange.getIn().getHeader(TABLE_SIZE, Long.class));
        Assert.assertEquals(ACTIVE, exchange.getIn().getHeader(TABLE_STATUS, TableStatus.class));
    }
}

