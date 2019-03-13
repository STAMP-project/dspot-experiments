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


import Warning.NONFINAL_FIELDS;
import Warning.NULL_FIELDS;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author mdogan 6/24/13
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ServiceConfigTest extends HazelcastTestSupport {
    static final String HAZELCAST_START_TAG = "" + ((((((("<hazelcast xmlns=\"http://www.hazelcast.com/schema/config\"\n" + "           xmlns:s=\"http://www.hazelcast.com/schema/sample\"\n") + "           xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "           xsi:schemaLocation=\"\n") + "           http://www.hazelcast.com/schema/sample\n") + "           hazelcast-sample-service.xsd\n") + "           http://www.hazelcast.com/schema/config\n") + "           http://www.hazelcast.com/schema/config/hazelcast-config-3.11.xsd\">\n");

    static final String HAZELCAST_END_TAG = "</hazelcast>\n";

    @Test
    public void testXml() {
        Config config = new ClasspathXmlConfig("com/hazelcast/config/hazelcast-service.xml");
        ServiceConfig serviceConfig = config.getServicesConfig().getServiceConfig("my-service");
        Assert.assertEquals("com.hazelcast.examples.MyService", serviceConfig.getClassName());
        assertParsedServiceConfig(serviceConfig);
    }

    @Test
    public void testYaml() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Config config = new ClasspathYamlConfig("com/hazelcast/config/hazelcast-service.yaml");
        ServiceConfig serviceConfig = config.getServicesConfig().getServiceConfig("my-service");
        Assert.assertEquals("com.hazelcast.examples.MyService", serviceConfig.getClassName());
        assertParsedServiceConfig(serviceConfig);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testXmlMissingParserClassThrows() {
        String xml = (((((((("" + (ServiceConfigTest.HAZELCAST_START_TAG)) + "    <services enable-defaults=\"false\">\n") + "        <service enabled=\"true\">\n") + "            <name>my-service</name>\n") + "            <class-name>com.hazelcast.examples.MyService</class-name>\n") + "            <configuration />\n") + "        </service>\n") + "    </services>\n") + (ServiceConfigTest.HAZELCAST_END_TAG);
        new InMemoryXmlConfig(xml);
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testYamlMissingParserClassThrows() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        String yaml = "" + (((((("hazelcast:\n" + "  services:\n") + "    enable-defaults: false\n") + "    my-service:\n") + "      enabled: true\n") + "      class-name: com.hazelcast.examples.MyService\n") + "      configuration: {}\n");
        new InMemoryYamlConfig(yaml);
    }

    @Test
    public void testService() {
        Config config = new Config();
        MyServiceConfig configObject = new MyServiceConfig();
        MyService service = new MyService();
        config.getServicesConfig().addServiceConfig(new ServiceConfig().setEnabled(true).setName("my-service").setConfigObject(configObject).setImplementation(service));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        factory.newHazelcastInstance(config);
        Assert.assertSame(configObject, service.config);
    }

    @Test
    public void testEqualsAndHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(ServiceConfig.class).allFieldsShouldBeUsed().suppress(NONFINAL_FIELDS, NULL_FIELDS).verify();
    }
}

