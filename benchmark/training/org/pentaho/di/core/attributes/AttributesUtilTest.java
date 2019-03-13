/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.attributes;


import AttributesUtil.XML_TAG;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Node;


@PrepareForTest(AttributesUtil.class)
@RunWith(PowerMockRunner.class)
public class AttributesUtilTest {
    private static final String CUSTOM_TAG = "customTag";

    private static final String A_KEY = "aKEY";

    private static final String A_VALUE = "aVALUE";

    private static final String A_GROUP = "attributesGroup";

    @Test
    public void testGetAttributesXml_DefaultTag() {
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class))).thenCallRealMethod();
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyString())).thenCallRealMethod();
        Map<String, String> attributesGroup = new HashMap<>();
        Map<String, Map<String, String>> attributesMap = new HashMap<>();
        attributesGroup.put(AttributesUtilTest.A_KEY, AttributesUtilTest.A_VALUE);
        attributesMap.put(AttributesUtilTest.A_GROUP, attributesGroup);
        String attributesXml = AttributesUtil.getAttributesXml(attributesMap);
        Assert.assertNotNull(attributesXml);
        // The default tag was used
        Assert.assertTrue(attributesXml.contains(XML_TAG));
        // The group is present
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.A_GROUP));
        // Both Key and Value are present
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.A_KEY));
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.A_VALUE));
        // Verify that getAttributesXml was invoked once (and with the right parameters)
        PowerMockito.verifyStatic(AttributesUtil.class);
        AttributesUtil.getAttributesXml(attributesMap, XML_TAG);
    }

    @Test
    public void testGetAttributesXml_CustomTag() {
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyString())).thenCallRealMethod();
        Map<String, String> attributesGroup = new HashMap<>();
        Map<String, Map<String, String>> attributesMap = new HashMap<>();
        attributesGroup.put(AttributesUtilTest.A_KEY, AttributesUtilTest.A_VALUE);
        attributesMap.put(AttributesUtilTest.A_GROUP, attributesGroup);
        String attributesXml = AttributesUtil.getAttributesXml(attributesMap, AttributesUtilTest.CUSTOM_TAG);
        Assert.assertNotNull(attributesXml);
        // The custom tag was used
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.CUSTOM_TAG));
        // The group is present
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.A_GROUP));
        // Both Key and Value are present
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.A_KEY));
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.A_VALUE));
    }

    @Test
    public void testGetAttributesXml_DefaultTag_NullParameter() {
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class))).thenCallRealMethod();
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyString())).thenCallRealMethod();
        String attributesXml = AttributesUtil.getAttributesXml(null);
        Assert.assertNotNull(attributesXml);
        // Check that it's not an empty XML fragment
        Assert.assertTrue(attributesXml.contains(XML_TAG));
    }

    @Test
    public void testGetAttributesXml_CustomTag_NullParameter() {
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyString())).thenCallRealMethod();
        String attributesXml = AttributesUtil.getAttributesXml(null, AttributesUtilTest.CUSTOM_TAG);
        Assert.assertNotNull(attributesXml);
        // Check that it's not an empty XML fragment
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.CUSTOM_TAG));
    }

    @Test
    public void testGetAttributesXml_DefaultTag_EmptyMap() {
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class))).thenCallRealMethod();
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyString())).thenCallRealMethod();
        Map<String, Map<String, String>> attributesMap = new HashMap<>();
        String attributesXml = AttributesUtil.getAttributesXml(attributesMap);
        Assert.assertNotNull(attributesXml);
        // Check that it's not an empty XML fragment
        Assert.assertTrue(attributesXml.contains(XML_TAG));
    }

    @Test
    public void testGetAttributesXml_CustomTag_EmptyMap() {
        PowerMockito.when(AttributesUtil.getAttributesXml(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyString())).thenCallRealMethod();
        Map<String, Map<String, String>> attributesMap = new HashMap<>();
        String attributesXml = AttributesUtil.getAttributesXml(attributesMap, AttributesUtilTest.CUSTOM_TAG);
        Assert.assertNotNull(attributesXml);
        // Check that it's not an empty XML fragment
        Assert.assertTrue(attributesXml.contains(AttributesUtilTest.CUSTOM_TAG));
    }

    @Test
    public void testLoadAttributes_NullParameter() {
        PowerMockito.when(AttributesUtil.loadAttributes(ArgumentMatchers.any(Node.class))).thenCallRealMethod();
        Map<String, Map<String, String>> attributesMap = AttributesUtil.loadAttributes(null);
        Assert.assertNotNull(attributesMap);
        Assert.assertTrue(attributesMap.isEmpty());
    }
}

