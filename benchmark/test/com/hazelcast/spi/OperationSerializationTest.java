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
package com.hazelcast.spi;


import Operation.BITMASK_SERVICE_NAME_SET;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class OperationSerializationTest extends HazelcastTestSupport {
    private static final String DUMMY_SERVICE_NAME = "foobar";

    private InternalSerializationService serializationService;

    @Test
    public void test_partitionId() {
        test_partitionId(0, false);
        test_partitionId(100, false);
        test_partitionId((-1), false);
        test_partitionId(Short.MAX_VALUE, false);
        test_partitionId(((Short.MAX_VALUE) + 1), true);
        test_partitionId(Integer.MAX_VALUE, true);
    }

    @Test
    public void test_replicaIndex() {
        test_replicaIndex(0, false);
        test_replicaIndex(1, true);
        test_replicaIndex(3, true);
    }

    @Test
    public void test_callTimeout() {
        test_callTimeout(0, false);
        test_callTimeout(100, false);
        test_callTimeout((-1), false);
        test_callTimeout(Integer.MAX_VALUE, false);
        test_callTimeout(((Integer.MAX_VALUE) + 1L), true);
        test_callTimeout(Long.MAX_VALUE, true);
    }

    @Test
    public void test_callId() {
        Operation op = new OperationSerializationTest.DummyOperation();
        op.setCallId(10000);
        Assert.assertEquals(10000, op.getCallId());
        assertSerializationCloneEquals(op);
    }

    @Test
    public void test_invocationTime() {
        Operation op = new OperationSerializationTest.DummyOperation();
        op.setInvocationTime(10000);
        Assert.assertEquals(10000, op.getInvocationTime());
        assertSerializationCloneEquals(op);
    }

    @Test
    public void test_waitTimeout() {
        test_waitTimeout((-1), false);
        test_waitTimeout(0, true);
        test_waitTimeout(1, true);
    }

    @Test
    public void test_callerUuid() {
        test_callerUuid(null, false);
        test_callerUuid("", true);
        test_callerUuid("foofbar", true);
    }

    @Test
    public void test_validateTarget_defaultValue() {
        Operation op = new OperationSerializationTest.DummyOperation();
        Assert.assertTrue("Default value of validate target should be TRUE", op.validatesTarget());
        assertSerializationCloneEquals(op);
    }

    @Test
    public void test_serviceName_whenOverridesGetServiceName_thenNotSerialized() {
        OperationSerializationTest.OperationWithServiceNameOverride op = new OperationSerializationTest.OperationWithServiceNameOverride();
        Assert.assertNull(getRawServiceName());
        Assert.assertFalse("service name should not be set", op.isFlagSet(BITMASK_SERVICE_NAME_SET));
        Operation copy = copy(op);
        Assert.assertSame(OperationSerializationTest.DUMMY_SERVICE_NAME, copy.getServiceName());
        Assert.assertNull(copy.getRawServiceName());
        Assert.assertFalse("service name should not be set", copy.isFlagSet(BITMASK_SERVICE_NAME_SET));
    }

    @Test
    public void test_serviceName_whenNotOverridesServiceName_thenSerialized() {
        OperationSerializationTest.DummyOperation op = new OperationSerializationTest.DummyOperation();
        setServiceName(OperationSerializationTest.DUMMY_SERVICE_NAME);
        Assert.assertSame(OperationSerializationTest.DUMMY_SERVICE_NAME, getRawServiceName());
        Assert.assertSame(OperationSerializationTest.DUMMY_SERVICE_NAME, getServiceName());
        Assert.assertTrue("service name should be set", op.isFlagSet(BITMASK_SERVICE_NAME_SET));
        Operation copy = copy(op);
        OperationSerializationTest.assertCopy(OperationSerializationTest.DUMMY_SERVICE_NAME, copy.getServiceName());
        OperationSerializationTest.assertCopy(OperationSerializationTest.DUMMY_SERVICE_NAME, copy.getRawServiceName());
        Assert.assertTrue("service name should be set", copy.isFlagSet(BITMASK_SERVICE_NAME_SET));
    }

    @Test
    public void test_serviceName_whenOverridesGetServiceNameAndRequiresExplicitServiceName_thenSerialized() {
        OperationSerializationTest.OperationWithExplicitServiceNameAndOverride op = new OperationSerializationTest.OperationWithExplicitServiceNameAndOverride();
        Assert.assertNull(getRawServiceName());
        Assert.assertFalse("service name should not be set", op.isFlagSet(BITMASK_SERVICE_NAME_SET));
        Operation copy = copy(op);
        Assert.assertEquals(OperationSerializationTest.DUMMY_SERVICE_NAME, copy.getRawServiceName());
        Assert.assertTrue("service name should be set", copy.isFlagSet(BITMASK_SERVICE_NAME_SET));
    }

    private static class DummyOperation extends Operation {
        public DummyOperation() {
        }

        @Override
        public void run() throws Exception {
        }
    }

    private static class OperationWithServiceNameOverride extends Operation {
        public OperationWithServiceNameOverride() {
        }

        @Override
        public void run() throws Exception {
        }

        @Override
        public String getServiceName() {
            return OperationSerializationTest.DUMMY_SERVICE_NAME;
        }
    }

    private static class OperationWithExplicitServiceNameAndOverride extends Operation {
        public OperationWithExplicitServiceNameAndOverride() {
        }

        @Override
        public void run() throws Exception {
        }

        @Override
        protected boolean requiresExplicitServiceName() {
            return true;
        }

        @Override
        public String getServiceName() {
            return OperationSerializationTest.DUMMY_SERVICE_NAME;
        }
    }
}

