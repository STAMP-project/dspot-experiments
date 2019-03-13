/**
 * Copyright 2010-2012 VMware and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springsource.loaded.test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.FileChangeListener;
import org.springsource.loaded.ReloadableType;
import org.springsource.loaded.agent.FileSystemWatcher;


public class FileSystemWatcherTests {
    @Test
    public void files() throws IOException {
        FileSystemWatcherTests.TestFileChangeListener listener = new FileSystemWatcherTests.TestFileChangeListener();
        File dir = getTempDir();
        FileSystemWatcher watcher = new FileSystemWatcher(listener, (-1), "test");
        pause(1000);
        File f1 = create(dir, "abc.txt");
        watcher.register(f1);
        pause(1100);
        File f2 = create(dir, "abcd.txt");
        watcher.register(f2);
        pause(1100);
        watcher.setPaused(true);
        // Whilst paused, touch both files
        touch(f2);
        touch(f1);
        watcher.setPaused(false);
        pause(3000);
        watcher.shutdown();
        System.out.println(listener.changesDetected);
        assertContains(listener.changesDetected, "abc.txt");
        assertContains(listener.changesDetected, "abcd.txt");
    }

    @Test
    public void jars() throws IOException {
        FileSystemWatcherTests.TestFileChangeListener listener = new FileSystemWatcherTests.TestFileChangeListener();
        File dir = getTempDir();
        FileSystemWatcher watcher = new FileSystemWatcher(listener, (-1), "test");
        pause(1000);
        File j1 = create(dir, "foo.jar");
        watcher.register(j1);
        pause(1100);
        File j2 = create(dir, "bar.jar");
        watcher.register(j2);
        pause(1100);
        watcher.setPaused(true);
        touch(j2);
        watcher.setPaused(false);
        pause(3000);
        watcher.shutdown();
        Assert.assertTrue(((listener.changesDetected.size()) != 0));
        assertContains(listener.changesDetected, "bar.jar");
    }

    static class TestFileChangeListener implements FileChangeListener {
        List<String> changesDetected = new ArrayList<String>();

        public void fileChanged(File file) {
            System.out.println(("File change detected " + file));
            changesDetected.add(file.getName());
        }

        public void register(ReloadableType rtype, File file) {
        }
    }
}

