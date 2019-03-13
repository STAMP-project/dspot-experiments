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
package org.apache.camel.component.aws.sdb.integration;


import SdbConstants.ATTRIBUTES;
import SdbConstants.ATTRIBUTE_NAMES;
import SdbConstants.ATTRIBUTE_NAME_COUNT;
import SdbConstants.ATTRIBUTE_NAME_SIZE;
import SdbConstants.ATTRIBUTE_VALUE_COUNT;
import SdbConstants.ATTRIBUTE_VALUE_SIZE;
import SdbConstants.CONSISTENT_READ;
import SdbConstants.DELETABLE_ITEMS;
import SdbConstants.DOMAIN_NAMES;
import SdbConstants.ITEMS;
import SdbConstants.ITEM_COUNT;
import SdbConstants.ITEM_NAME;
import SdbConstants.ITEM_NAME_SIZE;
import SdbConstants.MAX_NUMBER_OF_DOMAINS;
import SdbConstants.NEXT_TOKEN;
import SdbConstants.OPERATION;
import SdbConstants.REPLACEABLE_ATTRIBUTES;
import SdbConstants.REPLACEABLE_ITEMS;
import SdbConstants.SELECT_EXPRESSION;
import SdbConstants.TIMESTAMP;
import SdbConstants.UPDATE_CONDITION;
import SdbOperations.BatchDeleteAttributes;
import SdbOperations.BatchPutAttributes;
import SdbOperations.DeleteAttributes;
import SdbOperations.DeleteDomain;
import SdbOperations.DomainMetadata;
import SdbOperations.GetAttributes;
import SdbOperations.ListDomains;
import SdbOperations.PutAttributes;
import SdbOperations.Select;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeletableItem;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Must be manually tested. Provide your own accessKey and secretKey!")
public class SdbComponentIntegrationTest extends CamelTestSupport {
    @Test
    public void batchDeleteAttributes() {
        final List<DeletableItem> deletableItems = Arrays.asList(new DeletableItem[]{ new DeletableItem("ITEM1", null), new DeletableItem("ITEM2", null) });
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, BatchDeleteAttributes);
                exchange.getIn().setHeader(DELETABLE_ITEMS, deletableItems);
            }
        });
    }

    @Test
    public void batchPutAttributes() {
        final List<ReplaceableItem> replaceableItems = Arrays.asList(new ReplaceableItem[]{ new ReplaceableItem("ITEM1") });
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, BatchPutAttributes);
                exchange.getIn().setHeader(REPLACEABLE_ITEMS, replaceableItems);
            }
        });
    }

    @Test
    public void deleteAttributes() {
        final List<Attribute> attributes = Arrays.asList(new Attribute[]{ new Attribute("NAME1", "VALUE1") });
        final UpdateCondition condition = new UpdateCondition("Key1", "Value1", true);
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, DeleteAttributes);
                exchange.getIn().setHeader(ATTRIBUTES, attributes);
                exchange.getIn().setHeader(ITEM_NAME, "ITEM1");
                exchange.getIn().setHeader(UPDATE_CONDITION, condition);
            }
        });
    }

    @Test
    public void deleteDomain() {
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, DeleteDomain);
            }
        });
    }

    @Test
    public void domainMetadata() {
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, DomainMetadata);
            }
        });
        assertNotNull(exchange.getIn().getHeader(TIMESTAMP));
        assertNotNull(exchange.getIn().getHeader(ITEM_COUNT));
        assertNotNull(exchange.getIn().getHeader(ATTRIBUTE_NAME_COUNT));
        assertNotNull(exchange.getIn().getHeader(ATTRIBUTE_VALUE_COUNT));
        assertNotNull(exchange.getIn().getHeader(ATTRIBUTE_NAME_SIZE));
        assertNotNull(exchange.getIn().getHeader(ATTRIBUTE_VALUE_SIZE));
        assertNotNull(exchange.getIn().getHeader(ITEM_NAME_SIZE));
    }

    @Test
    public void getAttributes() {
        final List<String> attributeNames = Arrays.asList(new String[]{ "ATTRIBUTE1" });
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, GetAttributes);
                exchange.getIn().setHeader(ITEM_NAME, "ITEM1");
                exchange.getIn().setHeader(CONSISTENT_READ, Boolean.TRUE);
                exchange.getIn().setHeader(ATTRIBUTE_NAMES, attributeNames);
            }
        });
        assertNotNull(exchange.getIn().getHeader(ATTRIBUTES, List.class));
    }

    @Test
    public void listDomains() {
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, ListDomains);
                exchange.getIn().setHeader(MAX_NUMBER_OF_DOMAINS, new Integer(5));
                exchange.getIn().setHeader(NEXT_TOKEN, "TOKEN1");
            }
        });
        assertNotNull(exchange.getIn().getHeader(DOMAIN_NAMES, List.class));
    }

    @Test
    public void putAttributes() {
        final List<ReplaceableAttribute> replaceableAttributes = Arrays.asList(new ReplaceableAttribute[]{ new ReplaceableAttribute("NAME1", "VALUE1", true) });
        final UpdateCondition updateCondition = new UpdateCondition("NAME1", "VALUE1", true);
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, PutAttributes);
                exchange.getIn().setHeader(ITEM_NAME, "ITEM1");
                exchange.getIn().setHeader(UPDATE_CONDITION, updateCondition);
                exchange.getIn().setHeader(REPLACEABLE_ATTRIBUTES, replaceableAttributes);
            }
        });
    }

    @Test
    public void select() {
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, Select);
                exchange.getIn().setHeader(NEXT_TOKEN, "TOKEN1");
                exchange.getIn().setHeader(CONSISTENT_READ, Boolean.TRUE);
                exchange.getIn().setHeader(SELECT_EXPRESSION, "SELECT NAME1 FROM DOMAIN1 WHERE NAME1 LIKE 'VALUE1'");
            }
        });
        assertNotNull(exchange.getIn().getHeader(ITEMS, List.class));
    }
}

