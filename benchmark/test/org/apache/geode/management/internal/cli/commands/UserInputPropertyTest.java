/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import org.apache.geode.management.internal.cli.shell.Gfsh;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class UserInputPropertyTest {
    private UserInputProperty userInputProperty;

    private Gfsh gfsh;

    @Test
    public void propertyWithNoDefaultVaueWillPromptTillAValueIsSupplied_interactive() throws Exception {
        userInputProperty = new UserInputProperty("key", "prompt", false);
        Mockito.when(gfsh.readText(ArgumentMatchers.any())).thenReturn("").thenReturn("").thenReturn("value");
        String input = userInputProperty.promptForAcceptableValue(gfsh);
        assertThat(input).isEqualTo("value");
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(3)).readText(ArgumentMatchers.any());
    }

    @Test
    public void propertyWithNoDefaultValue_quietMode() throws Exception {
        Mockito.when(gfsh.isQuietMode()).thenReturn(true);
        userInputProperty = new UserInputProperty("key", "prompt", false);
        String input = userInputProperty.promptForAcceptableValue(gfsh);
        assertThat(input).isEqualTo("");
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
    }

    @Test
    public void propertyWithDefaultValue_Interactive() throws Exception {
        userInputProperty = new UserInputProperty("key", "prompt", "value", false);
        String input = userInputProperty.promptForAcceptableValue(gfsh);
        assertThat(input).isEqualTo("value");
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(gfsh).readText(ArgumentMatchers.any());
    }

    @Test
    public void propertyWithEmptyDefaultValue_Interactive() throws Exception {
        userInputProperty = new UserInputProperty("key", "prompt", "", false);
        String input = userInputProperty.promptForAcceptableValue(gfsh);
        assertThat(input).isEqualTo("");
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(gfsh).readText(ArgumentMatchers.any());
    }

    @Test
    public void propertyWithDefaultValue_Interactive_masked() throws Exception {
        userInputProperty = new UserInputProperty("key", "prompt", "value", true);
        String input = userInputProperty.promptForAcceptableValue(gfsh);
        assertThat(input).isEqualTo("value");
        Mockito.verify(gfsh).readPassword(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
    }

    @Test
    public void propertyWithDefaultValue_Quiet() throws Exception {
        Mockito.when(gfsh.isQuietMode()).thenReturn(true);
        userInputProperty = new UserInputProperty("key", "prompt", "value", false);
        String input = userInputProperty.promptForAcceptableValue(gfsh);
        assertThat(input).isEqualTo("value");
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
    }
}

