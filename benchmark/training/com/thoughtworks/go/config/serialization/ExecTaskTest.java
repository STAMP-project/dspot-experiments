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
package com.thoughtworks.go.config.serialization;


import ExecTask.COMMAND;
import ExecTask.EXEC_CONFIG_ERROR;
import com.thoughtworks.go.config.Argument;
import com.thoughtworks.go.config.ExecTask;
import com.thoughtworks.go.config.registry.ConfigElementImplementationRegistry;
import com.thoughtworks.go.helper.ConfigFileFixture;
import com.thoughtworks.go.util.ConfigElementImplementationRegistryMother;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ExecTaskTest {
    @Test
    public void shouldSupportMultipleArgs() throws Exception {
        String xml = "<exec command=\'ls\'>\n" + (("  <arg>arg1</arg>\n" + "  <arg>arg2</arg>\n") + "</exec>");
        ExecTask execTask = fromXmlPartial(xml, ExecTask.class);
        Assert.assertThat(execTask.getArgList(), Matchers.is(new com.thoughtworks.go.domain.config.Arguments(new Argument("arg1"), new Argument("arg2"))));
    }

    @Test
    public void shouldNotSupportArgsAttributeWithArgSubElement() throws Exception {
        String jobXml = "<job name=\'dev\'>\n" + (((((("  <tasks>\n" + "    <exec command=\'ls\' args=\'arg1 arg2\'>\n") + "      <arg>arg1</arg>\n") + "      <arg>arg2</arg>\n") + "    </exec>\n") + "  </tasks>") + "</job>");
        String configXml = ConfigFileFixture.withJob(jobXml);
        try {
            ConfigElementImplementationRegistry registry = ConfigElementImplementationRegistryMother.withNoPlugins();
            loadConfigHolder(configXml);
            Assert.fail("should throw exception if both 'args' attribute and 'arg' sub element are configured");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(EXEC_CONFIG_ERROR));
        }
    }

    @Test
    public void validateTask_shouldValidateThatCommandIsRequired() {
        ExecTask execTask = new ExecTask();
        execTask.validateTask(null);
        Assert.assertThat(execTask.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(execTask.errors().on(COMMAND), Matchers.is("Command cannot be empty"));
    }

    @Test
    public void shouldUseConfiguredWorkingDirectory() throws Exception {
        File absoluteFile = new File("test").getAbsoluteFile();
        ExecTask task = new ExecTask("command", "arguments", absoluteFile.getAbsolutePath());
        Assert.assertThat(task.workingDirectory(), Matchers.is(absoluteFile.getPath()));
    }
}

