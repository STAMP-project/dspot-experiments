/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.access.common.settings;


import com.thoughtworks.go.plugin.api.config.Property;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PluginSettingsConfigurationTest {
    @Test
    public void shouldSortConfigurationPropertiesBasedOnDisplayOrder() {
        PluginSettingsProperty p3 = createProperty("k3", 3);
        PluginSettingsProperty p0 = createProperty("k0", 0);
        PluginSettingsProperty p2 = createProperty("k2", 2);
        PluginSettingsProperty p1 = createProperty("k1", 1);
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        configuration.add(p3);
        configuration.add(p0);
        configuration.add(p2);
        configuration.add(p1);
        List<? extends Property> properties = configuration.list();
        Assert.assertThat(getKey(), Matchers.is("k0"));
        Assert.assertThat(getKey(), Matchers.is("k1"));
        Assert.assertThat(getKey(), Matchers.is("k2"));
        Assert.assertThat(getKey(), Matchers.is("k3"));
    }
}

