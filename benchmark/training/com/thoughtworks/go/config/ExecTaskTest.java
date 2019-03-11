/**
 * Copyright 2016 ThoughtWorks, Inc.
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
 */
package com.thoughtworks.go.config;


import ExecTask.ARGS;
import ExecTask.ARG_LIST_STRING;
import ExecTask.COMMAND;
import ExecTask.WORKING_DIR;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.domain.TaskProperty;
import com.thoughtworks.go.domain.config.Arguments;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.StageConfigMother;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ExecTaskTest {
    @Test
    public void describeTest() {
        ExecTask task = new ExecTask("ant", "-f build.xml run", "subfolder");
        task.setTimeout(600);
        Assert.assertThat(task.describe(), is("ant -f build.xml run"));
    }

    @Test
    public void describeMutipleArgumentsTest() {
        ExecTask task = new ExecTask("echo", null, new Arguments(new Argument("abc"), new Argument("hello baby!")));
        task.setTimeout(600);
        Assert.assertThat(task.describe(), is("echo abc \"hello baby!\""));
    }

    @Test
    public void shouldValidateConfig() throws Exception {
        ExecTask execTask = new ExecTask("arg1 arg2", new Arguments(new Argument("arg1"), new Argument("arg2")));
        execTask.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(execTask.errors().isEmpty(), is(false));
        Assert.assertThat(execTask.errors().on(ARGS), is(ExecTask.EXEC_CONFIG_ERROR));
        Assert.assertThat(execTask.errors().on(ARG_LIST_STRING), is(ExecTask.EXEC_CONFIG_ERROR));
    }

    @Test
    public void shouldAddErrorsOfEachArgumentToTheParent() {
        Argument argument = new Argument("invalid-argument");
        argument.addError(ARG_LIST_STRING, "Invalid argument");
        ExecTask execTask = new ExecTask("echo", new Arguments(argument), null);
        execTask.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(execTask.errors().on(ARG_LIST_STRING), is("Invalid argument"));
    }

    @Test
    public void shouldBeValid() throws Exception {
        ExecTask execTask = new ExecTask("", new Arguments(new Argument("arg1"), new Argument("arg2")));
        execTask.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(execTask.errors().isEmpty(), is(true));
        execTask = new ExecTask("command", "", "blah");
        execTask.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(execTask.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateWorkingDirectory() {
        ExecTask task = new ExecTask("ls", "-l", "../../../assertTaskInvalid");
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline");
        PipelineConfig pipeline = config.pipelineConfigByName(new CaseInsensitiveString("pipeline"));
        StageConfig stage = pipeline.get(0);
        JobConfig job = stage.getJobs().get(0);
        job.addTask(task);
        List<ConfigErrors> errors = config.validateAfterPreprocess();
        Assert.assertThat(errors.size(), is(1));
        String message = "The path of the working directory for the custom command in job 'job' in stage 'stage' of pipeline 'pipeline' is outside the agent sandbox.";
        Assert.assertThat(errors.get(0).firstError(), is(message));
        Assert.assertThat(task.errors().on(WORKING_DIR), is(message));
    }

    @Test
    public void shouldAllowSettingOfConfigAttributes() throws Exception {
        ExecTask exec = new ExecTask();
        exec.setConfigAttributes(DataStructureUtils.m(COMMAND, "ls", ARGS, "-la", WORKING_DIR, "my_dir"));
        Assert.assertThat(exec.command(), is("ls"));
        Assert.assertThat(exec.getArgs(), is("-la"));
        Assert.assertThat(exec.getArgListString(), is(""));
        Assert.assertThat(exec.workingDirectory(), is("my_dir"));
        exec.setConfigAttributes(DataStructureUtils.m(COMMAND, null, ARGS, null, WORKING_DIR, null));
        Assert.assertThat(exec.command(), is(nullValue()));
        Assert.assertThat(exec.getArgs(), is(""));
        Assert.assertThat(exec.workingDirectory(), is(nullValue()));
        exec.setConfigAttributes(DataStructureUtils.m(COMMAND, null, ARG_LIST_STRING, "-l\n-a\npavan\'s\\n working dir?", WORKING_DIR, null));
        Assert.assertThat(exec.command(), is(nullValue()));
        Assert.assertThat(exec.getArgListString(), is("-l\n-a\npavan\'s\\n working dir?"));
        Assert.assertThat(exec.getArgList().size(), is(3));
        Assert.assertThat(exec.getArgList().get(0), is(new Argument("-l")));
        Assert.assertThat(exec.getArgList().get(1), is(new Argument("-a")));
        Assert.assertThat(exec.getArgList().get(2), is(new Argument("pavan\'s\\n working dir?")));
        Assert.assertThat(exec.workingDirectory(), is(nullValue()));
    }

    @Test
    public void shouldNotSetAttributesWhenKeysNotPresentInAttributeMap() throws Exception {
        ExecTask exec = new ExecTask();
        exec.setConfigAttributes(DataStructureUtils.m(COMMAND, "ls", ARGS, "-la", WORKING_DIR, "my_dir"));
        exec.setConfigAttributes(DataStructureUtils.m());// Key is not present

        Assert.assertThat(exec.command(), is("ls"));
        Assert.assertThat(exec.getArgs(), is("-la"));
        Assert.assertThat(exec.workingDirectory(), is("my_dir"));
    }

    @Test
    public void shouldNotSetArgsIfTheValueIsBlank() throws Exception {
        ExecTask exec = new ExecTask();
        exec.setConfigAttributes(DataStructureUtils.m(COMMAND, "ls", ARGS, "", WORKING_DIR, "my_dir"));
        exec.setConfigAttributes(DataStructureUtils.m());
        Assert.assertThat(exec.command(), is("ls"));
        Assert.assertThat(exec.getArgList().size(), is(0));
        Assert.assertThat(exec.workingDirectory(), is("my_dir"));
    }

    @Test
    public void shouldNullOutWorkingDirectoryIfGivenBlank() {
        ExecTask exec = new ExecTask("ls", "-la", "foo");
        exec.setConfigAttributes(DataStructureUtils.m(COMMAND, "", ARGS, "", WORKING_DIR, ""));
        Assert.assertThat(exec.command(), is(""));
        Assert.assertThat(exec.getArgs(), is(""));
        Assert.assertThat(exec.workingDirectory(), is(nullValue()));
    }

    @Test
    public void shouldPopulateAllAttributesOnPropertiesForDisplay() {
        ExecTask execTask = new ExecTask("ls", "-la", "holy/dir");
        execTask.setTimeout(10L);
        Assert.assertThat(execTask.getPropertiesForDisplay(), hasItems(new TaskProperty("Command", "ls", "command"), new TaskProperty("Arguments", "-la", "arguments"), new TaskProperty("Working Directory", "holy/dir", "working_directory"), new TaskProperty("Timeout", "10", "timeout")));
        Assert.assertThat(execTask.getPropertiesForDisplay().size(), is(4));
        execTask = new ExecTask("ls", new Arguments(new Argument("-la"), new Argument("/proc")), "holy/dir");
        execTask.setTimeout(10L);
        Assert.assertThat(execTask.getPropertiesForDisplay(), hasItems(new TaskProperty("Command", "ls", "command"), new TaskProperty("Arguments", "-la /proc", "arguments"), new TaskProperty("Working Directory", "holy/dir", "working_directory"), new TaskProperty("Timeout", "10", "timeout")));
        Assert.assertThat(execTask.getPropertiesForDisplay().size(), is(4));
        execTask = new ExecTask("ls", new Arguments(new Argument()), null);
        Assert.assertThat(execTask.getPropertiesForDisplay(), hasItems(new TaskProperty("Command", "ls", "command")));
        Assert.assertThat(execTask.getPropertiesForDisplay().size(), is(1));
        execTask = new ExecTask("ls", "", ((String) (null)));
        Assert.assertThat(execTask.getPropertiesForDisplay(), hasItems(new TaskProperty("Command", "ls", "command")));
        Assert.assertThat(execTask.getPropertiesForDisplay().size(), is(1));
    }

    @Test
    public void shouldErrorOutForTemplates_WhenItHasATaskWithInvalidWorkingDirectory() {
        CruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("some_pipeline");
        StageConfig templateStage = StageConfigMother.stageWithTasks("templateStage");
        ExecTask execTask = new ExecTask("ls", "-la", "/");
        templateStage.getJobs().first().addTask(execTask);
        PipelineTemplateConfig template = new PipelineTemplateConfig(new CaseInsensitiveString("template_name"), templateStage);
        cruiseConfig.addTemplate(template);
        try {
            execTask.validateTask(ConfigSaveValidationContext.forChain(cruiseConfig, template, templateStage, templateStage.getJobs().first()));
            Assert.assertThat(execTask.errors().isEmpty(), is(false));
            Assert.assertThat(execTask.errors().on(WORKING_DIR), is("The path of the working directory for the custom command in job 'job' in stage 'templateStage' of template 'template_name' is outside the agent sandbox."));
        } catch (Exception e) {
            Assert.fail(("should not have failed. Exception: " + (e.getMessage())));
        }
    }

    @Test
    public void shouldReturnCommandTaskAttributes() {
        ExecTask task = new ExecTask("ls", "-laht", "src/build");
        Assert.assertThat(task.command(), is("ls"));
        Assert.assertThat(task.arguments(), is("-laht"));
        Assert.assertThat(task.workingDirectory(), is("src/build"));
    }

    @Test
    public void shouldReturnCommandArgumentList() {
        ExecTask task = new ExecTask("./bn", new Arguments(new Argument("clean"), new Argument("compile"), new Argument("\"buildfile\"")), "src/build");
        Assert.assertThat(task.arguments(), is("clean compile \"buildfile\""));
    }

    @Test
    public void shouldReturnEmptyCommandArguments() {
        ExecTask task = new ExecTask("./bn", new Arguments(), "src/build");
        Assert.assertThat(task.arguments(), is(""));
    }

    @Test
    public void shouldBeSameIfCommandMatches() {
        ExecTask task = new ExecTask("ls", new Arguments());
        Assert.assertTrue(task.equals(new ExecTask("ls", new Arguments())));
    }

    @Test
    public void shouldUnEqualIfCommandsDontMatch() {
        ExecTask task = new ExecTask("ls", new Arguments());
        Assert.assertFalse(task.equals(new ExecTask("rm", new Arguments())));
    }

    @Test
    public void shouldUnEqualIfCommandIsNull() {
        ExecTask task = new ExecTask(null, new Arguments());
        Assert.assertFalse(task.equals(new ExecTask("rm", new Arguments())));
    }

    @Test
    public void shouldUnEqualIfOtherTaskCommandIsNull() {
        ExecTask task = new ExecTask("ls", new Arguments());
        Assert.assertFalse(task.equals(new ExecTask(null, new Arguments())));
    }
}

