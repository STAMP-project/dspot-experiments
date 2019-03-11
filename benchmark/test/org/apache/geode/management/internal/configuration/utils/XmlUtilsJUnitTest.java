/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.configuration.utils;


import CacheXml.GEMFIRE_NAMESPACE;
import CacheXml.VERSION;
import CacheXml.VERSION_LATEST;
import java.io.InputStreamReader;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.IOUtils;
import org.apache.geode.internal.cache.xmlcache.CacheXml;
import org.apache.geode.management.internal.configuration.utils.XmlUtils.XPathContext;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Unit tests for {@link XmlUtils}. See Also {@link XmlUtilsAddNewNodeJUnitTest} for tests related
 * to {@link XmlUtils#addNewNode(Document, XmlEntity)}
 *
 * @since GemFire 8.1
 */
public class XmlUtilsJUnitTest {
    private static final String GEODE_SCHEMA_LOCATION = ((CacheXml.GEODE_NAMESPACE) + " ") + (CacheXml.LATEST_SCHEMA_LOCATION);

    private static final String GEMFIRE_SCHEMA_LOCATION = ((CacheXml.GEMFIRE_NAMESPACE) + " ") + (CacheXml.SCHEMA_8_1_LOCATION);

    /**
     * Test method for {@link XmlUtils#buildSchemaLocationMap(String)}.
     */
    @Test
    public void testBuildSchemaLocationMapAttribute() throws Exception {
        final Document doc = XmlUtils.createDocumentFromReader(new InputStreamReader(getClass().getResourceAsStream("XmlUtilsJUnitTest.testBuildSchemaLocationMapAttribute.xml")));
        final String schemaLocationAttribute = XmlUtils.getAttribute(doc.getDocumentElement(), XmlConstants.W3C_XML_SCHEMA_INSTANCE_ATTRIBUTE_SCHEMA_LOCATION, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        final Map<String, String> schemaLocationMap = XmlUtils.buildSchemaLocationMap(schemaLocationAttribute);
        Assert.assertNotNull(schemaLocationMap);
        Assert.assertEquals(2, schemaLocationMap.size());
        final String locations1 = schemaLocationMap.get("http://geode.apache.org/schema/cache");
        Assert.assertNotNull(locations1);
        Assert.assertEquals("http://geode.apache.org/schema/cache/cache-1.0.xsd", locations1);
        final String locations2 = schemaLocationMap.get("urn:java:org/apache/geode/management/internal/configuration/utils/XmlUtilsJUnitTest");
        Assert.assertNotNull(locations2);
        Assert.assertEquals("XmlUtilsJUnitTest.xsd", locations2);
        final String locations3 = schemaLocationMap.get("urn:__does_not_exist__");
        Assert.assertNull(locations3);
    }

    @Test
    public void testBuildSchemaLocationMapNullAttribute() throws Exception {
        final Document doc = XmlUtils.createDocumentFromReader(new InputStreamReader(getClass().getResourceAsStream("XmlUtilsJUnitTest.testBuildSchemaLocationMapNullAttribute.xml")));
        final String schemaLocationAttribute = XmlUtils.getAttribute(doc.getDocumentElement(), XmlConstants.W3C_XML_SCHEMA_INSTANCE_ATTRIBUTE_SCHEMA_LOCATION, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        Assert.assertNull(schemaLocationAttribute);
        final Map<String, String> schemaLocationMap = XmlUtils.buildSchemaLocationMap(schemaLocationAttribute);
        Assert.assertEquals(0, schemaLocationMap.size());
    }

    /**
     * Asserts map is empty if schemaLocation attribute is empty.
     */
    @Test
    public void testBuildSchemaLocationMapEmptyAttribute() throws Exception {
        final Document doc = XmlUtils.createDocumentFromReader(new InputStreamReader(getClass().getResourceAsStream("XmlUtilsJUnitTest.testBuildSchemaLocationMapEmptyAttribute.xml")));
        final String schemaLocationAttribute = XmlUtils.getAttribute(doc.getDocumentElement(), XmlConstants.W3C_XML_SCHEMA_INSTANCE_ATTRIBUTE_SCHEMA_LOCATION, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        Assert.assertNotNull(schemaLocationAttribute);
        final Map<String, String> schemaLocationMap = XmlUtils.buildSchemaLocationMap(schemaLocationAttribute);
        Assert.assertEquals(0, schemaLocationMap.size());
    }

    @Test
    public void testQuerySingleElement() throws Exception {
        final Document doc = XmlUtils.createDocumentFromReader(new InputStreamReader(getClass().getResourceAsStream("XmlUtilsJUnitTest.testQuerySingleElement.xml")));
        final Element root = doc.getDocumentElement();
        final String cacheNamespace = "http://geode.apache.org/schema/cache";
        final XPathContext cacheXPathContext = new XPathContext("cache", cacheNamespace);
        // There are mulitple region elements, this should get the first one.
        final NodeList n1 = XmlUtils.query(root, "//cache:region[1]", cacheXPathContext);
        final Node e1 = XmlUtils.querySingleElement(root, "//cache:region", cacheXPathContext);
        Assert.assertNotNull(e1);
        Assert.assertSame(root.getElementsByTagNameNS(cacheNamespace, "region").item(0), e1);
        Assert.assertSame(n1.item(0), e1);
        // This should get the second region with name "r2".
        final NodeList n2 = XmlUtils.query(root, "//cache:region[2]", cacheXPathContext);
        final Node e2 = XmlUtils.querySingleElement(root, "//cache:region[@name='r2']", cacheXPathContext);
        Assert.assertNotNull(e2);
        Assert.assertSame(root.getElementsByTagNameNS(cacheNamespace, "region").item(1), e2);
        Assert.assertSame(n2.item(0), e2);
        // This should get none since there is no r3.
        final Node e3 = XmlUtils.querySingleElement(root, "//cache:region[@name='r3']", cacheXPathContext);
        Assert.assertNull(e3);
        // Query attributes (not Elements)
        final String q4 = "//cache:region/@name";
        final NodeList n4 = XmlUtils.query(root, q4, cacheXPathContext);
        Assert.assertEquals(2, n4.getLength());
        Assert.assertEquals(Node.ATTRIBUTE_NODE, n4.item(0).getNodeType());
        // This should get none since path is to an attribute.
        try {
            XmlUtils.querySingleElement(root, q4, cacheXPathContext);
            Assert.fail("Expected XPathExpressionException");
        } catch (XPathExpressionException expected) {
            // ignore
        }
    }

    /**
     * Test method for {@link XmlUtils#changeNamespace(Node, String, String)}.
     */
    @Test
    public void testChangeNamespaceWithNoRootNamespace() throws Exception {
        Document doc = XmlUtils.getDocumentBuilder().newDocument();
        Element root = doc.createElement("root");
        root = ((Element) (doc.appendChild(root)));
        final Element child = doc.createElement("child");
        root.appendChild(child);
        final String ns2 = "urn:namespace2";
        final Element childWithNamespace = doc.createElementNS(ns2, "childWithNamespace");
        root.appendChild(childWithNamespace);
        root.appendChild(doc.createTextNode("some text"));
        Assert.assertEquals(null, root.getNamespaceURI());
        Assert.assertEquals(null, child.getNamespaceURI());
        Assert.assertEquals(ns2, childWithNamespace.getNamespaceURI());
        final String ns1 = "urn:namespace1";
        root = ((Element) (XmlUtils.changeNamespace(root, XMLConstants.NULL_NS_URI, ns1)));
        Assert.assertEquals(ns1, root.getNamespaceURI());
        Assert.assertEquals(ns1, root.getElementsByTagName("child").item(0).getNamespaceURI());
        Assert.assertEquals(ns2, root.getElementsByTagName("childWithNamespace").item(0).getNamespaceURI());
    }

    @Test
    public void testChangeNamespaceWithExistingRootNamespace() throws Exception {
        Document doc = XmlUtils.getDocumentBuilder().newDocument();
        final String ns0 = "urn:namespace0";
        Element root = doc.createElementNS(ns0, "root");
        root = ((Element) (doc.appendChild(root)));
        final Element child = doc.createElementNS(ns0, "child");
        root.appendChild(child);
        final String ns2 = "urn:namespace2";
        final Element childWithNamespace = doc.createElementNS(ns2, "childWithNamespace");
        root.appendChild(childWithNamespace);
        root.appendChild(doc.createTextNode("some text"));
        Assert.assertEquals(ns0, root.getNamespaceURI());
        Assert.assertEquals(ns0, child.getNamespaceURI());
        Assert.assertEquals(ns2, childWithNamespace.getNamespaceURI());
        final String ns1 = "urn:namespace1";
        root = ((Element) (XmlUtils.changeNamespace(root, ns0, ns1)));
        Assert.assertEquals(ns1, root.getNamespaceURI());
        Assert.assertEquals(ns1, root.getElementsByTagName("child").item(0).getNamespaceURI());
        Assert.assertEquals(ns2, root.getElementsByTagName("childWithNamespace").item(0).getNamespaceURI());
    }

    @Test
    public void testCreateAndUpgradeDocumentFromXml() throws Exception {
        Document doc = XmlUtils.createAndUpgradeDocumentFromXml(IOUtils.toString(this.getClass().getResourceAsStream("SharedConfigurationJUnitTest.xml"), "UTF-8"));
        String schemaLocation = XmlUtils.getAttribute(doc.getDocumentElement(), XmlConstants.W3C_XML_SCHEMA_INSTANCE_ATTRIBUTE_SCHEMA_LOCATION, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        Assert.assertNotNull(schemaLocation);
        Assert.assertEquals((((CacheXml.GEODE_NAMESPACE) + " ") + (CacheXml.LATEST_SCHEMA_LOCATION)), schemaLocation);
        Assert.assertEquals(VERSION_LATEST, XmlUtils.getAttribute(doc.getDocumentElement(), "version"));
    }

    private static String CLUSTER8_XML = "<cache xsi:schemaLocation=\"http://schema.pivotal.io/gemfire/cache http://schema.pivotal.io/gemfire/cache/cache-8.1.xsd\"\n" + (((((("       version=\"8.1\"\n" + "       xmlns=\"http://schema.pivotal.io/gemfire/cache\"\n") + "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n") + "    <region name=\"one\">\n") + "        <region-attributes scope=\"distributed-ack\" data-policy=\"replicate\"/>\n") + "    </region>\n") + "</cache>");

    private static String CLUSTER9_XML = "<cache xmlns=\"http://geode.apache.org/schema/cache\"\n" + (("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "       xsi:schemaLocation=\"http://geode.apache.org/schema/cache http://geode.apache.org/schema/cache/cache-1.0.xsd\"\n") + "       version=\"1.0\"></cache>");

    @Test
    public void testUpgradeSchemaFromGemfireNamespace() throws Exception {
        Document doc = XmlUtils.createDocumentFromXml(XmlUtilsJUnitTest.CLUSTER8_XML);
        Element oldRoot = doc.getDocumentElement();
        assertThat(oldRoot.getAttribute(VERSION)).isEqualTo("8.1");
        assertThat(oldRoot.getNamespaceURI()).isEqualTo(GEMFIRE_NAMESPACE);
        assertThat(oldRoot.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEMFIRE_SCHEMA_LOCATION);
        String version = "1.0";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, CacheXml.LATEST_SCHEMA_LOCATION, version);
        Element root = doc.getDocumentElement();
        assertThat(root.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(root.getAttribute(VERSION)).isEqualTo(version);
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
        Node regionNode = root.getElementsByTagName("region").item(0);
        assertThat(regionNode.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
    }

    @Test
    public void testUpgradeSchemaFromOtherInvaidNS() throws Exception {
        String xml = "<cache version=\"8.1\" xmlns=\"http://test.org/cache\"></cache>";
        Document doc = XmlUtils.createDocumentFromXml(xml);
        String version = "1.0";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, CacheXml.LATEST_SCHEMA_LOCATION, version);
        Element root = doc.getDocumentElement();
        assertThat(root.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(root.getAttribute(VERSION)).isEqualTo(version);
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
    }

    @Test
    public void testUpgradeSchemaFromGemfireNamespaceWithNoLocation() throws Exception {
        String xml = "<cache version=\"8.1\" xmlns=\"http://schema.pivotal.io/gemfire/cache\"></cache>";
        Document doc = XmlUtils.createDocumentFromXml(xml);
        Element oldRoot = doc.getDocumentElement();
        assertThat(oldRoot.getAttribute(VERSION)).isEqualTo("8.1");
        assertThat(oldRoot.getNamespaceURI()).isEqualTo(GEMFIRE_NAMESPACE);
        String version = "1.0";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, CacheXml.LATEST_SCHEMA_LOCATION, version);
        Element root = doc.getDocumentElement();
        assertThat(root.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(root.getAttribute(VERSION)).isEqualTo(version);
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
    }

    @Test
    public void testUpgradeSchemaFromGemfireWithCustomPrefix() throws Exception {
        String xml = "<a:cache xmlns:a=\"http://schema.pivotal.io/gemfire/cache\">\n" + ((("    <a:region name=\"one\">\n" + "        <a:region-attributes scope=\"distributed-ack\" data-policy=\"replicate\"/>\n") + "    </a:region>\n") + "</a:cache>");
        Document doc = XmlUtils.createDocumentFromXml(xml);
        Element oldRoot = doc.getDocumentElement();
        assertThat(oldRoot.getNamespaceURI()).isEqualTo(GEMFIRE_NAMESPACE);
        String version = "1.0";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, CacheXml.LATEST_SCHEMA_LOCATION, version);
        Element root = doc.getDocumentElement();
        assertThat(root.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(root.getAttribute(VERSION)).isEqualTo(version);
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
        Node regionNode = root.getElementsByTagNameNS(CacheXml.GEODE_NAMESPACE, "region").item(0);
        assertThat(regionNode.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
    }

    @Test
    public void testUpgradeVersionFromGeodeWithCustomPrefix() throws Exception {
        String xml = "<a:cache xmlns:a=\"http://geode.apache.org/schema/cache\">\n" + ((("    <a:region name=\"one\">\n" + "        <a:region-attributes scope=\"distributed-ack\" data-policy=\"replicate\"/>\n") + "    </a:region>\n") + "</a:cache>");
        Document doc = XmlUtils.createDocumentFromXml(xml);
        String schemaLocation2 = "http://geode.apache.org/schema/cache/cache-2.0.xsd";
        String version = "2.0";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, schemaLocation2, version);
        Element root = doc.getDocumentElement();
        assertThat(root.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(root.getAttribute(VERSION)).isEqualTo(version);
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo((((CacheXml.GEODE_NAMESPACE) + " ") + schemaLocation2));
        Node regionNode = root.getElementsByTagNameNS(CacheXml.GEODE_NAMESPACE, "region").item(0);
        assertThat(regionNode.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
    }

    @Test
    public void testUpgradeSchemaFromGeodeNamespace() throws Exception {
        Document doc = XmlUtils.createDocumentFromXml(XmlUtilsJUnitTest.CLUSTER9_XML);
        Element oldRoot = doc.getDocumentElement();
        assertThat(oldRoot.getAttribute(VERSION)).isEqualTo("1.0");
        assertThat(oldRoot.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(oldRoot.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
        String version = "1.0";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, CacheXml.LATEST_SCHEMA_LOCATION, version);
        Element root = doc.getDocumentElement();
        assertThat(root.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(root.getAttribute(VERSION)).isEqualTo(version);
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
    }

    @Test
    public void testUpgradeSchemaFromGeodeNamespaceToAnotherVersion() throws Exception {
        Document doc = XmlUtils.createDocumentFromXml(XmlUtilsJUnitTest.CLUSTER9_XML);
        Element oldRoot = doc.getDocumentElement();
        assertThat(oldRoot.getAttribute(VERSION)).isEqualTo("1.0");
        assertThat(oldRoot.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(oldRoot.getAttribute("xsi:schemaLocation")).isEqualTo(XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
        String schemaLocation2 = "http://geode.apache.org/schema/cache/cache-2.0.xsd";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, schemaLocation2, "2.0");
        Element root = doc.getDocumentElement();
        assertThat(root.getNamespaceURI()).isEqualTo(CacheXml.GEODE_NAMESPACE);
        assertThat(root.getAttribute(VERSION)).isEqualTo("2.0");
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo((((CacheXml.GEODE_NAMESPACE) + " ") + schemaLocation2));
    }

    @Test
    public void testUpgradeSchemaWithMultipNS() throws Exception {
        String xml = "<cache xmlns=\"http://cache\"\n" + ((((("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "       xmlns:aop=\"http://aop\"\n") + "       version=\"8.1\"\n") + "       xsi:schemaLocation=\"http://cache http://test.org/cache.xsd ") + "        http://aop http://test.org/aop.xsd\">\n") + "</cache>");
        Document doc = XmlUtils.createDocumentFromXml(xml);
        String version = "1.0";
        String namespace = "http://geode.apache.org/schema/cache";
        doc = XmlUtils.upgradeSchema(doc, CacheXml.GEODE_NAMESPACE, CacheXml.LATEST_SCHEMA_LOCATION, "1.0");
        Element root = doc.getDocumentElement();
        String expectedSchemaLocation = "http://aop http://test.org/aop.xsd " + (XmlUtilsJUnitTest.GEODE_SCHEMA_LOCATION);
        assertThat(root.getNamespaceURI()).isEqualTo(namespace);
        assertThat(root.getAttribute(VERSION)).isEqualTo(version);
        assertThat(root.getAttribute("xsi:schemaLocation")).isEqualTo(expectedSchemaLocation);
    }
}

