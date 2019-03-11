/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Configuration unit tests.
 */
public class ConfigurationTest {
    private static final Map<String, Map<String, String>> EMPTY_PROPERTIES = new HashMap<>();

    private static final Map<String, Map<String, Map<String, String>>> EMPTY_ATTRIBUTES = new HashMap<>();

    @Test
    public void testGetProperties_noParent() {
        Map<String, Map<String, String>> properties = new HashMap<>();
        Map<String, String> typeProperties1 = new HashMap<>();
        typeProperties1.put("prop1", "val1");
        typeProperties1.put("prop2", "val2");
        Map<String, String> typeProperties2 = new HashMap<>();
        typeProperties2.put("prop1", "val1");
        typeProperties2.put("prop3", "val3");
        properties.put("type1", typeProperties1);
        properties.put("type2", typeProperties2);
        Configuration configuration = new Configuration(properties, ConfigurationTest.EMPTY_ATTRIBUTES);
        Assert.assertEquals(properties, configuration.getProperties());
        Assert.assertEquals(ConfigurationTest.EMPTY_ATTRIBUTES, configuration.getAttributes());
    }

    @Test
    public void testGetFullProperties_noParent() {
        Map<String, Map<String, String>> properties = new HashMap<>();
        Map<String, String> typeProperties1 = new HashMap<>();
        typeProperties1.put("prop1", "val1");
        typeProperties1.put("prop2", "val2");
        Map<String, String> typeProperties2 = new HashMap<>();
        typeProperties2.put("prop1", "val1");
        typeProperties2.put("prop3", "val3");
        properties.put("type1", typeProperties1);
        properties.put("type2", typeProperties2);
        Configuration configuration = new Configuration(properties, ConfigurationTest.EMPTY_ATTRIBUTES);
        Assert.assertEquals(properties, configuration.getFullProperties());
        Assert.assertEquals(ConfigurationTest.EMPTY_ATTRIBUTES, configuration.getAttributes());
    }

    @Test
    public void testGetProperties_withParent() {
        Map<String, Map<String, String>> properties = new HashMap<>();
        Map<String, String> typeProperties1 = new HashMap<>();
        typeProperties1.put("prop1", "val1");
        typeProperties1.put("prop2", "val2");
        Map<String, String> typeProperties2 = new HashMap<>();
        typeProperties2.put("prop1", "val1");
        typeProperties2.put("prop3", "val3");
        properties.put("type1", typeProperties1);
        properties.put("type2", typeProperties2);
        Map<String, Map<String, String>> parentProperties = new HashMap<>();
        Map<String, String> parentTypeProperties1 = new HashMap<>();
        parentTypeProperties1.put("prop5", "val5");
        Map<String, String> parentTypeProperties3 = new HashMap<>();
        parentTypeProperties3.put("prop6", "val6");
        parentProperties.put("type1", parentTypeProperties1);
        parentProperties.put("type3", parentTypeProperties3);
        Configuration parentConfiguration = new Configuration(parentProperties, ConfigurationTest.EMPTY_ATTRIBUTES);
        Configuration configuration = new Configuration(properties, ConfigurationTest.EMPTY_ATTRIBUTES, parentConfiguration);
        // parent should not be reflected in getProperties() result
        Assert.assertEquals(properties, configuration.getProperties());
        Assert.assertEquals(ConfigurationTest.EMPTY_ATTRIBUTES, configuration.getAttributes());
    }

