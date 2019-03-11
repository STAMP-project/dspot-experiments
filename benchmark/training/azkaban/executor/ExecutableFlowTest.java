/**
 * Copyright 2014 LinkedIn Corp.
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
package azkaban.executor;


import FailureAction.CANCEL_ALL;
import Status.DISABLED;
import Status.RUNNING;
import azkaban.flow.Flow;
import azkaban.project.Project;
import azkaban.utils.JSONUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ExecutableFlowTest {
    private Project project;

    @Test
    public void testExecutorFlowCreation() throws Exception {
        final Flow flow = this.project.getFlow("jobe");
        Assert.assertNotNull(flow);
        final ExecutableFlow exFlow = new ExecutableFlow(this.project, flow);
        Assert.assertNotNull(exFlow.getExecutableNode("joba"));
        Assert.assertNotNull(exFlow.getExecutableNode("jobb"));
        Assert.assertNotNull(exFlow.getExecutableNode("jobc"));
        Assert.assertNotNull(exFlow.getExecutableNode("jobd"));
        Assert.assertNotNull(exFlow.getExecutableNode("jobe"));
        Assert.assertFalse(((exFlow.getExecutableNode("joba")) instanceof ExecutableFlowBase));
        Assert.assertTrue(((exFlow.getExecutableNode("jobb")) instanceof ExecutableFlowBase));
        Assert.assertTrue(((exFlow.getExecutableNode("jobc")) instanceof ExecutableFlowBase));
        Assert.assertTrue(((exFlow.getExecutableNode("jobd")) instanceof ExecutableFlowBase));
        Assert.assertFalse(((exFlow.getExecutableNode("jobe")) instanceof ExecutableFlowBase));
        final ExecutableFlowBase jobbFlow = ((ExecutableFlowBase) (exFlow.getExecutableNode("jobb")));
        final ExecutableFlowBase jobcFlow = ((ExecutableFlowBase) (exFlow.getExecutableNode("jobc")));
        final ExecutableFlowBase jobdFlow = ((ExecutableFlowBase) (exFlow.getExecutableNode("jobd")));
        Assert.assertEquals("innerFlow", jobbFlow.getFlowId());
        Assert.assertEquals("jobb", jobbFlow.getId());
        Assert.assertEquals(4, jobbFlow.getExecutableNodes().size());
        Assert.assertEquals("innerFlow", jobcFlow.getFlowId());
        Assert.assertEquals("jobc", jobcFlow.getId());
        Assert.assertEquals(4, jobcFlow.getExecutableNodes().size());
        Assert.assertEquals("innerFlow", jobdFlow.getFlowId());
        Assert.assertEquals("jobd", jobdFlow.getId());
        Assert.assertEquals(4, jobdFlow.getExecutableNodes().size());
    }

    @Test
    public void testExecutorFlowJson() throws Exception {
        final Flow flow = this.project.getFlow("jobe");
        Assert.assertNotNull(flow);
        final ExecutableFlow exFlow = new ExecutableFlow(this.project, flow);
        final Object obj = exFlow.toObject();
        final String exFlowJSON = JSONUtils.toJSON(obj);
        final Map<String, Object> flowObjMap = ((Map<String, Object>) (JSONUtils.parseJSONFromString(exFlowJSON)));
        final ExecutableFlow parsedExFlow = ExecutableFlow.createExecutableFlowFromObject(flowObjMap);
        ExecutableFlowTest.testEquals(exFlow, parsedExFlow);
    }

    @Test
    public void testExecutorFlowJson2() throws Exception {
        final Flow flow = this.project.getFlow("jobe");
        Assert.assertNotNull(flow);
        final ExecutableFlow exFlow = new ExecutableFlow(this.project, flow);
        exFlow.setExecutionId(101);
        // reset twice so that attempt = 2
        exFlow.resetForRetry();
        exFlow.resetForRetry();
        exFlow.setDelayedExecution(1000);
        final ExecutionOptions options = new ExecutionOptions();
        options.setConcurrentOption("blah");
        options.setDisabledJobs(Arrays.asList(new Object[]{ "bee", null, "boo" }));
        options.setFailureAction(CANCEL_ALL);
        options.setFailureEmails(Arrays.asList(new String[]{ "doo", null, "daa" }));
        options.setSuccessEmails(Arrays.asList(new String[]{ "dee", null, "dae" }));
        options.setPipelineLevel(2);
        options.setPipelineExecutionId(3);
        options.setNotifyOnFirstFailure(true);
        options.setNotifyOnLastFailure(true);
        final HashMap<String, String> flowProps = new HashMap<>();
        flowProps.put("la", "fa");
        options.addAllFlowParameters(flowProps);
        exFlow.setExecutionOptions(options);
        final Object obj = exFlow.toObject();
        final String exFlowJSON = JSONUtils.toJSON(obj);
        final Map<String, Object> flowObjMap = ((Map<String, Object>) (JSONUtils.parseJSONFromString(exFlowJSON)));
        final ExecutableFlow parsedExFlow = ExecutableFlow.createExecutableFlowFromObject(flowObjMap);
        ExecutableFlowTest.testEquals(exFlow, parsedExFlow);
    }

    @Test
    public void testExecutorFlowUpdates() throws Exception {
        final Flow flow = this.project.getFlow("jobe");
        final ExecutableFlow exFlow = new ExecutableFlow(this.project, flow);
        exFlow.setExecutionId(101);
        // Create copy of flow
        final Object obj = exFlow.toObject();
        final String exFlowJSON = JSONUtils.toJSON(obj);
        final Map<String, Object> flowObjMap = ((Map<String, Object>) (JSONUtils.parseJSONFromString(exFlowJSON)));
        final ExecutableFlow copyFlow = ExecutableFlow.createExecutableFlowFromObject(flowObjMap);
        ExecutableFlowTest.testEquals(exFlow, copyFlow);
        final ExecutableNode joba = exFlow.getExecutableNode("joba");
        final ExecutableFlowBase jobb = ((ExecutableFlowBase) (exFlow.getExecutableNode("jobb")));
        final ExecutableFlowBase jobc = ((ExecutableFlowBase) (exFlow.getExecutableNode("jobc")));
        final ExecutableFlowBase jobd = ((ExecutableFlowBase) (exFlow.getExecutableNode("jobd")));
        final ExecutableNode jobe = exFlow.getExecutableNode("jobe");
        assertNotNull(joba, jobb, jobc, jobd, jobe);
        final ExecutableNode jobbInnerFlowA = jobb.getExecutableNode("innerJobA");
        final ExecutableNode jobbInnerFlowB = jobb.getExecutableNode("innerJobB");
        final ExecutableNode jobbInnerFlowC = jobb.getExecutableNode("innerJobC");
        final ExecutableNode jobbInnerFlow = jobb.getExecutableNode("innerFlow");
        assertNotNull(jobbInnerFlowA, jobbInnerFlowB, jobbInnerFlowC, jobbInnerFlow);
        final ExecutableNode jobcInnerFlowA = jobc.getExecutableNode("innerJobA");
        final ExecutableNode jobcInnerFlowB = jobc.getExecutableNode("innerJobB");
        final ExecutableNode jobcInnerFlowC = jobc.getExecutableNode("innerJobC");
        final ExecutableNode jobcInnerFlow = jobc.getExecutableNode("innerFlow");
        assertNotNull(jobcInnerFlowA, jobcInnerFlowB, jobcInnerFlowC, jobcInnerFlow);
        final ExecutableNode jobdInnerFlowA = jobd.getExecutableNode("innerJobA");
        final ExecutableNode jobdInnerFlowB = jobd.getExecutableNode("innerJobB");
        final ExecutableNode jobdInnerFlowC = jobd.getExecutableNode("innerJobC");
        final ExecutableNode jobdInnerFlow = jobd.getExecutableNode("innerFlow");
        assertNotNull(jobdInnerFlowA, jobdInnerFlowB, jobdInnerFlowC, jobdInnerFlow);
        exFlow.setEndTime(1000);
        exFlow.setStartTime(500);
        exFlow.setStatus(RUNNING);
        exFlow.setUpdateTime(133);
        // Change one job and see if it updates
        final long time = System.currentTimeMillis();
        jobe.setEndTime(time);
        jobe.setUpdateTime(time);
        jobe.setStatus(DISABLED);
        jobe.setStartTime((time - 1));
        // Should be one node that was changed
        Map<String, Object> updateObject = exFlow.toUpdateObject(0);
        Assert.assertEquals(1, ((List) (updateObject.get("nodes"))).size());
        // Reapplying should give equal results.
        copyFlow.applyUpdateObject(updateObject);
        ExecutableFlowTest.testEquals(exFlow, copyFlow);
        // This update shouldn't provide any results
        updateObject = exFlow.toUpdateObject(System.currentTimeMillis());
        Assert.assertNull(updateObject.get("nodes"));
        // Change inner flow
        final long currentTime = time + 1;
        jobbInnerFlowA.setEndTime(currentTime);
        jobbInnerFlowA.setUpdateTime(currentTime);
        jobbInnerFlowA.setStatus(DISABLED);
        jobbInnerFlowA.setStartTime((currentTime - 100));
        // We should get 2 updates if we do a toUpdateObject using 0 as the start
        // time
        updateObject = exFlow.toUpdateObject(0);
        Assert.assertEquals(2, ((List) (updateObject.get("nodes"))).size());
        // This should provide 1 update. That we can apply
        updateObject = exFlow.toUpdateObject(jobe.getUpdateTime());
        Assert.assertNotNull(updateObject.get("nodes"));
        Assert.assertEquals(1, ((List) (updateObject.get("nodes"))).size());
        copyFlow.applyUpdateObject(updateObject);
        ExecutableFlowTest.testEquals(exFlow, copyFlow);
        // This shouldn't give any results anymore
        updateObject = exFlow.toUpdateObject(jobbInnerFlowA.getUpdateTime());
        Assert.assertNull(updateObject.get("nodes"));
    }
}

