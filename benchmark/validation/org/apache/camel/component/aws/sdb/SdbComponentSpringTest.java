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
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


public class SdbComponentSpringTest extends CamelSpringTestSupport {
    private AmazonSDBClientMock amazonSDBClient;

    @Test
    public void doesntCreateDomainOnStartIfExists() throws Exception {
        assertNull(amazonSDBClient.createDomainRequest);
    }

    @Test
    public void createDomainOnStartIfNotExists() throws Exception {
        DefaultProducerTemplate.newInstance(context, "aws-sdb://NonExistingDomain?amazonSDBClient=#amazonSDBClient&operation=GetAttributes");
        assertEquals("NonExistingDomain", amazonSDBClient.createDomainRequest.getDomainName());
    }

    @Test
    public void batchDeleteAttributes() {
        final List<DeletableItem> deletableItems = Arrays.asList(new DeletableItem[]{ new DeletableItem("ITEM1", null), new DeletableItem("ITEM2", null) });
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, BatchDeleteAttributes);
                exchange.getIn().setHeader(DELETABLE_ITEMS, deletableItems);
            }
        });
        assertEquals("TestDomain", amazonSDBClient.batchDeleteAttributesRequest.getDomainName());
        assertEquals(deletableItems, amazonSDBClient.batchDeleteAttributesRequest.getItems());
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
        assertEquals("TestDomain", amazonSDBClient.batchPutAttributesRequest.getDomainName());
        assertEquals(replaceableItems, amazonSDBClient.batchPutAttributesRequest.getItems());
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
        assertEquals("TestDomain", amazonSDBClient.deleteAttributesRequest.getDomainName());
        assertEquals("ITEM1", amazonSDBClient.deleteAttributesRequest.getItemName());
        assertEquals(condition, amazonSDBClient.deleteAttributesRequest.getExpected());
        assertEquals(attributes, amazonSDBClient.deleteAttributesRequest.getAttributes());
    }

    @Test
    public void deleteAttributesItemNameIsRequired() {
        final List<Attribute> attributes = Arrays.asList(new Attribute[]{ new Attribute("NAME1", "VALUE1") });
        final UpdateCondition condition = new UpdateCondition("Key1", "Value1", true);
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, DeleteAttributes);
                exchange.getIn().setHeader(ATTRIBUTES, attributes);
                exchange.getIn().setHeader(UPDATE_CONDITION, condition);
            }
        });
        Exception exception = exchange.getException();
        assertTrue((exception instanceof IllegalArgumentException));
    }

    @Test
    public void deleteDomain() {
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, DeleteDomain);
            }
        });
        assertEquals("TestDomain", amazonSDBClient.deleteDomainRequest.getDomainName());
    }

    @Test
    public void domainMetadata() {
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, DomainMetadata);
            }
        });
        assertEquals("TestDomain", amazonSDBClient.domainMetadataRequest.getDomainName());
        assertEquals(new Integer(10), exchange.getIn().getHeader(TIMESTAMP));
        assertEquals(new Integer(11), exchange.getIn().getHeader(ITEM_COUNT));
        assertEquals(new Integer(12), exchange.getIn().getHeader(ATTRIBUTE_NAME_COUNT));
        assertEquals(new Integer(13), exchange.getIn().getHeader(ATTRIBUTE_VALUE_COUNT));
        assertEquals(new Long(1000000), exchange.getIn().getHeader(ATTRIBUTE_NAME_SIZE));
        assertEquals(new Long(2000000), exchange.getIn().getHeader(ATTRIBUTE_VALUE_SIZE));
        assertEquals(new Long(3000000), exchange.getIn().getHeader(ITEM_NAME_SIZE));
    }

    @SuppressWarnings("unchecked")
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
        assertEquals("TestDomain", amazonSDBClient.getAttributesRequest.getDomainName());
        assertEquals("ITEM1", amazonSDBClient.getAttributesRequest.getItemName());
        assertEquals(Boolean.TRUE, amazonSDBClient.getAttributesRequest.getConsistentRead());
        assertEquals(attributeNames, amazonSDBClient.getAttributesRequest.getAttributeNames());
        List<Attribute> attributes = exchange.getIn().getHeader(ATTRIBUTES, List.class);
        assertEquals(2, attributes.size());
        assertEquals("AttributeOne", attributes.get(0).getName());
        assertEquals("Value One", attributes.get(0).getValue());
        assertEquals("AttributeTwo", attributes.get(1).getName());
        assertEquals("Value Two", attributes.get(1).getValue());
    }

    @Test
    public void getAttributesItemNameIsRequired() {
        final List<String> attributeNames = Arrays.asList(new String[]{ "ATTRIBUTE1" });
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, GetAttributes);
                exchange.getIn().setHeader(CONSISTENT_READ, Boolean.TRUE);
                exchange.getIn().setHeader(ATTRIBUTE_NAMES, attributeNames);
            }
        });
        Exception exception = exchange.getException();
        assertTrue((exception instanceof IllegalArgumentException));
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void listDomains() {
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, ListDomains);
                exchange.getIn().setHeader(MAX_NUMBER_OF_DOMAINS, new Integer(5));
                exchange.getIn().setHeader(NEXT_TOKEN, "TOKEN1");
            }
        });
        assertEquals(new Integer(5), amazonSDBClient.listDomainsRequest.getMaxNumberOfDomains());
        assertEquals("TOKEN1", amazonSDBClient.listDomainsRequest.getNextToken());
        List<String> domains = exchange.getIn().getHeader(DOMAIN_NAMES, List.class);
        assertEquals("TOKEN2", exchange.getIn().getHeader(NEXT_TOKEN));
        assertEquals(2, domains.size());
        assertTrue(domains.contains("DOMAIN1"));
        assertTrue(domains.contains("DOMAIN2"));
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
        assertEquals("TestDomain", amazonSDBClient.putAttributesRequest.getDomainName());
        assertEquals("ITEM1", amazonSDBClient.putAttributesRequest.getItemName());
        assertEquals(updateCondition, amazonSDBClient.putAttributesRequest.getExpected());
        assertEquals(replaceableAttributes, amazonSDBClient.putAttributesRequest.getAttributes());
    }

    @Test
    public void putAttributesItemNameIsRequired() {
        final List<ReplaceableAttribute> replaceableAttributes = Arrays.asList(new ReplaceableAttribute[]{ new ReplaceableAttribute("NAME1", "VALUE1", true) });
        final UpdateCondition updateCondition = new UpdateCondition("NAME1", "VALUE1", true);
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, PutAttributes);
                exchange.getIn().setHeader(UPDATE_CONDITION, updateCondition);
                exchange.getIn().setHeader(REPLACEABLE_ATTRIBUTES, replaceableAttributes);
            }
        });
        Exception exception = exchange.getException();
        assertTrue((exception instanceof IllegalArgumentException));
    }

    @SuppressWarnings("unchecked")
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
        assertEquals(Boolean.TRUE, amazonSDBClient.selectRequest.getConsistentRead());
        assertEquals("TOKEN1", amazonSDBClient.selectRequest.getNextToken());
        assertEquals("SELECT NAME1 FROM DOMAIN1 WHERE NAME1 LIKE 'VALUE1'", amazonSDBClient.selectRequest.getSelectExpression());
        List<Item> items = exchange.getIn().getHeader(ITEMS, List.class);
        assertEquals("TOKEN2", exchange.getIn().getHeader(NEXT_TOKEN));
        assertEquals(2, items.size());
        assertEquals("ITEM1", items.get(0).getName());
        assertEquals("ITEM2", items.get(1).getName());
    }
}

