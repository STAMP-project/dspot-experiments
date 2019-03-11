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
package org.apache.geode.internal.protocol.protobuf.v1.operations;


import FunctionAPI.ExecuteFunctionOnRegionRequest;
import ResourcePermissions.DATA_WRITE;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.protocol.protobuf.statistics.ProtobufClientStatistics;
import org.apache.geode.internal.protocol.protobuf.v1.FunctionAPI;
import org.apache.geode.internal.protocol.protobuf.v1.ProtobufSerializationService;
import org.apache.geode.internal.protocol.protobuf.v1.Result;
import org.apache.geode.internal.protocol.protobuf.v1.ServerMessageExecutionContext;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unfortunately, we can't test the happy path with a unit test, because the function service is
 * static, and there's mocking function execution is too complicated.
 */
@Category({ ClientServerTest.class })
public class ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest {
    private static final String TEST_REGION = "testRegion";

    private static final String TEST_FUNCTION_ID = "testFunction";

    public static final String NOT_A_REGION = "notARegion";

    private Region regionStub;

    private InternalCache cacheStub;

    private ExecuteFunctionOnRegionRequestOperationHandler operationHandler;

    private ProtobufSerializationService serializationService;

    private ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TestFunction function;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static class TestFunction implements Function {
        // non-null iff function has been executed.
        private AtomicReference<FunctionContext> context = new AtomicReference<>();

        @Override
        public String getId() {
            return ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID;
        }

        @Override
        public void execute(FunctionContext context) {
            this.context.set(context);
            context.getResultSender().lastResult("result");
        }

        FunctionContext getContext() {
            return context.get();
        }
    }

    @Test
    public void failsOnUnknownRegion() throws Exception {
        final FunctionAPI.ExecuteFunctionOnRegionRequest request = ExecuteFunctionOnRegionRequest.newBuilder().setFunctionID(ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID).setRegion(ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.NOT_A_REGION).build();
        expectedException.expect(RegionDestroyedException.class);
        final Result<FunctionAPI.ExecuteFunctionOnRegionResponse> result = operationHandler.process(serializationService, request, mockedMessageExecutionContext());
    }

    @Test
    public void requiresPermissions() throws Exception {
        final FunctionAPI.ExecuteFunctionOnRegionRequest request = ExecuteFunctionOnRegionRequest.newBuilder().setFunctionID(ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID).setRegion(ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TEST_REGION).build();
        SecurityService securityService = Mockito.mock(SecurityService.class);
        Mockito.when(securityService.isIntegratedSecurity()).thenReturn(true);
        Mockito.doThrow(new NotAuthorizedException("we should catch this")).when(securityService).authorize(Mockito.eq(DATA_WRITE), ArgumentMatchers.any());
        ServerMessageExecutionContext context = new ServerMessageExecutionContext(cacheStub, Mockito.mock(ProtobufClientStatistics.class), securityService);
        expectedException.expect(NotAuthorizedException.class);
        operationHandler.process(serializationService, request, context);
    }

    @Test
    public void functionNotFound() throws Exception {
        final FunctionAPI.ExecuteFunctionOnRegionRequest request = ExecuteFunctionOnRegionRequest.newBuilder().setFunctionID(ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID).setRegion(ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TEST_REGION).build();
        FunctionService.unregisterFunction(ExecuteFunctionOnRegionRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID);
        expectedException.expect(IllegalArgumentException.class);
        final Result<FunctionAPI.ExecuteFunctionOnRegionResponse> result = operationHandler.process(serializationService, request, mockedMessageExecutionContext());
    }
}

