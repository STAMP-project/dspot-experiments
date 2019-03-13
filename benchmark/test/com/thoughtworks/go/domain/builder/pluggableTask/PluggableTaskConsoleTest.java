/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain.builder.pluggableTask;


import Console.SecureEnvVarSpecifier;
import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.util.command.SafeOutputStreamConsumer;
import java.io.StringBufferInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static PluggableTaskConsole.MASK_VALUE;


public class PluggableTaskConsoleTest {
    @Mock
    SafeOutputStreamConsumer safeOutputStreamConsumer;

    private PluggableTaskConsole console;

    private List<String> keys = Arrays.asList("Social Net 1", "Social Net 2", "Social Net 3");

    private List<String> values = Arrays.asList("Twitter", "Facebook", "Mega Upload");

    @Test
    public void shouldPrintLineToPublisher() throws Exception {
        String line = "Test Line";
        Mockito.doNothing().when(safeOutputStreamConsumer).stdOutput(line);
        console.printLine(line);
        Mockito.verify(safeOutputStreamConsumer).stdOutput(line);
    }

    @Test
    public void shouldPrintEnvironmentVars() throws Exception {
        Map<String, String> env = new HashMap<>();
        Console.SecureEnvVarSpecifier varSpecifier = Mockito.mock(SecureEnvVarSpecifier.class);
        for (int i = 0; i < (keys.size()); i++) {
            env.put(keys.get(i), values.get(i));
            Mockito.when(varSpecifier.isSecure(keys.get(i))).thenReturn(((i % 2) == 0));
        }
        Mockito.doNothing().when(safeOutputStreamConsumer).stdOutput("Environment variables: ");
        for (int i = 0; i < (keys.size()); i++) {
            Mockito.doNothing().when(safeOutputStreamConsumer).stdOutput(String.format("Name= %s  Value= %s", keys.get(i), ((i % 2) == 0 ? MASK_VALUE : values.get(i))));
        }
        console.printEnvironment(env, varSpecifier);
        Mockito.verify(safeOutputStreamConsumer).stdOutput("Environment variables: ");
        for (int i = 0; i < (keys.size()); i++) {
            Mockito.verify(varSpecifier).isSecure(keys.get(i));
            Mockito.verify(safeOutputStreamConsumer).stdOutput(String.format("Name= %s  Value= %s", keys.get(i), ((i % 2) == 0 ? MASK_VALUE : values.get(i))));
        }
    }

    @Test
    public void shouldReadOutputOfAGiveStream() throws Exception {
        StringBufferInputStream in = new StringBufferInputStream(("Lorem ipsum dolor sit amet, consectetur adipisicing elit, \n" + ((((("used do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n " + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi \n") + "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit \n") + "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. \n ") + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui \n") + "officia deserunt mollit anim id est laborum.")));
        Mockito.doNothing().when(safeOutputStreamConsumer).stdOutput(ArgumentMatchers.anyString());
        console.readOutputOf(in);
        Mockito.verify(safeOutputStreamConsumer, Mockito.timeout(10000).times(7)).stdOutput(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldReadErrorOfAGiveStream() throws Exception {
        StringBufferInputStream in = new StringBufferInputStream(("Lorem ipsum dolor sit amet, consectetur adipisicing elit, \n" + ((((("used do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n " + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi \n") + "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit \n") + "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. \n ") + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui \n") + "officia deserunt mollit anim id est laborum.")));
        Mockito.doNothing().when(safeOutputStreamConsumer).errOutput(ArgumentMatchers.anyString());
        console.readErrorOf(in);
        Mockito.verify(safeOutputStreamConsumer, Mockito.timeout(10000).times(7)).errOutput(ArgumentMatchers.anyString());
    }
}

