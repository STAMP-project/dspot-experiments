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
package org.apache.camel.util;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.camel.model.ModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 */
public class DumpModelAsXmlTransformRouteTest extends ContextTestSupport {
    @Test
    public void testDumpModelAsXml() throws Exception {
        String xml = ModelHelper.dumpModelAsXml(context, context.getRouteDefinition("myRoute"));
        Assert.assertNotNull(xml);
        log.info(xml);
        Document doc = new XmlConverter().toDOMDocument(xml, null);
        NodeList nodes = doc.getElementsByTagName("simple");
        Assert.assertEquals(1, nodes.getLength());
        Element node = ((Element) (nodes.item(0)));
        Assert.assertNotNull("Node <simple> expected to be instanceof Element", node);
        Assert.assertEquals("Hello ${body}", node.getTextContent());
        nodes = doc.getElementsByTagName("to");
        Assert.assertEquals(1, nodes.getLength());
        node = ((Element) (nodes.item(0)));
        Assert.assertNotNull("Node <to> expected to be instanceof Element", node);
        Assert.assertEquals("mock:result", node.getAttribute("uri"));
        Assert.assertEquals("myMock", node.getAttribute("id"));
        Assert.assertEquals("true", node.getAttribute("customId"));
    }
}

