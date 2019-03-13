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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_CallTimeoutTestMillis extends HazelcastTestSupport {
    private static final long CALL_TIMEOUT = 12345;

    private HazelcastInstance hz;

    private InternalOperationService opService;

    private Address thisAddress;

    @Test
    public void callTimeout_whenDefaults() {
        Operation op = new DummyOperation();
        InvocationFuture future = ((InvocationFuture) (opService.invokeOnTarget(null, op, thisAddress)));
        Assert.assertEquals(Invocation_CallTimeoutTestMillis.CALL_TIMEOUT, future.invocation.callTimeoutMillis);
        Assert.assertEquals(Invocation_CallTimeoutTestMillis.CALL_TIMEOUT, future.invocation.op.getCallTimeout());
    }

    @Test
    public void callTimeout_whenExplicitlySet_andUsingBuilder() {
        Operation op = new DummyOperation();
        int explicitCallTimeout = 12;
        InvocationFuture future = ((InvocationFuture) (opService.createInvocationBuilder(null, op, thisAddress).setCallTimeout(explicitCallTimeout).invoke()));
        Assert.assertEquals(explicitCallTimeout, future.invocation.callTimeoutMillis);
        Assert.assertEquals(explicitCallTimeout, future.invocation.op.getCallTimeout());
    }
}

