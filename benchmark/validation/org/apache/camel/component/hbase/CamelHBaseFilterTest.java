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
package org.apache.camel.component.hbase;


import ExchangePattern.InOut;
import HBaseAttribute.HBASE_FAMILY;
import HBaseAttribute.HBASE_QUALIFIER;
import HBaseAttribute.HBASE_VALUE;
import java.util.LinkedList;
import java.util.List;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.hadoop.hbase.filter.Filter;
import org.junit.Test;


public class CamelHBaseFilterTest extends CamelHBaseTestSupport {
    List<Filter> filters = new LinkedList<>();

    @Test
    public void testPutMultiRowsAndScanWithFilters() throws Exception {
        if (CamelHBaseTestSupport.systemReady) {
            putMultipleRows();
            ProducerTemplate template = context.createProducerTemplate();
            Endpoint endpoint = context.getEndpoint("direct:scan");
            Exchange exchange = endpoint.createExchange(InOut);
            exchange.getIn().setHeader(HBASE_FAMILY.asHeader(), family[0]);
            exchange.getIn().setHeader(HBASE_QUALIFIER.asHeader(), column[0][0]);
            exchange.getIn().setHeader(HBASE_VALUE.asHeader(), body[0][0][0]);
            Exchange resp = template.send(endpoint, exchange);
            Message out = resp.getOut();
            assertTrue("two first keys returned", (((out.getHeaders().containsValue(body[0][0][0])) && (out.getHeaders().containsValue(body[1][0][0]))) && (!(out.getHeaders().containsValue(body[2][0][0])))));
        }
    }
}

