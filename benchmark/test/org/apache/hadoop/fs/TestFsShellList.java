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
package org.apache.hadoop.fs;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import org.apache.hadoop.conf.Configuration;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test FsShell -ls command.
 */
public class TestFsShellList {
    private static Configuration conf;

    private static FsShell shell;

    private static LocalFileSystem lfs;

    private static Path testRootDir;

    @Test
    public void testList() throws Exception {
        createFile(new Path(TestFsShellList.testRootDir, "abc"));
        String[] lsArgv = new String[]{ "-ls", TestFsShellList.testRootDir.toString() };
        Assert.assertThat(TestFsShellList.shell.run(lsArgv), Is.is(0));
        createFile(new Path(TestFsShellList.testRootDir, "abc\bd\tef"));
        createFile(new Path(TestFsShellList.testRootDir, "ghi"));
        createFile(new Path(TestFsShellList.testRootDir, "qq\r123"));
        lsArgv = new String[]{ "-ls", TestFsShellList.testRootDir.toString() };
        Assert.assertThat(TestFsShellList.shell.run(lsArgv), Is.is(0));
        lsArgv = new String[]{ "-ls", "-q", TestFsShellList.testRootDir.toString() };
        Assert.assertThat(TestFsShellList.shell.run(lsArgv), Is.is(0));
    }

    /* UGI params should take effect when we pass. */
    @Test(expected = IllegalArgumentException.class)
    public void testListWithUGI() throws Exception {
        FsShell fsShell = new FsShell(new Configuration());
        // Passing Dummy such that it should through IAE
        fsShell.getConf().set(HADOOP_SECURITY_AUTHENTICATION, "DUMMYAUTH");
        String[] lsArgv = new String[]{ "-ls", TestFsShellList.testRootDir.toString() };
        fsShell.run(lsArgv);
    }
}

