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
package com.thoughtworks.go.domain;


import AbstractTask.HAS_CANCEL_TASK;
import AbstractTask.ON_CANCEL_CONFIG;
import AbstractTask.RUN_IF_CONFIGS_ANY;
import AbstractTask.RUN_IF_CONFIGS_FAILED;
import AbstractTask.RUN_IF_CONFIGS_PASSED;
import AntTask.WORKING_DIRECTORY;
import ExecTask.ARGS;
import ExecTask.ARG_LIST_STRING;
import ExecTask.COMMAND;
import ExecTask.WORKING_DIR;
import OnCancelConfig.EXEC_ON_CANCEL;
import OnCancelConfig.ON_CANCEL_OPTIONS;
import RunIfConfig.ANY;
import RunIfConfig.FAILED;
import RunIfConfig.PASSED;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.service.TaskFactory;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AbstractTaskTest {
    private TaskFactory taskFactory = Mockito.mock(TaskFactory.class);

    @Test
    public void shouldKnowTheTypeOfExecTask() {
        Assert.assertThat(new ExecTask().getTaskType(), Matchers.is("exec"));
        Assert.assertThat(new FetchTask().getTaskType(), Matchers.is("fetch"));
    }

    @Test
    public void shouldSetConfigAttributes() {
        AbstractTask task = new ExecTask();
        Map attributes = new HashMap();
        attributes.put(RUN_IF_CONFIGS_ANY, "1");
        attributes.put(RUN_IF_CONFIGS_FAILED, "1");
        attributes.put(RUN_IF_CONFIGS_PASSED, "1");
        task.setConfigAttributes(attributes);
        Assert.assertThat(task.getConditions().match(ANY), Matchers.is(true));
        Assert.assertThat(task.getConditions().match(FAILED), Matchers.is(true));
        Assert.assertThat(task.getConditions().match(PASSED), Matchers.is(true));
        Assert.assertThat(task.hasCancelTask(), Matchers.is(false));
    }

    @Test
    public void shouldSetOnCancelExecTask() {
        AbstractTask task = new ExecTask();
        Map onCancelMapAttrib = new HashMap();
        onCancelMapAttrib.put(COMMAND, "sudo");
        onCancelMapAttrib.put(ARGS, "ls -la");
        onCancelMapAttrib.put(WORKING_DIR, "working_dir");
        onCancelMapAttrib.put(RUN_IF_CONFIGS_ANY, "1");
        onCancelMapAttrib.put(RUN_IF_CONFIGS_FAILED, "1");
        onCancelMapAttrib.put(RUN_IF_CONFIGS_PASSED, "1");
        Map onCancelConfigAttributes = new HashMap();
        onCancelConfigAttributes.put(EXEC_ON_CANCEL, onCancelMapAttrib);
        onCancelConfigAttributes.put(ON_CANCEL_OPTIONS, "exec");
        Map actualTaskAttributes = new HashMap();
        actualTaskAttributes.put(ON_CANCEL_CONFIG, onCancelConfigAttributes);
        actualTaskAttributes.put(HAS_CANCEL_TASK, "1");
        ExecTask execTask = new ExecTask();
        Mockito.when(taskFactory.taskInstanceFor(execTask.getTaskType())).thenReturn(execTask);
        task.setConfigAttributes(actualTaskAttributes, taskFactory);
        Assert.assertThat(task.hasCancelTask(), Matchers.is(true));
        ExecTask expected = new ExecTask("sudo", "ls -la", "working_dir");
        expected.getConditions().add(ANY);
        expected.getConditions().add(FAILED);
        expected.getConditions().add(PASSED);
        Assert.assertThat(task.cancelTask(), Matchers.is(expected));
    }

    @Test
    public void shouldBeAbleToRemoveOnCancelConfig() {
        AbstractTask task = new ExecTask();
        task.setCancelTask(new ExecTask());
        Map cancelTaskAttributes = new HashMap();
        cancelTaskAttributes.put(COMMAND, "ls");
        cancelTaskAttributes.put(ARG_LIST_STRING, "-la");
        Map onCancelConfigAttributes = new HashMap();
        onCancelConfigAttributes.put(EXEC_ON_CANCEL, cancelTaskAttributes);
        Map cancelConfigAttributes = new HashMap();
        cancelConfigAttributes.put(ON_CANCEL_OPTIONS, "exec");
        Map actualTaskAttributes = new HashMap();
        actualTaskAttributes.put(HAS_CANCEL_TASK, "0");
        actualTaskAttributes.put(ON_CANCEL_CONFIG, cancelConfigAttributes);
        task.setConfigAttributes(actualTaskAttributes);
        Assert.assertThat(task.hasCancelTask(), Matchers.is(false));
    }

    @Test
    public void shouldResetRunifConfigsWhenTheConfigIsNotPresent() {
        AbstractTask task = new ExecTask();
        task.getConditions().add(ANY);
        task.getConditions().add(PASSED);
        Map attributes = new HashMap();
        attributes.put(RUN_IF_CONFIGS_ANY, "0");
        attributes.put(RUN_IF_CONFIGS_FAILED, "1");
        attributes.put(RUN_IF_CONFIGS_PASSED, "0");
        task.setConfigAttributes(attributes);
        Assert.assertThat(task.getConditions().match(ANY), Matchers.is(false));
        Assert.assertThat(task.getConditions().match(FAILED), Matchers.is(true));
        Assert.assertThat(task.getConditions().match(PASSED), Matchers.is(false));
    }

    @Test
    public void validate_shouldErrorOutWhenAnOncancelTaskHasAnOncancelTask() {
        AbstractTask task = new ExecTask();
        ExecTask onCancelTask = new ExecTask();
        onCancelTask.setCancelTask(new ExecTask());
        task.setCancelTask(onCancelTask);
        task.validate(null);
        Assert.assertThat(task.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(task.errors().on(ON_CANCEL_CONFIG), Matchers.is("Cannot nest 'oncancel' within a cancel task"));
    }

    @Test
    public void validate_shouldBeValidNoOncancelTaskIsDefined() {
        AbstractTask task = new ExecTask("ls", "-la", "foo");
        task.validate(null);
        Assert.assertThat(task.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void validate_shouldBeValidWhenOncancelTaskIsNotNested() {
        AbstractTask task = new ExecTask("ls", "-la", "foo");
        task.setCancelTask(new ExecTask());
        task.validate(null);
        Assert.assertThat(task.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldReturnCommaSeparatedRunIfConfigsConditionsForDisplay() {
        AbstractTask execTask = new ExecTask("ls", "-la", "42");
        execTask.getConditions().add(PASSED);
        execTask.getConditions().add(FAILED);
        execTask.getConditions().add(ANY);
        String actual = execTask.getConditionsForDisplay();
        Assert.assertThat(actual, Matchers.is("Passed, Failed, Any"));
    }

    @Test
    public void shouldReturnPassedByDefaultWhenNoRunIfConfigIsSpecified() {
        AbstractTask execTask = new ExecTask("ls", "-la", "42");
        Assert.assertThat(execTask.getConditionsForDisplay(), Matchers.is("Passed"));
    }

    @Test
    public void shouldValidateTree() {
        String pipelineName = "p1";
        PipelineConfig pipelineConfig = GoConfigMother.configWithPipelines(pipelineName).pipelineConfigByName(new CaseInsensitiveString(pipelineName));
        StageConfig stageConfig = pipelineConfig.getStages().get(0);
        JobConfig jobConfig = stageConfig.getJobs().get(0);
        AbstractTask execTask = new ExecTask("ls", "-la", "42");
        AntTask antTask = new AntTask();
        antTask.setWorkingDirectory("/abc");
        execTask.setCancelTask(antTask);
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", pipelineConfig, stageConfig, jobConfig);
        Assert.assertThat(execTask.validateTree(context), Matchers.is(false));
        Assert.assertThat(antTask.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(antTask.errors().get(WORKING_DIRECTORY).size(), Matchers.is(1));
        Assert.assertThat(antTask.errors().get(WORKING_DIRECTORY).contains("Task of job 'job' in stage 'stage' of pipeline 'p1' has path '/abc' which is outside the working directory."), Matchers.is(true));
    }
}

