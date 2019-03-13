/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.swf;


import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.DynamicWorkflowClientExternal;
import com.amazonaws.services.simpleworkflow.model.DescribeWorkflowExecutionRequest;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecutionDetail;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecutionInfo;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CamelSWFWorkflowClientTest {
    private SWFConfiguration configuration;

    private AmazonSimpleWorkflowClient swClient;

    private SWFEndpoint endpoint;

    private CamelSWFWorkflowClient camelSWFWorkflowClient;

    private DynamicWorkflowClientExternal clientExternal;

    @Test
    public void testDescribeWorkflowInstance() throws Exception {
        WorkflowExecutionInfo executionInfo = new WorkflowExecutionInfo();
        executionInfo.setCloseStatus("COMPLETED");
        Date closeTimestamp = new Date();
        executionInfo.setCloseTimestamp(closeTimestamp);
        executionInfo.setExecutionStatus("CLOSED");
        executionInfo.setTagList(Collections.emptyList());
        WorkflowExecutionDetail workflowExecutionDetail = new WorkflowExecutionDetail();
        workflowExecutionDetail.setExecutionInfo(executionInfo);
        Mockito.when(swClient.describeWorkflowExecution(ArgumentMatchers.any(DescribeWorkflowExecutionRequest.class))).thenReturn(workflowExecutionDetail);
        Map<String, Object> description = camelSWFWorkflowClient.describeWorkflowInstance("123", "run1");
        DescribeWorkflowExecutionRequest describeRequest = new DescribeWorkflowExecutionRequest();
        describeRequest.setDomain(configuration.getDomainName());
        describeRequest.setExecution(new WorkflowExecution().withWorkflowId("123").withRunId("run1"));
        Mockito.verify(swClient).describeWorkflowExecution(describeRequest);
        MatcherAssert.assertThat(((String) (description.get("closeStatus"))), CoreMatchers.is("COMPLETED"));
        MatcherAssert.assertThat(((Date) (description.get("closeTimestamp"))), CoreMatchers.is(closeTimestamp));
        MatcherAssert.assertThat(((String) (description.get("executionStatus"))), CoreMatchers.is("CLOSED"));
        MatcherAssert.assertThat(((List<?>) (description.get("tagList"))), CoreMatchers.is(Collections.EMPTY_LIST));
        MatcherAssert.assertThat(((WorkflowExecutionDetail) (description.get("executionDetail"))), CoreMatchers.is(workflowExecutionDetail));
    }

    @Test
    public void testSignalWorkflowExecution() throws Exception {
        camelSWFWorkflowClient.signalWorkflowExecution("123", "run1", "signalMethod", "Hi");
        Mockito.verify(clientExternal).signalWorkflowExecution("signalMethod", new Object[]{ "Hi" });
    }

    @Test
    public void testGetWorkflowExecutionState() throws Throwable {
        Class<String> stateType = String.class;
        Mockito.when(clientExternal.getWorkflowExecutionState(stateType)).thenReturn("some state");
        String state = ((String) (camelSWFWorkflowClient.getWorkflowExecutionState("123", "run1", stateType)));
        Mockito.verify(clientExternal).getWorkflowExecutionState(stateType);
        MatcherAssert.assertThat(state, CoreMatchers.is("some state"));
    }

    @Test
    public void testRequestCancelWorkflowExecution() throws Throwable {
        camelSWFWorkflowClient.requestCancelWorkflowExecution("123", "run1");
        Mockito.verify(clientExternal).requestCancelWorkflowExecution();
    }

    @Test
    public void testTerminateWorkflowExecution() throws Throwable {
        camelSWFWorkflowClient.terminateWorkflowExecution("123", "run1", "reason", "details", null);
        Mockito.verify(clientExternal).terminateWorkflowExecution("reason", "details", null);
    }

    @Test
    public void testStartWorkflowExecution() throws Throwable {
        WorkflowExecution workflowExecution = new WorkflowExecution();
        workflowExecution.setWorkflowId("123");
        workflowExecution.setRunId("run1");
        Mockito.when(clientExternal.getWorkflowExecution()).thenReturn(workflowExecution);
        String[] ids = camelSWFWorkflowClient.startWorkflowExecution(null, null, "eventName", "version", null, Collections.singletonList("camelTest"));
        Mockito.verify(clientExternal).startWorkflowExecution(new Object[]{ null });
        MatcherAssert.assertThat("123", CoreMatchers.is(ids[0]));
        MatcherAssert.assertThat("run1", CoreMatchers.is(ids[1]));
    }
}

