/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flink.runtime.taskexecutor;


import RegistrationResponse.Success;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.instance.HardwareDescription;
import org.apache.flink.runtime.registration.RegisteredRpcConnection;
import org.apache.flink.runtime.registration.RegistrationConnectionListener;
import org.apache.flink.runtime.registration.RegistrationResponse;
import org.apache.flink.runtime.resourcemanager.ResourceManagerId;
import org.apache.flink.runtime.resourcemanager.utils.TestingResourceManagerGateway;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@link TaskExecutorToResourceManagerConnection}.
 */
public class TaskExecutorToResourceManagerConnectionTest extends TestLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutorToResourceManagerConnectionTest.class);

    private static final int TEST_TIMEOUT_MILLIS = 10000;

    private static final String RESOURCE_MANAGER_ADDRESS = "localhost";

    private static final ResourceManagerId RESOURCE_MANAGER_ID = ResourceManagerId.generate();

    private static final String TASK_MANAGER_ADDRESS = "localhost";

    private static final ResourceID TASK_MANAGER_RESOURCE_ID = ResourceID.generate();

    private static final int TASK_MANAGER_DATA_PORT = 12345;

    private static final HardwareDescription TASK_MANAGER_HARDWARE_DESCRIPTION = HardwareDescription.extractFromSystem(Long.MAX_VALUE);

    private TestingRpcService rpcService;

    private TestingResourceManagerGateway testingResourceManagerGateway;

    private CompletableFuture<Void> registrationSuccessFuture;

    @Test
    public void testResourceManagerRegistration() throws Exception {
        final TaskExecutorToResourceManagerConnection resourceManagerRegistration = createTaskExecutorToResourceManagerConnection();
        testingResourceManagerGateway.setRegisterTaskExecutorFunction(( tuple) -> {
            final String actualAddress = tuple.f0;
            final ResourceID actualResourceId = tuple.f1;
            final Integer actualDataPort = tuple.f2;
            final HardwareDescription actualHardwareDescription = tuple.f3;
            Assert.assertThat(actualAddress, Matchers.is(Matchers.equalTo(TaskExecutorToResourceManagerConnectionTest.TASK_MANAGER_ADDRESS)));
            Assert.assertThat(actualResourceId, Matchers.is(Matchers.equalTo(TaskExecutorToResourceManagerConnectionTest.TASK_MANAGER_RESOURCE_ID)));
            Assert.assertThat(actualDataPort, Matchers.is(Matchers.equalTo(TaskExecutorToResourceManagerConnectionTest.TASK_MANAGER_DATA_PORT)));
            Assert.assertThat(actualHardwareDescription, Matchers.is(Matchers.equalTo(TaskExecutorToResourceManagerConnectionTest.TASK_MANAGER_HARDWARE_DESCRIPTION)));
            return CompletableFuture.completedFuture(TaskExecutorToResourceManagerConnectionTest.successfulRegistration());
        });
        resourceManagerRegistration.start();
        registrationSuccessFuture.get(TaskExecutorToResourceManagerConnectionTest.TEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    private class TestRegistrationConnectionListener<T extends RegisteredRpcConnection<?, ?, S>, S extends Success> implements RegistrationConnectionListener<T, S> {
        @Override
        public void onRegistrationSuccess(final T connection, final S success) {
            registrationSuccessFuture.complete(null);
        }

        @Override
        public void onRegistrationFailure(final Throwable failure) {
            registrationSuccessFuture.completeExceptionally(failure);
        }
    }
}

