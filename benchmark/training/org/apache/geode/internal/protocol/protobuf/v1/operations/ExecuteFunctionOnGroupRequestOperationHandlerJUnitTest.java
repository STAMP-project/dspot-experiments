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


import FunctionAPI.ExecuteFunctionOnGroupRequest;
import ResourcePermissions.DATA_WRITE;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.distributed.DistributedSystemDisconnectedException;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCacheForClientAccess;
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


@Category({ ClientServerTest.class })
public class ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest {
    private static final String TEST_GROUP1 = "group1";

    private static final String TEST_GROUP2 = "group2";

    private static final String TEST_FUNCTION_ID = "testFunction";

    public static final String NOT_A_GROUP = "notAGroup";

    private InternalCacheForClientAccess cacheStub;

    private DistributionManager distributionManager;

    private ExecuteFunctionOnGroupRequestOperationHandler operationHandler;

    private ProtobufSerializationService serializationService;

    private ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TestFunction function;

    private InternalDistributedSystem distributedSystem;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static class TestFunction implements Function {
        // non-null iff function has been executed.
        private AtomicReference<FunctionContext> context = new AtomicReference<>();

        @Override
        public String getId() {
            return ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID;
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

    @Test(expected = DistributedSystemDisconnectedException.class)
    public void succeedsWithValidMembers() throws Exception {
        Mockito.when(distributionManager.getMemberWithName(ArgumentMatchers.any(String.class))).thenReturn(new InternalDistributedMember("localhost", 0), new InternalDistributedMember("localhost", 1), null);
        final FunctionAPI.ExecuteFunctionOnGroupRequest request = ExecuteFunctionOnGroupRequest.newBuilder().setFunctionID(ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID).addGroupName(ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TEST_GROUP1).addGroupName(ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TEST_GROUP2).build();
        final Result<FunctionAPI.ExecuteFunctionOnGroupResponse> result = operationHandler.process(serializationService, request, mockedMessageExecutionContext());
        // unfortunately FunctionService fishes for a DistributedSystem and throws an exception
        // if it can't find one. It uses a static method on InternalDistributedSystem, so no
        // mocking is possible. If the test throws DistributedSystemDisconnectedException it
        // means that the operation handler got to the point of trying get an execution
        // context
    }

    @Test
    public void requiresPermissions() throws Exception {
        final SecurityService securityService = Mockito.mock(SecurityService.class);
        Mockito.when(securityService.isIntegratedSecurity()).thenReturn(true);
        Mockito.doThrow(new NotAuthorizedException("we should catch this")).when(securityService).authorize(ArgumentMatchers.eq(DATA_WRITE), ArgumentMatchers.any());
        Mockito.when(cacheStub.getSecurityService()).thenReturn(securityService);
        final FunctionAPI.ExecuteFunctionOnGroupRequest request = ExecuteFunctionOnGroupRequest.newBuilder().setFunctionID(ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TEST_FUNCTION_ID).addGroupName(ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TEST_GROUP1).build();
        ServerMessageExecutionContext context = new ServerMessageExecutionContext(cacheStub, Mockito.mock(ProtobufClientStatistics.class), securityService);
        expectedException.expect(NotAuthorizedException.class);
        operationHandler.process(serializationService, request, context);
    }

    @Test
    public void functionNotFound() throws Exception {
        final FunctionAPI.ExecuteFunctionOnGroupRequest request = ExecuteFunctionOnGroupRequest.newBuilder().setFunctionID("I am not a function, I am a human").addGroupName(ExecuteFunctionOnGroupRequestOperationHandlerJUnitTest.TEST_GROUP1).build();
        expectedException.expect(IllegalArgumentException.class);
        final Result<FunctionAPI.ExecuteFunctionOnGroupResponse> result = operationHandler.process(serializationService, request, mockedMessageExecutionContext());
    }
}

