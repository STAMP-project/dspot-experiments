/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import PathWatcher.Config;
import PathWatcher.Config.UNLIMITED_DEPTH;
import PathWatcher.LOG;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.PathWatcher.PathWatchEvent;
import org.eclipse.jetty.util.PathWatcher.PathWatchEventType;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;

import static PathWatchEventType.ADDED;
import static PathWatchEventType.DELETED;
import static PathWatchEventType.MODIFIED;


@Disabled
@ExtendWith(WorkDirExtension.class)
public class PathWatcherTest {
    public static final int QUIET_TIME;

    public static final int WAIT_TIME;

    public static final int LONG_TIME;

    static {
        if (OS.LINUX.isCurrentOs())
            QUIET_TIME = 300;
        else
            if (OS.MAC.isCurrentOs())
                QUIET_TIME = 5000;
            else
                QUIET_TIME = 1000;


        WAIT_TIME = 2 * (PathWatcherTest.QUIET_TIME);
        LONG_TIME = 5 * (PathWatcherTest.QUIET_TIME);
    }

    public static class PathWatchEventCapture implements PathWatcher.Listener {
        public static final String FINISH_TAG = "#finished#.tag";

        private static final Logger LOG = Log.getLogger(PathWatcherTest.PathWatchEventCapture.class);

        private final Path baseDir;

        /**
         * Map of relative paths seen, to their events seen (in order seen)
         */
        public Map<String, List<PathWatchEventType>> events = new HashMap<>();

        public int latchCount = 1;

        public CountDownLatch finishedLatch;

        private PathWatchEventType triggerType;

        private Path triggerPath;

        public PathWatchEventCapture(Path baseDir) {
            this.baseDir = baseDir;
        }

        public void reset() {
            finishedLatch = new CountDownLatch(latchCount);
            events.clear();
        }

        public void reset(int count) {
            setFinishTrigger(count);
            events.clear();
        }

        @Override
        public void onPathWatchEvent(PathWatchEvent event) {
            synchronized(events) {
                Path relativePath = this.baseDir.relativize(event.getPath());
                String key = relativePath.toString().replace(File.separatorChar, '/');
                List<PathWatchEventType> types = this.events.get(key);
                if (types == null) {
                    types = new ArrayList();
                }
                types.add(event.getType());
                this.events.put(key, types);
                PathWatcherTest.PathWatchEventCapture.LOG.debug("Captured Event: {} | {}", event.getType(), key);
            }
            // if triggered by path
            if ((triggerPath) != null) {
                if ((triggerPath.equals(event.getPath())) && ((event.getType()) == (triggerType))) {
                    PathWatcherTest.PathWatchEventCapture.LOG.debug("Encountered finish trigger: {} on {}", event.getType(), event.getPath());
                    finishedLatch.countDown();
                }
            } else
                if ((finishedLatch) != null) {
                    finishedLatch.countDown();
                }

        }

        /**
         * Validate the events seen match expectations.
         * <p>
         * Note: order of events is only important when looking at a specific file or directory. Events for multiple
         * files can overlap in ways that this assertion doesn't care about.
         *
         * @param expectedEvents
         * 		the events expected
         */
        public void assertEvents(Map<String, PathWatchEventType[]> expectedEvents) {
            try {
                MatcherAssert.assertThat("Event match (file|directory) count", this.events.size(), Matchers.is(expectedEvents.size()));
                for (Map.Entry<String, PathWatchEventType[]> entry : expectedEvents.entrySet()) {
                    String relativePath = entry.getKey();
                    PathWatchEventType[] expectedTypes = entry.getValue();
                    assertEvents(relativePath, expectedTypes);
                }
            } catch (Throwable th) {
                System.err.println(this.events);
                throw th;
            }
        }

        /**
         * Validate the events seen match expectations.
         * <p>
         * Note: order of events is only important when looking at a specific file or directory. Events for multiple
         * files can overlap in ways that this assertion doesn't care about.
         *
         * @param relativePath
         * 		the test relative path to look for
         * @param expectedEvents
         * 		the events expected
         */
        public void assertEvents(String relativePath, PathWatchEventType... expectedEvents) {
            synchronized(events) {
                List<PathWatchEventType> actualEvents = this.events.get(relativePath);
                MatcherAssert.assertThat((("Events for path [" + relativePath) + "]"), actualEvents, Matchers.contains(expectedEvents));
            }
        }

