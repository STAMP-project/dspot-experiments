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
package com.hazelcast.config;


import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Test cases specific only to XML based configuration. The cases not
 * XML specific should be added to {@link XMLConfigBuilderTest}.
 * <p/>
 * This test class is expected to contain only <strong>extra</strong> test
 * cases over the ones defined in {@link XMLConfigBuilderTest} in order
 * to cover XML specific cases where XML configuration derives from the
 * YAML configuration to allow usage of XML-native constructs.
 *
 * @see XMLConfigBuilderTest
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class XmlOnlyConfigBuilderTest {
    private static final String HAZELCAST_START_TAG = "<hazelcast xmlns=\"http://www.hazelcast.com/schema/config\">\n";

    private static final String HAZELCAST_END_TAG = "</hazelcast>\n";

    @Test(expected = InvalidConfigurationException.class)
    public void testMissingNamespace() {
        String xml = "<hazelcast/>";
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidNamespace() {
        String xml = "<hazelcast xmlns=\"http://foo.bar\"/>";
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }

    @Test
    public void testValidNamespace() {
        String xml = (XmlOnlyConfigBuilderTest.HAZELCAST_START_TAG) + (XmlOnlyConfigBuilderTest.HAZELCAST_END_TAG);
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testHazelcastTagAppearsTwice() {
        String xml = ((XmlOnlyConfigBuilderTest.HAZELCAST_START_TAG) + "<hazelcast/>") + (XmlOnlyConfigBuilderTest.HAZELCAST_END_TAG);
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testHazelcastInstanceNameEmpty() {
        String xml = ((XmlOnlyConfigBuilderTest.HAZELCAST_START_TAG) + "<instance-name></instance-name>") + (XmlOnlyConfigBuilderTest.HAZELCAST_END_TAG);
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }

    @Test
    public void testXsdVersion() {
        String origVersionOverride = System.getProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION);
        XmlOnlyConfigBuilderTest.assertXsdVersion("0.0", "0.0");
        XmlOnlyConfigBuilderTest.assertXsdVersion("3.9", "3.9");
        XmlOnlyConfigBuilderTest.assertXsdVersion("3.9-SNAPSHOT", "3.9");
        XmlOnlyConfigBuilderTest.assertXsdVersion("3.9.1-SNAPSHOT", "3.9");
        XmlOnlyConfigBuilderTest.assertXsdVersion("3.10", "3.10");
        XmlOnlyConfigBuilderTest.assertXsdVersion("3.10-SNAPSHOT", "3.10");
        XmlOnlyConfigBuilderTest.assertXsdVersion("3.10.1-SNAPSHOT", "3.10");
        XmlOnlyConfigBuilderTest.assertXsdVersion("99.99.99", "99.99");
        XmlOnlyConfigBuilderTest.assertXsdVersion("99.99.99-SNAPSHOT", "99.99");
        XmlOnlyConfigBuilderTest.assertXsdVersion("99.99.99-Beta", "99.99");
        XmlOnlyConfigBuilderTest.assertXsdVersion("99.99.99-Beta-SNAPSHOT", "99.99");
        if (origVersionOverride != null) {
            System.setProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION, origVersionOverride);
        } else {
            System.clearProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION);
        }
    }

    @Test
    public void testConfig2Xml2DefaultConfig() {
        XmlOnlyConfigBuilderTest.testConfig2Xml2Config("hazelcast-default.xml");
    }

    @Test
    public void testConfig2Xml2FullConfig() {
        XmlOnlyConfigBuilderTest.testConfig2Xml2Config("hazelcast-fullconfig.xml");
    }

    @Test
    public void testConfig2Xml2Config_withAdvancedNetworkConfig() {
        XmlOnlyConfigBuilderTest.testConfig2Xml2Config("hazelcast-fullconfig-advanced-network-config.xml");
    }

    @Test
    public void testXSDDefaultXML() throws Exception {
        XmlOnlyConfigBuilderTest.testXSDConfigXML("hazelcast-default.xml");
    }

    @Test
    public void testFullConfig() throws Exception {
        XmlOnlyConfigBuilderTest.testXSDConfigXML("hazelcast-fullconfig.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeConfig_noName_noExtractor() {
        String xml = ((((((XmlOnlyConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <attributes>\n") + "           <attribute></attribute>\n") + "       </attributes>") + "   </map>") + (XmlOnlyConfigBuilderTest.HAZELCAST_END_TAG);
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeConfig_noName_noExtractor_singleTag() {
        String xml = ((((((XmlOnlyConfigBuilderTest.HAZELCAST_START_TAG) + "   <map name=\"people\">\n") + "       <attributes>\n") + "           <attribute/>\n") + "       </attributes>") + "   </map>") + (XmlOnlyConfigBuilderTest.HAZELCAST_END_TAG);
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testCacheConfig_withInvalidEvictionConfig_failsFast() {
        String xml = ((((XmlOnlyConfigBuilderTest.HAZELCAST_START_TAG) + "    <cache name=\"cache\">") + "        <eviction size=\"10000000\" max-size-policy=\"ENTRY_COUNT\" eviction-policy=\"INVALID\"/>") + "    </cache>") + (XmlOnlyConfigBuilderTest.HAZELCAST_END_TAG);
        XmlOnlyConfigBuilderTest.buildConfig(xml);
    }
}

