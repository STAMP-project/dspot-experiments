/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets.command;


import AbstractExecution.NO_HA_HASRESULT_NO_OPTIMIZEFORWRITE;
import AbstractExecution.NO_HA_HASRESULT_OPTIMIZEFORWRITE;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.operations.ExecuteFunctionOperationContext;
import org.apache.geode.internal.cache.execute.AbstractExecution;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.Part;
import org.apache.geode.internal.security.AuthorizeRequest;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class ExecuteRegionFunctionGeode18Test {
    private static final String FUNCTION_ID = "function_id";

    @Mock
    private Function functionObject;

    private ExecuteRegionFunctionGeode18 executeRegionFunctionGeode18;

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void executingFunctionByStringWithNoHAShouldSetWaitOnException() {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        String functionName = "functionName";
        Mockito.when(execution.execute(functionName)).thenReturn(Mockito.mock(ResultCollector.class));
        this.executeRegionFunctionGeode18.executeFunctionWithResult(functionName, NO_HA_HASRESULT_NO_OPTIMIZEFORWRITE, functionObject, execution);
        Mockito.verify(execution, Mockito.times(1)).setWaitOnExceptionFlag(true);
    }

    @Test
    public void executingFunctionByStringWithNoHAWithOptimizeForWriteShouldSetWaitOnException() {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        String functionName = "functionName";
        Mockito.when(execution.execute(functionName)).thenReturn(Mockito.mock(ResultCollector.class));
        this.executeRegionFunctionGeode18.executeFunctionWithResult(functionName, NO_HA_HASRESULT_OPTIMIZEFORWRITE, functionObject, execution);
        Mockito.verify(execution, Mockito.times(1)).setWaitOnExceptionFlag(true);
    }

    @Test
    public void executeFunctionObjectShouldSetWaitOnException() {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        Mockito.when(execution.execute(functionObject)).thenReturn(Mockito.mock(ResultCollector.class));
        this.executeRegionFunctionGeode18.executeFunctionWithResult(functionObject, NO_HA_HASRESULT_OPTIMIZEFORWRITE, functionObject, execution);
        Mockito.verify(execution, Mockito.times(1)).setWaitOnExceptionFlag(true);
    }

    @Test
    public void generateNullArgumentMessageIfRegionIsNull() {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        Mockito.when(execution.execute(functionObject)).thenReturn(Mockito.mock(ResultCollector.class));
        TestCase.assertEquals("The input region for the execute function request is null", this.executeRegionFunctionGeode18.generateNullArgumentMessage(null, null));
    }

    @Test
    public void generateNullArgumentMessageIfFunctionIsNullAndRegionIsNotNull() {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        Mockito.when(execution.execute(functionObject)).thenReturn(Mockito.mock(ResultCollector.class));
        TestCase.assertEquals("The input function for the execute function request is null", this.executeRegionFunctionGeode18.generateNullArgumentMessage("someRegion", null));
    }

    @Test
    public void populateFiltersWillReturnFiltersReadFromClientMessage() throws Exception {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        Mockito.when(execution.execute(functionObject)).thenReturn(Mockito.mock(ResultCollector.class));
        Message clientMessage = Mockito.mock(Message.class);
        Part part1 = Mockito.mock(Part.class);
        Object object1 = new Object();
        Mockito.when(part1.getStringOrObject()).thenReturn(object1);
        Part part2 = Mockito.mock(Part.class);
        Object object2 = new Object();
        Mockito.when(part2.getStringOrObject()).thenReturn(object2);
        Part part3 = Mockito.mock(Part.class);
        Object object3 = new Object();
        Mockito.when(part3.getStringOrObject()).thenReturn(object3);
        Mockito.when(clientMessage.getPart(7)).thenReturn(part1);
        Mockito.when(clientMessage.getPart(8)).thenReturn(part2);
        Mockito.when(clientMessage.getPart(9)).thenReturn(part3);
        int filterSize = 3;
        Set filter = this.executeRegionFunctionGeode18.populateFilters(clientMessage, filterSize);
        Assert.assertSame(filterSize, filter.size());
        Assert.assertTrue(filter.contains(object1));
        Assert.assertTrue(filter.contains(object2));
        Assert.assertTrue(filter.contains(object3));
    }

    @Test
    public void populateRemovedNodexWillReturnNodesReadFromClient() throws Exception {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        Mockito.when(execution.execute(functionObject)).thenReturn(Mockito.mock(ResultCollector.class));
        Message clientMessage = Mockito.mock(Message.class);
        Part part1 = Mockito.mock(Part.class);
        Object object1 = new Object();
        Mockito.when(part1.getStringOrObject()).thenReturn(object1);
        Part part2 = Mockito.mock(Part.class);
        Object object2 = new Object();
        Mockito.when(part2.getStringOrObject()).thenReturn(object2);
        Part part3 = Mockito.mock(Part.class);
        Object object3 = new Object();
        Mockito.when(part3.getStringOrObject()).thenReturn(object3);
        Mockito.when(clientMessage.getPart(7)).thenReturn(part1);
        Mockito.when(clientMessage.getPart(8)).thenReturn(part2);
        Mockito.when(clientMessage.getPart(9)).thenReturn(part3);
        Set nodes = this.executeRegionFunctionGeode18.populateRemovedNodes(clientMessage, 3, 6);
        Assert.assertTrue(nodes.contains(object1));
        Assert.assertTrue(nodes.contains(object2));
        Assert.assertTrue(nodes.contains(object3));
    }

    @Test
    public void getAuthorizedExecuteFunctionReturnsNullIfAuthorizationIsNull() {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        Mockito.when(execution.execute(functionObject)).thenReturn(Mockito.mock(ResultCollector.class));
        String functionName = "functionName";
        String regionPath = "regionPath";
        ExecuteFunctionOperationContext context = executeRegionFunctionGeode18.getAuthorizedExecuteFunctionOperationContext(null, null, true, null, functionName, regionPath);
        Assert.assertNull(context);
    }

    @Test
    public void getAuthorizedExecuteFunctionReturnsExecutionContextIfAuthorizeRequestIsNotNull() {
        AbstractExecution execution = Mockito.mock(AbstractExecution.class);
        Mockito.when(execution.execute(functionObject)).thenReturn(Mockito.mock(ResultCollector.class));
        String functionName = "functionName";
        String regionPath = "regionPath";
        AuthorizeRequest request = Mockito.mock(AuthorizeRequest.class);
        Mockito.when(request.executeFunctionAuthorize(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(Mockito.mock(ExecuteFunctionOperationContext.class));
        ExecuteFunctionOperationContext context = executeRegionFunctionGeode18.getAuthorizedExecuteFunctionOperationContext(null, null, true, request, functionName, regionPath);
        Assert.assertNotNull(context);
    }
}