        /**
         * Set the path and type that will trigger this capture to be finished
         *
         * @param triggerPath
         * 		the trigger path we look for to know that the capture is complete
         * @param triggerType
         * 		the trigger type we look for to know that the capture is complete
         */
        public void setFinishTrigger(Path triggerPath, PathWatchEventType triggerType) {
            this.triggerPath = triggerPath;
            this.triggerType = triggerType;
            this.latchCount = 1;
            this.finishedLatch = new CountDownLatch(1);
            PathWatcherTest.PathWatchEventCapture.LOG.debug("Setting finish trigger {} for path {}", triggerType, triggerPath);
        }

        public void setFinishTrigger(int count) {
            latchCount = count;
            finishedLatch = new CountDownLatch(latchCount);
        }

        /**
         * Await the countdown latch on the finish trigger
         *
         * @param pathWatcher
         * 		the watcher instance we are waiting on
         * @throws IOException
         * 		if unable to create the finish tag file
         * @throws InterruptedException
         * 		if unable to await the finish of the run
         * @see #setFinishTrigger(Path, PathWatchEventType)
         */
        public void awaitFinish(PathWatcher pathWatcher) throws IOException, InterruptedException {
            // assertThat("Trigger Path must be set",triggerPath,notNullValue());
            // assertThat("Trigger Type must be set",triggerType,notNullValue());
            double multiplier = 25.0;
            long awaitMillis = ((long) (((double) (pathWatcher.getUpdateQuietTimeMillis())) * multiplier));
            PathWatcherTest.PathWatchEventCapture.LOG.debug("Waiting for finish ({} ms)", awaitMillis);
            MatcherAssert.assertThat((("Timed Out (" + awaitMillis) + "ms) waiting for capture to finish"), finishedLatch.await(awaitMillis, TimeUnit.MILLISECONDS), Matchers.is(true));
            PathWatcherTest.PathWatchEventCapture.LOG.debug("Finished capture");
        }

        @Override
        public String toString() {
            return events.toString();
        }
    }

    private static final int KB = 1024;

    private static final int MB = (PathWatcherTest.KB) * (PathWatcherTest.KB);

    public WorkDir testdir;

