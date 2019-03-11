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
package com.thoughtworks.go.domain.config;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static ConfigurationValue.VALUE;


public class ConfigurationValueTest {
    @Test
    public void shouldCheckForEqualityOfValue() {
        ConfigurationValue configurationValue = new ConfigurationValue(VALUE);
        Assert.assertThat(configurationValue, Matchers.is(new ConfigurationValue(VALUE)));
    }

    @Test
    public void shouldHandleBooleanValueAsAString() throws Exception {
        final ConfigurationValue configurationValue = new ConfigurationValue(true);
        Assert.assertThat(configurationValue.getValue(), Matchers.is("true"));
    }

    @Test
    public void shouldHandleIntegerValueAsAString() throws Exception {
        final ConfigurationValue configurationValue = new ConfigurationValue(1);
        Assert.assertThat(configurationValue.getValue(), Matchers.is("1"));
    }

    @Test
    public void shouldHandleLongValueAsAString() throws Exception {
        final ConfigurationValue configurationValue = new ConfigurationValue(5L);
        Assert.assertThat(configurationValue.getValue(), Matchers.is("5"));
    }

    @Test
    public void shouldHandleDoubleValueAsAString() throws Exception {
        final ConfigurationValue configurationValue = new ConfigurationValue(3.1428571429);
        Assert.assertThat(configurationValue.getValue(), Matchers.is("3.1428571429"));
    }
}

