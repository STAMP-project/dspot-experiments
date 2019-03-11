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
package org.apache.flink.runtime.checkpoint.hooks;


import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.checkpoint.MasterTriggerRestoreHook;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;


/**
 * Tests for the MasterHooks utility class.
 */
public class MasterHooksTest extends TestLogger {
    // ------------------------------------------------------------------------
    // hook management
    // ------------------------------------------------------------------------
    @Test
    public void wrapHook() throws Exception {
        final String id = "id";
        Thread thread = Thread.currentThread();
        final ClassLoader originalClassLoader = thread.getContextClassLoader();
        final ClassLoader userClassLoader = new URLClassLoader(new URL[0]);
        final Runnable command = Mockito.spy(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(userClassLoader, Thread.currentThread().getContextClassLoader());
            }
        });
        MasterTriggerRestoreHook<String> hook = Mockito.spy(new MasterTriggerRestoreHook<String>() {
            @Override
            public String getIdentifier() {
                Assert.assertEquals(userClassLoader, Thread.currentThread().getContextClassLoader());
                return id;
            }

            @Override
            public void reset() throws Exception {
                Assert.assertEquals(userClassLoader, Thread.currentThread().getContextClassLoader());
            }

            @Override
            public void close() throws Exception {
                Assert.assertEquals(userClassLoader, Thread.currentThread().getContextClassLoader());
            }

            @Nullable
            @Override
            public CompletableFuture<String> triggerCheckpoint(long checkpointId, long timestamp, Executor executor) throws Exception {
                Assert.assertEquals(userClassLoader, Thread.currentThread().getContextClassLoader());
                executor.execute(command);
                return null;
            }

            @Override
            public void restoreCheckpoint(long checkpointId, @Nullable
            String checkpointData) throws Exception {
                Assert.assertEquals(userClassLoader, Thread.currentThread().getContextClassLoader());
            }

            @Nullable
            @Override
            public SimpleVersionedSerializer<String> createCheckpointDataSerializer() {
                Assert.assertEquals(userClassLoader, Thread.currentThread().getContextClassLoader());
                return null;
            }
        });
        MasterTriggerRestoreHook<String> wrapped = MasterHooks.wrapHook(hook, userClassLoader);
        // verify getIdentifier
        wrapped.getIdentifier();
        Mockito.verify(hook, Mockito.times(1)).getIdentifier();
        Assert.assertEquals(originalClassLoader, thread.getContextClassLoader());
        // verify triggerCheckpoint and its wrapped executor
        MasterHooksTest.TestExecutor testExecutor = new MasterHooksTest.TestExecutor();
        wrapped.triggerCheckpoint(0L, 0, testExecutor);
        Assert.assertEquals(originalClassLoader, thread.getContextClassLoader());
        Assert.assertNotNull(testExecutor.command);
        testExecutor.command.run();
        Mockito.verify(command, Mockito.times(1)).run();
        Assert.assertEquals(originalClassLoader, thread.getContextClassLoader());
        // verify restoreCheckpoint
        wrapped.restoreCheckpoint(0L, "");
        Mockito.verify(hook, Mockito.times(1)).restoreCheckpoint(org.mockito.ArgumentMatchers.eq(0L), eq(""));
        Assert.assertEquals(originalClassLoader, thread.getContextClassLoader());
        // verify createCheckpointDataSerializer
        wrapped.createCheckpointDataSerializer();
        Mockito.verify(hook, Mockito.times(1)).createCheckpointDataSerializer();
        Assert.assertEquals(originalClassLoader, thread.getContextClassLoader());
        // verify close
        wrapped.close();
        Mockito.verify(hook, Mockito.times(1)).close();
        Assert.assertEquals(originalClassLoader, thread.getContextClassLoader());
    }

    private static class TestExecutor implements Executor {
        Runnable command;

        @Override
        public void execute(Runnable command) {
            this.command = command;
        }
    }
}

