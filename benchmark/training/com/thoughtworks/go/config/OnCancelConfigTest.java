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
package com.thoughtworks.go.config;


import BuildTask.BUILD_FILE;
import BuildTask.TARGET;
import BuildTask.WORKING_DIRECTORY;
import ExecTask.ARGS;
import ExecTask.COMMAND;
import ExecTask.WORKING_DIR;
import NantTask.NANT_PATH;
import OnCancelConfig.ANT_ON_CANCEL;
import OnCancelConfig.EXEC_ON_CANCEL;
import OnCancelConfig.NANT_ON_CANCEL;
import OnCancelConfig.ON_CANCEL_OPTIONS;
import OnCancelConfig.RAKE_ON_CANCEL;
import com.thoughtworks.go.service.TaskFactory;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class OnCancelConfigTest {
    private TaskFactory taskFactory;

    @Test
    public void shouldReturnTheOnCancelOptionBasedOnWhatTheOnCancelTaskIs() {
        Assert.assertThat(new OnCancelConfig().onCancelOption(), Matchers.is(""));
        Assert.assertThat(onCancelOption(), Matchers.is("Custom Command"));
        Assert.assertThat(onCancelOption(), Matchers.is("Ant"));
        Assert.assertThat(onCancelOption(), Matchers.is("Rake"));
    }

    @Test
    public void shouldAddErrorOnErrorCollection() {
        OnCancelConfig onCancelConfig = new OnCancelConfig();
        onCancelConfig.addError("key", "some error");
        Assert.assertThat(onCancelConfig.errors().on("key"), Matchers.is("some error"));
    }

    @Test
    public void shouldSetPrimitiveAttributesForExecTask() {
        Map hashMap = new HashMap();
        hashMap.put(ON_CANCEL_OPTIONS, "exec");
        Map valueMap = new HashMap();
        valueMap.put(COMMAND, "ls");
        valueMap.put(ARGS, "blah");
        valueMap.put(WORKING_DIR, "pwd");
        hashMap.put(EXEC_ON_CANCEL, valueMap);
        hashMap.put(ANT_ON_CANCEL, new HashMap());
        ExecTask execTask = new ExecTask();
        Mockito.when(taskFactory.taskInstanceFor(execTask.getTaskType())).thenReturn(execTask);
        OnCancelConfig cancelConfig = OnCancelConfig.create(hashMap, taskFactory);
        Assert.assertThat(cancelConfig.getTask(), Matchers.is(new ExecTask("ls", "blah", "pwd")));
    }

    @Test
    public void shouldSetPrimitiveAttributesForAntTask() {
        Map hashMap = new HashMap();
        hashMap.put(ON_CANCEL_OPTIONS, "ant");
        Map valueMap = new HashMap();
        valueMap.put(BUILD_FILE, "build.xml");
        valueMap.put(TARGET, "blah");
        valueMap.put(WORKING_DIRECTORY, "pwd");
        hashMap.put(ANT_ON_CANCEL, valueMap);
        hashMap.put(EXEC_ON_CANCEL, new HashMap());
        Mockito.when(taskFactory.taskInstanceFor(new AntTask().getTaskType())).thenReturn(new AntTask());
        OnCancelConfig cancelConfig = OnCancelConfig.create(hashMap, taskFactory);
        AntTask expectedAntTask = new AntTask();
        expectedAntTask.setBuildFile("build.xml");
        expectedAntTask.setTarget("blah");
        expectedAntTask.setWorkingDirectory("pwd");
        Assert.assertThat(cancelConfig.getTask(), Matchers.is(expectedAntTask));
    }

    @Test
    public void shouldSetPrimitiveAttributesForNantTask() {
        Map hashMap = new HashMap();
        hashMap.put(ON_CANCEL_OPTIONS, "nant");
        Map valueMap = new HashMap();
        valueMap.put(BUILD_FILE, "default.build");
        valueMap.put(TARGET, "compile");
        valueMap.put(WORKING_DIRECTORY, "pwd");
        valueMap.put(NANT_PATH, "/usr/bin/nant");
        hashMap.put(NANT_ON_CANCEL, valueMap);
        hashMap.put(EXEC_ON_CANCEL, new HashMap());
        hashMap.put(ANT_ON_CANCEL, new HashMap());
        hashMap.put(RAKE_ON_CANCEL, new HashMap());
        Mockito.when(taskFactory.taskInstanceFor(new NantTask().getTaskType())).thenReturn(new NantTask());
        OnCancelConfig cancelConfig = OnCancelConfig.create(hashMap, taskFactory);
        NantTask expectedNantTask = new NantTask();
        expectedNantTask.setBuildFile("default.build");
        expectedNantTask.setTarget("compile");
        expectedNantTask.setWorkingDirectory("pwd");
        expectedNantTask.setNantPath("/usr/bin/nant");
        Assert.assertThat(cancelConfig.getTask(), Matchers.is(expectedNantTask));
    }

    @Test
    public void shouldSetPrimitiveAttributesForRakeTask() {
        Map hashMap = new HashMap();
        hashMap.put(ON_CANCEL_OPTIONS, "rake");
        Map valueMap = new HashMap();
        valueMap.put(BUILD_FILE, "rakefile");
        valueMap.put(TARGET, "build");
        valueMap.put(WORKING_DIRECTORY, "pwd");
        hashMap.put(RAKE_ON_CANCEL, valueMap);
        hashMap.put(EXEC_ON_CANCEL, new HashMap());
        Mockito.when(taskFactory.taskInstanceFor(new RakeTask().getTaskType())).thenReturn(new RakeTask());
        OnCancelConfig cancelConfig = OnCancelConfig.create(hashMap, taskFactory);
        RakeTask expectedRakeTask = new RakeTask();
        expectedRakeTask.setBuildFile("rakefile");
        expectedRakeTask.setTarget("build");
        expectedRakeTask.setWorkingDirectory("pwd");
        Assert.assertThat(cancelConfig.getTask(), Matchers.is(expectedRakeTask));
    }
}

