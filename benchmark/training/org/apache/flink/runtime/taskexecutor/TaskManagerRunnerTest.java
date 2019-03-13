/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.taskexecutor;


import TaskManagerOptions.REGISTRATION_TIMEOUT;
import TaskManagerRunner.RUNTIME_FAILURE_RETURN_CODE;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.testutils.SystemExitTrackingSecurityManager;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Tests for the {@link TaskManagerRunner}.
 */
public class TaskManagerRunnerTest extends TestLogger {
    @Rule
    public final Timeout timeout = Timeout.seconds(30);

    @Test
    public void testShouldShutdownOnFatalError() throws Exception {
        try (TaskManagerRunner taskManagerRunner = TaskManagerRunnerTest.createTaskManagerRunner(TaskManagerRunnerTest.createConfiguration())) {
            taskManagerRunner.start();
            final SystemExitTrackingSecurityManager systemExitTrackingSecurityManager = TaskManagerRunnerTest.runWithSystemExitTracking(() -> {
                taskManagerRunner.onFatalError(new RuntimeException());
                taskManagerRunner.getTerminationFuture().get(30, TimeUnit.SECONDS);
            });
            Assert.assertThat(systemExitTrackingSecurityManager.getCount(), Matchers.is(Matchers.equalTo(1)));
            Assert.assertThat(systemExitTrackingSecurityManager.getStatus(), Matchers.is(Matchers.equalTo(RUNTIME_FAILURE_RETURN_CODE)));
        }
    }

    @Test
    public void testShouldShutdownIfRegistrationWithJobManagerFails() throws Exception {
        final Configuration configuration = TaskManagerRunnerTest.createConfiguration();
        configuration.setString(REGISTRATION_TIMEOUT, "10 ms");
        try (TaskManagerRunner taskManagerRunner = TaskManagerRunnerTest.createTaskManagerRunner(configuration)) {
            final SystemExitTrackingSecurityManager systemExitTrackingSecurityManager = TaskManagerRunnerTest.runWithSystemExitTracking(() -> {
                taskManagerRunner.start();
                taskManagerRunner.getTerminationFuture().get();
            });
            Assert.assertThat(systemExitTrackingSecurityManager.getCount(), Matchers.is(Matchers.equalTo(1)));
            Assert.assertThat(systemExitTrackingSecurityManager.getStatus(), Matchers.is(Matchers.equalTo(RUNTIME_FAILURE_RETURN_CODE)));
        }
    }
}

