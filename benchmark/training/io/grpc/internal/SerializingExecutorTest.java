/**
 * Copyright 2017 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SerializingExecutorTest {
    private SerializingExecutorTest.SingleExecutor singleExecutor = new SerializingExecutorTest.SingleExecutor();

    private SerializingExecutor executor = new SerializingExecutor(singleExecutor);

    private List<Integer> runs = new ArrayList<>();

    private class AddToRuns implements Runnable {
        private final int val;

        public AddToRuns(int val) {
            this.val = val;
        }

        @Override
        public void run() {
            runs.add(val);
        }
    }

    @Test
    public void resumable() {
        class CoyExecutor implements Executor {
            int runCount;

            @Override
            public void execute(Runnable command) {
                (runCount)++;
                if ((runCount) == 1) {
                    throw new RuntimeException();
                }
                command.run();
            }
        }
        executor = new SerializingExecutor(new CoyExecutor());
        try {
            executor.execute(new SerializingExecutorTest.AddToRuns(1));
            Assert.fail();
        } catch (RuntimeException expected) {
        }
        // Ensure that the runnable enqueued was actually removed on the failed execute above.
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        assertThat(runs).containsExactly(2);
    }

    @Test
    public void serial() {
        executor.execute(new SerializingExecutorTest.AddToRuns(1));
        Assert.assertEquals(Collections.<Integer>emptyList(), runs);
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(1), runs);
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        Assert.assertEquals(Arrays.asList(1), runs);
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(1, 2), runs);
    }

    @Test
    public void parallel() {
        executor.execute(new SerializingExecutorTest.AddToRuns(1));
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        executor.execute(new SerializingExecutorTest.AddToRuns(3));
        Assert.assertEquals(Collections.<Integer>emptyList(), runs);
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(1, 2, 3), runs);
    }

    @Test
    public void reentrant() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                executor.execute(new SerializingExecutorTest.AddToRuns(3));
                runs.add(1);
            }
        });
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(1, 2, 3), runs);
    }

    @Test
    public void testFirstRunnableThrows() {
        final RuntimeException ex = new RuntimeException();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                runs.add(1);
                throw ex;
            }
        });
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        executor.execute(new SerializingExecutorTest.AddToRuns(3));
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(1, 2, 3), runs);
    }

    @Test
    public void lastRunnableThrows() {
        final RuntimeException ex = new RuntimeException();
        executor.execute(new SerializingExecutorTest.AddToRuns(1));
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        executor.execute(new Runnable() {
            @Override
            public void run() {
                runs.add(3);
                throw ex;
            }
        });
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(1, 2, 3), runs);
        // Scheduling more still works
        executor.execute(new SerializingExecutorTest.AddToRuns(4));
        Assert.assertEquals(Arrays.asList(1, 2, 3), runs);
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), runs);
    }

    @Test
    public void firstExecuteThrows() {
        final RuntimeException ex = new RuntimeException();
        SerializingExecutorTest.ForwardingExecutor forwardingExecutor = new SerializingExecutorTest.ForwardingExecutor(new Executor() {
            @Override
            public void execute(Runnable r) {
                throw ex;
            }
        });
        executor = new SerializingExecutor(forwardingExecutor);
        try {
            executor.execute(new SerializingExecutorTest.AddToRuns(1));
            Assert.fail("expected exception");
        } catch (RuntimeException e) {
            Assert.assertSame(ex, e);
        }
        Assert.assertEquals(Collections.<Integer>emptyList(), runs);
        forwardingExecutor.executor = singleExecutor;
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        executor.execute(new SerializingExecutorTest.AddToRuns(3));
        Assert.assertEquals(Collections.<Integer>emptyList(), runs);
        singleExecutor.drain();
        Assert.assertEquals(Arrays.asList(2, 3), runs);
    }

    @Test
    public void direct() {
        executor = new SerializingExecutor(MoreExecutors.directExecutor());
        executor.execute(new SerializingExecutorTest.AddToRuns(1));
        Assert.assertEquals(Arrays.asList(1), runs);
        executor.execute(new SerializingExecutorTest.AddToRuns(2));
        Assert.assertEquals(Arrays.asList(1, 2), runs);
        executor.execute(new SerializingExecutorTest.AddToRuns(3));
        Assert.assertEquals(Arrays.asList(1, 2, 3), runs);
    }

    @Test
    public void testDirectReentrant() {
        executor = new SerializingExecutor(MoreExecutors.directExecutor());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                executor.execute(new SerializingExecutorTest.AddToRuns(2));
                runs.add(1);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2), runs);
        executor.execute(new SerializingExecutorTest.AddToRuns(3));
        Assert.assertEquals(Arrays.asList(1, 2, 3), runs);
    }

    private static class SingleExecutor implements Executor {
        private Runnable runnable;

        @Override
        public void execute(Runnable r) {
            if ((runnable) != null) {
                Assert.fail("Already have runnable scheduled");
            }
            runnable = r;
        }

        public void drain() {
            if ((runnable) != null) {
                Runnable r = runnable;
                runnable = null;
                r.run();
            }
        }
    }

    private static class ForwardingExecutor implements Executor {
        Executor executor;

        public ForwardingExecutor(Executor executor) {
            this.executor = executor;
        }

        @Override
        public void execute(Runnable r) {
            executor.execute(r);
        }
    }
}