    @Test
    public void testSequence() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createFile(dir.resolve("file0"));
        Files.createDirectories(dir.resolve("subdir0/subsubdir0"));
        Files.createFile(dir.resolve("subdir0/fileA"));
        Files.createFile(dir.resolve("subdir0/subsubdir0/unseen"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config config = new PathWatcher.Config(dir);
        config.setRecurseDepth(1);
        pathWatcher.watch(config);
        try {
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            // Check initial scan events
            capture.setFinishTrigger(4);
            pathWatcher.start();
            expected.put("file0", new PathWatchEventType[]{ ADDED });
            expected.put("subdir0", new PathWatchEventType[]{ ADDED });
            expected.put("subdir0/fileA", new PathWatchEventType[]{ ADDED });
            expected.put("subdir0/subsubdir0", new PathWatchEventType[]{ ADDED });
            capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS);
            capture.assertEvents(expected);
            Thread.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
            // Check adding files
            capture.reset(3);
            expected.clear();
            Files.createFile(dir.resolve("subdir0/subsubdir0/toodeep"));
            expected.put("subdir0/subsubdir0", new PathWatchEventType[]{ MODIFIED });
            Files.createFile(dir.resolve("file1"));
            expected.put("file1", new PathWatchEventType[]{ ADDED });
            Files.createFile(dir.resolve("subdir0/fileB"));
            expected.put("subdir0/fileB", new PathWatchEventType[]{ ADDED });
            capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS);
            capture.assertEvents(expected);
            Thread.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
            // Check slow modification
            capture.reset(1);
            expected.clear();
            long start = System.nanoTime();
            new Thread(() -> {
                updateFileOverTime(dir.resolve("file1"), (2 * (PathWatcherTest.QUIET_TIME)), TimeUnit.MILLISECONDS);
            }).start();
            expected.put("file1", new PathWatchEventType[]{ MODIFIED });
            capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS);
            long end = System.nanoTime();
            capture.assertEvents(expected);
            MatcherAssert.assertThat((end - start), Matchers.greaterThan(TimeUnit.MILLISECONDS.toNanos((2 * (PathWatcherTest.QUIET_TIME)))));
            Thread.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
            // Check slow add
            capture.reset(1);
            expected.clear();
            start = System.nanoTime();
            new Thread(() -> {
                updateFileOverTime(dir.resolve("file2"), (2 * (PathWatcherTest.QUIET_TIME)), TimeUnit.MILLISECONDS);
            }).start();
            expected.put("file2", new PathWatchEventType[]{ ADDED });
            capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS);
            end = System.nanoTime();
            capture.assertEvents(expected);
            MatcherAssert.assertThat((end - start), Matchers.greaterThan(TimeUnit.MILLISECONDS.toNanos((2 * (PathWatcherTest.QUIET_TIME)))));
            Thread.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
            // Check move directory
            if (OS.LINUX.isCurrentOs()) {
                capture.reset(5);
                expected.clear();
                Files.move(dir.resolve("subdir0"), dir.resolve("subdir1"), StandardCopyOption.ATOMIC_MOVE);
                expected.put("subdir0", new PathWatchEventType[]{ DELETED });
                // TODO expected.put("subdir0/fileA",new PathWatchEventType[] { DELETED });
                // TODO expected.put("subdir0/subsubdir0",new PathWatchEventType[] { DELETED });
                expected.put("subdir1", new PathWatchEventType[]{ ADDED });
                expected.put("subdir1/fileA", new PathWatchEventType[]{ ADDED });
                expected.put("subdir1/fileB", new PathWatchEventType[]{ ADDED });
                expected.put("subdir1/subsubdir0", new PathWatchEventType[]{ ADDED });
                capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS);
                capture.assertEvents(expected);
                Thread.sleep(PathWatcherTest.WAIT_TIME);
                capture.assertEvents(expected);
            }
            // Check delete file
            capture.reset(2);
            expected.clear();
            Files.delete(dir.resolve("file1"));
            expected.put("file1", new PathWatchEventType[]{ DELETED });
            Files.delete(dir.resolve("file2"));
            expected.put("file2", new PathWatchEventType[]{ DELETED });
            capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS);
            capture.assertEvents(expected);
            Thread.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    @Test
    public void testRestart() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        Files.createDirectories(dir.resolve("b/c"));
        Files.createFile(dir.resolve("a.txt"));
        Files.createFile(dir.resolve("b.txt"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setNotifyExistingOnStart(true);
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        capture.setFinishTrigger(2);
        pathWatcher.addListener(capture);
        PathWatcher.Config config = new PathWatcher.Config(dir);
        config.setRecurseDepth(UNLIMITED_DEPTH);
        config.addIncludeGlobRelative("*.txt");
        pathWatcher.watch(config);
        try {
            pathWatcher.start();
            // Let quiet time do its thing
            PathWatcherTest.awaitQuietTime(pathWatcher);
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("a.txt", new PathWatchEventType[]{ ADDED });
            expected.put("b.txt", new PathWatchEventType[]{ ADDED });
            Thread.currentThread().sleep(1000);// TODO poor test

            capture.assertEvents(expected);
            // stop it
            pathWatcher.stop();
            capture.reset();
            Thread.currentThread().sleep(1000);// TODO poor test

            pathWatcher.start();
            PathWatcherTest.awaitQuietTime(pathWatcher);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    /**
     * When starting up the PathWatcher, the events should occur
     * indicating files that are of interest that already exist
     * on the filesystem.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testStartupFindFiles() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createFile(dir.resolve("foo.war"));
        Files.createDirectories(dir.resolve("bar/WEB-INF"));
        Files.createFile(dir.resolve("bar/WEB-INF/web.xml"));
        // Files we don't care about
        Files.createFile(dir.resolve("foo.war.backup"));
        String hidden_war = ".hidden.war";
        if (OS.WINDOWS.isCurrentOs())
            hidden_war = "hidden.war";

        Files.createFile(dir.resolve(hidden_war));
        if (OS.WINDOWS.isCurrentOs())
            Files.setAttribute(dir.resolve(hidden_war), "dos:hidden", Boolean.TRUE);

        Files.createDirectories(dir.resolve(".wat/WEB-INF"));
        Files.createFile(dir.resolve(".wat/huh.war"));
        Files.createFile(dir.resolve(".wat/WEB-INF/web.xml"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config baseDirConfig = new PathWatcher.Config(dir);
        baseDirConfig.setRecurseDepth(2);
        baseDirConfig.addExcludeHidden();
        baseDirConfig.addIncludeGlobRelative("*.war");
        baseDirConfig.addIncludeGlobRelative("*/WEB-INF/web.xml");
        pathWatcher.watch(baseDirConfig);
        try {
            capture.setFinishTrigger(2);
            pathWatcher.start();
            // Let quiet time do its thing
            capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS);
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("bar/WEB-INF/web.xml", new PathWatchEventType[]{ ADDED });
            expected.put("foo.war", new PathWatchEventType[]{ ADDED });
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    @Test
    public void testGlobPattern() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createFile(dir.resolve("a.txt"));
        Files.createDirectories(dir.resolve("b/b.txt"));
        Files.createDirectories(dir.resolve("c/d"));
        Files.createFile(dir.resolve("c/d/d.txt"));
        Files.createFile(dir.resolve(".foo.txt"));
        // Files we don't care about
        Files.createFile(dir.resolve("txt.foo"));
        Files.createFile(dir.resolve("b/foo.xml"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config baseDirConfig = new PathWatcher.Config(dir);
        baseDirConfig.setRecurseDepth(UNLIMITED_DEPTH);
        baseDirConfig.addExcludeHidden();
        baseDirConfig.addIncludeGlobRelative("**.txt");
        pathWatcher.watch(baseDirConfig);
        try {
            capture.setFinishTrigger(3);
            pathWatcher.start();
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("a.txt", new PathWatchEventType[]{ ADDED });
            expected.put("b/b.txt", new PathWatchEventType[]{ ADDED });
            expected.put("c/d/d.txt", new PathWatchEventType[]{ ADDED });
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    @Test
    public void testDeployFiles_Update_Delete() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createFile(dir.resolve("foo.war"));
        Files.createDirectories(dir.resolve("bar/WEB-INF"));
        Files.createFile(dir.resolve("bar/WEB-INF/web.xml"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config baseDirConfig = new PathWatcher.Config(dir);
        baseDirConfig.setRecurseDepth(100);
        baseDirConfig.addExcludeHidden();
        baseDirConfig.addIncludeGlobRelative("*.war");
        baseDirConfig.addIncludeGlobRelative("*/WEB-INF/web.xml");
        pathWatcher.watch(baseDirConfig);
        try {
            capture.setFinishTrigger(2);
            pathWatcher.start();
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            capture.setFinishTrigger(3);
            // Update web.xml
            Path webFile = dir.resolve("bar/WEB-INF/web.xml");
            // capture.setFinishTrigger(webFile,MODIFIED);
            PathWatcherTest.updateFile(webFile, "Hello Update");
            // Delete war
            Files.delete(dir.resolve("foo.war"));
            // Add a another new war
            Files.createFile(dir.resolve("bar.war"));
            // Let capture complete
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("bar/WEB-INF/web.xml", new PathWatchEventType[]{ ADDED, MODIFIED });
            expected.put("foo.war", new PathWatchEventType[]{ ADDED, DELETED });
            expected.put("bar.war", new PathWatchEventType[]{ ADDED });
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    @Test
    public void testDeployFiles_NewWar() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createFile(dir.resolve("foo.war"));
        Files.createDirectories(dir.resolve("bar/WEB-INF"));
        Files.createFile(dir.resolve("bar/WEB-INF/web.xml"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config baseDirConfig = new PathWatcher.Config(dir);
        baseDirConfig.setRecurseDepth(2);
        baseDirConfig.addExcludeHidden();
        baseDirConfig.addIncludeGlobRelative("*.war");
        baseDirConfig.addIncludeGlobRelative("*/WEB-INF/web.xml");
        pathWatcher.watch(baseDirConfig);
        try {
            capture.setFinishTrigger(2);
            pathWatcher.start();
            // Pretend that startup occurred
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            // New war added
            capture.setFinishTrigger(1);
            Path warFile = dir.resolve("hello.war");
            PathWatcherTest.updateFile(warFile, "Create Hello");
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            PathWatcherTest.updateFile(warFile, "Hello 1");
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            PathWatcherTest.updateFile(warFile, "Hello two");
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            PathWatcherTest.updateFile(warFile, "Hello three");
            // Let capture finish
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("bar/WEB-INF/web.xml", new PathWatchEventType[]{ ADDED });
            expected.put("foo.war", new PathWatchEventType[]{ ADDED });
            expected.put("hello.war", new PathWatchEventType[]{ ADDED });
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    @Test
    public void testDeployFiles_NewDir() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createFile(dir.resolve("foo.war"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config baseDirConfig = new PathWatcher.Config(dir);
        baseDirConfig.setRecurseDepth(2);
        baseDirConfig.addExcludeHidden();
        baseDirConfig.addIncludeGlobRelative("*.war");
        baseDirConfig.addIncludeGlobRelative("*/WEB-INF/web.xml");
        pathWatcher.watch(baseDirConfig);
        try {
            capture.setFinishTrigger(1);
            pathWatcher.start();
            // Pretend that startup occurred
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            // New war added
            capture.setFinishTrigger(1);
            Files.createDirectories(dir.resolve("bar/WEB-INF"));
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            Files.createFile(dir.resolve("bar/WEB-INF/web.xml"));
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            PathWatcherTest.updateFile(dir.resolve("bar/WEB-INF/web.xml"), "Update");
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            PathWatcherTest.updateFile(dir.resolve("bar/WEB-INF/web.xml"), "Update web.xml");
            // Let capture finish
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("bar/WEB-INF/web.xml", new PathWatchEventType[]{ ADDED });
            expected.put("foo.war", new PathWatchEventType[]{ ADDED });
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    @Test
    public void testDeployFilesBeyondDepthLimit() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createDirectories(dir.resolve("foo/WEB-INF/lib"));
        Files.createDirectories(dir.resolve("bar/WEB-INF/lib"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config baseDirConfig = new PathWatcher.Config(dir);
        baseDirConfig.setRecurseDepth(0);
        pathWatcher.watch(baseDirConfig);
        try {
            capture.setFinishTrigger(2);
            pathWatcher.start();
            // Pretend that startup occurred
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("foo", new PathWatchEventType[]{ ADDED });
            expected.put("bar", new PathWatchEventType[]{ ADDED });
            capture.assertEvents(expected);
            capture.reset(1);
            expected.clear();
            expected.put("bar", new PathWatchEventType[]{ MODIFIED });
            Files.createFile(dir.resolve("bar/index.html"));
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
            capture.reset(1);
            expected.clear();
            expected.put("bob", new PathWatchEventType[]{ ADDED });
            Files.createFile(dir.resolve("bar/WEB-INF/lib/ignored"));
            LOG.debug("create bob");
            Files.createDirectories(dir.resolve("bob/WEB-INF/lib"));
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            LOG.debug("create bob/index.html");
            Files.createFile(dir.resolve("bob/index.html"));
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            LOG.debug("update bob/index.html");
            PathWatcherTest.updateFile(dir.resolve("bob/index.html"), "Update");
            Thread.sleep(((PathWatcherTest.QUIET_TIME) / 2));
            LOG.debug("update bob/index.html");
            PathWatcherTest.updateFile(dir.resolve("bob/index.html"), "Update index.html");
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    @Test
    public void testWatchFile() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createDirectories(dir.resolve("bar/WEB-INF"));
        Files.createFile(dir.resolve("bar/WEB-INF/web.xml"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test configuration
        pathWatcher.watch(dir.resolve("bar/WEB-INF/web.xml"));
        pathWatcher.setNotifyExistingOnStart(false);
        try {
            pathWatcher.start();
            Thread.sleep(PathWatcherTest.WAIT_TIME);
            MatcherAssert.assertThat(capture.events.size(), Matchers.is(0));
            Files.createFile(dir.resolve("bar/index.htnl"));
            Files.createFile(dir.resolve("bar/WEB-INF/other.xml"));
            Files.createDirectories(dir.resolve("bar/WEB-INF/lib"));
            Thread.sleep(PathWatcherTest.WAIT_TIME);
            MatcherAssert.assertThat(capture.events.size(), Matchers.is(0));
            capture.setFinishTrigger(1);
            PathWatcherTest.updateFile(dir.resolve("bar/WEB-INF/web.xml"), "Update web.xml");
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("bar/WEB-INF/web.xml", new PathWatchEventType[]{ MODIFIED });
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }

    /**
     * Pretend to modify a new war file that is large, and being copied into place
     * using some sort of technique that is slow enough that it takes a while for
     * the entire war file to exist in place.
     * <p>
     * This is to test the quiet time logic to ensure that only a single MODIFIED event occurs on this new war file
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testDeployFiles_ModifyWar_LargeSlowCopy() throws Exception {
        Path dir = testdir.getEmptyPathDir();
        // Files we are interested in
        Files.createFile(dir.resolve("foo.war"));
        Files.createFile(dir.resolve("hello.war"));
        Files.createDirectories(dir.resolve("bar/WEB-INF"));
        Files.createFile(dir.resolve("bar/WEB-INF/web.xml"));
        PathWatcher pathWatcher = new PathWatcher();
        pathWatcher.setUpdateQuietTime(PathWatcherTest.QUIET_TIME, TimeUnit.MILLISECONDS);
        // Add listener
        PathWatcherTest.PathWatchEventCapture capture = new PathWatcherTest.PathWatchEventCapture(dir);
        pathWatcher.addListener(capture);
        // Add test dir configuration
        PathWatcher.Config baseDirConfig = new PathWatcher.Config(dir);
        baseDirConfig.setRecurseDepth(2);
        baseDirConfig.addExcludeHidden();
        baseDirConfig.addIncludeGlobRelative("*.war");
        baseDirConfig.addIncludeGlobRelative("*/WEB-INF/web.xml");
        pathWatcher.watch(baseDirConfig);
        try {
            capture.setFinishTrigger(3);
            pathWatcher.start();
            // Pretend that startup occurred
            Assertions.assertTrue(capture.finishedLatch.await(PathWatcherTest.LONG_TIME, TimeUnit.MILLISECONDS));
            // New war added (slowly)
            capture.setFinishTrigger(1);
            Path warFile = dir.resolve("hello.war");
            long start = System.nanoTime();
            new Thread(() -> {
                updateFileOverTime(warFile, (2 * (PathWatcherTest.QUIET_TIME)), TimeUnit.MILLISECONDS);
            }).start();
            Assertions.assertTrue(capture.finishedLatch.await((4 * (PathWatcherTest.QUIET_TIME)), TimeUnit.MILLISECONDS));
            long end = System.nanoTime();
            MatcherAssert.assertThat((end - start), Matchers.greaterThan(TimeUnit.MILLISECONDS.toNanos((2 * (PathWatcherTest.QUIET_TIME)))));
            Map<String, PathWatchEventType[]> expected = new HashMap<>();
            expected.put("bar/WEB-INF/web.xml", new PathWatchEventType[]{ ADDED });
            expected.put("foo.war", new PathWatchEventType[]{ ADDED });
            expected.put("hello.war", new PathWatchEventType[]{ ADDED, MODIFIED });
            capture.assertEvents(expected);
            TimeUnit.MILLISECONDS.sleep(PathWatcherTest.WAIT_TIME);
            capture.assertEvents(expected);
        } finally {
            pathWatcher.stop();
        }
    }
}

