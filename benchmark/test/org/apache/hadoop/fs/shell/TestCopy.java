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
package org.apache.hadoop.fs.shell;


import java.io.InterruptedIOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.shell.CopyCommands.Put;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestCopy {
    static Configuration conf;

    static Path path = new Path("mockfs:/file");

    static Path tmpPath = new Path("mockfs:/file._COPYING_");

    static Put cmd;

    static FileSystem mockFs;

    static PathData target;

    static FileStatus fileStat;

    @Test
    public void testCopyStreamTarget() throws Exception {
        FSDataOutputStream out = Mockito.mock(FSDataOutputStream.class);
        whenFsCreate().thenReturn(out);
        Mockito.when(TestCopy.mockFs.getFileStatus(ArgumentMatchers.eq(TestCopy.tmpPath))).thenReturn(TestCopy.fileStat);
        Mockito.when(TestCopy.mockFs.rename(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.eq(TestCopy.path))).thenReturn(true);
        FSInputStream in = Mockito.mock(FSInputStream.class);
        Mockito.when(in.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn((-1));
        tryCopyStream(in, true);
        Mockito.verify(TestCopy.mockFs, Mockito.never()).delete(ArgumentMatchers.eq(TestCopy.path), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs).rename(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.eq(TestCopy.path));
        Mockito.verify(TestCopy.mockFs, Mockito.never()).delete(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs, Mockito.never()).close();
    }

    @Test
    public void testCopyStreamTargetExists() throws Exception {
        FSDataOutputStream out = Mockito.mock(FSDataOutputStream.class);
        whenFsCreate().thenReturn(out);
        Mockito.when(TestCopy.mockFs.getFileStatus(ArgumentMatchers.eq(TestCopy.path))).thenReturn(TestCopy.fileStat);
        TestCopy.target.refreshStatus();// so it's updated as existing

        TestCopy.cmd.setOverwrite(true);
        Mockito.when(TestCopy.mockFs.getFileStatus(ArgumentMatchers.eq(TestCopy.tmpPath))).thenReturn(TestCopy.fileStat);
        Mockito.when(TestCopy.mockFs.delete(ArgumentMatchers.eq(TestCopy.path), ArgumentMatchers.eq(false))).thenReturn(true);
        Mockito.when(TestCopy.mockFs.rename(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.eq(TestCopy.path))).thenReturn(true);
        FSInputStream in = Mockito.mock(FSInputStream.class);
        Mockito.when(in.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn((-1));
        tryCopyStream(in, true);
        Mockito.verify(TestCopy.mockFs).delete(ArgumentMatchers.eq(TestCopy.path), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs).rename(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.eq(TestCopy.path));
        Mockito.verify(TestCopy.mockFs, Mockito.never()).delete(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs, Mockito.never()).close();
    }

    @Test
    public void testInterruptedCreate() throws Exception {
        whenFsCreate().thenThrow(new InterruptedIOException());
        Mockito.when(TestCopy.mockFs.getFileStatus(ArgumentMatchers.eq(TestCopy.tmpPath))).thenReturn(TestCopy.fileStat);
        FSDataInputStream in = Mockito.mock(FSDataInputStream.class);
        tryCopyStream(in, false);
        Mockito.verify(TestCopy.mockFs).delete(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs, Mockito.never()).rename(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class));
        Mockito.verify(TestCopy.mockFs, Mockito.never()).delete(ArgumentMatchers.eq(TestCopy.path), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs, Mockito.never()).close();
    }

    @Test
    public void testInterruptedCopyBytes() throws Exception {
        FSDataOutputStream out = Mockito.mock(FSDataOutputStream.class);
        whenFsCreate().thenReturn(out);
        Mockito.when(TestCopy.mockFs.getFileStatus(ArgumentMatchers.eq(TestCopy.tmpPath))).thenReturn(TestCopy.fileStat);
        FSInputStream in = Mockito.mock(FSInputStream.class);
        // make IOUtils.copyBytes fail
        Mockito.when(in.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenThrow(new InterruptedIOException());
        tryCopyStream(in, false);
        Mockito.verify(TestCopy.mockFs).delete(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs, Mockito.never()).rename(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class));
        Mockito.verify(TestCopy.mockFs, Mockito.never()).delete(ArgumentMatchers.eq(TestCopy.path), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs, Mockito.never()).close();
    }

    @Test
    public void testInterruptedRename() throws Exception {
        FSDataOutputStream out = Mockito.mock(FSDataOutputStream.class);
        whenFsCreate().thenReturn(out);
        Mockito.when(TestCopy.mockFs.getFileStatus(ArgumentMatchers.eq(TestCopy.tmpPath))).thenReturn(TestCopy.fileStat);
        Mockito.when(TestCopy.mockFs.rename(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.eq(TestCopy.path))).thenThrow(new InterruptedIOException());
        FSInputStream in = Mockito.mock(FSInputStream.class);
        Mockito.when(in.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn((-1));
        tryCopyStream(in, false);
        Mockito.verify(TestCopy.mockFs).delete(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs).rename(ArgumentMatchers.eq(TestCopy.tmpPath), ArgumentMatchers.eq(TestCopy.path));
        Mockito.verify(TestCopy.mockFs, Mockito.never()).delete(ArgumentMatchers.eq(TestCopy.path), ArgumentMatchers.anyBoolean());
        Mockito.verify(TestCopy.mockFs, Mockito.never()).close();
    }

    static class MockFileSystem extends FilterFileSystem {
        Configuration conf;

        MockFileSystem() {
            super(TestCopy.mockFs);
        }

        @Override
        public void initialize(URI uri, Configuration conf) {
            this.conf = conf;
        }

        @Override
        public Path makeQualified(Path path) {
            return path;
        }

        @Override
        public Configuration getConf() {
            return conf;
        }
    }
}

