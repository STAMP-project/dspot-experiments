/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.util;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.drill.exec.util.concurrent.ExecutorServiceUtil;
import org.apache.drill.test.DrillTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for validating the Drill executor service utility class
 */
public final class ExecutorServiceUtilTest extends DrillTest {
    @Test
    public void testSuccessfulExecution() throws Exception {
        final int numThreads = 2;
        final int numTasks = 20;
        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        List<ExecutorServiceUtilTest.RequestContainer> requests = new ArrayList<>(numTasks);
        // Set the test parameters (using the default values)
        ExecutorServiceUtilTest.TestParams params = new ExecutorServiceUtilTest.TestParams();
        // Launch the tasks
        for (int idx = 0; idx < numTasks; idx++) {
            ExecutorServiceUtilTest.CallableTask task = new ExecutorServiceUtilTest.CallableTask(params);
            Future<ExecutorServiceUtilTest.TaskResult> future = ExecutorServiceUtil.submit(service, task);
            requests.add(new ExecutorServiceUtilTest.RequestContainer(future, task));
        }
        int numSuccess = 0;
        // Wait for the tasks to finish
        for (int idx = 0; idx < numTasks; idx++) {
            ExecutorServiceUtilTest.RequestContainer request = requests.get(idx);
            try {
                ExecutorServiceUtilTest.TaskResult result = request.future.get();
                Assert.assertNotNull(result);
                if (result.isSuccess()) {
                    ++numSuccess;
                }
            } catch (Exception e) {
                // NOOP
            }
        }
        Assert.assertEquals(numTasks, numSuccess);
    }

    @Test
    public void testFailedExecution() throws Exception {
        final int numThreads = 2;
        final int numTasks = 20;
        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        List<ExecutorServiceUtilTest.RequestContainer> requests = new ArrayList<>(numTasks);
        // Set the test parameters
        ExecutorServiceUtilTest.TestParams params = new ExecutorServiceUtilTest.TestParams();
        params.generateException = true;
        // Launch the tasks
        for (int idx = 0; idx < numTasks; idx++) {
            ExecutorServiceUtilTest.CallableTask task = new ExecutorServiceUtilTest.CallableTask(params);
            Future<ExecutorServiceUtilTest.TaskResult> future = ExecutorServiceUtil.submit(service, task);
            requests.add(new ExecutorServiceUtilTest.RequestContainer(future, task));
        }
        int numSuccess = 0;
        int numFailures = 0;
        // Wait for the tasks to finish
        for (int idx = 0; idx < numTasks; idx++) {
            ExecutorServiceUtilTest.RequestContainer request = requests.get(idx);
            try {
                ExecutorServiceUtilTest.TaskResult result = request.future.get();
                Assert.assertNotNull(result);
                if (result.isSuccess()) {
                    ++numSuccess;
                }
            } catch (Exception e) {
                Assert.assertTrue(request.task.result.isFailed());
                ++numFailures;
            }
        }
        Assert.assertEquals(0, numSuccess);
        Assert.assertEquals(numTasks, numFailures);
    }

    @Test
    public void testMixedExecution() throws Exception {
        final int numThreads = 2;
        final int numTasks = 20;
        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        List<ExecutorServiceUtilTest.RequestContainer> requests = new ArrayList<>(numTasks);
        // Set the test parameters
        ExecutorServiceUtilTest.TestParams successParams = new ExecutorServiceUtilTest.TestParams();
        ExecutorServiceUtilTest.TestParams failedParams = new ExecutorServiceUtilTest.TestParams();
        failedParams.generateException = true;
        int expNumFailedTasks = 0;
        int expNumSuccessTasks = 0;
        // Launch the tasks
        for (int idx = 0; idx < numTasks; idx++) {
            ExecutorServiceUtilTest.CallableTask task = null;
            if ((idx % 2) == 0) {
                task = new ExecutorServiceUtilTest.CallableTask(successParams);
                ++expNumSuccessTasks;
            } else {
                task = new ExecutorServiceUtilTest.CallableTask(failedParams);
                ++expNumFailedTasks;
            }
            Future<ExecutorServiceUtilTest.TaskResult> future = ExecutorServiceUtil.submit(service, task);
            requests.add(new ExecutorServiceUtilTest.RequestContainer(future, task));
        }
        int numSuccess = 0;
        int numFailures = 0;
        // Wait for the tasks to finish
        for (int idx = 0; idx < numTasks; idx++) {
            ExecutorServiceUtilTest.RequestContainer request = requests.get(idx);
            try {
                ExecutorServiceUtilTest.TaskResult result = request.future.get();
                Assert.assertNotNull(result);
                if (result.isSuccess()) {
                    ++numSuccess;
                }
            } catch (Exception e) {
                Assert.assertTrue(request.task.result.isFailed());
                ++numFailures;
            }
        }
        Assert.assertEquals(expNumSuccessTasks, numSuccess);
        Assert.assertEquals(expNumFailedTasks, numFailures);
    }

    @Test
    public void testCancelExecution() throws Exception {
        final int numThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        ExecutorServiceUtilTest.RequestContainer request = null;
        // Set the test parameters
        ExecutorServiceUtilTest.TestParams params = new ExecutorServiceUtilTest.TestParams();
        params.controller = new ExecutorServiceUtilTest.TaskExecutionController();
        // Launch the task
        ExecutorServiceUtilTest.CallableTask task = new ExecutorServiceUtilTest.CallableTask(params);
        Future<ExecutorServiceUtilTest.TaskResult> future = ExecutorServiceUtil.submit(service, task);
        request = new ExecutorServiceUtilTest.RequestContainer(future, task);
        // Allow the task to start
        params.controller.start();
        params.controller.hasStarted();
        // Allow the task to exit but with a delay so that we can test the blocking nature of "cancel"
        params.controller.delayMillisOnExit = 50;
        params.controller.exit();
        // Cancel the task
        boolean result = request.future.cancel(true);
        if (result) {
            // We were able to cancel this task; let's make sure that it is done now that the current thread is
            // unblocked
            Assert.assertTrue(task.result.isCancelled());
        } else {
            // Cancellation could't happen most probably because this thread got context switched for
            // for a long time (should be rare); let's make sure the task is done and successful
            Assert.assertTrue(task.result.isSuccess());
        }
    }

