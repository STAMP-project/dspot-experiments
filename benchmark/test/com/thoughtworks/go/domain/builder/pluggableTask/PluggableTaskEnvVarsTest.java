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
package com.thoughtworks.go.domain.builder.pluggableTask;


import Console.SecureEnvVarSpecifier;
import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PluggableTaskEnvVarsTest {
    private EnvironmentVariableContext context;

    private PluggableTaskEnvVars envVars;

    private List<String> keys = Arrays.asList("Social Net 1", "Social Net 2", "Social Net 3");

    private List<String> values = Arrays.asList("Twitter", "Facebook", "Mega Upload");

    @Test
    public void shouldReturnEnvVarsMap() throws Exception {
        Map<String, String> envMap = envVars.asMap();
        Assert.assertThat(envMap.keySet().containsAll(keys), Matchers.is(true));
        Assert.assertThat(envMap.values().containsAll(values), Matchers.is(true));
        for (int i = 0; i < (keys.size()); i++) {
            Assert.assertThat(envMap.get(keys.get(i)), Matchers.is(values.get(i)));
        }
    }

    @Test
    public void testSecureEnvSpecifier() throws Exception {
        Console.SecureEnvVarSpecifier secureEnvVarSpecifier = envVars.secureEnvSpecifier();
        for (int i = 0; i < (keys.size()); i++) {
            Assert.assertThat(secureEnvVarSpecifier.isSecure(keys.get(i)), Matchers.is(((i % 2) != 0)));
        }
    }

    @Test
    public void shouldPrintToConsole() throws Exception {
        Console console = Mockito.mock(Console.class);
        envVars.writeTo(console);
        Mockito.verify(console).printEnvironment(envVars.asMap(), envVars.secureEnvSpecifier());
    }
}

