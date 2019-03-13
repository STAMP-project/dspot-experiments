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


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.StringReader;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class TestStat extends FileSystemTestHelper {
    static {
        FileSystem.enableSymlinks();
    }

    private static Stat stat;

    private class StatOutput {
        final String doesNotExist;

        final String directory;

        final String file;

        final String[] symlinks;

        final String stickydir;

        StatOutput(String doesNotExist, String directory, String file, String[] symlinks, String stickydir) {
            this.doesNotExist = doesNotExist;
            this.directory = directory;
            this.file = file;
            this.symlinks = symlinks;
            this.stickydir = stickydir;
        }

        void test() throws Exception {
            BufferedReader br;
            FileStatus status;
            try {
                br = new BufferedReader(new StringReader(doesNotExist));
                TestStat.stat.parseExecResult(br);
            } catch (FileNotFoundException e) {
                // expected
            }
            br = new BufferedReader(new StringReader(directory));
            TestStat.stat.parseExecResult(br);
            status = TestStat.stat.getFileStatusForTesting();
            Assert.assertTrue(status.isDirectory());
            br = new BufferedReader(new StringReader(file));
            TestStat.stat.parseExecResult(br);
            status = TestStat.stat.getFileStatusForTesting();
            Assert.assertTrue(status.isFile());
            for (String symlink : symlinks) {
                br = new BufferedReader(new StringReader(symlink));
                TestStat.stat.parseExecResult(br);
                status = TestStat.stat.getFileStatusForTesting();
                Assert.assertTrue(status.isSymlink());
            }
            br = new BufferedReader(new StringReader(stickydir));
            TestStat.stat.parseExecResult(br);
            status = TestStat.stat.getFileStatusForTesting();
            Assert.assertTrue(status.isDirectory());
            Assert.assertTrue(status.getPermission().getStickyBit());
        }
    }

    @Test(timeout = 10000)
    public void testStatLinux() throws Exception {
        String[] symlinks = new String[]{ "6,symbolic link,1373584236,1373584236,777,andrew,andrew,`link' -> `target'", "6,symbolic link,1373584236,1373584236,777,andrew,andrew,'link' -> 'target'" };
        TestStat.StatOutput linux = new TestStat.StatOutput("stat: cannot stat `watermelon': No such file or directory", "4096,directory,1373584236,1373586485,755,andrew,root,`.'", "0,regular empty file,1373584228,1373584228,644,andrew,andrew,`target'", symlinks, "4096,directory,1374622334,1375124212,1755,andrew,andrew,`stickydir'");
        linux.test();
    }

    @Test(timeout = 10000)
    public void testStatFreeBSD() throws Exception {
        String[] symlinks = new String[]{ "6,Symbolic Link,1373508941,1373508941,120755,awang,awang,`link' -> `target'" };
        TestStat.StatOutput freebsd = new TestStat.StatOutput("stat: symtest/link: stat: No such file or directory", "512,Directory,1373583695,1373583669,40755,awang,awang,`link' -> `'", "0,Regular File,1373508937,1373508937,100644,awang,awang,`link' -> `'", symlinks, "512,Directory,1375139537,1375139537,41755,awang,awang,`link' -> `'");
        freebsd.test();
    }

    @Test(timeout = 10000)
    public void testStatFileNotFound() throws Exception {
        Assume.assumeTrue(Stat.isAvailable());
        try {
            TestStat.stat.getFileStatus();
            Assert.fail("Expected FileNotFoundException");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test(timeout = 10000)
    public void testStatEnvironment() throws Exception {
        Assert.assertEquals("C", TestStat.stat.getEnvironment("LANG"));
    }

    @Test(timeout = 10000)
    public void testStat() throws Exception {
        Assume.assumeTrue(Stat.isAvailable());
        FileSystem fs = FileSystem.getLocal(new Configuration());
        Path testDir = new Path(getTestRootPath(fs), "teststat");
        fs.mkdirs(testDir);
        Path sub1 = new Path(testDir, "sub1");
        Path sub2 = new Path(testDir, "sub2");
        fs.mkdirs(sub1);
        fs.createSymlink(sub1, sub2, false);
        FileStatus stat1 = getFileStatus();
        FileStatus stat2 = getFileStatus();
        Assert.assertTrue(stat1.isDirectory());
        Assert.assertFalse(stat2.isDirectory());
        fs.delete(testDir, true);
    }
}