    // ----------------------------------------------------------------------------
    // Internal Classes
    // ----------------------------------------------------------------------------
    @SuppressWarnings("unused")
    private static final class TaskResult {
        private enum ExecutionStatus {

            NOT_STARTED,
            RUNNING,
            SUCCEEDED,
            FAILED,
            CANCELLED;}

        private ExecutorServiceUtilTest.TaskResult.ExecutionStatus status;

        TaskResult() {
            status = ExecutorServiceUtilTest.TaskResult.ExecutionStatus.NOT_STARTED;
        }

        private boolean isSuccess() {
            return status.equals(ExecutorServiceUtilTest.TaskResult.ExecutionStatus.SUCCEEDED);
        }

        private boolean isFailed() {
            return status.equals(ExecutorServiceUtilTest.TaskResult.ExecutionStatus.FAILED);
        }

        private boolean isCancelled() {
            return status.equals(ExecutorServiceUtilTest.TaskResult.ExecutionStatus.CANCELLED);
        }

        private boolean isFailedOrCancelled() {
            return (status.equals(ExecutorServiceUtilTest.TaskResult.ExecutionStatus.CANCELLED)) || (status.equals(ExecutorServiceUtilTest.TaskResult.ExecutionStatus.FAILED));
        }
    }

    @SuppressWarnings("unused")
    private static final class TaskExecutionController {
        private boolean canStart = false;

        private boolean canExit = false;

        private boolean started = false;

        private boolean exited = false;

        private int delayMillisOnExit = 0;

        private Object monitor = new Object();

        private void canStart() {
            synchronized(monitor) {
                while (!(canStart)) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ie) {
                        // NOOP
                    }
                } 
                started = true;
                monitor.notify();
            }
        }

        private void canExit() {
            synchronized(monitor) {
                while (!(canExit)) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ie) {
                        // NOOP
                    }
                } 
            }
            // Wait requested delay time before exiting
            for (int i = 0; i < (delayMillisOnExit); i++) {
                try {
                    Thread.sleep(1);// sleep 1 ms

                } catch (InterruptedException ie) {
                    // NOOP
                }
            }
            synchronized(monitor) {
                exited = true;
                monitor.notify();
            }
        }

        private void start() {
            synchronized(monitor) {
                canStart = true;
                monitor.notify();
            }
        }

        private void exit() {
            synchronized(monitor) {
                canExit = true;
                monitor.notify();
            }
        }

        private void hasStarted() {
            synchronized(monitor) {
                while (!(started)) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ie) {
                        // NOOP
                    }
                } 
            }
        }

        private void hasExited() {
            synchronized(monitor) {
                while (!(exited)) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ie) {
                        // NOOP
                    }
                } 
            }
        }
    }

    private static final class TestParams {
        private int waitTimeMillis = 2;

        private boolean generateException = false;

        private ExecutorServiceUtilTest.TaskExecutionController controller = null;
    }

    private static final class CallableTask implements Callable<ExecutorServiceUtilTest.TaskResult> {
        private volatile ExecutorServiceUtilTest.TaskResult result = new ExecutorServiceUtilTest.TaskResult();

        private final ExecutorServiceUtilTest.TestParams params;

        private CallableTask(ExecutorServiceUtilTest.TestParams params) {
            this.params = params;
        }

        @Override
        public ExecutorServiceUtilTest.TaskResult call() throws Exception {
            beforeStart();
            result.status = ExecutorServiceUtilTest.TaskResult.ExecutionStatus.RUNNING;
            boolean interrupted = false;
            Exception exc = null;
            try {
                for (int i = 0; i < (params.waitTimeMillis); i++) {
                    try {
                        Thread.sleep(1);// sleep 1 ms

                    } catch (InterruptedException ie) {
                        interrupted = true;
                    }
                }
                if (params.generateException) {
                    throw new RuntimeException("Test emulated exception..");
                }
            } catch (Exception e) {
                exc = e;
                throw e;
            } finally {
                beforeExit();
                if (interrupted) {
                    result.status = ExecutorServiceUtilTest.TaskResult.ExecutionStatus.CANCELLED;
                } else
                    if (exc != null) {
                        result.status = ExecutorServiceUtilTest.TaskResult.ExecutionStatus.FAILED;
                    } else {
                        result.status = ExecutorServiceUtilTest.TaskResult.ExecutionStatus.SUCCEEDED;
                    }

            }
            return result;
        }

        private void beforeStart() {
            if ((params.controller) != null) {
                params.controller.canStart();
            }
        }

        private void beforeExit() {
            if ((params.controller) != null) {
                params.controller.canExit();
            }
        }
    }

    private static final class RequestContainer {
        private final Future<ExecutorServiceUtilTest.TaskResult> future;

        private final ExecutorServiceUtilTest.CallableTask task;

        private RequestContainer(Future<ExecutorServiceUtilTest.TaskResult> future, ExecutorServiceUtilTest.CallableTask task) {
            this.future = future;
            this.task = task;
        }
    }
}

