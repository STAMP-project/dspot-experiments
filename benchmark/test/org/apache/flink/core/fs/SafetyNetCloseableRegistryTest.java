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
package org.apache.flink.core.fs;


import FileSystem.WriteMode.NO_OVERWRITE;
import java.io.Closeable;
import java.io.IOException;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.util.ExceptionUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link SafetyNetCloseableRegistry}.
 */
public class SafetyNetCloseableRegistryTest extends AbstractCloseableRegistryTest<WrappingProxyCloseable<? extends Closeable>, SafetyNetCloseableRegistry.PhantomDelegatingCloseableRef> {
    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testCorrectScopesForSafetyNet() throws Exception {
        CheckedThread t1 = new CheckedThread() {
            @Override
            public void go() throws Exception {
                try {
                    FileSystem fs1 = FileSystem.getLocalFileSystem();
                    // ensure no safety net in place
                    Assert.assertFalse((fs1 instanceof SafetyNetWrapperFileSystem));
                    FileSystemSafetyNet.initializeSafetyNetForThread();
                    fs1 = FileSystem.getLocalFileSystem();
                    // ensure safety net is in place now
                    Assert.assertTrue((fs1 instanceof SafetyNetWrapperFileSystem));
                    Path tmp = new Path(tmpFolder.newFolder().toURI().toString(), "test_file");
                    try (FSDataOutputStream stream = fs1.create(tmp, NO_OVERWRITE)) {
                        CheckedThread t2 = new CheckedThread() {
                            @Override
                            public void go() {
                                FileSystem fs2 = FileSystem.getLocalFileSystem();
                                // ensure the safety net does not leak here
                                Assert.assertFalse((fs2 instanceof SafetyNetWrapperFileSystem));
                                FileSystemSafetyNet.initializeSafetyNetForThread();
                                fs2 = FileSystem.getLocalFileSystem();
                                // ensure we can bring another safety net in place
                                Assert.assertTrue((fs2 instanceof SafetyNetWrapperFileSystem));
                                FileSystemSafetyNet.closeSafetyNetAndGuardedResourcesForThread();
                                fs2 = FileSystem.getLocalFileSystem();
                                // and that we can remove it again
                                Assert.assertFalse((fs2 instanceof SafetyNetWrapperFileSystem));
                            }
                        };
                        t2.start();
                        t2.sync();
                        // ensure stream is still open and was never closed by any interferences
                        stream.write(42);
                        FileSystemSafetyNet.closeSafetyNetAndGuardedResourcesForThread();
                        // ensure leaking stream was closed
                        try {
                            stream.write(43);
                            Assert.fail();
                        } catch (IOException ignore) {
                        }
                        fs1 = FileSystem.getLocalFileSystem();
                        // ensure safety net was removed
                        Assert.assertFalse((fs1 instanceof SafetyNetWrapperFileSystem));
                    } finally {
                        fs1.delete(tmp, false);
                    }
                } catch (Exception e) {
                    Assert.fail(ExceptionUtils.stringifyException(e));
                }
            }
        };
        t1.start();
        t1.sync();
    }

    @Test
    public void testSafetyNetClose() throws Exception {
        setup(20);
        startThreads();
        joinThreads();
        for (int i = 0; (i < 5) && ((unclosedCounter.get()) > 0); ++i) {
            System.gc();
            Thread.sleep(50);
        }
        Assert.assertEquals(0, unclosedCounter.get());
        closeableRegistry.close();
    }

    @Test
    public void testReaperThreadSpawnAndStop() throws Exception {
        Assert.assertFalse(SafetyNetCloseableRegistry.isReaperThreadRunning());
        try (SafetyNetCloseableRegistry ignored = new SafetyNetCloseableRegistry()) {
            Assert.assertTrue(SafetyNetCloseableRegistry.isReaperThreadRunning());
            try (SafetyNetCloseableRegistry ignored2 = new SafetyNetCloseableRegistry()) {
                Assert.assertTrue(SafetyNetCloseableRegistry.isReaperThreadRunning());
            }
            Assert.assertTrue(SafetyNetCloseableRegistry.isReaperThreadRunning());
        }
        Assert.assertFalse(SafetyNetCloseableRegistry.isReaperThreadRunning());
    }
}

