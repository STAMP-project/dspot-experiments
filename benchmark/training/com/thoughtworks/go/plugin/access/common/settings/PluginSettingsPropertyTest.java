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


import Property.DISPLAY_NAME;
import Property.DISPLAY_ORDER;
import Property.REQUIRED;
import Property.SECURE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PluginSettingsPropertyTest {
    @Test
    public void validatePropertyDefaults() {
        PluginSettingsProperty property = new PluginSettingsProperty("Test-Property");
        Assert.assertThat(property.getOptions().size(), Matchers.is(4));
        Assert.assertThat(property.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(property.getOption(SECURE), Matchers.is(false));
        Assert.assertThat(property.getOption(DISPLAY_NAME), Matchers.is("Test-Property"));
        Assert.assertThat(property.getOption(DISPLAY_ORDER), Matchers.is(0));
        property = new PluginSettingsProperty("Test-Property", "Dummy Value");
        Assert.assertThat(property.getOptions().size(), Matchers.is(4));
        Assert.assertThat(property.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(property.getOption(SECURE), Matchers.is(false));
        Assert.assertThat(property.getOption(DISPLAY_NAME), Matchers.is("Test-Property"));
        Assert.assertThat(property.getOption(DISPLAY_ORDER), Matchers.is(0));
    }

    @Test
    public void shouldCompareTwoPropertiesBasedOnOrder() {
        PluginSettingsProperty p1 = createProperty("Test-Property", 1);
        PluginSettingsProperty p2 = createProperty("Test-Property", 0);
        Assert.assertThat(p1.compareTo(p2), Matchers.is(1));
    }
}

