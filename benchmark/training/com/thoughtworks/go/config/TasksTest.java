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
package com.thoughtworks.go.config;


import AntTask.WORKING_DIRECTORY;
import ExecTask.ARGS;
import ExecTask.ARG_LIST_STRING;
import Tasks.TASK_OPTIONS;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.service.TaskFactory;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertFalse;


public class TasksTest {
    @Test
    public void shouldReturnEmptyTasks() throws Exception {
        AntTask antTask1 = new AntTask();
        FetchTask fetchArtifact = new FetchTask();
        Tasks tasks = new Tasks(antTask1, fetchArtifact);
        Tasks finds = tasks.findByType(NantTask.class);
        Assert.assertThat(finds.size(), Matchers.is(0));
    }

    @Test
    public void shouldSetConfigAttributesForBuiltinTask() throws Exception {
        HashMap attributes = new HashMap();
        attributes.put(TASK_OPTIONS, "ant");
        attributes.put("ant", antTaskAttribs("build.xml", "test", "foo"));
        TaskFactory taskFactory = Mockito.mock(TaskFactory.class);
        AntTask antTask = new AntTask();
        Mockito.when(taskFactory.taskInstanceFor(antTask.getTaskType())).thenReturn(antTask);
        Tasks tasks = new Tasks();
        Tasks spy = Mockito.spy(tasks);
        spy.setConfigAttributes(attributes, taskFactory);
        Assert.assertThat(spy.size(), Matchers.is(1));
        Assert.assertThat(spy.get(0), Matchers.is(antTask("build.xml", "test", "foo")));
    }

    @Test
    public void shouldIncrementIndexOfGivenTask() {
        Tasks tasks = new Tasks();
        AntTask task1 = antTask("b1", "t1", "w1");
        tasks.add(task1);
        AntTask task2 = antTask("b2", "t2", "w2");
        tasks.add(task2);
        AntTask task3 = antTask("b3", "t3", "w3");
        tasks.add(task3);
        tasks.incrementIndex(0);
        Assert.assertThat(tasks.get(0), Matchers.is(task2));
        Assert.assertThat(tasks.get(1), Matchers.is(task1));
        Assert.assertThat(tasks.get(2), Matchers.is(task3));
    }

    @Test
    public void shouldErrorOutWhenTaskIsNotFoundWhileIncrementing() {
        try {
            new Tasks().incrementIndex(1);
            Assert.fail("Should have thrown up");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("There is not valid task at position 1."));
        }
    }

    @Test
    public void shouldDecrementIndexOfGivenTask() {
        Tasks tasks = new Tasks();
        AntTask task1 = antTask("b1", "t1", "w1");
        tasks.add(task1);
        AntTask task2 = antTask("b2", "t2", "w2");
        tasks.add(task2);
        AntTask task3 = antTask("b3", "t3", "w3");
        tasks.add(task3);
        tasks.decrementIndex(2);
        Assert.assertThat(tasks.get(0), Matchers.is(task1));
        Assert.assertThat(tasks.get(1), Matchers.is(task3));
        Assert.assertThat(tasks.get(2), Matchers.is(task2));
    }

    @Test
    public void shouldErrorOutWhenTaskIsNotFoundWhileDecrementing() {
        try {
            new Tasks().decrementIndex(1);
            Assert.fail("Should have thrown up");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("There is not valid task at position 1."));
        }
    }

    @Test
    public void shouldValidateTreeForAllTasks() {
        AntTask antTask = antTask("build.xml", "compile", "/abc");
        ExecTask execTask = new ExecTask("foo", new com.thoughtworks.go.domain.config.Arguments(new Argument("arg")));
        Tasks tasks = new Tasks(antTask, execTask);
        String pipelineName = "p1";
        PipelineConfig pipelineConfig = GoConfigMother.configWithPipelines(pipelineName).pipelineConfigByName(new CaseInsensitiveString(pipelineName));
        StageConfig stageConfig = pipelineConfig.getStages().get(0);
        JobConfig jobConfig = stageConfig.getJobs().get(0);
        jobConfig.setTasks(tasks);
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", pipelineConfig, stageConfig, jobConfig);
        assertFalse(tasks.validateTree(context));
        Assert.assertThat(tasks.errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(antTask.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(antTask.errors().get(WORKING_DIRECTORY).size(), Matchers.is(1));
        Assert.assertThat(antTask.errors().get(WORKING_DIRECTORY).contains("Task of job 'job' in stage 'stage' of pipeline 'p1' has path '/abc' which is outside the working directory."), Matchers.is(true));
        Assert.assertThat(execTask.errors().get(ARG_LIST_STRING).size(), Matchers.is(1));
        Assert.assertThat(execTask.errors().get(ARG_LIST_STRING).contains("Can not use both 'args' attribute and 'arg' sub element in 'exec' element!"), Matchers.is(true));
        Assert.assertThat(execTask.errors().get(ARGS).size(), Matchers.is(1));
        Assert.assertThat(execTask.errors().get(ARGS).contains("Can not use both 'args' attribute and 'arg' sub element in 'exec' element!"), Matchers.is(true));
    }
}

