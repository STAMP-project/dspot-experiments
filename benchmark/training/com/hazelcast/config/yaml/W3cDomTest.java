/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config.yaml;


import com.hazelcast.internal.yaml.YamlLoader;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlTest;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * A modified copy of the of the main test case of {@link YamlTest} that
 * verifies the behavior of the W3C DOM adapters' supported methods.
 * <p/>
 * These tests utilize that we work with the node adapters with which we
 * can access the mapping via getAttributes().
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class W3cDomTest extends HazelcastTestSupport {
    private static final int NOT_EXISTING = 42;

    @Test
    public void testW3cDomAdapter() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map-extended.yaml");
        YamlNode yamlRoot = YamlLoader.load(inputStream, "root-map");
        Node domRoot = W3cDomUtil.asW3cNode(yamlRoot);
        NamedNodeMap rootAttributes = domRoot.getAttributes();
        Node embeddedMap = rootAttributes.getNamedItem("embedded-map");
        NamedNodeMap embeddedMapAttributes = embeddedMap.getAttributes();
        String scalarString = embeddedMapAttributes.getNamedItem("scalar-str").getTextContent();
        int scalarInt = Integer.parseInt(embeddedMapAttributes.getNamedItem("scalar-int").getTextContent());
        double scalarDouble = Double.parseDouble(embeddedMapAttributes.getNamedItem("scalar-double").getTextContent());
        boolean scalarBool = Boolean.parseBoolean(embeddedMapAttributes.getNamedItem("scalar-bool").getTextContent());
        Node embeddedListNode = embeddedMapAttributes.getNamedItem("embedded-list");
        NodeList embeddedList = embeddedListNode.getChildNodes();
        String elItem0 = embeddedList.item(0).getTextContent();
        Node elItem0AsNode = embeddedList.item(0);
        int elItem1 = Integer.parseInt(embeddedList.item(1).getTextContent());
        double elItem2 = Double.parseDouble(embeddedList.item(2).getTextContent());
        boolean elItem3 = Boolean.parseBoolean(embeddedList.item(3).getTextContent());
        NodeList embeddedList2 = embeddedMapAttributes.getNamedItem("embedded-list2").getChildNodes();
        String el2Item0 = embeddedList2.item(0).getTextContent();
        double el2Item1 = Double.parseDouble(embeddedList2.item(1).getTextContent());
        String keysValue = embeddedList.item(4).getAttributes().getNamedItem("key").getTextContent();
        String multilineStr = domRoot.getAttributes().getNamedItem("multiline-str").getTextContent();
        Assert.assertEquals("embedded-map", embeddedMap.getNodeName());
        Assert.assertEquals("scalar-str", embeddedMap.getChildNodes().item(0).getNodeName());
        Assert.assertNull(embeddedMap.getChildNodes().item(W3cDomTest.NOT_EXISTING));
        Assert.assertNull(embeddedMap.getTextContent());
        Assert.assertEquals("embedded-list", embeddedMapAttributes.item(4).getNodeName());
        Assert.assertEquals("embedded-list", embeddedListNode.getNodeName());
        Assert.assertEquals("embedded-map", embeddedListNode.getParentNode().getNodeName());
        Assert.assertSame(EmptyNamedNodeMap.emptyNamedNodeMap(), embeddedListNode.getAttributes());
        Assert.assertEquals(6, embeddedMap.getAttributes().getLength());
        // root-map/embedded-map/scalars
        Assert.assertEquals(6, embeddedMap.getChildNodes().getLength());
        Assert.assertEquals("h4z3lc4st", scalarString);
        Assert.assertEquals(123, scalarInt);
        Assert.assertEquals(123.12312, scalarDouble, 1.0E-4);
        Assert.assertTrue(scalarBool);
        // root-map/embedded-map/embedded-list
        Assert.assertEquals("value1", elItem0);
        Assert.assertEquals(W3cDomTest.NOT_EXISTING, elItem1);
        Assert.assertEquals(42.42, elItem2, 0.1);
        Assert.assertFalse(elItem3);
        NodeList elItem0ChildNodes = elItem0AsNode.getChildNodes();
        Assert.assertEquals(1, elItem0ChildNodes.getLength());
        Assert.assertEquals("value1", elItem0ChildNodes.item(0).getNodeValue());
        Assert.assertEquals("value1", elItem0AsNode.getNodeValue());
        // root-map/embedded-map/embedded-list2
        Assert.assertEquals(2, embeddedList2.getLength());
        Assert.assertEquals("value2", el2Item0);
        Assert.assertEquals(1.0, el2Item1, 1.0);
        Assert.assertEquals("value", keysValue);
        Assert.assertEquals(("Hazelcast IMDG\n" + ("The Leading Open Source In-Memory Data Grid:\n" + "Distributed Computing, Simplified.\n")), multilineStr);
        Element embeddedMapAsElement = ((Element) (embeddedMap));
        String scalarStrAttr = embeddedMapAsElement.getAttribute("scalar-str");
        Assert.assertEquals("h4z3lc4st", scalarStrAttr);
        Assert.assertEquals("embedded-map", embeddedMapAsElement.getTagName());
        Assert.assertTrue(embeddedMapAsElement.hasAttribute("scalar-str"));
        Assert.assertEquals("", embeddedMapAsElement.getAttribute("non-existing"));
        NodeList nodesByTagName = embeddedMapAsElement.getElementsByTagName("scalar-str");
        Assert.assertEquals(1, nodesByTagName.getLength());
        Assert.assertEquals("h4z3lc4st", nodesByTagName.item(0).getNodeValue());
    }
}

