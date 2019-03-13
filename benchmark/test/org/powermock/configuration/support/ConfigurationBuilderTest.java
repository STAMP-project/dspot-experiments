/**
 * Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.powermock.configuration.support;


import org.junit.Test;
import org.powermock.configuration.Configuration;
import org.powermock.configuration.MockitoConfiguration;
import org.powermock.configuration.PowerMockConfiguration;


public class ConfigurationBuilderTest {
    private static final String CONF_PATH = "org/powermock/extensions";

    private static final String CONFIGURATION_FILE = (ConfigurationBuilderTest.CONF_PATH) + "/test.properties";

    @Test
    public void should_create_configuration_from_file() {
        final Configuration configuration = ConfigurationBuilder.createConfigurationFor(MockitoConfiguration.class).fromFile(ConfigurationBuilderTest.CONFIGURATION_FILE);
        assertThat(configuration).as("Configuration is map").isNotNull();
    }

    @Test
    public void should_read_mock_maker_class_from_configuration() {
        final MockitoConfiguration configuration = ConfigurationBuilder.createConfigurationFor(MockitoConfiguration.class).fromFile(ConfigurationBuilderTest.CONFIGURATION_FILE);
        assertThat(configuration.getMockMakerClass()).as("Configuration is map").isEqualTo("TestMockMaker");
    }

    @Test
    public void should_not_read_mock_maker_class_from_configuration_without_prefix() {
        final MockitoConfiguration configuration = ConfigurationBuilder.createConfigurationFor(MockitoConfiguration.class).fromFile(((ConfigurationBuilderTest.CONF_PATH) + "/test_without_prefix.properties"));
        assertThat(configuration.getMockMakerClass()).as("Configuration is map").isNull();
    }

    @Test
    public void should_return_empty_configuration__when_configuration_file_non_exist() {
        final MockitoConfiguration configuration = ConfigurationBuilder.createConfigurationFor(MockitoConfiguration.class).fromFile(((ConfigurationBuilderTest.CONF_PATH) + "/test_without_prefix"));
        assertThat(configuration.getMockMakerClass()).as("Configuration is null.").isNull();
    }

    @Test
    public void should_return_real_value_instead_alias() {
        final String value = "value";
        final MockitoConfiguration configuration = ConfigurationBuilder.createConfigurationFor(MockitoConfiguration.class).withValueAlias("alias", value).fromFile(((ConfigurationBuilderTest.CONF_PATH) + "/test_with_alias.properties"));
        assertThat(configuration.getMockMakerClass()).as("Configuration is map").isEqualTo(value);
    }

    @Test
    public void should_read_powermock_global_ignore_as_array() {
        PowerMockConfiguration configuration = ConfigurationBuilder.createConfigurationFor(PowerMockConfiguration.class).fromFile(ConfigurationBuilderTest.CONFIGURATION_FILE);
        assertThat(configuration.getGlobalIgnore()).as("Configuration is map").containsExactly("org.somepacckage.*", "org.other.Class");
    }
}

