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


import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.cli.shell.Gfsh;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class UsernamePasswordInterceptorTest {
    private Gfsh gfsh;

    private GfshParseResult parseResult;

    private UsernamePasswordInterceptor interceptor;

    @Test
    public void WithConnectedGfshWithNoUsernameAndNoPasswordWillPrompt_interactive() throws Exception {
        Mockito.when(gfsh.readText("Username: ")).thenReturn("user");
        Mockito.when(gfsh.readPassword("Password: ")).thenReturn("pass");
        Mockito.when(gfsh.isConnectedAndReady()).thenReturn(true);
        Mockito.when(parseResult.getUserInput()).thenReturn("command");
        Mockito.when(parseResult.getParamValueAsString("username")).thenReturn("");
        Mockito.when(parseResult.getParamValueAsString("password")).thenReturn("");
        interceptor.preExecution(parseResult);
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(parseResult, Mockito.times(0)).setUserInput("command");
    }

    @Test
    public void WithConnectedGfshWithUsernameButNoPasswordWillPrompt_interactive() throws Exception {
        Mockito.when(gfsh.readPassword("Password: ")).thenReturn("pass");
        Mockito.when(gfsh.isConnectedAndReady()).thenReturn(true);
        Mockito.when(parseResult.getUserInput()).thenReturn("command --username=user");
        Mockito.when(parseResult.getParamValueAsString("username")).thenReturn("user");
        Mockito.when(parseResult.getParamValueAsString("password")).thenReturn("");
        interceptor.preExecution(parseResult);
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(1)).readPassword(ArgumentMatchers.any());
        Mockito.verify(parseResult, Mockito.times(1)).setUserInput("command --username=user --password=pass");
    }

    @Test
    public void WithConnectedGfshWithUsernameAndPasswordWillNotPrompt_interactive() throws Exception {
        Mockito.when(gfsh.isConnectedAndReady()).thenReturn(true);
        Mockito.when(parseResult.getUserInput()).thenReturn("command --username=user --password=pass");
        Mockito.when(parseResult.getParamValueAsString("username")).thenReturn("user");
        Mockito.when(parseResult.getParamValueAsString("password")).thenReturn("pass");
        interceptor.preExecution(parseResult);
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(parseResult, Mockito.times(0)).setUserInput("command --username=user --password=pass");
    }

    @Test
    public void WithConnectedGfshWithPasswordButNoUsernameWillPrompt_interactive() throws Exception {
        Mockito.when(gfsh.readText("Username: ")).thenReturn("user");
        Mockito.when(gfsh.isConnectedAndReady()).thenReturn(true);
        Mockito.when(parseResult.getUserInput()).thenReturn("command --password=pass");
        Mockito.when(parseResult.getParamValueAsString("username")).thenReturn("");
        Mockito.when(parseResult.getParamValueAsString("password")).thenReturn("pass");
        interceptor.preExecution(parseResult);
        Mockito.verify(gfsh, Mockito.times(1)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(parseResult, Mockito.times(1)).setUserInput("command --password=pass --username=user");
    }

    @Test
    public void WithNonConnectedGfshWithoutUsernameAndPasswordWillNotPrompt_interactive() throws Exception {
        Mockito.when(gfsh.isConnectedAndReady()).thenReturn(false);
        Mockito.when(parseResult.getUserInput()).thenReturn("command");
        Mockito.when(parseResult.getParamValueAsString("username")).thenReturn("");
        Mockito.when(parseResult.getParamValueAsString("password")).thenReturn("");
        interceptor.preExecution(parseResult);
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        Mockito.verify(parseResult, Mockito.times(0)).setUserInput(ArgumentMatchers.any());
    }
}

