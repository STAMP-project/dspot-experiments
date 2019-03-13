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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class XmlSchemaValidationTest {
    @Rule
    public ExpectedException rule = ExpectedException.none();

    @Test
    public void testXmlDeniesDuplicateGroupConfig() {
        expectDuplicateElementError("group");
        String groupConfig = "" + ((("    <group>\n" + "        <name>foobar</name>\n") + "        <password>dev-pass</password>\n") + "    </group>\n");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + groupConfig) + groupConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicateNetworkConfig() {
        expectDuplicateElementError("network");
        String networkConfig = "" + ((((("    <network>\n" + "        <join>\n") + "            <multicast enabled=\"false\"/>\n") + "            <tcp-ip enabled=\"true\"/>\n") + "        </join>\n") + "    </network>\n");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + networkConfig) + networkConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicateLicenseKeyConfig() {
        expectDuplicateElementError("license-key");
        String licenseConfig = "    <license-key>foo</license-key>";
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + licenseConfig) + licenseConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicatePropertiesConfig() {
        expectDuplicateElementError("properties");
        String propertiesConfig = "" + (("    <properties>\n" + "        <property name=\'foo\'>fooval</property>\n") + "    </properties>\n");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + propertiesConfig) + propertiesConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicatePartitionGroupConfig() {
        expectDuplicateElementError("partition-group");
        String partitionConfig = "" + (((("   <partition-group>\n" + "      <member-group>\n") + "          <interface>foo</interface>\n") + "      </member-group>\n") + "   </partition-group>\n");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + partitionConfig) + partitionConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicateListenersConfig() {
        expectDuplicateElementError("listeners");
        String listenersConfig = "" + (("   <listeners>\n" + "        <listener>foo</listener>\n\n") + "   </listeners>\n");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + listenersConfig) + listenersConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicateSerializationConfig() {
        expectDuplicateElementError("serialization");
        String serializationConfig = "" + ((((((((((((((("       <serialization>\n" + "        <portable-version>0</portable-version>\n") + "        <data-serializable-factories>\n") + "            <data-serializable-factory factory-id=\"1\">com.hazelcast.examples.DataSerializableFactory\n") + "            </data-serializable-factory>\n") + "        </data-serializable-factories>\n") + "        <portable-factories>\n") + "            <portable-factory factory-id=\"1\">com.hazelcast.examples.PortableFactory</portable-factory>\n") + "        </portable-factories>\n") + "        <serializers>\n") + "            <global-serializer>com.hazelcast.examples.GlobalSerializerFactory</global-serializer>\n") + "            <serializer type-class=\"com.hazelcast.examples.DummyType\"\n") + "                class-name=\"com.hazelcast.examples.SerializerFactory\"/>\n") + "        </serializers>\n") + "        <check-class-def-errors>true</check-class-def-errors>\n") + "    </serialization>\n");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + serializationConfig) + serializationConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicateServicesConfig() {
        expectDuplicateElementError("services");
        String servicesConfig = "" + ((((("   <services>\n" + "       <service enabled=\"true\">\n") + "            <name>custom-service</name>\n") + "            <class-name>com.hazelcast.examples.MyService</class-name>\n") + "        </service>\n") + "   </services>");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + servicesConfig) + servicesConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicateSecurityConfig() {
        expectDuplicateElementError("security");
        String securityConfig = "   <security/>\n";
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + securityConfig) + securityConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }

    @Test
    public void testXmlDeniesDuplicateMemberAttributesConfig() {
        expectDuplicateElementError("member-attributes");
        String memberAttConfig = "" + (("    <member-attributes>\n" + "        <attribute name=\"attribute.float\" type=\"float\">1234.5678</attribute>\n") + "    </member-attributes>\n");
        XmlSchemaValidationTest.buildConfig(((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + memberAttConfig) + memberAttConfig) + (XMLConfigBuilderTest.HAZELCAST_END_TAG)));
    }
}

