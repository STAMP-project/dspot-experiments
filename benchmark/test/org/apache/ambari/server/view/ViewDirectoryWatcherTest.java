/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.view;


import com.google.common.base.Function;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.ambari.server.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class ViewDirectoryWatcherTest {
    private static final Configuration configuration = createNiceMock(Configuration.class);

    private static final ViewRegistry viewRegistry = createNiceMock(ViewRegistry.class);

    private File testDir;

    @Test
    public void testDirectoryWatcherStart() throws Exception {
        ViewDirectoryWatcher viewDirectoryWatcher = new ViewDirectoryWatcher();
        expect(ViewDirectoryWatcherTest.configuration.getViewsDir()).andReturn(testDir).once();
        viewDirectoryWatcher.configuration = ViewDirectoryWatcherTest.configuration;
        viewDirectoryWatcher.viewRegistry = ViewDirectoryWatcherTest.viewRegistry;
        replay(ViewDirectoryWatcherTest.configuration);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        viewDirectoryWatcher.addHook(new Function<Path, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable
            Path path) {
                countDownLatch.countDown();
                return true;
            }
        });
        viewDirectoryWatcher.start();
        countDownLatch.await(1, TimeUnit.SECONDS);
        // Expect watecher to start
        Assert.assertTrue(viewDirectoryWatcher.isRunning());
        verify(ViewDirectoryWatcherTest.configuration);
    }

    @Test
    public void testDirectoryExtractionOnFileAdd() throws Exception {
        ViewDirectoryWatcher viewDirectoryWatcher = new ViewDirectoryWatcher();
        expect(ViewDirectoryWatcherTest.configuration.getViewsDir()).andReturn(testDir).once();
        viewDirectoryWatcher.configuration = ViewDirectoryWatcherTest.configuration;
        viewDirectoryWatcher.viewRegistry = ViewDirectoryWatcherTest.viewRegistry;
        ViewDirectoryWatcherTest.viewRegistry.readViewArchive(Paths.get(testDir.getAbsolutePath(), "file.jar"));
        replay(ViewDirectoryWatcherTest.configuration, ViewDirectoryWatcherTest.viewRegistry);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        viewDirectoryWatcher.addHook(new Function<Path, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable
            Path path) {
                countDownLatch.countDown();
                return true;
            }
        });
        viewDirectoryWatcher.start();
        // Create a new File at destination
        createZipFile();
        countDownLatch.await(30, TimeUnit.SECONDS);
        // Expect watcher to respond
        verify(ViewDirectoryWatcherTest.configuration, ViewDirectoryWatcherTest.viewRegistry);
    }

    @Test
    public void testDirectoryWatcherStop() throws Exception {
        ViewDirectoryWatcher viewDirectoryWatcher = new ViewDirectoryWatcher();
        expect(ViewDirectoryWatcherTest.configuration.getViewsDir()).andReturn(testDir).once();
        viewDirectoryWatcher.configuration = ViewDirectoryWatcherTest.configuration;
        viewDirectoryWatcher.viewRegistry = ViewDirectoryWatcherTest.viewRegistry;
        replay(ViewDirectoryWatcherTest.configuration);
        viewDirectoryWatcher.start();
        // Time to start
        Thread.sleep(100);
        viewDirectoryWatcher.stop();
        Assert.assertFalse(viewDirectoryWatcher.isRunning());
        verify(ViewDirectoryWatcherTest.configuration);
    }
}

