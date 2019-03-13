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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigurableTest {
    public static final String JSON_LOCATION = "add_service_api/configurable.json";

    public static final String JSON_LOCATION2 = "add_service_api/configurable2.json";

    public static final String JSON_LOCATION3 = "add_service_api/configurable3.json";

    public static final String INVALID_CONFIGS_LOCATION = "add_service_api/invalid_configurables.txt";

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableTest.class);

    /**
     * Parse normal JSON configuration
     */
    @Test
    public void testParseConfigurable() throws Exception {
        ConfigurableTest.TestConfigurable configurable = new ObjectMapper().readValue(Resources.getResource(ConfigurableTest.JSON_LOCATION), ConfigurableTest.TestConfigurable.class);
        Assert.assertEquals(ImmutableMap.of("zoo.cfg", ImmutableMap.of("dataDir", "/zookeeper1")), configurable.getConfiguration().getProperties());
        Assert.assertEquals(ImmutableMap.of("zoo.cfg", ImmutableMap.of("final", ImmutableMap.of("someProp", "true"))), configurable.getConfiguration().getAttributes());
    }

    @Test
    public void parseInvalidConfigurables() throws Exception {
        String invalidConfigsTxt = Resources.toString(Resources.getResource(ConfigurableTest.INVALID_CONFIGS_LOCATION), StandardCharsets.UTF_8);
        List<String> invalidConfigs = // filter block comment (Apache license) and use line comments as separators between json snippets
        Splitter.on(Pattern.compile("\'\'\'(.|[\\r\\n])*?\'\'\'|#.*$", Pattern.MULTILINE)).omitEmptyStrings().trimResults().splitToList(invalidConfigsTxt);
        for (String config : invalidConfigs) {
            ConfigurableTest.LOG.info("Invalid config to parse:\n{}", config);
            try {
                new ObjectMapper().readValue(config, ConfigurableTest.TestConfigurable.class);
                Assert.fail(((("Expected " + (JsonProcessingException.class.getSimpleName())) + " while processing config:\n") + config));
            } catch (JsonProcessingException ex) {
                Throwable rootCause = ExceptionUtils.getRootCause(ex);
                ConfigurableTest.LOG.info("Error message: {}", rootCause.getMessage());
                Assert.assertTrue(((((("Expected " + (IllegalArgumentException.class.getSimpleName())) + " during parsing JSON:\n") + config) + "\n found: ") + rootCause), (rootCause instanceof IllegalArgumentException));
            }
        }
    }

    /**
     * Deserialize normal JSON configuration
     */
    @Test
    public void testSerializeDeserialize() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ConfigurableTest.TestConfigurable configurable = mapper.readValue(Resources.getResource(ConfigurableTest.JSON_LOCATION), ConfigurableTest.TestConfigurable.class);
        String persisted = mapper.writeValueAsString(configurable);
        Configurable restored = mapper.readValue(persisted, ConfigurableTest.TestConfigurable.class);
        Assert.assertEquals(configurable.getConfiguration().getProperties(), restored.getConfiguration().getProperties());
        Assert.assertEquals(configurable.getConfiguration().getAttributes(), restored.getConfiguration().getAttributes());
    }

    /**
     * Parse flattened configuration
     */
    @Test
    public void testParseConfigurableFromResourceManager() throws Exception {
        URL url = Resources.getResource(ConfigurableTest.JSON_LOCATION2);
        ConfigurableTest.TestConfigurable configurable = new ObjectMapper().readValue(url, ConfigurableTest.TestConfigurable.class);
        Assert.assertEquals(ImmutableMap.of("zoo.cfg", ImmutableMap.of("dataDir", "/zookeeper1")), configurable.getConfiguration().getProperties());
        Assert.assertEquals(ImmutableMap.of("zoo.cfg", ImmutableMap.of("final", ImmutableMap.of("someProp", "true"))), configurable.getConfiguration().getAttributes());
    }

    /**
     * Parse legacy configuration
     */
    @Test
    public void testParseLegacyConfigurable() throws Exception {
        URL url = Resources.getResource(ConfigurableTest.JSON_LOCATION3);
        ConfigurableTest.TestConfigurable configurable = new ObjectMapper().readValue(url, ConfigurableTest.TestConfigurable.class);
        Assert.assertEquals(ImmutableMap.of("cluster-env", ImmutableMap.of("dataDir", "/zookeeper1", "custom-property", "true")), configurable.getConfiguration().getProperties());
    }

    @Test
    public void testTransformAttributesMap() {
        Map<String, Map<String, String>> attributes = ImmutableMap.of("propertyName1", ImmutableMap.of("minimum", "3000", "maximum", "4000"), "propertyName2", ImmutableMap.of("minimum", "3500", "maximum", "4500", "hidden", "true"));
        Map<String, Map<String, String>> transformed = ImmutableMap.of("minimum", ImmutableMap.of("propertyName1", "3000", "propertyName2", "3500"), "maximum", ImmutableMap.of("propertyName1", "4000", "propertyName2", "4500"), "hidden", ImmutableMap.of("propertyName2", "true"));
        Assert.assertEquals(transformed, ConfigurableHelper.transformAttributesMap(attributes));
        Assert.assertEquals(attributes, ConfigurableHelper.transformAttributesMap(transformed));
    }

    static class TestConfigurable implements Configurable {
        Configuration configuration;

        @Override
        public Configuration getConfiguration() {
            return configuration;
        }

        @Override
        public void setConfiguration(Configuration configuration) {
            this.configuration = configuration;
        }
    }
}

