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
package org.apache.zookeeper.common;


import FileChangeWatcher.State.RUNNING;
import FileChangeWatcher.State.STOPPED;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileChangeWatcherTest extends ZKTestCase {
    private static File tempDir;

    private static File tempFile;

    private static final Logger LOG = LoggerFactory.getLogger(FileChangeWatcherTest.class);

    @Test
    public void testCallbackWorksOnFileChanges() throws IOException, InterruptedException {
        FileChangeWatcher watcher = null;
        try {
            final List<WatchEvent<?>> events = new ArrayList<>();
            watcher = new FileChangeWatcher(FileChangeWatcherTest.tempDir.toPath(), ( event) -> {
                LOG.info(((("Got an update: " + (event.kind())) + " ") + (event.context())));
                // Filter out the extra ENTRY_CREATE events that are
                // sometimes seen at the start. Even though we create the watcher
                // after the file exists, sometimes we still get a create event.
                if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                    return;
                }
                synchronized(events) {
                    events.add(event);
                    events.notifyAll();
                }
            });
            watcher.start();
            watcher.waitForState(RUNNING);
            Thread.sleep(1000L);// XXX hack

            for (int i = 0; i < 3; i++) {
                FileChangeWatcherTest.LOG.info(("Modifying file, attempt " + (i + 1)));
                FileUtils.writeStringToFile(FileChangeWatcherTest.tempFile, (("Hello world " + i) + "\n"), StandardCharsets.UTF_8, true);
                synchronized(events) {
                    if ((events.size()) < (i + 1)) {
                        events.wait(3000L);
                    }
                    Assert.assertEquals("Wrong number of events", (i + 1), events.size());
                    WatchEvent<?> event = events.get(i);
                    Assert.assertEquals(StandardWatchEventKinds.ENTRY_MODIFY, event.kind());
                    Assert.assertEquals(FileChangeWatcherTest.tempFile.getName(), event.context().toString());
                }
            }
        } finally {
            if (watcher != null) {
                watcher.stop();
                watcher.waitForState(STOPPED);
            }
        }
    }

    @Test
    public void testCallbackWorksOnFileTouched() throws IOException, InterruptedException {
        FileChangeWatcher watcher = null;
        try {
            final List<WatchEvent<?>> events = new ArrayList<>();
            watcher = new FileChangeWatcher(FileChangeWatcherTest.tempDir.toPath(), ( event) -> {
                LOG.info(((("Got an update: " + (event.kind())) + " ") + (event.context())));
                // Filter out the extra ENTRY_CREATE events that are
                // sometimes seen at the start. Even though we create the watcher
                // after the file exists, sometimes we still get a create event.
                if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                    return;
                }
                synchronized(events) {
                    events.add(event);
                    events.notifyAll();
                }
            });
            watcher.start();
            watcher.waitForState(RUNNING);
            Thread.sleep(1000L);// XXX hack

            FileChangeWatcherTest.LOG.info("Touching file");
            FileUtils.touch(FileChangeWatcherTest.tempFile);
            synchronized(events) {
                if (events.isEmpty()) {
                    events.wait(3000L);
                }
                Assert.assertFalse(events.isEmpty());
                WatchEvent<?> event = events.get(0);
                Assert.assertEquals(StandardWatchEventKinds.ENTRY_MODIFY, event.kind());
                Assert.assertEquals(FileChangeWatcherTest.tempFile.getName(), event.context().toString());
            }
        } finally {
            if (watcher != null) {
                watcher.stop();
                watcher.waitForState(STOPPED);
            }
        }
    }

    @Test
    public void testCallbackWorksOnFileAdded() throws IOException, InterruptedException {
        FileChangeWatcher watcher = null;
        try {
            final List<WatchEvent<?>> events = new ArrayList<>();
            watcher = new FileChangeWatcher(FileChangeWatcherTest.tempDir.toPath(), ( event) -> {
                LOG.info(((("Got an update: " + (event.kind())) + " ") + (event.context())));
                synchronized(events) {
                    events.add(event);
                    events.notifyAll();
                }
            });
            watcher.start();
            watcher.waitForState(RUNNING);
            Thread.sleep(1000L);// XXX hack

            File tempFile2 = File.createTempFile("zk_test_", "", FileChangeWatcherTest.tempDir);
            tempFile2.deleteOnExit();
            synchronized(events) {
                if (events.isEmpty()) {
                    events.wait(3000L);
                }
                Assert.assertFalse(events.isEmpty());
                WatchEvent<?> event = events.get(0);
                Assert.assertEquals(StandardWatchEventKinds.ENTRY_CREATE, event.kind());
                Assert.assertEquals(tempFile2.getName(), event.context().toString());
            }
        } finally {
            if (watcher != null) {
                watcher.stop();
                watcher.waitForState(STOPPED);
            }
        }
    }

    @Test
    public void testCallbackWorksOnFileDeleted() throws IOException, InterruptedException {
        FileChangeWatcher watcher = null;
        try {
            final List<WatchEvent<?>> events = new ArrayList<>();
            watcher = new FileChangeWatcher(FileChangeWatcherTest.tempDir.toPath(), ( event) -> {
                LOG.info(((("Got an update: " + (event.kind())) + " ") + (event.context())));
                // Filter out the extra ENTRY_CREATE events that are
                // sometimes seen at the start. Even though we create the watcher
                // after the file exists, sometimes we still get a create event.
                if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                    return;
                }
                synchronized(events) {
                    events.add(event);
                    events.notifyAll();
                }
            });
            watcher.start();
            watcher.waitForState(RUNNING);
            Thread.sleep(1000L);// XXX hack

            FileChangeWatcherTest.tempFile.delete();
            synchronized(events) {
                if (events.isEmpty()) {
                    events.wait(3000L);
                }
                Assert.assertFalse(events.isEmpty());
                WatchEvent<?> event = events.get(0);
                Assert.assertEquals(StandardWatchEventKinds.ENTRY_DELETE, event.kind());
                Assert.assertEquals(FileChangeWatcherTest.tempFile.getName(), event.context().toString());
            }
        } finally {
            if (watcher != null) {
                watcher.stop();
                watcher.waitForState(STOPPED);
            }
        }
    }

    @Test
    public void testCallbackErrorDoesNotCrashWatcherThread() throws IOException, InterruptedException {
        FileChangeWatcher watcher = null;
        try {
            final AtomicInteger callCount = new AtomicInteger(0);
            watcher = new FileChangeWatcher(FileChangeWatcherTest.tempDir.toPath(), ( event) -> {
                LOG.info(((("Got an update: " + (event.kind())) + " ") + (event.context())));
                int oldValue;
                synchronized(callCount) {
                    oldValue = callCount.getAndIncrement();
                    callCount.notifyAll();
                }
                if (oldValue == 0) {
                    throw new RuntimeException("This error should not crash the watcher thread");
                }
            });
            watcher.start();
            watcher.waitForState(RUNNING);
            Thread.sleep(1000L);// XXX hack

            FileChangeWatcherTest.LOG.info("Modifying file");
            FileUtils.writeStringToFile(FileChangeWatcherTest.tempFile, "Hello world\n", StandardCharsets.UTF_8, true);
            synchronized(callCount) {
                while ((callCount.get()) == 0) {
                    callCount.wait(3000L);
                } 
            }
            FileChangeWatcherTest.LOG.info("Modifying file again");
            FileUtils.writeStringToFile(FileChangeWatcherTest.tempFile, "Hello world again\n", StandardCharsets.UTF_8, true);
            synchronized(callCount) {
                if ((callCount.get()) == 1) {
                    callCount.wait(3000L);
                }
            }
            // The value of callCount can exceed 1 only if the callback thread
            // survives the exception thrown by the first callback.
            Assert.assertTrue(((callCount.get()) > 1));
        } finally {
            if (watcher != null) {
                watcher.stop();
                watcher.waitForState(STOPPED);
            }
        }
    }
}

