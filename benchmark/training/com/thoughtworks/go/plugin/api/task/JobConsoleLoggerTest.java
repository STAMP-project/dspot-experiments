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
package com.thoughtworks.go.plugin.api.task;


import com.thoughtworks.go.plugin.api.task.Console.SecureEnvVarSpecifier;
import com.thoughtworks.go.util.ReflectionUtil;
import java.io.InputStream;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class JobConsoleLoggerTest {
    private TaskExecutionContext taskExecutionContext;

    private Console mockedConsole;

    private JobConsoleLogger consoleLogger;

    private EnvironmentVariables environment;

    @Test
    public void shouldFailGetLoggerIfContextIsNotSet() {
        ReflectionUtil.setStaticField(JobConsoleLogger.class, "context", null);
        try {
            JobConsoleLogger.getConsoleLogger();
            Assert.fail("expected this to fail");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("context is null"));
        }
    }

    @Test
    public void shouldDelegatePrintLineToConsole() {
        String line = "some line";
        consoleLogger.printLine(line);
        Mockito.verify(mockedConsole).printLine(line);
    }

    @Test
    public void shouldDelegateReadOutputOfToConsole() {
        InputStream inputStream = Mockito.mock(InputStream.class);
        consoleLogger.readOutputOf(inputStream);
        Mockito.verify(mockedConsole).readOutputOf(inputStream);
    }

    @Test
    public void shouldDelegateReadErrorOfToConsole() {
        InputStream inputStream = Mockito.mock(InputStream.class);
        consoleLogger.readErrorOf(inputStream);
        Mockito.verify(mockedConsole).readErrorOf(inputStream);
    }

    @Test
    public void shouldDelegatePrintEnvironmentToConsole() {
        Console.SecureEnvVarSpecifier secureEnvVarSpecifier = Mockito.mock(SecureEnvVarSpecifier.class);
        Mockito.when(environment.secureEnvSpecifier()).thenReturn(secureEnvVarSpecifier);
        HashMap<String, String> environmentVariablesMap = new HashMap<>();
        consoleLogger.printEnvironment(environmentVariablesMap);
        Mockito.verify(mockedConsole).printEnvironment(environmentVariablesMap, secureEnvVarSpecifier);
    }
}

