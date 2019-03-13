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
package org.apache.geode.management.internal.cli.commands.lifecycle;


import javax.management.remote.JMXServiceURL;
import org.apache.geode.management.internal.cli.shell.Gfsh;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class StartJConsoleCommandTest {
    @Rule
    public GfshParserRule gfshParser = new GfshParserRule();

    private StartJConsoleCommand command;

    private Gfsh gfsh;

    ArgumentCaptor<String[]> argumentCaptor;

    @Test
    public void succcessOutput() throws Exception {
        gfshParser.executeAndAssertThat(command, "start jconsole").containsOutput("some output");
        Mockito.verify(command, Mockito.times(1)).getProcess(argumentCaptor.capture());
        String[] commandString = argumentCaptor.getValue();
        assertThat(commandString).hasSize(2);
        assertThat(commandString[0]).contains("jconsole");
        assertThat(commandString[1]).isEqualTo("-interval=4");
    }

    @Test
    public void succcessOutputWithVersion() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("some error message");
        Mockito.doReturn(builder).when(command).getErrorStringBuilder(ArgumentMatchers.any());
        gfshParser.executeAndAssertThat(command, "start jconsole --version").containsOutput("some error message");
        Mockito.verify(command, Mockito.times(1)).getProcess(argumentCaptor.capture());
        String[] commandString = argumentCaptor.getValue();
        assertThat(commandString).hasSize(2);
        assertThat(commandString[0]).contains("jconsole");
        assertThat(commandString[1]).isEqualTo("-version");
    }

    @Test
    public void successWithServiceUrl() throws Exception {
        Mockito.doReturn(new JMXServiceURL("service:jmx:rmi://localhost")).when(command).getJmxServiceUrl();
        Mockito.doReturn(true).when(command).isConnectedAndReady();
        gfshParser.executeAndAssertThat(command, "start jconsole").containsOutput("some output");
        Mockito.verify(command, Mockito.times(1)).getProcess(argumentCaptor.capture());
        String[] commandString = argumentCaptor.getValue();
        assertThat(commandString).hasSize(3);
        assertThat(commandString[0]).contains("jconsole");
        assertThat(commandString[1]).isEqualTo("-interval=4");
        assertThat(commandString[2]).isEqualTo("service:jmx:rmi://localhost");
    }
}

