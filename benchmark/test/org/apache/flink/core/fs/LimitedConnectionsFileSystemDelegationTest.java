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
package org.apache.flink.core.fs;


import WriteMode.OVERWRITE;
import java.io.IOException;
import java.util.Random;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static FileSystemKind.FILE_SYSTEM;
import static FileSystemKind.OBJECT_STORE;


/**
 * Tests that the method delegation works properly the {@link LimitedConnectionsFileSystem}
 * and its created input and output streams.
 */
public class LimitedConnectionsFileSystemDelegationTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    @SuppressWarnings("deprecation")
    public void testDelegateFsMethods() throws IOException {
        final FileSystem fs = Mockito.mock(FileSystem.class);
        Mockito.when(fs.open(ArgumentMatchers.any(Path.class))).thenReturn(Mockito.mock(FSDataInputStream.class));
        Mockito.when(fs.open(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyInt())).thenReturn(Mockito.mock(FSDataInputStream.class));
        Mockito.when(fs.create(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean())).thenReturn(Mockito.mock(FSDataOutputStream.class));
        Mockito.when(fs.create(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(WriteMode.class))).thenReturn(Mockito.mock(FSDataOutputStream.class));
        Mockito.when(fs.create(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyLong())).thenReturn(Mockito.mock(FSDataOutputStream.class));
        final LimitedConnectionsFileSystem lfs = new LimitedConnectionsFileSystem(fs, 1000);
        final Random rnd = new Random();
        lfs.isDistributedFS();
        Mockito.verify(fs).isDistributedFS();
        lfs.getWorkingDirectory();
        Mockito.verify(fs).isDistributedFS();
        lfs.getHomeDirectory();
        Mockito.verify(fs).getHomeDirectory();
        lfs.getUri();
        Mockito.verify(fs).getUri();
        {
            Path path = Mockito.mock(Path.class);
            lfs.getFileStatus(path);
            Mockito.verify(fs).getFileStatus(path);
        }
        {
            FileStatus path = Mockito.mock(FileStatus.class);
            int pos = rnd.nextInt();
            int len = rnd.nextInt();
            lfs.getFileBlockLocations(path, pos, len);
            Mockito.verify(fs).getFileBlockLocations(path, pos, len);
        }
        {
            Path path = Mockito.mock(Path.class);
            int bufferSize = rnd.nextInt();
            lfs.open(path, bufferSize);
            Mockito.verify(fs).open(path, bufferSize);
        }
        {
            Path path = Mockito.mock(Path.class);
            lfs.open(path);
            Mockito.verify(fs).open(path);
        }
        lfs.getDefaultBlockSize();
        Mockito.verify(fs).getDefaultBlockSize();
        {
            Path path = Mockito.mock(Path.class);
            lfs.listStatus(path);
            Mockito.verify(fs).listStatus(path);
        }
        {
            Path path = Mockito.mock(Path.class);
            lfs.exists(path);
            Mockito.verify(fs).exists(path);
        }
        {
            Path path = Mockito.mock(Path.class);
            boolean recursive = rnd.nextBoolean();
            lfs.delete(path, recursive);
            Mockito.verify(fs).delete(path, recursive);
        }
        {
            Path path = Mockito.mock(Path.class);
            lfs.mkdirs(path);
            Mockito.verify(fs).mkdirs(path);
        }
        {
            Path path = Mockito.mock(Path.class);
            boolean overwrite = rnd.nextBoolean();
            int bufferSize = rnd.nextInt();
            short replication = ((short) (rnd.nextInt()));
            long blockSize = rnd.nextInt();
            lfs.create(path, overwrite, bufferSize, replication, blockSize);
            Mockito.verify(fs).create(path, overwrite, bufferSize, replication, blockSize);
        }
        {
            Path path = Mockito.mock(Path.class);
            WriteMode mode = (rnd.nextBoolean()) ? WriteMode.OVERWRITE : WriteMode.NO_OVERWRITE;
            lfs.create(path, mode);
            Mockito.verify(fs).create(path, mode);
        }
        {
            Path path1 = Mockito.mock(Path.class);
            Path path2 = Mockito.mock(Path.class);
            lfs.rename(path1, path2);
            Mockito.verify(fs).rename(path1, path2);
        }
        {
            FileSystemKind kind = (rnd.nextBoolean()) ? FILE_SYSTEM : OBJECT_STORE;
            Mockito.when(fs.getKind()).thenReturn(kind);
            Assert.assertEquals(kind, lfs.getKind());
            Mockito.verify(fs).getKind();
        }
    }

    @Test
    public void testDelegateOutStreamMethods() throws IOException {
        // mock the output stream
        final FSDataOutputStream mockOut = Mockito.mock(FSDataOutputStream.class);
        final long outPos = 46651L;
        Mockito.when(mockOut.getPos()).thenReturn(outPos);
        final FileSystem fs = Mockito.mock(FileSystem.class);
        Mockito.when(fs.create(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(WriteMode.class))).thenReturn(mockOut);
        final LimitedConnectionsFileSystem lfs = new LimitedConnectionsFileSystem(fs, 100);
        final FSDataOutputStream out = lfs.create(Mockito.mock(Path.class), OVERWRITE);
        // validate the output stream
        out.write(77);
        Mockito.verify(mockOut).write(77);
        {
            byte[] bytes = new byte[1786];
            out.write(bytes, 100, 111);
            Mockito.verify(mockOut).write(bytes, 100, 111);
        }
        Assert.assertEquals(outPos, out.getPos());
        out.flush();
        Mockito.verify(mockOut).flush();
        out.sync();
        Mockito.verify(mockOut).sync();
        out.close();
        Mockito.verify(mockOut).close();
    }

    @Test
    public void testDelegateInStreamMethods() throws IOException {
        // mock the input stream
        final FSDataInputStream mockIn = Mockito.mock(FSDataInputStream.class);
        final int value = 93;
        final int bytesRead = 11;
        final long inPos = 93;
        final int available = 17;
        final boolean markSupported = true;
        Mockito.when(mockIn.read()).thenReturn(value);
        Mockito.when(mockIn.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(11);
        Mockito.when(mockIn.getPos()).thenReturn(inPos);
        Mockito.when(mockIn.available()).thenReturn(available);
        Mockito.when(mockIn.markSupported()).thenReturn(markSupported);
        final FileSystem fs = Mockito.mock(FileSystem.class);
        Mockito.when(fs.open(ArgumentMatchers.any(Path.class))).thenReturn(mockIn);
        final LimitedConnectionsFileSystem lfs = new LimitedConnectionsFileSystem(fs, 100);
        final FSDataInputStream in = lfs.open(Mockito.mock(Path.class));
        // validate the input stream
        Assert.assertEquals(value, in.read());
        Assert.assertEquals(bytesRead, in.read(new byte[11], 2, 5));
        Assert.assertEquals(inPos, in.getPos());
        in.seek(17876);
        Mockito.verify(mockIn).seek(17876);
        Assert.assertEquals(available, in.available());
        Assert.assertEquals(markSupported, in.markSupported());
        in.mark(9876);
        Mockito.verify(mockIn).mark(9876);
        in.close();
        Mockito.verify(mockIn).close();
    }
}

