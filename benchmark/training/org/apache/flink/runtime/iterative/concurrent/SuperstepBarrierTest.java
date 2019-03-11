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
package org.apache.flink.runtime.iterative.concurrent;


import java.util.Random;
import org.apache.flink.runtime.event.TaskEvent;
import org.apache.flink.runtime.iterative.event.AllWorkersDoneEvent;
import org.apache.flink.runtime.iterative.event.TerminationEvent;
import org.junit.Test;


/**
 * Tests for {@link SuperstepBarrier}.
 */
public class SuperstepBarrierTest {
    @Test
    public void syncAllWorkersDone() throws InterruptedException {
        for (int n = 0; n < 20; n++) {
            sync(new AllWorkersDoneEvent());
        }
    }

    @Test
    public void syncTermination() throws InterruptedException {
        for (int n = 0; n < 20; n++) {
            sync(new TerminationEvent());
        }
    }

    class IterationHead implements Runnable {
        private final SuperstepBarrier barrier;

        private final SuperstepBarrierTest.TerminationSignaled terminationSignaled;

        private final Random random;

        IterationHead(SuperstepBarrier barrier, SuperstepBarrierTest.TerminationSignaled terminationSignaled) {
            this.barrier = barrier;
            this.terminationSignaled = terminationSignaled;
            random = new Random();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(random.nextInt(10));
                barrier.waitForOtherWorkers();
                if (barrier.terminationSignaled()) {
                    terminationSignaled.setTerminationSignaled();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class IterationSync implements Runnable {
        private final SuperstepBarrier barrier;

        private final TaskEvent event;

        private final Random random;

        IterationSync(SuperstepBarrier barrier, TaskEvent event) {
            this.barrier = barrier;
            this.event = event;
            random = new Random();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(random.nextInt(10));
                barrier.onEvent(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class TerminationSignaled {
        private volatile boolean terminationSignaled;

        public boolean isTerminationSignaled() {
            return terminationSignaled;
        }

        public void setTerminationSignaled() {
            terminationSignaled = true;
        }
    }
}

