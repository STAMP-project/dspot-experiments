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
package org.apache.camel.builder.saxon;


import org.apache.camel.Exchange;
import org.apache.camel.component.xquery.XQueryBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class XQueryTest extends Assert {
    @Test
    public void testXQuery() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody("<products><product type='food'><pizza/></product><product type='beer'><stella/></product></products>");
        Object result = XQueryBuilder.xquery(".//product[@type = 'beer']/*").evaluate(exchange, Object.class);
        Assert.assertTrue(("Should be a document but was: " + (className(result))), (result instanceof Document));
        Document doc = ((Document) (result));
        Assert.assertEquals("Root document element name", "stella", doc.getDocumentElement().getLocalName());
        result = XQueryBuilder.xquery(".//product[@type = 'beer']/*").evaluate(exchange, String.class);
        Assert.assertEquals("Get a wrong result", "<stella/>", result);
    }
}

