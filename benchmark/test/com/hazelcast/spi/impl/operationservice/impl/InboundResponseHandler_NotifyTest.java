/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InboundResponseHandler_NotifyTest extends HazelcastTestSupport {
    private InvocationRegistry invocationRegistry;

    private OperationServiceImpl operationService;

    private InboundResponseHandler inboundResponseHandler;

    // ================== normalResponse ========================
    @Test
    public void normalResponse_whenInvocationExist() {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        Object value = "foo";
        inboundResponseHandler.notifyNormalResponse(callId, value, 0, null);
        Assert.assertEquals(value, invocation.future.join());
        assertInvocationDeregisteredEventually(callId);
    }

    @Test
    public void normalResponse_whenInvocationMissing_thenNothingBadHappens() {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        invocationRegistry.deregister(invocation);
        inboundResponseHandler.notifyNormalResponse(callId, "foo", 0, null);
        assertInvocationDeregisteredEventually(callId);
    }

    @Test
    public void normalResponse_whenBackupCompletesFirst() {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        inboundResponseHandler.notifyBackupComplete(callId);
        Assert.assertSame(invocation, invocationRegistry.get(callId));
        Object value = "foo";
        inboundResponseHandler.notifyNormalResponse(callId, value, 1, null);
        assertInvocationDeregisteredEventually(callId);
        Assert.assertEquals(value, invocation.future.join());
    }

    // todo: more permutations with different number of backups arriving in different orders.
    @Test
    public void normalResponse_whenBackupMissing_thenEventuallySuccess() throws Exception {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        final long callId = invocation.op.getCallId();
        String result = "foo";
        inboundResponseHandler.notifyNormalResponse(callId, result, 1, null);
        Assert.assertEquals(result, invocation.future.get(1, TimeUnit.MINUTES));
        assertInvocationDeregisteredEventually(callId);
    }

    // ==================== backupResponse ======================
    @Test
    public void backupResponse_whenInvocationExist() {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        Object value = "foo";
        inboundResponseHandler.notifyNormalResponse(callId, value, 1, null);
        Assert.assertSame(invocation, invocationRegistry.get(callId));
        inboundResponseHandler.notifyBackupComplete(callId);
        assertInvocationDeregisteredEventually(callId);
        Assert.assertEquals(value, invocation.future.join());
    }

    @Test
    public void backupResponse_whenInvocationMissing_thenNothingBadHappens() {
        Invocation invocation = newInvocation();
        long callId = invocation.op.getCallId();
        invocationRegistry.register(invocation);
        invocationRegistry.deregister(invocation);
        inboundResponseHandler.notifyBackupComplete(callId);
        assertInvocationDeregisteredEventually(callId);
    }

    // ==================== errorResponse ======================
    @Test
    public void errorResponse_whenInvocationExists() {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        inboundResponseHandler.notifyErrorResponse(callId, new ExpectedRuntimeException(), null);
        try {
            invocation.future.join();
            Assert.fail();
        } catch (ExpectedRuntimeException expected) {
        }
        assertInvocationDeregisteredEventually(callId);
    }

    @Test
    public void errorResponse_whenInvocationMissing_thenNothingBadHappens() {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        invocationRegistry.deregister(invocation);
        inboundResponseHandler.notifyErrorResponse(callId, new ExpectedRuntimeException(), null);
        assertInvocationDeregisteredEventually(callId);
    }

    // ==================== timeoutResponse =====================
    @Test
    public void timeoutResponse() {
        Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        inboundResponseHandler.notifyCallTimeout(callId, null);
        try {
            Assert.assertNull(invocation.future.join());
            Assert.fail();
        } catch (OperationTimeoutException expected) {
        }
        assertInvocationDeregisteredEventually(callId);
    }
}

