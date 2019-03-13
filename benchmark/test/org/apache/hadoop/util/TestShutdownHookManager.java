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
package org.apache.hadoop.util;


import ShutdownHookManager.HookEntry;
import ShutdownHookManager.TIMEOUT_MINIMUM;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestShutdownHookManager {
    static final Logger LOG = LoggerFactory.getLogger(TestShutdownHookManager.class.getName());

    /**
     * Verify hook registration, then execute the hook callback stage
     * of shutdown to verify invocation, execution order and timeout
     * processing.
     */
    @Test
    public void shutdownHookManager() {
        ShutdownHookManager mgr = ShutdownHookManager.get();
        Assert.assertNotNull("No ShutdownHookManager", mgr);
        Assert.assertEquals(0, mgr.getShutdownHooksInOrder().size());
        TestShutdownHookManager.Hook hook1 = new TestShutdownHookManager.Hook("hook1", 0, false);
        TestShutdownHookManager.Hook hook2 = new TestShutdownHookManager.Hook("hook2", 0, false);
        TestShutdownHookManager.Hook hook3 = new TestShutdownHookManager.Hook("hook3", 1000, false);
        TestShutdownHookManager.Hook hook4 = new TestShutdownHookManager.Hook("hook4", 25000, true);
        TestShutdownHookManager.Hook hook5 = new TestShutdownHookManager.Hook("hook5", (((CommonConfigurationKeysPublic.SERVICE_SHUTDOWN_TIMEOUT_DEFAULT) + 1) * 1000), true);
        mgr.addShutdownHook(hook1, 0);
        Assert.assertTrue(mgr.hasShutdownHook(hook1));
        Assert.assertEquals(1, mgr.getShutdownHooksInOrder().size());
        Assert.assertEquals(hook1, mgr.getShutdownHooksInOrder().get(0).getHook());
        Assert.assertTrue(mgr.removeShutdownHook(hook1));
        Assert.assertFalse(mgr.hasShutdownHook(hook1));
        Assert.assertFalse(mgr.removeShutdownHook(hook1));
        mgr.addShutdownHook(hook1, 0);
        Assert.assertTrue(mgr.hasShutdownHook(hook1));
        Assert.assertEquals(1, mgr.getShutdownHooksInOrder().size());
        Assert.assertEquals(CommonConfigurationKeysPublic.SERVICE_SHUTDOWN_TIMEOUT_DEFAULT, mgr.getShutdownHooksInOrder().get(0).getTimeout());
        mgr.addShutdownHook(hook2, 1);
        Assert.assertTrue(mgr.hasShutdownHook(hook1));
        Assert.assertTrue(mgr.hasShutdownHook(hook2));
        Assert.assertEquals(2, mgr.getShutdownHooksInOrder().size());
        Assert.assertEquals(hook2, mgr.getShutdownHooksInOrder().get(0).getHook());
        Assert.assertEquals(hook1, mgr.getShutdownHooksInOrder().get(1).getHook());
        // Test hook finish without timeout
        mgr.addShutdownHook(hook3, 2, 4, TimeUnit.SECONDS);
        Assert.assertTrue(mgr.hasShutdownHook(hook3));
        Assert.assertEquals(hook3, mgr.getShutdownHooksInOrder().get(0).getHook());
        Assert.assertEquals(4, mgr.getShutdownHooksInOrder().get(0).getTimeout());
        // Test hook finish with timeout; highest priority
        int hook4timeout = 2;
        mgr.addShutdownHook(hook4, 3, hook4timeout, TimeUnit.SECONDS);
        Assert.assertTrue(mgr.hasShutdownHook(hook4));
        Assert.assertEquals(hook4, mgr.getShutdownHooksInOrder().get(0).getHook());
        Assert.assertEquals(2, mgr.getShutdownHooksInOrder().get(0).getTimeout());
        // a default timeout hook and verify it gets the default timeout
        mgr.addShutdownHook(hook5, 5);
        ShutdownHookManager.HookEntry hookEntry5 = mgr.getShutdownHooksInOrder().get(0);
        Assert.assertEquals(hook5, hookEntry5.getHook());
        Assert.assertEquals("default timeout not used", ShutdownHookManager.getShutdownTimeout(new Configuration()), hookEntry5.getTimeout());
        Assert.assertEquals("hook priority", 5, hookEntry5.getPriority());
        // remove this to avoid a longer sleep in the test run
        Assert.assertTrue(("failed to remove " + hook5), mgr.removeShutdownHook(hook5));
        // now execute the hook shutdown sequence
        TestShutdownHookManager.INVOCATION_COUNT.set(0);
        TestShutdownHookManager.LOG.info("invoking executeShutdown()");
        int timeouts = ShutdownHookManager.executeShutdown();
        TestShutdownHookManager.LOG.info("Shutdown completed");
        Assert.assertEquals("Number of timed out hooks", 1, timeouts);
        List<ShutdownHookManager.HookEntry> hooks = mgr.getShutdownHooksInOrder();
        // analyze the hooks
        for (ShutdownHookManager.HookEntry entry : hooks) {
            TestShutdownHookManager.Hook hook = ((TestShutdownHookManager.Hook) (entry.getHook()));
            Assert.assertTrue(("Was not invoked " + hook), hook.invoked);
            // did any hook raise an exception?
            hook.maybeThrowAssertion();
        }
        // check the state of some of the invoked hooks
        // hook4 was invoked first, but it timed out.
        Assert.assertEquals(("Expected to be invoked first " + hook4), 1, hook4.invokedOrder);
        Assert.assertFalse(("Expected to time out " + hook4), hook4.completed);
        // hook1 completed, but in order after the others, so its start time
        // is the longest.
        Assert.assertTrue(("Expected to complete " + hook1), hook1.completed);
        long invocationInterval = (hook1.startTime) - (hook4.startTime);
        Assert.assertTrue(("invocation difference too short " + invocationInterval), (invocationInterval >= (hook4timeout * 1000)));
        Assert.assertTrue(("sleeping hook4 blocked other threads for " + invocationInterval), (invocationInterval < (hook4.sleepTime)));
        // finally, clear the hooks
        mgr.clearShutdownHooks();
        // and verify that the hooks are empty
        Assert.assertFalse(mgr.hasShutdownHook(hook1));
        Assert.assertEquals("shutdown hook list is not empty", 0, mgr.getShutdownHooksInOrder().size());
    }

    @Test
    public void testShutdownTimeoutConfiguration() throws Throwable {
        // set the shutdown timeout and verify it can be read back.
        Configuration conf = new Configuration();
        long shutdownTimeout = 5;
        conf.setTimeDuration(CommonConfigurationKeysPublic.SERVICE_SHUTDOWN_TIMEOUT, shutdownTimeout, TimeUnit.SECONDS);
        Assert.assertEquals(CommonConfigurationKeysPublic.SERVICE_SHUTDOWN_TIMEOUT, shutdownTimeout, ShutdownHookManager.getShutdownTimeout(conf));
    }

    /**
     * Verify that low timeouts simply fall back to
     * {@link ShutdownHookManager#TIMEOUT_MINIMUM}.
     */
    @Test
    public void testShutdownTimeoutBadConfiguration() throws Throwable {
        // set the shutdown timeout and verify it can be read back.
        Configuration conf = new Configuration();
        long shutdownTimeout = 50;
        conf.setTimeDuration(CommonConfigurationKeysPublic.SERVICE_SHUTDOWN_TIMEOUT, shutdownTimeout, TimeUnit.NANOSECONDS);
        Assert.assertEquals(CommonConfigurationKeysPublic.SERVICE_SHUTDOWN_TIMEOUT, TIMEOUT_MINIMUM, ShutdownHookManager.getShutdownTimeout(conf));
    }

    /**
     * Verifies that a hook cannot be re-registered: an attempt to do so
     * will simply be ignored.
     */
    @Test
    public void testDuplicateRegistration() throws Throwable {
        ShutdownHookManager mgr = ShutdownHookManager.get();
        TestShutdownHookManager.Hook hook = new TestShutdownHookManager.Hook("hook1", 0, false);
        // add the hook
        mgr.addShutdownHook(hook, 2, 1, TimeUnit.SECONDS);
        // add it at a higher priority. This will be ignored.
        mgr.addShutdownHook(hook, 5);
        List<ShutdownHookManager.HookEntry> hookList = mgr.getShutdownHooksInOrder();
        Assert.assertEquals("Hook added twice", 1, hookList.size());
        ShutdownHookManager.HookEntry entry = hookList.get(0);
        Assert.assertEquals("priority of hook", 2, entry.getPriority());
        Assert.assertEquals("timeout of hook", 1, entry.getTimeout());
        // remove the hook
        Assert.assertTrue(("failed to remove hook " + hook), mgr.removeShutdownHook(hook));
        // which will fail a second time
        Assert.assertFalse("expected hook removal to fail", mgr.removeShutdownHook(hook));
        // now register it
        mgr.addShutdownHook(hook, 5);
        hookList = mgr.getShutdownHooksInOrder();
        entry = hookList.get(0);
        Assert.assertEquals("priority of hook", 5, entry.getPriority());
        Assert.assertNotEquals("timeout of hook", 1, entry.getTimeout());
    }

    private static final AtomicInteger INVOCATION_COUNT = new AtomicInteger();

    /**
     * Hooks for testing; save state for ease of asserting on
     * invocation.
     */
    private class Hook implements Runnable {
        private final String name;

        private final long sleepTime;

        private final boolean expectFailure;

        private AssertionError assertion;

        private boolean invoked;

        private int invokedOrder;

        private boolean completed;

        private boolean interrupted;

        private long startTime;

        Hook(final String name, final long sleepTime, final boolean expectFailure) {
            this.name = name;
            this.sleepTime = sleepTime;
            this.expectFailure = expectFailure;
        }

        @Override
        public void run() {
            try {
                invoked = true;
                invokedOrder = TestShutdownHookManager.INVOCATION_COUNT.incrementAndGet();
                startTime = System.currentTimeMillis();
                TestShutdownHookManager.LOG.info("Starting shutdown of {} with sleep time of {}", name, sleepTime);
                if ((sleepTime) > 0) {
                    Thread.sleep(sleepTime);
                }
                TestShutdownHookManager.LOG.info("Completed shutdown of {}", name);
                completed = true;
                if (expectFailure) {
                    assertion = new AssertionError(("Expected a failure of " + (name)));
                }
            } catch (InterruptedException ex) {
                TestShutdownHookManager.LOG.info("Shutdown {} interrupted exception", name, ex);
                interrupted = true;
                if (!(expectFailure)) {
                    assertion = new AssertionError(("Timeout of " + (name)), ex);
                }
            }
            maybeThrowAssertion();
        }

        /**
         * Raise any exception generated during the shutdown process.
         *
         * @throws AssertionError
         * 		any assertion from the shutdown.
         */
        void maybeThrowAssertion() throws AssertionError {
            if ((assertion) != null) {
                throw assertion;
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Hook{");
            sb.append("name='").append(name).append('\'');
            sb.append(", sleepTime=").append(sleepTime);
            sb.append(", expectFailure=").append(expectFailure);
            sb.append(", invoked=").append(invoked);
            sb.append(", invokedOrder=").append(invokedOrder);
            sb.append(", completed=").append(completed);
            sb.append(", interrupted=").append(interrupted);
            sb.append('}');
            return sb.toString();
        }
    }
}

