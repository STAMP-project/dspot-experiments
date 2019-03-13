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
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class XmlYamlConfigBuilderEqualsTest extends HazelcastTestSupport {
    @Test
    public void testFullConfigNormalNetwork() {
        Config xmlConfig = new ClasspathXmlConfig("hazelcast-fullconfig.xml");
        Config yamlConfig = new ClasspathYamlConfig("hazelcast-fullconfig.yaml");
        sortClientPermissionConfigs(xmlConfig);
        sortClientPermissionConfigs(yamlConfig);
        String xmlConfigFromXml = new ConfigXmlGenerator(true).generate(xmlConfig);
        String xmlConfigFromYaml = new ConfigXmlGenerator(true).generate(yamlConfig);
        Assert.assertEquals(xmlConfigFromXml, xmlConfigFromYaml);
    }

    @Test
    public void testFullConfigAdvancedNetwork() {
        Config xmlConfig = new ClasspathXmlConfig("hazelcast-fullconfig-advanced-network-config.xml");
        Config yamlConfig = new ClasspathYamlConfig("hazelcast-fullconfig-advanced-network-config.yaml");
        sortClientPermissionConfigs(xmlConfig);
        sortClientPermissionConfigs(yamlConfig);
        String xmlConfigFromXml = new ConfigXmlGenerator(true).generate(xmlConfig);
        String xmlConfigFromYaml = new ConfigXmlGenerator(true).generate(yamlConfig);
        Assert.assertEquals(xmlConfigFromXml, xmlConfigFromYaml);
    }

    @Test
    public void testDefaultConfig() {
        Config xmlConfig = new ClasspathXmlConfig("hazelcast-default.xml");
        Config yamlConfig = new ClasspathYamlConfig("hazelcast-default.yaml");
        sortClientPermissionConfigs(xmlConfig);
        sortClientPermissionConfigs(yamlConfig);
        String xmlConfigFromXml = new ConfigXmlGenerator(true).generate(xmlConfig);
        String xmlConfigFromYaml = new ConfigXmlGenerator(true).generate(yamlConfig);
        Assert.assertEquals(xmlConfigFromXml, xmlConfigFromYaml);
    }

    @Test
    public void testFullExample() throws IOException {
        String fullExampleXml = readResourceToString("hazelcast-full-example.xml");
        String fullExampleYaml = readResourceToString("hazelcast-full-example.yaml");
        // remove imports to prevent the test from failing with importing non-existing files
        fullExampleXml = fullExampleXml.replace("<import resource=\"your-configuration-XML-file\"/>", "");
        fullExampleYaml = fullExampleYaml.replace("\r", "").replace("import:\n    - your-configuration-YAML-file", "");
        // create file to the working directory needed for the EncryptionReplacer
        createPasswordFile("password.txt", "h4z3lc4$t");
        Config xmlConfig = new InMemoryXmlConfig(fullExampleXml);
        Config yamlConfig = new InMemoryYamlConfig(fullExampleYaml);
        sortClientPermissionConfigs(xmlConfig);
        sortClientPermissionConfigs(yamlConfig);
        String xmlConfigFromXml = new ConfigXmlGenerator(true).generate(xmlConfig);
        String xmlConfigFromYaml = new ConfigXmlGenerator(true).generate(yamlConfig);
        Assert.assertEquals(xmlConfigFromXml, xmlConfigFromYaml);
    }

    private static class PermissionConfigComparator implements Comparator<PermissionConfig> {
        @Override
        public int compare(PermissionConfig o1, PermissionConfig o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return o1.getType().name().compareTo(o2.getType().name());
        }
    }
}

