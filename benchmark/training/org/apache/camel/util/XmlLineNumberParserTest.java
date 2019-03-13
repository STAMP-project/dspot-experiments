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


import XmlLineNumberParser.LINE_NUMBER;
import XmlLineNumberParser.LINE_NUMBER_END;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlLineNumberParserTest extends Assert {
    @Test
    public void testParse() throws Exception {
        InputStream fis = Files.newInputStream(Paths.get("src/test/resources/org/apache/camel/util/camel-context.xml"));
        Document dom = XmlLineNumberParser.parseXml(fis);
        Assert.assertNotNull(dom);
        NodeList list = dom.getElementsByTagName("beans");
        Assert.assertEquals(1, list.getLength());
        Node node = list.item(0);
        String lineNumber = ((String) (node.getUserData(LINE_NUMBER)));
        String lineNumberEnd = ((String) (node.getUserData(LINE_NUMBER_END)));
        Assert.assertEquals("24", lineNumber);
        Assert.assertEquals("49", lineNumberEnd);
    }

    @Test
    public void testParseCamelContext() throws Exception {
        InputStream fis = Files.newInputStream(Paths.get("src/test/resources/org/apache/camel/util/camel-context.xml"));
        Document dom = XmlLineNumberParser.parseXml(fis, null, "camelContext", null);
        Assert.assertNotNull(dom);
        NodeList list = dom.getElementsByTagName("camelContext");
        Assert.assertEquals(1, list.getLength());
        Node node = list.item(0);
        String lineNumber = ((String) (node.getUserData(LINE_NUMBER)));
        String lineNumberEnd = ((String) (node.getUserData(LINE_NUMBER_END)));
        Assert.assertEquals("29", lineNumber);
        Assert.assertEquals("47", lineNumberEnd);
    }

    @Test
    public void testParseCamelContextForceNamespace() throws Exception {
        InputStream fis = Files.newInputStream(Paths.get("src/test/resources/org/apache/camel/util/camel-context.xml"));
        Document dom = XmlLineNumberParser.parseXml(fis, null, "camelContext", "http://camel.apache.org/schema/spring");
        Assert.assertNotNull(dom);
        NodeList list = dom.getElementsByTagName("camelContext");
        Assert.assertEquals(1, list.getLength());
        Node node = list.item(0);
        String lineNumber = ((String) (node.getUserData(LINE_NUMBER)));
        String lineNumberEnd = ((String) (node.getUserData(LINE_NUMBER_END)));
        String ns = node.getNamespaceURI();
        Assert.assertEquals("http://camel.apache.org/schema/spring", ns);
        Assert.assertEquals("29", lineNumber);
        Assert.assertEquals("47", lineNumberEnd);
        // and there are two routes
        list = dom.getElementsByTagName("route");
        Assert.assertEquals(2, list.getLength());
        Node node1 = list.item(0);
        Node node2 = list.item(1);
        String lineNumber1 = ((String) (node1.getUserData(LINE_NUMBER)));
        String lineNumberEnd1 = ((String) (node1.getUserData(LINE_NUMBER_END)));
        Assert.assertEquals("31", lineNumber1);
        Assert.assertEquals("37", lineNumberEnd1);
        String lineNumber2 = ((String) (node2.getUserData(LINE_NUMBER)));
        String lineNumberEnd2 = ((String) (node2.getUserData(LINE_NUMBER_END)));
        Assert.assertEquals("39", lineNumber2);
        Assert.assertEquals("45", lineNumberEnd2);
    }
}

