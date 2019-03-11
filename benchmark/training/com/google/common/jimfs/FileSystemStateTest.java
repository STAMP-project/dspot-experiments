/**
 * Copyright 2013 Google Inc.
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
package com.google.common.jimfs;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FileSystemState}.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class FileSystemStateTest {
    private final FileSystemStateTest.TestRunnable onClose = new FileSystemStateTest.TestRunnable();

    private final FileSystemState state = new FileSystemState(onClose);

    @Test
    public void testIsOpen() throws IOException {
        Assert.assertTrue(state.isOpen());
        state.close();
        Assert.assertFalse(state.isOpen());
    }

    @Test
    public void testCheckOpen() throws IOException {
        state.checkOpen();// does not throw

        state.close();
        try {
            state.checkOpen();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
    }

    @Test
    public void testClose_callsOnCloseRunnable() throws IOException {
        Assert.assertEquals(0, onClose.runCount);
        state.close();
        Assert.assertEquals(1, onClose.runCount);
    }

    @Test
    public void testClose_multipleTimesDoNothing() throws IOException {
        state.close();
        Assert.assertEquals(1, onClose.runCount);
        state.close();
        state.close();
        Assert.assertEquals(1, onClose.runCount);
    }

    @Test
    public void testClose_registeredResourceIsClosed() throws IOException {
        FileSystemStateTest.TestCloseable resource = new FileSystemStateTest.TestCloseable();
        state.register(resource);
        Assert.assertFalse(resource.closed);
        state.close();
        Assert.assertTrue(resource.closed);
    }

    @Test
    public void testClose_unregisteredResourceIsNotClosed() throws IOException {
        FileSystemStateTest.TestCloseable resource = new FileSystemStateTest.TestCloseable();
        state.register(resource);
        Assert.assertFalse(resource.closed);
        state.unregister(resource);
        state.close();
        Assert.assertFalse(resource.closed);
    }

    @Test
    public void testClose_multipleRegisteredResourcesAreClosed() throws IOException {
        List<FileSystemStateTest.TestCloseable> resources = ImmutableList.of(new FileSystemStateTest.TestCloseable(), new FileSystemStateTest.TestCloseable(), new FileSystemStateTest.TestCloseable());
        for (FileSystemStateTest.TestCloseable resource : resources) {
            state.register(resource);
            Assert.assertFalse(resource.closed);
        }
        state.close();
        for (FileSystemStateTest.TestCloseable resource : resources) {
            Assert.assertTrue(resource.closed);
        }
    }

    @Test
    public void testClose_resourcesThatThrowOnClose() {
        List<FileSystemStateTest.TestCloseable> resources = ImmutableList.of(new FileSystemStateTest.TestCloseable(), new FileSystemStateTest.ThrowsOnClose("a"), new FileSystemStateTest.TestCloseable(), new FileSystemStateTest.ThrowsOnClose("b"), new FileSystemStateTest.ThrowsOnClose("c"), new FileSystemStateTest.TestCloseable(), new FileSystemStateTest.TestCloseable());
        for (FileSystemStateTest.TestCloseable resource : resources) {
            state.register(resource);
            Assert.assertFalse(resource.closed);
        }
        try {
            state.close();
            Assert.fail();
        } catch (IOException expected) {
            Throwable[] suppressed = expected.getSuppressed();
            Assert.assertEquals(2, suppressed.length);
            ImmutableSet<String> messages = ImmutableSet.of(expected.getMessage(), suppressed[0].getMessage(), suppressed[1].getMessage());
            Assert.assertEquals(ImmutableSet.of("a", "b", "c"), messages);
        }
        for (FileSystemStateTest.TestCloseable resource : resources) {
            Assert.assertTrue(resource.closed);
        }
    }

    private static class TestCloseable implements Closeable {
        boolean closed = false;

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }

    private static final class TestRunnable implements Runnable {
        int runCount = 0;

        @Override
        public void run() {
            (runCount)++;
        }
    }

    private static class ThrowsOnClose extends FileSystemStateTest.TestCloseable {
        private final String string;

        private ThrowsOnClose(String string) {
            this.string = string;
        }

        @Override
        public void close() throws IOException {
            super.close();
            throw new IOException(string);
        }
    }
}

