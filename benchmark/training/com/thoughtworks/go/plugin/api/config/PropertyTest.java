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
package com.thoughtworks.go.plugin.api.config;


import Property.DISPLAY_NAME;
import Property.DISPLAY_ORDER;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PropertyTest {
    @Test
    public void shouldGetOptionValue() {
        Property property = new Property("key");
        property.with(DISPLAY_NAME, "some display name");
        property.with(DISPLAY_ORDER, 3);
        Assert.assertThat(property.getOption(DISPLAY_NAME), Matchers.is("some display name"));
        Assert.assertThat(property.getOption(DISPLAY_ORDER), Matchers.is(3));
    }

    @Test
    public void shouldReturnDefaultValueWhenNoValueOrEmptyValueIsSpecified() {
        String defaultValue = "test-default";
        Property property = new Property("key", null, defaultValue);
        Assert.assertThat(property.getValue(), Matchers.is(defaultValue));
        property = new Property("key", "", defaultValue);
        Assert.assertThat(property.getValue(), Matchers.is(""));
        property = new Property("key").withDefault(defaultValue);
        Assert.assertThat(property.getValue(), Matchers.is(defaultValue));
        property = new Property("key");
        Assert.assertThat(((property.getValue()) == null), Matchers.is(true));
        String value = "yek";
        property = new Property("key", value, defaultValue);
        Assert.assertThat(property.getValue(), Matchers.is(value));
    }
}

