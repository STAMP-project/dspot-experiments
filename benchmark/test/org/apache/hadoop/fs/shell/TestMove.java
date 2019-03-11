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


import MoveCommands.Rename;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathExistsException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestMove {
    static Configuration conf;

    static FileSystem mockFs;

    @Test
    public void testMoveTargetExistsWithoutExplicitRename() throws Exception {
        Path srcPath = new Path("mockfs:/file");
        Path targetPath = new Path("mockfs:/fold0");
        Path dupPath = new Path("mockfs:/fold0/file");
        Path srcPath2 = new Path("mockfs://user/file");
        Path targetPath2 = new Path("mockfs://user/fold0");
        Path dupPath2 = new Path("mockfs://user/fold0/file");
        TestMove.InstrumentedRenameCommand cmd;
        String[] cmdargs = new String[]{ "mockfs:/file", "mockfs:/fold0" };
        FileStatus src_fileStat;
        FileStatus target_fileStat;
        FileStatus dup_fileStat;
        URI myuri;
        src_fileStat = Mockito.mock(FileStatus.class);
        target_fileStat = Mockito.mock(FileStatus.class);
        dup_fileStat = Mockito.mock(FileStatus.class);
        myuri = new URI("mockfs://user");
        Mockito.when(src_fileStat.isDirectory()).thenReturn(false);
        Mockito.when(target_fileStat.isDirectory()).thenReturn(true);
        Mockito.when(dup_fileStat.isDirectory()).thenReturn(false);
        Mockito.when(src_fileStat.getPath()).thenReturn(srcPath2);
        Mockito.when(target_fileStat.getPath()).thenReturn(targetPath2);
        Mockito.when(dup_fileStat.getPath()).thenReturn(dupPath2);
        Mockito.when(TestMove.mockFs.getFileStatus(ArgumentMatchers.eq(srcPath))).thenReturn(src_fileStat);
        Mockito.when(TestMove.mockFs.getFileStatus(ArgumentMatchers.eq(targetPath))).thenReturn(target_fileStat);
        Mockito.when(TestMove.mockFs.getFileStatus(ArgumentMatchers.eq(dupPath))).thenReturn(dup_fileStat);
        Mockito.when(TestMove.mockFs.getFileStatus(ArgumentMatchers.eq(srcPath2))).thenReturn(src_fileStat);
        Mockito.when(TestMove.mockFs.getFileStatus(ArgumentMatchers.eq(targetPath2))).thenReturn(target_fileStat);
        Mockito.when(TestMove.mockFs.getFileStatus(ArgumentMatchers.eq(dupPath2))).thenReturn(dup_fileStat);
        Mockito.when(TestMove.mockFs.getUri()).thenReturn(myuri);
        cmd = new TestMove.InstrumentedRenameCommand();
        cmd.setConf(TestMove.conf);
        setOverwrite(true);
        run(cmdargs);
        // make sure command failed with the proper exception
        Assert.assertTrue("Rename should have failed with path exists exception", ((cmd.error) instanceof PathExistsException));
    }

    static class MockFileSystem extends FilterFileSystem {
        Configuration conf;

        MockFileSystem() {
            super(TestMove.mockFs);
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

    private static class InstrumentedRenameCommand extends MoveCommands.Rename {
        public static String NAME = "InstrumentedRename";

        private Exception error = null;

        @Override
        public void displayError(Exception e) {
            error = e;
        }
    }
}

