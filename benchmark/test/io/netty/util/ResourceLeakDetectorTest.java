/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util;


import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;


public class ResourceLeakDetectorTest {
    @Test(timeout = 60000)
    public void testConcurrentUsage() throws Throwable {
        final AtomicBoolean finished = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        // With 50 threads issue #6087 is reproducible on every run.
        Thread[] threads = new Thread[50];
        final CyclicBarrier barrier = new CyclicBarrier(threads.length);
        for (int i = 0; i < (threads.length); i++) {
            Thread t = new Thread(new Runnable() {
                Queue<ResourceLeakDetectorTest.LeakAwareResource> resources = new ArrayDeque<ResourceLeakDetectorTest.LeakAwareResource>(100);

                @Override
                public void run() {
                    try {
                        barrier.await();
                        // Run 10000 times or until the test is marked as finished.
                        for (int b = 0; (b < 1000) && (!(finished.get())); b++) {
                            // Allocate 100 LeakAwareResource per run and close them after it.
                            for (int a = 0; a < 100; a++) {
                                ResourceLeakDetectorTest.DefaultResource resource = new ResourceLeakDetectorTest.DefaultResource();
                                ResourceLeakTracker<ResourceLeakDetectorTest.Resource> leak = ResourceLeakDetectorTest.DefaultResource.detector.track(resource);
                                ResourceLeakDetectorTest.LeakAwareResource leakAwareResource = new ResourceLeakDetectorTest.LeakAwareResource(resource, leak);
                                resources.add(leakAwareResource);
                            }
                            if (closeResources(true)) {
                                finished.set(true);
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Throwable e) {
                        error.compareAndSet(null, e);
                    } finally {
                        // Just close all resource now without assert it to eliminate more reports.
                        closeResources(false);
                    }
                }

                private boolean closeResources(boolean checkClosed) {
                    for (; ;) {
                        ResourceLeakDetectorTest.LeakAwareResource r = resources.poll();
                        if (r == null) {
                            return false;
                        }
                        boolean closed = r.close();
                        if (checkClosed && (!closed)) {
                            error.compareAndSet(null, new AssertionError("ResourceLeak.close() returned 'false' but expected 'true'"));
                            return true;
                        }
                    }
                }
            });
            threads[i] = t;
            t.start();
        }
        // Just wait until all threads are done.
        for (Thread t : threads) {
            t.join();
        }
        // Check if we had any leak reports in the ResourceLeakDetector itself
        ResourceLeakDetectorTest.DefaultResource.detector.assertNoErrors();
        ResourceLeakDetectorTest.assertNoErrors(error);
    }

    // Mimic the way how we implement our classes that should help with leak detection
    private static final class LeakAwareResource implements ResourceLeakDetectorTest.Resource {
        private final ResourceLeakDetectorTest.Resource resource;

        private final ResourceLeakTracker<ResourceLeakDetectorTest.Resource> leak;

        LeakAwareResource(ResourceLeakDetectorTest.Resource resource, ResourceLeakTracker<ResourceLeakDetectorTest.Resource> leak) {
            this.resource = resource;
            this.leak = leak;
        }

        @Override
        public boolean close() {
            // Using ResourceLeakDetector.close(...) to prove this fixes the leak problem reported
            // in https://github.com/netty/netty/issues/6034 .
            // 
            // The following implementation would produce a leak:
            // return leak.close();
            return leak.close(resource);
        }
    }

    private static final class DefaultResource implements ResourceLeakDetectorTest.Resource {
        // Sample every allocation
        static final ResourceLeakDetectorTest.TestResourceLeakDetector<ResourceLeakDetectorTest.Resource> detector = new ResourceLeakDetectorTest.TestResourceLeakDetector<ResourceLeakDetectorTest.Resource>(ResourceLeakDetectorTest.Resource.class, 1, Integer.MAX_VALUE);

        @Override
        public boolean close() {
            return true;
        }
    }

    private interface Resource {
        boolean close();
    }

    private static final class TestResourceLeakDetector<T> extends ResourceLeakDetector<T> {
        private final AtomicReference<Throwable> error = new AtomicReference<Throwable>();

        TestResourceLeakDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
            super(resourceType, samplingInterval, maxActive);
        }

        @Override
        protected void reportTracedLeak(String resourceType, String records) {
            reportError(new AssertionError(((("Leak reported for '" + resourceType) + "\':\n") + records)));
        }

        @Override
        protected void reportUntracedLeak(String resourceType) {
            reportError(new AssertionError((("Leak reported for '" + resourceType) + '\'')));
        }

        @Override
        protected void reportInstancesLeak(String resourceType) {
            reportError(new AssertionError((("Leak reported for '" + resourceType) + '\'')));
        }

        private void reportError(AssertionError cause) {
            error.compareAndSet(null, cause);
        }

        void assertNoErrors() throws Throwable {
            ResourceLeakDetectorTest.assertNoErrors(error);
        }
    }
}

