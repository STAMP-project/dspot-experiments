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
package org.apache.camel.maven;


import Constants.NAME_ATTRIBUTE_NAME;
import javax.xml.xpath.XPath;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class DomFinderTest {
    private DomFinder domFinder;

    @Test
    public void testFindElementsAndTypes() throws Exception {
        Document document = XmlHelper.buildNamespaceAwareDocument(ResourceUtils.getResourceAsFile("xmls/3_elements.xml"));
        XPath xPath = XmlHelper.buildXPath(new CamelSpringNamespace());
        domFinder = new DomFinder(document, xPath);
        NodeList elements = domFinder.findElementsAndTypes();
        Assert.assertEquals(3, elements.getLength());
    }

    @Test
    public void testFindAttributesElements() throws Exception {
        Document document = XmlHelper.buildNamespaceAwareDocument(ResourceUtils.getResourceAsFile("xmls/complex_type.xml"));
        XPath xPath = XmlHelper.buildXPath(new CamelSpringNamespace());
        domFinder = new DomFinder(document, xPath);
        NodeList attributesList = domFinder.findAttributesElements("interceptSendToEndpointDefinition");
        Assert.assertEquals(2, attributesList.getLength());
        Assert.assertEquals("uri", ((Element) (attributesList.item(0))).getAttribute(NAME_ATTRIBUTE_NAME));
        Assert.assertEquals("skipSendToOriginalEndpoint", ((Element) (attributesList.item(1))).getAttribute(NAME_ATTRIBUTE_NAME));
    }

    @Test
    public void testFindBaseType() throws Exception {
        Document document = XmlHelper.buildNamespaceAwareDocument(ResourceUtils.getResourceAsFile("xmls/complex_type_w_parent.xml"));
        XPath xPath = XmlHelper.buildXPath(new CamelSpringNamespace());
        domFinder = new DomFinder(document, xPath);
        String baseTypeName = domFinder.findBaseType("keyManagersParametersFactoryBean");
        Assert.assertEquals("tns:abstractKeyManagersParametersFactoryBean", baseTypeName);
    }
}

