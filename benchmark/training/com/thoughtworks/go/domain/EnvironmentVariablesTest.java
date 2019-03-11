/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.EnvironmentVariableConfig;
import com.thoughtworks.go.config.EnvironmentVariablesConfig;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EnvironmentVariablesTest {
    @Test
    public void add_shouldAddEnvironmentVariable() {
        final EnvironmentVariables environmentVariables = new EnvironmentVariables();
        Assert.assertThat(environmentVariables, hasSize(0));
        environmentVariables.add("foo", "bar");
        Assert.assertThat(environmentVariables, hasSize(1));
        Assert.assertThat(environmentVariables, ArgumentMatchers.contains(new EnvironmentVariable("foo", "bar")));
    }

    @Test
    public void toEnvironmentVariables_shouldConvertEnvironmentVariablesConfigToEnvironmentVariable() {
        final EnvironmentVariablesConfig environmentVariableConfigs = new EnvironmentVariablesConfig(Arrays.asList(new EnvironmentVariableConfig("foo", "bar"), new EnvironmentVariableConfig(new GoCipher(), "baz", "car", true)));
        final EnvironmentVariables environmentVariables = EnvironmentVariables.toEnvironmentVariables(environmentVariableConfigs);
        Assert.assertThat(environmentVariables, containsInAnyOrder(new EnvironmentVariable("foo", "bar", false), new EnvironmentVariable("baz", "car", true)));
    }

    @Test
    public void addTo_shouldAddEnvironmentVariablesToEnvironmentVariableContext() {
        final EnvironmentVariableContext environmentVariableContext = Mockito.mock(EnvironmentVariableContext.class);
        final EnvironmentVariables environmentVariables = new EnvironmentVariables(new EnvironmentVariable("foo", "bar"), new EnvironmentVariable("baz", "car", true));
        environmentVariables.addTo(environmentVariableContext);
        Mockito.verify(environmentVariableContext, Mockito.times(1)).setProperty("foo", "bar", false);
        Mockito.verify(environmentVariableContext, Mockito.times(1)).setProperty("baz", "car", true);
    }

    @Test
    public void addToIfExists_shouldAddEnvironmentVariableToEnvironmentVariableContext() {
        final EnvironmentVariableContext environmentVariableContext = Mockito.mock(EnvironmentVariableContext.class);
        final EnvironmentVariables environmentVariables = new EnvironmentVariables(new EnvironmentVariable("foo", "bar"), new EnvironmentVariable("baz", "car", true));
        Mockito.when(environmentVariableContext.hasProperty("foo")).thenReturn(false);
        Mockito.when(environmentVariableContext.hasProperty("baz")).thenReturn(true);
        environmentVariables.addToIfExists(environmentVariableContext);
        Mockito.verify(environmentVariableContext, Mockito.times(0)).setProperty("foo", "bar", false);
        Mockito.verify(environmentVariableContext, Mockito.times(1)).setProperty("baz", "car", true);
    }

    @Test
    public void shouldGetOnlyInsecureValues() {
        EnvironmentVariables variables = new EnvironmentVariables(new EnvironmentVariable("key1", "value1", true), new EnvironmentVariable("key2", "value2"));
        Assert.assertThat(variables.getInsecureEnvironmentVariableOrDefault("key1", "def1"), is("def1"));
        Assert.assertThat(variables.getInsecureEnvironmentVariableOrDefault("key2", null), is("value2"));
        Assert.assertThat(variables.getInsecureEnvironmentVariableOrDefault("key3", null), is(nullValue()));
    }

    @Test
    public void shouldOverrideWithProvidedOverrideValues() {
        EnvironmentVariables variables = new EnvironmentVariables(new EnvironmentVariable("key1", "value1"), new EnvironmentVariable("key2", "value2"));
        EnvironmentVariables variablesForOverride = new EnvironmentVariables(new EnvironmentVariable("key2", "value2-new"));
        variables.overrideWith(variablesForOverride);
        Assert.assertThat(variables.getInsecureEnvironmentVariableOrDefault("key1", null), is("value1"));
        Assert.assertThat(variables.getInsecureEnvironmentVariableOrDefault("key2", null), is("value2-new"));
        Assert.assertThat(variables.getInsecureEnvironmentVariableOrDefault("key3", null), is(nullValue()));
    }
}

