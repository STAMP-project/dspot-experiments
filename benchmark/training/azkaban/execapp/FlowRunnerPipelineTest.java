/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.execapp;


import Status.DISABLED;
import Status.QUEUED;
import Status.READY;
import Status.RUNNING;
import Status.SKIPPED;
import Status.SUCCEEDED;
import azkaban.execapp.event.FlowWatcher;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutionOptions;
import azkaban.executor.InteractiveTestJob;
import org.junit.Test;


/**
 * Flows in this test: joba jobb joba1 jobc->joba jobd->joba jobe->jobb,jobc,jobd jobf->jobe,joba1
 *
 * jobb = innerFlow innerJobA innerJobB->innerJobA innerJobC->innerJobB
 * innerFlow->innerJobB,innerJobC
 *
 * jobd=innerFlow2 innerFlow2->innerJobA
 *
 * @author rpark
 */
public class FlowRunnerPipelineTest extends FlowRunnerTestBase {
    private FlowRunnerTestUtil testUtil;

    @Test
    public void testBasicPipelineLevel1RunDisabledJobs() throws Exception {
        final FlowRunner previousRunner = this.testUtil.createFromFlowMap("jobf", "prev");
        final ExecutionOptions options = new ExecutionOptions();
        options.setPipelineExecutionId(previousRunner.getExecutableFlow().getExecutionId());
        options.setPipelineLevel(1);
        final FlowWatcher watcher = new azkaban.execapp.event.LocalFlowWatcher(previousRunner);
        final FlowRunner pipelineRunner = this.testUtil.createFromFlowMap("jobf", "pipe", options);
        pipelineRunner.setFlowWatcher(watcher);
        // 1. START FLOW
        final ExecutableFlow pipelineFlow = pipelineRunner.getExecutableFlow();
        final ExecutableFlow previousFlow = previousRunner.getExecutableFlow();
        // disable the innerFlow (entire sub-flow)
        previousFlow.getExecutableNodePath("jobb").setStatus(DISABLED);
        FlowRunnerTestUtil.startThread(previousRunner);
        assertStatus(previousFlow, "joba", RUNNING);
        assertStatus(previousFlow, "joba", RUNNING);
        assertStatus(previousFlow, "joba1", RUNNING);
        FlowRunnerTestUtil.startThread(pipelineRunner);
        assertStatus(pipelineFlow, "joba", QUEUED);
        assertStatus(pipelineFlow, "joba1", QUEUED);
        InteractiveTestJob.getTestJob("prev:joba").succeedJob();
        assertStatus(previousFlow, "joba", SUCCEEDED);
        assertStatus(previousFlow, "jobb", SKIPPED);
        assertStatus(previousFlow, "jobb:innerJobA", READY);
        assertStatus(previousFlow, "jobd", RUNNING);
        assertStatus(previousFlow, "jobc", RUNNING);
        assertStatus(previousFlow, "jobd:innerJobA", RUNNING);
        assertStatus(pipelineFlow, "joba", RUNNING);
        assertStatus(previousFlow, "jobb:innerJobA", READY);
        assertStatus(previousFlow, "jobb:innerJobB", READY);
        assertStatus(previousFlow, "jobb:innerJobC", READY);
        InteractiveTestJob.getTestJob("pipe:joba").succeedJob();
        assertStatus(pipelineFlow, "joba", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb", RUNNING);
        assertStatus(pipelineFlow, "jobd", RUNNING);
        assertStatus(pipelineFlow, "jobc", QUEUED);
        assertStatus(pipelineFlow, "jobd:innerJobA", QUEUED);
        assertStatus(pipelineFlow, "jobb:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("prev:jobd:innerJobA").succeedJob();
        assertStatus(previousFlow, "jobd:innerJobA", SUCCEEDED);
        assertStatus(previousFlow, "jobd:innerFlow2", RUNNING);
        assertStatus(pipelineFlow, "jobd:innerJobA", RUNNING);
        // Finish the previous d side
        InteractiveTestJob.getTestJob("prev:jobd:innerFlow2").succeedJob();
        assertStatus(previousFlow, "jobd:innerFlow2", SUCCEEDED);
        assertStatus(previousFlow, "jobd", SUCCEEDED);
        InteractiveTestJob.getTestJob("pipe:jobb:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("prev:jobc").succeedJob();
        assertStatus(previousFlow, "jobb:innerJobB", READY);
        assertStatus(previousFlow, "jobb:innerJobC", READY);
        assertStatus(previousFlow, "jobb:innerFlow", READY);
        assertStatus(previousFlow, "jobc", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerJobA", SUCCEEDED);
        assertStatus(pipelineFlow, "jobc", RUNNING);
        assertStatus(pipelineFlow, "jobb:innerJobB", RUNNING);
        assertStatus(pipelineFlow, "jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobc").succeedJob();
        assertStatus(previousFlow, "jobb:innerFlow", READY);
        assertStatus(previousFlow, "jobb", SKIPPED);
        assertStatus(previousFlow, "jobe", RUNNING);
        assertStatus(pipelineFlow, "jobc", SUCCEEDED);
        InteractiveTestJob.getTestJob("pipe:jobb:innerJobB").succeedJob();
        InteractiveTestJob.getTestJob("pipe:jobb:innerJobC").succeedJob();
        InteractiveTestJob.getTestJob("prev:jobe").succeedJob();
        assertStatus(previousFlow, "jobe", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerJobB", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerJobC", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerFlow", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobd:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("pipe:jobb:innerFlow").succeedJob();
        assertStatus(pipelineFlow, "jobb", SUCCEEDED);
        assertStatus(pipelineFlow, "jobd:innerJobA", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerFlow", SUCCEEDED);
        assertStatus(pipelineFlow, "jobd:innerFlow2", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobd:innerFlow2").succeedJob();
        InteractiveTestJob.getTestJob("prev:joba1").succeedJob();
        assertStatus(pipelineFlow, "jobd:innerFlow2", SUCCEEDED);
        assertStatus(pipelineFlow, "jobd", SUCCEEDED);
        assertStatus(previousFlow, "jobf", RUNNING);
        assertStatus(previousFlow, "joba1", SUCCEEDED);
        assertStatus(pipelineFlow, "joba1", RUNNING);
        assertStatus(pipelineFlow, "jobe", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobe").succeedJob();
        InteractiveTestJob.getTestJob("prev:jobf").succeedJob();
        assertStatus(pipelineFlow, "jobe", SUCCEEDED);
        assertStatus(previousFlow, "jobf", SUCCEEDED);
        assertFlowStatus(previousFlow, SUCCEEDED);
        InteractiveTestJob.getTestJob("pipe:joba1").succeedJob();
        assertStatus(pipelineFlow, "joba1", SUCCEEDED);
        assertStatus(pipelineFlow, "jobf", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobf").succeedJob();
        assertThreadShutDown(previousRunner);
        assertThreadShutDown(pipelineRunner);
        assertFlowStatus(pipelineFlow, SUCCEEDED);
    }

    @Test
    public void testBasicPipelineLevel1Run() throws Exception {
        final FlowRunner previousRunner = this.testUtil.createFromFlowMap("jobf", "prev");
        final ExecutionOptions options = new ExecutionOptions();
        options.setPipelineExecutionId(previousRunner.getExecutableFlow().getExecutionId());
        options.setPipelineLevel(1);
        final FlowWatcher watcher = new azkaban.execapp.event.LocalFlowWatcher(previousRunner);
        final FlowRunner pipelineRunner = this.testUtil.createFromFlowMap("jobf", "pipe", options);
        pipelineRunner.setFlowWatcher(watcher);
        // 1. START FLOW
        final ExecutableFlow pipelineFlow = pipelineRunner.getExecutableFlow();
        final ExecutableFlow previousFlow = previousRunner.getExecutableFlow();
        FlowRunnerTestUtil.startThread(previousRunner);
        assertStatus(previousFlow, "joba", RUNNING);
        assertStatus(previousFlow, "joba", RUNNING);
        assertStatus(previousFlow, "joba1", RUNNING);
        FlowRunnerTestUtil.startThread(pipelineRunner);
        assertStatus(pipelineFlow, "joba", QUEUED);
        assertStatus(pipelineFlow, "joba1", QUEUED);
        InteractiveTestJob.getTestJob("prev:joba").succeedJob();
        assertStatus(previousFlow, "joba", SUCCEEDED);
        assertStatus(previousFlow, "jobb", RUNNING);
        assertStatus(previousFlow, "jobb:innerJobA", RUNNING);
        assertStatus(previousFlow, "jobd", RUNNING);
        assertStatus(previousFlow, "jobc", RUNNING);
        assertStatus(previousFlow, "jobd:innerJobA", RUNNING);
        assertStatus(pipelineFlow, "joba", RUNNING);
        InteractiveTestJob.getTestJob("prev:jobb:innerJobA").succeedJob();
        assertStatus(previousFlow, "jobb:innerJobA", SUCCEEDED);
        assertStatus(previousFlow, "jobb:innerJobB", RUNNING);
        assertStatus(previousFlow, "jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("pipe:joba").succeedJob();
        assertStatus(pipelineFlow, "joba", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb", RUNNING);
        assertStatus(pipelineFlow, "jobd", RUNNING);
        assertStatus(pipelineFlow, "jobc", QUEUED);
        assertStatus(pipelineFlow, "jobd:innerJobA", QUEUED);
        assertStatus(pipelineFlow, "jobb:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("prev:jobd:innerJobA").succeedJob();
        assertStatus(previousFlow, "jobd:innerJobA", SUCCEEDED);
        assertStatus(previousFlow, "jobd:innerFlow2", RUNNING);
        assertStatus(pipelineFlow, "jobd:innerJobA", RUNNING);
        // Finish the previous d side
        InteractiveTestJob.getTestJob("prev:jobd:innerFlow2").succeedJob();
        assertStatus(previousFlow, "jobd:innerFlow2", SUCCEEDED);
        assertStatus(previousFlow, "jobd", SUCCEEDED);
        InteractiveTestJob.getTestJob("prev:jobb:innerJobB").succeedJob();
        InteractiveTestJob.getTestJob("prev:jobb:innerJobC").succeedJob();
        InteractiveTestJob.getTestJob("prev:jobc").succeedJob();
        InteractiveTestJob.getTestJob("pipe:jobb:innerJobA").succeedJob();
        assertStatus(previousFlow, "jobb:innerJobB", SUCCEEDED);
        assertStatus(previousFlow, "jobb:innerJobC", SUCCEEDED);
        assertStatus(previousFlow, "jobb:innerFlow", RUNNING);
        assertStatus(previousFlow, "jobc", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerJobA", SUCCEEDED);
        assertStatus(pipelineFlow, "jobc", RUNNING);
        assertStatus(pipelineFlow, "jobb:innerJobB", RUNNING);
        assertStatus(pipelineFlow, "jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("prev:jobb:innerFlow").succeedJob();
        InteractiveTestJob.getTestJob("pipe:jobc").succeedJob();
        assertStatus(previousFlow, "jobb:innerFlow", SUCCEEDED);
        assertStatus(previousFlow, "jobb", SUCCEEDED);
        assertStatus(previousFlow, "jobe", RUNNING);
        assertStatus(pipelineFlow, "jobc", SUCCEEDED);
        InteractiveTestJob.getTestJob("pipe:jobb:innerJobB").succeedJob();
        InteractiveTestJob.getTestJob("pipe:jobb:innerJobC").succeedJob();
        InteractiveTestJob.getTestJob("prev:jobe").succeedJob();
        assertStatus(previousFlow, "jobe", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerJobB", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerJobC", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerFlow", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobd:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("pipe:jobb:innerFlow").succeedJob();
        assertStatus(pipelineFlow, "jobb", SUCCEEDED);
        assertStatus(pipelineFlow, "jobd:innerJobA", SUCCEEDED);
        assertStatus(pipelineFlow, "jobb:innerFlow", SUCCEEDED);
        assertStatus(pipelineFlow, "jobd:innerFlow2", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobd:innerFlow2").succeedJob();
        InteractiveTestJob.getTestJob("prev:joba1").succeedJob();
        assertStatus(pipelineFlow, "jobd:innerFlow2", SUCCEEDED);
        assertStatus(pipelineFlow, "jobd", SUCCEEDED);
        assertStatus(previousFlow, "jobf", RUNNING);
        assertStatus(previousFlow, "joba1", SUCCEEDED);
        assertStatus(pipelineFlow, "joba1", RUNNING);
        assertStatus(pipelineFlow, "jobe", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobe").succeedJob();
        InteractiveTestJob.getTestJob("prev:jobf").succeedJob();
        assertStatus(pipelineFlow, "jobe", SUCCEEDED);
        assertStatus(previousFlow, "jobf", SUCCEEDED);
        assertFlowStatus(previousFlow, SUCCEEDED);
        InteractiveTestJob.getTestJob("pipe:joba1").succeedJob();
        assertStatus(pipelineFlow, "joba1", SUCCEEDED);
        assertStatus(pipelineFlow, "jobf", RUNNING);
        InteractiveTestJob.getTestJob("pipe:jobf").succeedJob();
        assertThreadShutDown(previousRunner);
        assertThreadShutDown(pipelineRunner);
        assertFlowStatus(pipelineFlow, SUCCEEDED);
    }

    @Test
    public void testBasicPipelineLevel2Run() throws Exception {
        final FlowRunner previousRunner = this.testUtil.createFromFlowMap("pipelineFlow", "prev");
        final ExecutionOptions options = new ExecutionOptions();
        options.setPipelineExecutionId(previousRunner.getExecutableFlow().getExecutionId());
        options.setPipelineLevel(2);
        final FlowWatcher watcher = new azkaban.execapp.event.LocalFlowWatcher(previousRunner);
        final FlowRunner pipelineRunner = this.testUtil.createFromFlowMap("pipelineFlow", "pipe", options);
        pipelineRunner.setFlowWatcher(watcher);
        // 1. START FLOW
        final ExecutableFlow pipelineFlow = pipelineRunner.getExecutableFlow();
        final ExecutableFlow previousFlow = previousRunner.getExecutableFlow();
        FlowRunnerTestUtil.startThread(previousRunner);
        assertStatus(previousFlow, "pipeline1", RUNNING);
        FlowRunnerTestUtil.startThread(pipelineRunner);
        assertStatus(pipelineFlow, "pipeline1", QUEUED);
        InteractiveTestJob.getTestJob("prev:pipeline1").succeedJob();
        assertStatus(previousFlow, "pipeline1", SUCCEEDED);
        assertStatus(previousFlow, "pipeline2", RUNNING);
        InteractiveTestJob.getTestJob("prev:pipeline2").succeedJob();
        assertStatus(previousFlow, "pipeline2", SUCCEEDED);
        assertStatus(previousFlow, "pipelineEmbeddedFlow3", RUNNING);
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerJobA", RUNNING);
        assertStatus(pipelineFlow, "pipeline1", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipeline1").succeedJob();
        assertStatus(pipelineFlow, "pipeline1", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline2", QUEUED);
        InteractiveTestJob.getTestJob("prev:pipelineEmbeddedFlow3:innerJobA").succeedJob();
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerJobA", SUCCEEDED);
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerJobB", RUNNING);
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerJobC", RUNNING);
        assertStatus(pipelineFlow, "pipeline2", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipeline2").succeedJob();
        assertStatus(pipelineFlow, "pipeline2", SUCCEEDED);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3", RUNNING);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobA", QUEUED);
        InteractiveTestJob.getTestJob("prev:pipelineEmbeddedFlow3:innerJobB").succeedJob();
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerJobB", SUCCEEDED);
        InteractiveTestJob.getTestJob("prev:pipelineEmbeddedFlow3:innerJobC").succeedJob();
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerFlow", RUNNING);
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerJobC", SUCCEEDED);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipelineEmbeddedFlow3:innerJobA").succeedJob();
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobA", SUCCEEDED);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobC", QUEUED);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobB", QUEUED);
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerFlow", RUNNING);
        InteractiveTestJob.getTestJob("prev:pipelineEmbeddedFlow3:innerFlow").succeedJob();
        assertStatus(previousFlow, "pipelineEmbeddedFlow3:innerFlow", SUCCEEDED);
        assertStatus(previousFlow, "pipelineEmbeddedFlow3", SUCCEEDED);
        assertStatus(previousFlow, "pipeline4", RUNNING);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobC", RUNNING);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobB", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipelineEmbeddedFlow3:innerJobB").succeedJob();
        InteractiveTestJob.getTestJob("pipe:pipelineEmbeddedFlow3:innerJobC").succeedJob();
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobC", SUCCEEDED);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerJobB", SUCCEEDED);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerFlow", QUEUED);
        InteractiveTestJob.getTestJob("prev:pipeline4").succeedJob();
        assertStatus(previousFlow, "pipeline4", SUCCEEDED);
        assertStatus(previousFlow, "pipelineFlow", RUNNING);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerFlow", RUNNING);
        InteractiveTestJob.getTestJob("prev:pipelineFlow").succeedJob();
        assertStatus(previousFlow, "pipelineFlow", SUCCEEDED);
        assertFlowStatus(previousFlow, SUCCEEDED);
        assertThreadShutDown(previousRunner);
        InteractiveTestJob.getTestJob("pipe:pipelineEmbeddedFlow3:innerFlow").succeedJob();
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3:innerFlow", SUCCEEDED);
        assertStatus(pipelineFlow, "pipelineEmbeddedFlow3", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline4", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipeline4").succeedJob();
        assertStatus(pipelineFlow, "pipeline4", SUCCEEDED);
        assertStatus(pipelineFlow, "pipelineFlow", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipelineFlow").succeedJob();
        assertStatus(pipelineFlow, "pipelineFlow", SUCCEEDED);
        assertFlowStatus(pipelineFlow, SUCCEEDED);
        assertThreadShutDown(pipelineRunner);
    }

    @Test
    public void testBasicPipelineLevel2Run2() throws Exception {
        final FlowRunner previousRunner = this.testUtil.createFromFlowMap("pipeline1_2", "prev");
        final ExecutionOptions options = new ExecutionOptions();
        options.setPipelineExecutionId(previousRunner.getExecutableFlow().getExecutionId());
        options.setPipelineLevel(2);
        final FlowWatcher watcher = new azkaban.execapp.event.LocalFlowWatcher(previousRunner);
        final FlowRunner pipelineRunner = this.testUtil.createFromFlowMap("pipeline1_2", "pipe", options);
        pipelineRunner.setFlowWatcher(watcher);
        // 1. START FLOW
        final ExecutableFlow pipelineFlow = pipelineRunner.getExecutableFlow();
        final ExecutableFlow previousFlow = previousRunner.getExecutableFlow();
        FlowRunnerTestUtil.startThread(previousRunner);
        assertStatus(previousFlow, "pipeline1_1", RUNNING);
        assertStatus(previousFlow, "pipeline1_1:innerJobA", RUNNING);
        FlowRunnerTestUtil.startThread(pipelineRunner);
        assertStatus(pipelineFlow, "pipeline1_1", RUNNING);
        assertStatus(pipelineFlow, "pipeline1_1:innerJobA", QUEUED);
        InteractiveTestJob.getTestJob("prev:pipeline1_1:innerJobA").succeedJob();
        assertStatus(previousFlow, "pipeline1_1:innerJobA", SUCCEEDED);
        assertStatus(previousFlow, "pipeline1_1:innerFlow2", RUNNING);
        InteractiveTestJob.getTestJob("prev:pipeline1_1:innerFlow2").succeedJob();
        assertStatus(previousFlow, "pipeline1_1", SUCCEEDED);
        assertStatus(previousFlow, "pipeline1_1:innerFlow2", SUCCEEDED);
        assertStatus(previousFlow, "pipeline1_2", RUNNING);
        assertStatus(previousFlow, "pipeline1_2:innerJobA", RUNNING);
        assertStatus(pipelineFlow, "pipeline1_1:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipeline1_1:innerJobA").succeedJob();
        assertStatus(pipelineFlow, "pipeline1_1:innerJobA", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline1_1:innerFlow2", QUEUED);
        InteractiveTestJob.getTestJob("prev:pipeline1_2:innerJobA").succeedJob();
        assertStatus(previousFlow, "pipeline1_2:innerJobA", SUCCEEDED);
        assertStatus(previousFlow, "pipeline1_2:innerFlow2", RUNNING);
        assertStatus(pipelineFlow, "pipeline1_1:innerFlow2", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipeline1_1:innerFlow2").succeedJob();
        assertStatus(pipelineFlow, "pipeline1_1", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline1_1:innerFlow2", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline1_2", RUNNING);
        assertStatus(pipelineFlow, "pipeline1_2:innerJobA", QUEUED);
        InteractiveTestJob.getTestJob("pipe:pipeline1_1:innerFlow2").succeedJob();
        assertStatus(pipelineFlow, "pipeline1_1", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline1_1:innerFlow2", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline1_2", RUNNING);
        assertStatus(pipelineFlow, "pipeline1_2:innerJobA", QUEUED);
        InteractiveTestJob.getTestJob("prev:pipeline1_2:innerFlow2").succeedJob();
        assertStatus(previousFlow, "pipeline1_2:innerFlow2", SUCCEEDED);
        assertStatus(previousFlow, "pipeline1_2", SUCCEEDED);
        assertFlowStatus(previousFlow, SUCCEEDED);
        assertThreadShutDown(previousRunner);
        assertStatus(pipelineFlow, "pipeline1_2:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipeline1_2:innerJobA").succeedJob();
        assertStatus(pipelineFlow, "pipeline1_2:innerJobA", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline1_2:innerFlow2", RUNNING);
        InteractiveTestJob.getTestJob("pipe:pipeline1_2:innerFlow2").succeedJob();
        assertStatus(pipelineFlow, "pipeline1_2", SUCCEEDED);
        assertStatus(pipelineFlow, "pipeline1_2:innerFlow2", SUCCEEDED);
        assertFlowStatus(pipelineFlow, SUCCEEDED);
        assertThreadShutDown(pipelineRunner);
    }
}

