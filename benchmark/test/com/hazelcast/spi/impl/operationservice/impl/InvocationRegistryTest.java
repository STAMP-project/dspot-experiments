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


import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.Operation;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class InvocationRegistryTest extends HazelcastTestSupport {
    private InvocationRegistry invocationRegistry;

    private ILogger logger;

    // ====================== register ===============================
    @Test
    public void register_Invocation() {
        Operation op = new DummyBackupAwareOperation();
        Invocation invocation = newInvocation(op);
        long oldCallId = invocationRegistry.getLastCallId();
        invocationRegistry.register(invocation);
        Assert.assertEquals((oldCallId + 1), op.getCallId());
        Assert.assertSame(invocation, invocationRegistry.get(op.getCallId()));
    }

    @Test
    public void register_whenAlreadyRegistered_thenException() {
        Operation op = new DummyBackupAwareOperation();
        Invocation invocation = newInvocation(op);
        invocationRegistry.register(invocation);
        final long originalCallId = invocationRegistry.getLastCallId();
        for (int i = 0; i < 10; i++) {
            try {
                invocationRegistry.register(invocation);
                Assert.fail();
            } catch (IllegalStateException e) {
                // expected
            }
            Assert.assertSame(invocation, invocationRegistry.get(originalCallId));
            Assert.assertEquals(originalCallId, invocation.op.getCallId());
        }
    }

    // ====================== deregister ===============================
    @Test
    public void deregister_whenAlreadyDeregistered_thenIgnored() {
        Operation op = new DummyBackupAwareOperation();
        Invocation invocation = newInvocation(op);
        invocationRegistry.register(invocation);
        long callId = op.getCallId();
        invocationRegistry.deregister(invocation);
        invocationRegistry.deregister(invocation);
        Assert.assertNull(invocationRegistry.get(callId));
    }

    @Test
    public void deregister_whenSkipped() {
        Operation op = new DummyOperation();
        Invocation invocation = newInvocation(op);
        invocationRegistry.register(invocation);
        invocationRegistry.deregister(invocation);
        Assert.assertFalse(invocation.isActive());
    }

    @Test
    public void deregister_whenRegistered_thenRemoved() {
        Operation op = new DummyBackupAwareOperation();
        Invocation invocation = newInvocation(op);
        invocationRegistry.register(invocation);
        long callId = op.getCallId();
        invocationRegistry.deregister(invocation);
        Assert.assertNull(invocationRegistry.get(callId));
    }

    // ====================== size ===============================
    @Test
    public void test_size() {
        Assert.assertEquals(0, invocationRegistry.size());
        Invocation firstInvocation = newInvocation();
        invocationRegistry.register(firstInvocation);
        Assert.assertEquals(1, invocationRegistry.size());
        Invocation secondInvocation = newInvocation();
        invocationRegistry.register(secondInvocation);
        Assert.assertEquals(2, invocationRegistry.size());
    }

    // ===================== reset ============================
    @Test
    public void reset_thenAllInvocationsMemberLeftException() throws InterruptedException, ExecutionException {
        Invocation invocation = newInvocation(new DummyBackupAwareOperation());
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        invocationRegistry.reset(null);
        InvocationFuture f = invocation.future;
        try {
            f.get();
            Assert.fail();
        } catch (MemberLeftException expected) {
        }
        Assert.assertNull(invocationRegistry.get(callId));
    }

    // ===================== shutdown ============================
    @Test
    public void shutdown_thenAllInvocationsAborted() throws InterruptedException, ExecutionException {
        Invocation invocation = newInvocation(new DummyBackupAwareOperation());
        invocationRegistry.register(invocation);
        long callId = invocation.op.getCallId();
        invocationRegistry.shutdown();
        InvocationFuture f = invocation.future;
        try {
            f.join();
            Assert.fail();
        } catch (HazelcastInstanceNotActiveException expected) {
        }
        Assert.assertNull(invocationRegistry.get(callId));
    }
}

