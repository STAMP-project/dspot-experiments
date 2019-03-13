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
package org.apache.geode.connectors.jdbc.internal.xml;


import java.util.Stack;
import org.apache.geode.cache.Region;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.internal.cache.extension.ExtensionPoint;
import org.apache.geode.internal.cache.xmlcache.RegionCreation;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.Attributes;


public class JdbcConnectorServiceXmlParserTest {
    private Attributes attributes;

    private RegionCreation regionCreation;

    private ExtensionPoint<Region<?, ?>> extensionPoint;

    private Stack<Object> stack;

    @Test
    public void getNamespaceUriReturnsNamespace() {
        assertThat(new JdbcConnectorServiceXmlParser().getNamespaceUri()).isEqualTo(JdbcConnectorServiceXmlParser.NAMESPACE);
    }

    @Test
    public void startElementCreatesJdbcServiceConfiguration() throws Exception {
        JdbcConnectorServiceXmlParser parser = new JdbcConnectorServiceXmlParser();
        stack.push(regionCreation);
        parser.setStack(stack);
        parser.startElement(JdbcConnectorServiceXmlParser.NAMESPACE, ElementType.JDBC_MAPPING.getTypeName(), null, attributes);
        assertThat(stack.pop()).isInstanceOf(RegionMapping.class);
    }

    @Test
    public void startElementWithWrongUriDoesNothing() throws Exception {
        JdbcConnectorServiceXmlParser parser = new JdbcConnectorServiceXmlParser();
        stack.push(regionCreation);
        parser.setStack(stack);
        parser.startElement("wrongNamespace", ElementType.JDBC_MAPPING.getTypeName(), null, attributes);
        assertThat(stack.pop()).isEqualTo(regionCreation);
    }

    @Test
    public void endElementRemovesJdbcServiceConfiguration() throws Exception {
        JdbcConnectorServiceXmlParser parser = new JdbcConnectorServiceXmlParser();
        stack.push(regionCreation);
        RegionMapping regionMapping = Mockito.mock(RegionMapping.class);
        stack.push(regionMapping);
        parser.setStack(stack);
        parser.endElement(JdbcConnectorServiceXmlParser.NAMESPACE, ElementType.JDBC_MAPPING.getTypeName(), null);
        assertThat(stack.pop()).isEqualTo(regionCreation);
        Mockito.verifyZeroInteractions(regionMapping);
    }

    @Test
    public void endElementRemovesWithWrongUriDoesNothing() throws Exception {
        JdbcConnectorServiceXmlParser parser = new JdbcConnectorServiceXmlParser();
        stack.push(regionCreation);
        RegionMapping regionMapping = Mockito.mock(RegionMapping.class);
        stack.push(regionMapping);
        parser.setStack(stack);
        parser.endElement("wrongNamespace", ElementType.JDBC_MAPPING.getTypeName(), null);
        assertThat(stack.pop()).isEqualTo(regionMapping);
        Mockito.verifyZeroInteractions(regionMapping);
    }
}

