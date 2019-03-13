/**
 * Copyright 2014 Google Inc.
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedDirectoryStreamException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for what happens when a file system is closed.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class JimfsFileSystemCloseTest {
    private JimfsFileSystem fs = ((JimfsFileSystem) (Jimfs.newFileSystem(Configuration.unix())));

    @Test
    public void testIsNotOpen() throws IOException {
        Assert.assertTrue(fs.isOpen());
        fs.close();
        Assert.assertFalse(fs.isOpen());
    }

    @Test
    public void testIsNotAvailableFromProvider() throws IOException {
        URI uri = fs.getUri();
        Assert.assertEquals(fs, FileSystems.getFileSystem(uri));
        fs.close();
        try {
            FileSystems.getFileSystem(uri);
            Assert.fail();
        } catch (FileSystemNotFoundException expected) {
        }
    }

    @Test
    public void testOpenStreamsClosed() throws IOException {
        Path p = fs.getPath("/foo");
        OutputStream out = Files.newOutputStream(p);
        InputStream in = Files.newInputStream(p);
        out.write(1);
        Assert.assertEquals(1, in.read());
        fs.close();
        try {
            out.write(1);
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream is closed", expected.getMessage());
        }
        try {
            in.read();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("stream is closed", expected.getMessage());
        }
    }

    @Test
    public void testOpenChannelsClosed() throws IOException {
        Path p = fs.getPath("/foo");
        FileChannel fc = FileChannel.open(p, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        SeekableByteChannel sbc = Files.newByteChannel(p, StandardOpenOption.READ);
        AsynchronousFileChannel afc = AsynchronousFileChannel.open(p, StandardOpenOption.READ, StandardOpenOption.WRITE);
        Assert.assertTrue(fc.isOpen());
        Assert.assertTrue(sbc.isOpen());
        Assert.assertTrue(afc.isOpen());
        fs.close();
        Assert.assertFalse(fc.isOpen());
        Assert.assertFalse(sbc.isOpen());
        Assert.assertFalse(afc.isOpen());
        try {
            fc.size();
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            sbc.size();
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            afc.size();
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
    }

    @Test
    public void testOpenDirectoryStreamsClosed() throws IOException {
        Path p = fs.getPath("/foo");
        Files.createDirectory(p);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            fs.close();
            try {
                stream.iterator();
                Assert.fail();
            } catch (ClosedDirectoryStreamException expected) {
            }
        }
    }

    @Test
    public void testOpenWatchServicesClosed() throws IOException {
        WatchService ws1 = fs.newWatchService();
        WatchService ws2 = fs.newWatchService();
        Assert.assertNull(ws1.poll());
        Assert.assertNull(ws2.poll());
        fs.close();
        try {
            ws1.poll();
            Assert.fail();
        } catch (ClosedWatchServiceException expected) {
        }
        try {
            ws2.poll();
            Assert.fail();
        } catch (ClosedWatchServiceException expected) {
        }
    }

    @Test
    public void testPathMethodsThrow() throws IOException {
        Path p = fs.getPath("/foo");
        Files.createDirectory(p);
        WatchService ws = fs.newWatchService();
        fs.close();
        try {
            p.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            Assert.fail();
        } catch (ClosedWatchServiceException expected) {
        }
        try {
            p = p.toRealPath();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        // While technically (according to the FileSystem.close() spec) all methods on Path should
        // probably throw, we only throw for methods that access the file system itself in some way...
        // path manipulation methods seem totally harmless to keep working, and I don't see any need to
        // add the overhead of checking that the file system is open for each of those method calls.
    }

    @Test
    public void testOpenFileAttributeViewsThrow() throws IOException {
        Path p = fs.getPath("/foo");
        Files.createFile(p);
        BasicFileAttributeView view = Files.getFileAttributeView(p, BasicFileAttributeView.class);
        fs.close();
        try {
            view.readAttributes();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            view.setTimes(null, null, null);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
    }

    @Test
    public void testFileSystemMethodsThrow() throws IOException {
        fs.close();
        try {
            fs.getPath("/foo");
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            fs.getRootDirectories();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            fs.getFileStores();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            fs.getPathMatcher("glob:*.java");
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            fs.getUserPrincipalLookupService();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            fs.newWatchService();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            fs.supportedFileAttributeViews();
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
    }

    @Test
    public void testFilesMethodsThrow() throws IOException {
        Path file = fs.getPath("/file");
        Path dir = fs.getPath("/dir");
        Path nothing = fs.getPath("/nothing");
        Files.createDirectory(dir);
        Files.createFile(file);
        fs.close();
        // not exhaustive, but should cover every major type of functionality accessible through Files
        // TODO(cgdecker): reflectively invoke all methods with default arguments?
        try {
            Files.delete(file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.createDirectory(nothing);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.createFile(nothing);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.write(nothing, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.newInputStream(file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.newOutputStream(file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.newByteChannel(file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.newDirectoryStream(dir);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.copy(file, nothing);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.move(file, nothing);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.copy(dir, nothing);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.move(dir, nothing);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.createSymbolicLink(nothing, file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.createLink(nothing, file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.exists(file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.getAttribute(file, "size");
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.setAttribute(file, "lastModifiedTime", FileTime.fromMillis(0));
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.getFileAttributeView(file, BasicFileAttributeView.class);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.readAttributes(file, "basic:size,lastModifiedTime");
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.readAttributes(file, BasicFileAttributes.class);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.isDirectory(dir);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.readAllBytes(file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
        try {
            Files.isReadable(file);
            Assert.fail();
        } catch (ClosedFileSystemException expected) {
        }
    }
}

