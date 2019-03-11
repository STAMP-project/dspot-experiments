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
package org.apache.druid.indexing.overlord;


import TaskState.FAILED;
import TaskState.SUCCESS;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.TaskToolbox;
import org.apache.druid.indexing.common.actions.TaskActionClient;
import org.apache.druid.indexing.common.config.TaskConfig;
import org.apache.druid.indexing.common.task.AbstractTask;
import org.apache.druid.indexing.common.task.NoopTask;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SingleTaskBackgroundRunnerTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private SingleTaskBackgroundRunner runner;

    @Test
    public void testRun() throws InterruptedException, ExecutionException {
        Assert.assertEquals(SUCCESS, runner.run(new NoopTask(null, null, 500L, 0, null, null, null)).get().getStatusCode());
    }

    @Test
    public void testStop() throws InterruptedException, ExecutionException, TimeoutException {
        final ListenableFuture<TaskStatus> future = // infinite task
        runner.run(new NoopTask(null, null, Long.MAX_VALUE, 0, null, null, null));
        runner.stop();
        Assert.assertEquals(FAILED, future.get(1000, TimeUnit.MILLISECONDS).getStatusCode());
    }

    @Test
    public void testStopWithRestorableTask() throws InterruptedException, ExecutionException, TimeoutException {
        final SingleTaskBackgroundRunnerTest.BooleanHolder holder = new SingleTaskBackgroundRunnerTest.BooleanHolder();
        final ListenableFuture<TaskStatus> future = runner.run(new SingleTaskBackgroundRunnerTest.RestorableTask(holder));
        runner.stop();
        Assert.assertEquals(SUCCESS, future.get(1000, TimeUnit.MILLISECONDS).getStatusCode());
        Assert.assertTrue(holder.get());
    }

    private static class RestorableTask extends AbstractTask {
        private final SingleTaskBackgroundRunnerTest.BooleanHolder gracefullyStopped;

        RestorableTask(SingleTaskBackgroundRunnerTest.BooleanHolder gracefullyStopped) {
            super("testId", "testDataSource", Collections.emptyMap());
            this.gracefullyStopped = gracefullyStopped;
        }

        @Override
        public String getType() {
            return "restorable";
        }

        @Override
        public boolean isReady(TaskActionClient taskActionClient) {
            return true;
        }

        @Override
        public TaskStatus run(TaskToolbox toolbox) {
            return TaskStatus.success(getId());
        }

        @Override
        public boolean canRestore() {
            return true;
        }

        @Override
        public void stopGracefully(TaskConfig taskConfig) {
            gracefullyStopped.set();
        }
    }

    private static class BooleanHolder {
        private boolean value;

        void set() {
            this.value = true;
        }

        boolean get() {
            return value;
        }
    }
}

