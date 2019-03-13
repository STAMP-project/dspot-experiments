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
package org.apache.camel.component.cxf.jaxrs;


import Exchange.CONTENT_TYPE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.org.apache.cxf.message.Message;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.apache.camel.support.ExchangeHelper;
import org.apache.cxf.message.MessageImpl;
import org.junit.Assert;
import org.junit.Test;


public class DefaultCxfRsBindingTest extends Assert {
    private DefaultCamelContext context = new DefaultCamelContext();

    @Test
    public void testSetCharsetWithContentType() {
        DefaultCxfRsBinding cxfRsBinding = new DefaultCxfRsBinding();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setHeader(CONTENT_TYPE, "text/xml;charset=ISO-8859-1");
        cxfRsBinding.setCharsetWithContentType(exchange);
        String charset = ExchangeHelper.getCharsetName(exchange);
        Assert.assertEquals("Get a wrong charset", "ISO-8859-1", charset);
        exchange.getIn().setHeader(CONTENT_TYPE, "text/xml");
        cxfRsBinding.setCharsetWithContentType(exchange);
        charset = ExchangeHelper.getCharsetName(exchange);
        Assert.assertEquals("Get a worng charset name", "UTF-8", charset);
    }

    @Test
    public void testCopyProtocolHeader() {
        DefaultCxfRsBinding cxfRsBinding = new DefaultCxfRsBinding();
        cxfRsBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message camelMessage = new org.apache.camel.support.DefaultMessage(context);
        org.apache.cxf.message.Message cxfMessage = new MessageImpl();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("emptyList", Collections.<String>emptyList());
        headers.put("zeroSizeList", new ArrayList<String>(0));
        cxfMessage.put(org.apache.cxf.message.Message, headers);
        cxfRsBinding.copyProtocolHeader(cxfMessage, camelMessage, exchange);
        Assert.assertNull("We should get nothing here", camelMessage.getHeader("emptyList"));
        Assert.assertNull("We should get nothing here", camelMessage.getHeader("zeroSizeList"));
    }
}

