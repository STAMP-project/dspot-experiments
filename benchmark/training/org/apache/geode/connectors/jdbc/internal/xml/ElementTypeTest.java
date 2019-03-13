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


import ElementType.FIELD_MAPPING;
import ElementType.JDBC_MAPPING;
import java.util.Arrays;
import java.util.Stack;
import org.apache.geode.cache.CacheXmlException;
import org.apache.geode.cache.Region;
import org.apache.geode.connectors.jdbc.internal.configuration.FieldMapping;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.internal.cache.extension.ExtensionPoint;
import org.apache.geode.internal.cache.xmlcache.RegionCreation;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.Attributes;


public class ElementTypeTest {
    private Attributes attributes;

    private RegionCreation regionCreation;

    private ExtensionPoint<Region<?, ?>> extensionPoint;

    private Stack<Object> stack;

    @Test
    public void gettingElementTypeThatDoesNotExistThrowsException() {
        assertThatThrownBy(() -> ElementType.getTypeFromName("non-existent element")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void startElementRegionMappingThrowsWithoutJdbcServiceConfiguration() {
        stack.push(new Object());
        assertThatThrownBy(() -> JDBC_MAPPING.startElement(stack, attributes)).isInstanceOf(CacheXmlException.class);
    }

    @Test
    public void startElementRegionMapping() {
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.DATA_SOURCE)).thenReturn("connectionName");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.TABLE)).thenReturn("table");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.PDX_NAME)).thenReturn("pdxClass");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.IDS)).thenReturn("ids");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.CATALOG)).thenReturn("catalog");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.SCHEMA)).thenReturn("schema");
        Mockito.when(regionCreation.getFullPath()).thenReturn("/region");
        stack.push(regionCreation);
        JDBC_MAPPING.startElement(stack, attributes);
        RegionMapping regionMapping = ((RegionMapping) (stack.pop()));
        assertThat(regionMapping.getRegionName()).isEqualTo("region");
        assertThat(regionMapping.getDataSourceName()).isEqualTo("connectionName");
        assertThat(regionMapping.getTableName()).isEqualTo("table");
        assertThat(regionMapping.getPdxName()).isEqualTo("pdxClass");
        assertThat(regionMapping.getIds()).isEqualTo("ids");
        assertThat(regionMapping.getCatalog()).isEqualTo("catalog");
        assertThat(regionMapping.getSchema()).isEqualTo("schema");
    }

    @Test
    public void endElementRegionMapping() {
        RegionMapping mapping = Mockito.mock(RegionMapping.class);
        stack.push(regionCreation);
        stack.push(mapping);
        JDBC_MAPPING.endElement(stack);
        assertThat(stack.size()).isEqualTo(1);
    }

    @Test
    public void startElementFieldMappingThrowsWithoutRegionMapping() {
        stack.push(new Object());
        assertThatThrownBy(() -> FIELD_MAPPING.startElement(stack, attributes)).isInstanceOf(CacheXmlException.class).hasMessage("<jdbc:field-mapping> elements must occur within <jdbc:mapping> elements");
    }

    @Test
    public void startElementFieldMapping() {
        RegionMapping mapping = new RegionMapping();
        stack.push(mapping);
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.PDX_NAME)).thenReturn("myPdxName");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.PDX_TYPE)).thenReturn("myPdxType");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.JDBC_NAME)).thenReturn("myJdbcName");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.JDBC_TYPE)).thenReturn("myJdbcType");
        Mockito.when(attributes.getValue(JdbcConnectorServiceXmlParser.JDBC_NULLABLE)).thenReturn("true");
        FieldMapping expected = new FieldMapping("myPdxName", "myPdxType", "myJdbcName", "myJdbcType", true);
        FIELD_MAPPING.startElement(stack, attributes);
        RegionMapping mapping1 = ((RegionMapping) (stack.pop()));
        assertThat(mapping1.getFieldMappings()).isEqualTo(Arrays.asList(expected));
    }

    @Test
    public void endElementFieldMapping() {
        RegionMapping mapping = Mockito.mock(RegionMapping.class);
        stack.push(mapping);
        FIELD_MAPPING.endElement(stack);
        assertThat(stack.size()).isEqualTo(1);
        Mockito.verifyZeroInteractions(mapping);
    }
}

