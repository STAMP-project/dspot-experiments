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
package com.thoughtworks.go.domain.builder;


import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.thoughtworks.go.config.RunIfConfig;
import com.thoughtworks.go.domain.StubGoPublisher;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JunitExtRunner.class)
public class BuilderTest {
    private StubGoPublisher goPublisher = new StubGoPublisher();

    private EnvironmentVariableContext environmentVariableContext;

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, WINDOWS })
    public void shouldReportErrorWhenCancelCommandDoesNotExist() throws Exception {
        StubBuilder stubBuilder = new StubBuilder();
        CommandBuilder cancelBuilder = new CommandBuilder("echo2", "cancel task", new File("."), new com.thoughtworks.go.domain.RunIfConfigs(RunIfConfig.FAILED), stubBuilder, "");
        CommandBuilder builder = new CommandBuilder("echo", "normal task", new File("."), new com.thoughtworks.go.domain.RunIfConfigs(RunIfConfig.FAILED), cancelBuilder, "");
        builder.cancel(goPublisher, new EnvironmentVariableContext(), null, null, "utf-8");
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString("Error happened while attempting to execute 'echo2 cancel task'"));
    }

    @Test
    public void shouldRunCancelBuilderWhenCanceled() throws Exception {
        StubBuilder stubBuilder = new StubBuilder();
        CommandBuilder builder = new CommandBuilder("echo", "", new File("."), new com.thoughtworks.go.domain.RunIfConfigs(RunIfConfig.FAILED), stubBuilder, "");
        builder.cancel(goPublisher, environmentVariableContext, null, null, "utf-8");
        Assert.assertThat(stubBuilder.wasCalled, Matchers.is(true));
    }

    @Test
    public void shouldLogToConsoleOutWhenCanceling() {
        StubBuilder stubBuilder = new StubBuilder();
        CommandBuilder builder = new CommandBuilder("echo", "", new File("."), new com.thoughtworks.go.domain.RunIfConfigs(RunIfConfig.FAILED), stubBuilder, "");
        builder.cancel(goPublisher, environmentVariableContext, null, null, "utf-8");
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString("On Cancel Task"));
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString("On Cancel Task completed"));
    }
}

