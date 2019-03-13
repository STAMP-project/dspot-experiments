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
package org.apache.camel;


import javax.activation.DataHandler;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class BodyAndHeaderConvertTest extends Assert {
    protected Exchange exchange;

    @Test
    public void testConversionOfBody() throws Exception {
        Document document = exchange.getIn().getBody(Document.class);
        Assert.assertNotNull(document);
        Element element = document.getDocumentElement();
        Assert.assertEquals("Root element name", "hello", element.getLocalName());
    }

    @Test
    public void testConversionOfExchangeProperties() throws Exception {
        String text = exchange.getProperty("foo", String.class);
        Assert.assertEquals("foo property", "1234", text);
    }

    @Test
    public void testConversionOfMessageHeaders() throws Exception {
        String text = exchange.getIn().getHeader("bar", String.class);
        Assert.assertEquals("bar header", "567", text);
    }

    @Test
    public void testConversionOfMessageAttachments() throws Exception {
        DataHandler handler = exchange.getIn().getAttachment("att");
        Assert.assertNotNull("attachment got lost", handler);
        Attachment attachment = exchange.getIn().getAttachmentObject("att");
        Assert.assertNotNull("attachment got lost", attachment);
    }
}

