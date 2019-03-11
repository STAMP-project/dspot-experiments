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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.shell.CopyCommands.CopyFromLocal;
import org.apache.hadoop.fs.shell.CopyCommands.Cp;
import org.apache.hadoop.fs.shell.CopyCommands.Get;
import org.apache.hadoop.fs.shell.CopyCommands.Put;
import org.junit.Test;


public class TestCopyPreserveFlag {
    private static final int MODIFICATION_TIME = 12345000;

    private static final int ACCESS_TIME = 23456000;

    private static final Path DIR_FROM = new Path("d0");

    private static final Path DIR_FROM_SPL = new Path("d0 space");

    private static final Path DIR_TO1 = new Path("d1");

    private static final Path DIR_TO2 = new Path("d2");

    private static final Path FROM = new Path(TestCopyPreserveFlag.DIR_FROM, "f0");

    private static final Path FROM_SPL = new Path(TestCopyPreserveFlag.DIR_FROM_SPL, "f0");

    private static final Path TO = new Path(TestCopyPreserveFlag.DIR_TO1, "f1");

    private static final FsPermission PERMISSIONS = new FsPermission(FsAction.ALL, FsAction.EXECUTE, FsAction.READ_WRITE);

    private FileSystem fs;

    private Path testDir;

    private Configuration conf;

    @Test(timeout = 10000)
    public void testPutWithP() throws Exception {
        run(new Put(), "-p", TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesPreserved(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testPutWithoutP() throws Exception {
        run(new Put(), TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesChanged(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testPutWithSplCharacter() throws Exception {
        fs.mkdirs(TestCopyPreserveFlag.DIR_FROM_SPL);
        fs.createNewFile(TestCopyPreserveFlag.FROM_SPL);
        run(new Put(), TestCopyPreserveFlag.FROM_SPL.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesChanged(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testCopyFromLocal() throws Exception {
        run(new CopyFromLocal(), TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesChanged(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testCopyFromLocalWithThreads() throws Exception {
        run(new CopyFromLocal(), "-t", "10", TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesChanged(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testCopyFromLocalWithThreadsPreserve() throws Exception {
        run(new CopyFromLocal(), "-p", "-t", "10", TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesPreserved(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testGetWithP() throws Exception {
        run(new Get(), "-p", TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesPreserved(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testGetWithoutP() throws Exception {
        run(new Get(), TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesChanged(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testCpWithP() throws Exception {
        run(new Cp(), "-p", TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesPreserved(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testCpWithoutP() throws Exception {
        run(new Cp(), TestCopyPreserveFlag.FROM.toString(), TestCopyPreserveFlag.TO.toString());
        assertAttributesChanged(TestCopyPreserveFlag.TO);
    }

    @Test(timeout = 10000)
    public void testDirectoryCpWithP() throws Exception {
        run(new Cp(), "-p", TestCopyPreserveFlag.DIR_FROM.toString(), TestCopyPreserveFlag.DIR_TO2.toString());
        assertAttributesPreserved(TestCopyPreserveFlag.DIR_TO2);
    }

    @Test(timeout = 10000)
    public void testDirectoryCpWithoutP() throws Exception {
        run(new Cp(), TestCopyPreserveFlag.DIR_FROM.toString(), TestCopyPreserveFlag.DIR_TO2.toString());
        assertAttributesChanged(TestCopyPreserveFlag.DIR_TO2);
    }
}

