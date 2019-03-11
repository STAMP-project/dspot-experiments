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
package com.thoughtworks.go.buildsession;


import com.thoughtworks.go.domain.BuildCommand;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginCommandExecutorTest {
    private PluginCommandExecutor pluginCommandExecutor;

    private TfsExecutor tfsExecutor;

    private BuildCommand buildCommand;

    private BuildSession buildSession;

    @Test
    public void shouldNotExecuteATfsExecutorIfPluginTypeIsNotTfs() throws Exception {
        Mockito.when(buildCommand.getStringArg("type")).thenReturn("non-tfs type");
        try {
            pluginCommandExecutor.execute(buildCommand, buildSession);
            Assert.fail("Should have failed since this type in not understood");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Don't know how to handle plugin of type: non-tfs type"));
        }
    }

    @Test
    public void shouldExecuteATfsExecutorIfPluginTypeIsTfs() throws Exception {
        Mockito.when(buildCommand.getStringArg("type")).thenReturn("tfs");
        Mockito.when(tfsExecutor.execute(buildCommand, buildSession)).thenReturn(true);
        boolean result = pluginCommandExecutor.execute(buildCommand, buildSession);
        Mockito.verify(tfsExecutor).execute(buildCommand, buildSession);
        Assert.assertThat(result, Matchers.is(true));
    }
}