    @Test
    public void testGetFullProperties_withParent() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        // all parents should be reflected in getFullProperties() result
        Map<String, Map<String, String>> fullProperties = configuration.getFullProperties();
        // type1, type2, type3, type4
        Assert.assertEquals(4, fullProperties.size());
        // type1
        Map<String, String> type1Props = fullProperties.get("type1");
        Assert.assertEquals(5, type1Props.size());
        Assert.assertEquals("val1.3", type1Props.get("prop1"));
        Assert.assertEquals("val2.2", type1Props.get("prop2"));
        Assert.assertEquals("val3.1", type1Props.get("prop3"));
        Assert.assertEquals("val6.2", type1Props.get("prop6"));
        Assert.assertEquals("val9.3", type1Props.get("prop9"));
        // type2
        Map<String, String> type2Props = fullProperties.get("type2");
        Assert.assertEquals(2, type2Props.size());
        Assert.assertEquals("val4.3", type2Props.get("prop4"));
        Assert.assertEquals("val5.1", type2Props.get("prop5"));
        // type3
        Map<String, String> type3Props = fullProperties.get("type3");
        Assert.assertEquals(2, type3Props.size());
        Assert.assertEquals("val7.3", type3Props.get("prop7"));
        Assert.assertEquals("val8.2", type3Props.get("prop8"));
        // type4
        Map<String, String> type4Props = fullProperties.get("type4");
        Assert.assertEquals(2, type4Props.size());
        Assert.assertEquals("val10.3", type4Props.get("prop10"));
        Assert.assertEquals("val11.3", type4Props.get("prop11"));
        // ensure that underlying property map is not modified in getFullProperties
        Configuration expectedConfiguration = createConfigurationWithParents_PropsOnly();
        Assert.assertEquals(expectedConfiguration.getProperties(), configuration.getProperties());
        Assert.assertEquals(expectedConfiguration.getParentConfiguration().getProperties(), configuration.getParentConfiguration().getProperties());
        Assert.assertEquals(expectedConfiguration.getParentConfiguration().getParentConfiguration().getProperties(), configuration.getParentConfiguration().getParentConfiguration().getProperties());
        Assert.assertEquals(ConfigurationTest.EMPTY_ATTRIBUTES, configuration.getAttributes());
        Collection<String> configTypes = configuration.getAllConfigTypes();
        Assert.assertEquals(4, configTypes.size());
        Assert.assertTrue(configTypes.containsAll(Arrays.asList("type1", "type2", "type3", "type4")));
    }

    @Test
    public void containsConfigType() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        Assert.assertTrue(configuration.containsConfigType("type1"));
        Assert.assertTrue(configuration.containsConfigType("type2"));
        Assert.assertTrue(configuration.containsConfigType("type3"));
        Assert.assertTrue(configuration.containsConfigType("type4"));
        Assert.assertFalse(configuration.containsConfigType("type5"));
        configuration = createConfigurationWithParents_AttributesOnly();
        Assert.assertTrue(configuration.containsConfigType("type1"));
        Assert.assertTrue(configuration.containsConfigType("type2"));
        Assert.assertFalse(configuration.containsConfigType("type3"));
    }

    @Test
    public void containsConfig() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        Assert.assertTrue(configuration.containsConfig("type1", "prop1"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop2"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop3"));
        Assert.assertTrue(configuration.containsConfig("type2", "prop4"));
        Assert.assertTrue(configuration.containsConfig("type2", "prop5"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop6"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop9"));
        Assert.assertTrue(configuration.containsConfig("type3", "prop7"));
        Assert.assertTrue(configuration.containsConfig("type3", "prop8"));
        Assert.assertTrue(configuration.containsConfig("type4", "prop10"));
        Assert.assertTrue(configuration.containsConfig("type4", "prop11"));
        Assert.assertFalse(configuration.containsConfig("type1", "prop99"));
        Assert.assertFalse(configuration.containsConfig("core-site", "io.file.buffer.size"));
        configuration = createConfigurationWithParents_AttributesOnly();
        Assert.assertTrue(configuration.containsConfig("type1", "prop1"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop2"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop3"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop6"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop7"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop8"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop9"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop10"));
        Assert.assertTrue(configuration.containsConfig("type1", "prop11"));
        Assert.assertTrue(configuration.containsConfig("type2", "prop100"));
        Assert.assertTrue(configuration.containsConfig("type2", "prop101"));
        Assert.assertTrue(configuration.containsConfig("type2", "prop102"));
        Assert.assertFalse(configuration.containsConfig("type1", "prop99"));
        Assert.assertFalse(configuration.containsConfig("core-site", "io.file.buffer.size"));
    }

    @Test
    public void testGetFullProperties_withParent_specifyDepth() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        // specify a depth of 1 which means to include only 1 level up the parent chain
        Map<String, Map<String, String>> fullProperties = configuration.getFullProperties(1);
        // type1, type2, type3, type4
        Assert.assertEquals(4, fullProperties.size());
        // type1
        Map<String, String> type1Props = fullProperties.get("type1");
        Assert.assertEquals(4, type1Props.size());
        Assert.assertEquals("val1.3", type1Props.get("prop1"));
        Assert.assertEquals("val2.2", type1Props.get("prop2"));
        Assert.assertEquals("val6.2", type1Props.get("prop6"));
        Assert.assertEquals("val9.3", type1Props.get("prop9"));
        // type2
        Map<String, String> type2Props = fullProperties.get("type2");
        Assert.assertEquals(1, type2Props.size());
        Assert.assertEquals("val4.3", type2Props.get("prop4"));
        // type3
        Map<String, String> type3Props = fullProperties.get("type3");
        Assert.assertEquals(2, type3Props.size());
        Assert.assertEquals("val7.3", type3Props.get("prop7"));
        Assert.assertEquals("val8.2", type3Props.get("prop8"));
        // type4
        Map<String, String> type4Props = fullProperties.get("type4");
        Assert.assertEquals(2, type4Props.size());
        Assert.assertEquals("val10.3", type4Props.get("prop10"));
        Assert.assertEquals("val11.3", type4Props.get("prop11"));
        // ensure that underlying property maps are not modified in getFullProperties
        Configuration expectedConfiguration = createConfigurationWithParents_PropsOnly();
        Assert.assertEquals(expectedConfiguration.getProperties(), configuration.getProperties());
        Assert.assertEquals(expectedConfiguration.getParentConfiguration().getProperties(), configuration.getParentConfiguration().getProperties());
        Assert.assertEquals(expectedConfiguration.getParentConfiguration().getParentConfiguration().getProperties(), configuration.getParentConfiguration().getParentConfiguration().getProperties());
        Assert.assertEquals(ConfigurationTest.EMPTY_ATTRIBUTES, configuration.getAttributes());
    }

    @Test
    public void testGetAttributes_noParent() {
        Map<String, Map<String, Map<String, String>>> attributes = new HashMap<>();
        Map<String, Map<String, String>> attributeProperties = new HashMap<>();
        Map<String, String> properties1 = new HashMap<>();
        properties1.put("prop1", "val1");
        properties1.put("prop2", "val2");
        Map<String, String> properties2 = new HashMap<>();
        properties2.put("prop1", "val3");
        attributeProperties.put("attribute1", properties1);
        attributeProperties.put("attribute2", properties2);
        attributes.put("type1", attributeProperties);
        // test
        Configuration configuration = new Configuration(ConfigurationTest.EMPTY_PROPERTIES, attributes);
        // assert attributes
        Assert.assertEquals(attributes, configuration.getAttributes());
        // assert empty properties
        Assert.assertEquals(ConfigurationTest.EMPTY_PROPERTIES, configuration.getProperties());
    }

    @Test
    public void testGetFullAttributes_withParent() {
        Configuration configuration = createConfigurationWithParents_AttributesOnly();
        // all parents should be reflected in getFullAttributes() result
        Map<String, Map<String, Map<String, String>>> fullAttributes = configuration.getFullAttributes();
        Assert.assertEquals(2, fullAttributes.size());
        // type 1
        Map<String, Map<String, String>> type1Attributes = fullAttributes.get("type1");
        // attribute1, attribute2, attribute3, attribute4
        Assert.assertEquals(4, type1Attributes.size());
        // attribute1
        Map<String, String> attribute1Properties = type1Attributes.get("attribute1");
        Assert.assertEquals(5, attribute1Properties.size());
        Assert.assertEquals("val1.3", attribute1Properties.get("prop1"));
        Assert.assertEquals("val2.2", attribute1Properties.get("prop2"));
        Assert.assertEquals("val3.1", attribute1Properties.get("prop3"));
        Assert.assertEquals("val6.2", attribute1Properties.get("prop6"));
        Assert.assertEquals("val9.3", attribute1Properties.get("prop9"));
        // attribute2
        Map<String, String> attribute2Properties = type1Attributes.get("attribute2");
        Assert.assertEquals(2, attribute2Properties.size());
        Assert.assertEquals("val4.3", attribute2Properties.get("prop4"));
        Assert.assertEquals("val5.1", attribute2Properties.get("prop5"));
        // attribute3
        Map<String, String> attribute3Properties = type1Attributes.get("attribute3");
        Assert.assertEquals(2, attribute3Properties.size());
        Assert.assertEquals("val7.3", attribute3Properties.get("prop7"));
        Assert.assertEquals("val8.2", attribute3Properties.get("prop8"));
        // attribute4
        Map<String, String> attribute4Properties = type1Attributes.get("attribute4");
        Assert.assertEquals(2, attribute4Properties.size());
        Assert.assertEquals("val10.3", attribute4Properties.get("prop10"));
        Assert.assertEquals("val11.3", attribute4Properties.get("prop11"));
        // type 2
        Map<String, Map<String, String>> type2Attributes = fullAttributes.get("type2");
        // attribute100, attribute101
        Assert.assertEquals(2, type2Attributes.size());
        Map<String, String> attribute100Properties = type2Attributes.get("attribute100");
        Assert.assertEquals(3, attribute100Properties.size());
        Assert.assertEquals("val100.3", attribute100Properties.get("prop100"));
        Assert.assertEquals("val101.1", attribute100Properties.get("prop101"));
        Assert.assertEquals("val102.3", attribute100Properties.get("prop102"));
        Map<String, String> attribute101Properties = type2Attributes.get("attribute101");
        Assert.assertEquals(2, attribute101Properties.size());
        Assert.assertEquals("val100.2", attribute101Properties.get("prop100"));
        Assert.assertEquals("val101.1", attribute101Properties.get("prop101"));
        // ensure that underlying attribute maps are not modified in getFullProperties
        Configuration expectedConfiguration = createConfigurationWithParents_AttributesOnly();
        Assert.assertEquals(expectedConfiguration.getAttributes(), configuration.getAttributes());
        Assert.assertEquals(expectedConfiguration.getParentConfiguration().getAttributes(), configuration.getParentConfiguration().getAttributes());
        Assert.assertEquals(expectedConfiguration.getParentConfiguration().getParentConfiguration().getAttributes(), configuration.getParentConfiguration().getParentConfiguration().getAttributes());
        Assert.assertEquals(ConfigurationTest.EMPTY_PROPERTIES, configuration.getProperties());
        Collection<String> configTypes = configuration.getAllConfigTypes();
        Assert.assertEquals(2, configTypes.size());
        Assert.assertTrue(configTypes.containsAll(Arrays.asList("type1", "type2")));
    }

    @Test
    public void testGetPropertyValue() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        Assert.assertEquals("val1.3", configuration.getPropertyValue("type1", "prop1"));
        Assert.assertEquals("val2.2", configuration.getPropertyValue("type1", "prop2"));
        Assert.assertEquals("val3.1", configuration.getPropertyValue("type1", "prop3"));
        Assert.assertEquals("val4.3", configuration.getPropertyValue("type2", "prop4"));
        Assert.assertEquals("val5.1", configuration.getPropertyValue("type2", "prop5"));
        Assert.assertEquals("val6.2", configuration.getPropertyValue("type1", "prop6"));
        Assert.assertEquals("val7.3", configuration.getPropertyValue("type3", "prop7"));
        Assert.assertEquals("val8.2", configuration.getPropertyValue("type3", "prop8"));
        Assert.assertEquals("val9.3", configuration.getPropertyValue("type1", "prop9"));
        Assert.assertEquals("val10.3", configuration.getPropertyValue("type4", "prop10"));
        Assert.assertEquals("val11.3", configuration.getPropertyValue("type4", "prop11"));
    }

    @Test
    public void testGetAttributeValue() {
        Configuration configuration = createConfigurationWithParents_AttributesOnly();
        Assert.assertEquals("val1.3", configuration.getAttributeValue("type1", "prop1", "attribute1"));
        Assert.assertEquals("val2.2", configuration.getAttributeValue("type1", "prop2", "attribute1"));
        Assert.assertEquals("val3.1", configuration.getAttributeValue("type1", "prop3", "attribute1"));
        Assert.assertEquals("val4.3", configuration.getAttributeValue("type1", "prop4", "attribute2"));
        Assert.assertEquals("val5.1", configuration.getAttributeValue("type1", "prop5", "attribute2"));
        Assert.assertEquals("val6.2", configuration.getAttributeValue("type1", "prop6", "attribute1"));
        Assert.assertEquals("val7.3", configuration.getAttributeValue("type1", "prop7", "attribute3"));
        Assert.assertEquals("val8.2", configuration.getAttributeValue("type1", "prop8", "attribute3"));
        Assert.assertEquals("val100.3", configuration.getAttributeValue("type2", "prop100", "attribute100"));
        Assert.assertEquals("val101.1", configuration.getAttributeValue("type2", "prop101", "attribute100"));
        Assert.assertEquals("val102.3", configuration.getAttributeValue("type2", "prop102", "attribute100"));
        Assert.assertEquals("val100.2", configuration.getAttributeValue("type2", "prop100", "attribute101"));
        Assert.assertEquals("val101.1", configuration.getAttributeValue("type2", "prop101", "attribute101"));
    }

    @Test
    public void testRemoveProperty() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        // property only exists in root level config
        Assert.assertEquals("val3.1", configuration.removeProperty("type1", "prop3"));
        Assert.assertNull(configuration.getPropertyValue("type1", "prop3"));
        // property only exists in configuration instance
        Assert.assertEquals("val9.3", configuration.removeProperty("type1", "prop9"));
        Assert.assertNull(configuration.getPropertyValue("type1", "prop9"));
        // property at multiple levels
        Assert.assertEquals("val1.3", configuration.removeProperty("type1", "prop1"));
        Assert.assertNull(configuration.getPropertyValue("type1", "prop1"));
        Assert.assertEquals("val4.3", configuration.removeProperty("type2", "prop4"));
        Assert.assertNull(configuration.getPropertyValue("type2", "prop4"));
        Assert.assertEquals("val2.2", configuration.removeProperty("type1", "prop2"));
        Assert.assertNull(configuration.getPropertyValue("type1", "prop2"));
        // type and property don't exist
        Assert.assertNull(configuration.getPropertyValue("typeXXX", "XXXXX"));
        // type exists but property doesn't
        Assert.assertNull(configuration.getPropertyValue("type1", "XXXXX"));
    }

    @Test
    public void testRemoveConfigTypes() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        configuration.removeConfigType("type1");
        Assert.assertNull(configuration.getProperties().get("type1"));
    }

    @Test
    public void testRemoveConfigTypesForAttributes() {
        Configuration configuration = createConfigurationWithParents_PropsOnly();
        configuration.removeConfigType("type1");
        Assert.assertNull(configuration.getAttributes().get("type1"));
    }

    @Test
    public void moveProperties() {
        // GIVEN
        String sourceType = "source";
        String targetType = "target";
        String sourceValue = "source value";
        String targetValue = "target value";
        Map<String, String> keepers = ImmutableMap.of("keep1", "v1", "keep2", "v3");
        Map<String, String> movers = ImmutableMap.of("move1", "v2", "move2", "v4");
        Set<String> common = ImmutableSet.of("common1", "common2");
        Configuration config = new Configuration(new HashMap(), new HashMap());
        for (Map.Entry<String, String> e : keepers.entrySet()) {
            config.setProperty(sourceType, e.getKey(), e.getValue());
        }
        for (Map.Entry<String, String> e : movers.entrySet()) {
            config.setProperty(sourceType, e.getKey(), e.getValue());
        }
        for (String key : common) {
            config.setProperty(sourceType, key, sourceValue);
            config.setProperty(targetType, key, targetValue);
        }
        // WHEN
        Sets.SetView<String> propertiesToMove = Sets.union(movers.keySet(), common);
        Set<String> moved = config.moveProperties(sourceType, targetType, propertiesToMove);
        // THEN
        for (Map.Entry<String, String> e : keepers.entrySet()) {
            Assert.assertEquals(e.getValue(), config.getPropertyValue(sourceType, e.getKey()));
        }
        for (Map.Entry<String, String> e : movers.entrySet()) {
            Assert.assertEquals(e.getValue(), config.getPropertyValue(targetType, e.getKey()));
            Assert.assertFalse(config.isPropertySet(sourceType, e.getKey()));
        }
        for (String key : common) {
            Assert.assertEquals(targetValue, config.getPropertyValue(targetType, key));
            Assert.assertFalse(config.isPropertySet(sourceType, key));
        }
        Assert.assertEquals(propertiesToMove, moved);
    }
}

