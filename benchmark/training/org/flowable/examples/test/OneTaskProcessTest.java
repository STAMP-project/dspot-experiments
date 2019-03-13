/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.examples.test;


import java.util.List;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.ScriptTask;
import org.flowable.engine.event.EventLogEntry;
import org.flowable.engine.impl.event.logger.EventLogger;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 * This class provides examples how to test one task process
 */
public class OneTaskProcessTest extends PluggableFlowableTestCase {
    protected EventLogger databaseEventLogger;

    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/runtime/oneTaskProcess.bpmn20.xml" })
    public void testStandardJUnitOneTaskProcess() {
        // Arrange -> start process
        ProcessInstance oneTaskProcess = this.runtimeService.createProcessInstanceBuilder().processDefinitionKey("oneTaskProcess").start();
        // Act -> complete task
        this.taskService.complete(this.taskService.createTaskQuery().processInstanceId(oneTaskProcess.getProcessInstanceId()).singleResult().getId());
        // Assert -> process instance is finished
        Assert.assertThat(this.runtimeService.createProcessInstanceQuery().processInstanceId(oneTaskProcess.getId()).count(), Is.is(0L));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/runtime/oneTaskProcess.bpmn20.xml" })
    public void testProcessModelByAnotherProcess() {
        testProcessModelByAnotherProcess(createTestProcessBpmnModel("oneTaskProcess"));
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/twoTasksProcess.bpmn20.xml" })
    public void testProcessModelFailure() {
        // deploy different process - test should fail
        try {
            testProcessModelByAnotherProcess(createTestProcessBpmnModel("twoTasksProcess"));
            fail("Expected exception was not thrown.");
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), Is.is("\nExpected: is <0L>\n     but: was <1L>"));
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/api/oneTaskProcess.bpmn20.xml" })
    public void testGenerateProcessTestSemiAutomatically() {
        // Generate "user" events
        testStandardJUnitOneTaskProcess();
        List<EventLogEntry> eventLogEntries = managementService.getEventLogEntries(null, null);
        // automatic step to generate process test skeleton from already generated "user" events
        List<FlowNode> testFlowNodesSkeleton = createTestFlowNodesFromEventLogEntries(eventLogEntries);
        // add assertion manually
        ScriptTask assertTask = new ScriptTask();
        assertTask.setName("Assert task");
        assertTask.setId("assertTask");
        assertTask.setAsynchronous(true);
        assertTask.setScriptFormat("groovy");
        assertTask.setScript(("import org.flowable.engine.impl.context.Context;\n" + ((("import static org.hamcrest.core.Is.is;\n" + "import static org.flowable.examples.test.MatcherAssert.assertThat;\n") + "\n") + "assertThat(Context.getProcessEngineConfiguration().getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).count(), is(0L));")));
        testFlowNodesSkeleton.add(assertTask);
        // generate BpmnModel from given skeleton
        BpmnModel bpmnModel = decorateTestSkeletonAsProcess(testFlowNodesSkeleton);
        // run process in the same way as ordinary process model test
        testProcessModelByAnotherProcess(bpmnModel);
    }
}

