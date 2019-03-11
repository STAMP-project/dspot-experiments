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


import FsShellPermissions.Chgrp;
import FsShellPermissions.Chown;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.shell.CommandFactory;
import org.apache.hadoop.fs.shell.FsCommand;
import org.apache.hadoop.fs.shell.PathData;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test validates that chmod, chown, chgrp returning correct exit codes
 */
public class TestFsShellReturnCode {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.fs.TestFsShellReturnCode");

    private static final Configuration conf = new Configuration();

    private static FileSystem fileSys;

    private static FsShell fsShell;

    private static String TEST_ROOT_DIR = GenericTestUtils.getTempPath("testCHReturnCode");

    /**
     * Test Chmod 1. Create and write file on FS 2. Verify that exit code for
     * chmod on existing file is 0 3. Verify that exit code for chmod on
     * non-existing file is 1 4. Verify that exit code for chmod with glob input
     * on non-existing file is 1 5. Verify that exit code for chmod with glob
     * input on existing file in 0
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testChmod() throws Exception {
        Path p1 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChmod/fileExists");
        final String f1 = p1.toUri().getPath();
        final String f2 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChmod/fileDoesNotExist").toUri().getPath();
        final String f3 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChmod/nonExistingfiles*").toUri().getPath();
        final Path p4 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChmod/file1");
        final Path p5 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChmod/file2");
        final Path p6 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChmod/file3");
        final String f7 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChmod/file*").toUri().getPath();
        // create and write test file
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p1);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p1));
        // Test 1: Test 1: exit code for chmod on existing is 0
        String[] argv = new String[]{ "-chmod", "777", f1 };
        Assert.assertEquals(0, TestFsShellReturnCode.fsShell.run(argv));
        // Test 2: exit code for chmod on non-existing path is 1
        String[] argv2 = new String[]{ "-chmod", "777", f2 };
        Assert.assertEquals(1, TestFsShellReturnCode.fsShell.run(argv2));
        // Test 3: exit code for chmod on non-existing path with globbed input is 1
        String[] argv3 = new String[]{ "-chmod", "777", f3 };
        Assert.assertEquals(1, TestFsShellReturnCode.fsShell.run(argv3));
        // create required files
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p4);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p4));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p5);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p5));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p6);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p6));
        // Test 4: exit code for chmod on existing path with globbed input is 0
        String[] argv4 = new String[]{ "-chmod", "777", f7 };
        Assert.assertEquals(0, TestFsShellReturnCode.fsShell.run(argv4));
    }

    /**
     * Test Chown 1. Create and write file on FS 2. Verify that exit code for
     * Chown on existing file is 0 3. Verify that exit code for Chown on
     * non-existing file is 1 4. Verify that exit code for Chown with glob input
     * on non-existing file is 1 5. Verify that exit code for Chown with glob
     * input on existing file in 0
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testChown() throws Exception {
        Path p1 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChown/fileExists");
        final String f1 = p1.toUri().getPath();
        final String f2 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChown/fileDoesNotExist").toUri().getPath();
        final String f3 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChown/nonExistingfiles*").toUri().getPath();
        final Path p4 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChown/file1");
        final Path p5 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChown/file2");
        final Path p6 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChown/file3");
        final String f7 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChown/file*").toUri().getPath();
        // create and write test file
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p1);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p1));
        // Test 1: exit code for chown on existing file is 0
        change(0, "admin", null, f1);
        // Test 2: exit code for chown on non-existing path is 1
        change(1, "admin", null, f2);
        // Test 3: exit code for chown on non-existing path with globbed input is 1
        change(1, "admin", null, f3);
        // create required files
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p4);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p4));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p5);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p5));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p6);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p6));
        // Test 4: exit code for chown on existing path with globbed input is 0
        change(0, "admin", null, f7);
        // Test 5: test for setOwner invocation on FS from command handler.
        change(0, "admin", "Test", f1);
        change(0, "admin", "", f1);
    }

    /**
     * Test Chgrp 1. Create and write file on FS 2. Verify that exit code for
     * chgrp on existing file is 0 3. Verify that exit code for chgrp on
     * non-existing file is 1 4. Verify that exit code for chgrp with glob input
     * on non-existing file is 1 5. Verify that exit code for chgrp with glob
     * input on existing file in 0
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testChgrp() throws Exception {
        Path p1 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChgrp/fileExists");
        final String f1 = p1.toUri().getPath();
        final String f2 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChgrp/fileDoesNotExist").toUri().getPath();
        final String f3 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChgrp/nonExistingfiles*").toUri().getPath();
        final Path p4 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChgrp/file1");
        final Path p5 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChgrp/file2");
        final Path p6 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChgrp/file3");
        final String f7 = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testChgrp/file*").toUri().getPath();
        // create and write test file
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p1);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p1));
        // Test 1: exit code for chgrp on existing file is 0
        change(0, null, "admin", f1);
        // Test 2: exit code for chgrp on non existing path is 1
        change(1, null, "admin", f2);
        change(1, null, "admin", f2, f1);// exit code used to be for last item

        // Test 3: exit code for chgrp on non-existing path with globbed input is 1
        change(1, null, "admin", f3);
        change(1, null, "admin", f3, f1);
        // create required files
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p4);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p4));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p5);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p5));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, p6);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.exists(p6));
        // Test 4: exit code for chgrp on existing path with globbed input is 0
        change(0, null, "admin", f7);
        change(1, null, "admin", f2, f7);
    }

    @Test(timeout = 30000)
    public void testGetWithInvalidSourcePathShouldNotDisplayNullInConsole() throws Exception {
        Configuration conf = new Configuration();
        FsShell shell = new FsShell();
        shell.setConf(conf);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream(bytes);
        final PrintStream oldErr = System.err;
        System.setErr(out);
        final String results;
        try {
            Path tdir = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "notNullCopy");
            TestFsShellReturnCode.fileSys.delete(tdir, true);
            TestFsShellReturnCode.fileSys.mkdirs(tdir);
            String[] args = new String[3];
            args[0] = "-get";
            args[1] = new Path(tdir.toUri().getPath(), "/invalidSrc").toString();
            args[2] = new Path(tdir.toUri().getPath(), "/invalidDst").toString();
            Assert.assertTrue("file exists", (!(TestFsShellReturnCode.fileSys.exists(new Path(args[1])))));
            Assert.assertTrue("file exists", (!(TestFsShellReturnCode.fileSys.exists(new Path(args[2])))));
            int run = shell.run(args);
            results = bytes.toString();
            Assert.assertEquals("Return code should be 1", 1, run);
            Assert.assertTrue(" Null is coming when source path is invalid. ", (!(results.contains("get: null"))));
            Assert.assertTrue(" Not displaying the intended message ", results.contains((("get: `" + (args[1])) + "': No such file or directory")));
        } finally {
            IOUtils.closeStream(out);
            System.setErr(oldErr);
        }
    }

    @Test(timeout = 30000)
    public void testRmWithNonexistentGlob() throws Exception {
        Configuration conf = new Configuration();
        FsShell shell = new FsShell();
        shell.setConf(conf);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream err = new PrintStream(bytes);
        final PrintStream oldErr = System.err;
        System.setErr(err);
        final String results;
        try {
            int exit = shell.run(new String[]{ "-rm", "nomatch*" });
            Assert.assertEquals(1, exit);
            results = bytes.toString();
            Assert.assertTrue(results.contains("rm: `nomatch*': No such file or directory"));
        } finally {
            IOUtils.closeStream(err);
            System.setErr(oldErr);
        }
    }

    @Test(timeout = 30000)
    public void testRmForceWithNonexistentGlob() throws Exception {
        Configuration conf = new Configuration();
        FsShell shell = new FsShell();
        shell.setConf(conf);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream err = new PrintStream(bytes);
        final PrintStream oldErr = System.err;
        System.setErr(err);
        try {
            int exit = shell.run(new String[]{ "-rm", "-f", "nomatch*" });
            Assert.assertEquals(0, exit);
            Assert.assertTrue(bytes.toString().isEmpty());
        } finally {
            IOUtils.closeStream(err);
            System.setErr(oldErr);
        }
    }

    @Test(timeout = 30000)
    public void testInvalidDefaultFS() throws Exception {
        // if default fs doesn't exist or is invalid, but the path provided in
        // arguments is valid - fsshell should work
        FsShell shell = new FsShell();
        Configuration conf = new Configuration();
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, "hhhh://doesnotexist/");
        shell.setConf(conf);
        String[] args = new String[2];
        args[0] = "-ls";
        args[1] = "file:///";// this is valid, so command should run

        int res = shell.run(args);
        System.out.println(("res =" + res));
        shell.setConf(conf);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream(bytes);
        final PrintStream oldErr = System.err;
        System.setErr(out);
        final String results;
        try {
            int run = shell.run(args);
            results = bytes.toString();
            TestFsShellReturnCode.LOG.info(("result=" + results));
            Assert.assertTrue("Return code should be 0", (run == 0));
        } finally {
            IOUtils.closeStream(out);
            System.setErr(oldErr);
        }
    }

    @Test(timeout = 30000)
    public void testInterrupt() throws Exception {
        TestFsShellReturnCode.MyFsShell shell = new TestFsShellReturnCode.MyFsShell();
        shell.setConf(new Configuration());
        final Path d = new Path(TestFsShellReturnCode.TEST_ROOT_DIR, "testInterrupt");
        final Path f1 = new Path(d, "f1");
        final Path f2 = new Path(d, "f2");
        Assert.assertTrue(TestFsShellReturnCode.fileSys.mkdirs(d));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, f1);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.isFile(f1));
        TestFsShellReturnCode.writeFile(TestFsShellReturnCode.fileSys, f2);
        Assert.assertTrue(TestFsShellReturnCode.fileSys.isFile(f2));
        int exitCode = run(new String[]{ "-testInterrupt", f1.toString(), f2.toString() });
        // processing a file throws an interrupt, it should blow on first file
        Assert.assertEquals(1, TestFsShellReturnCode.InterruptCommand.processed);
        Assert.assertEquals(130, exitCode);
        exitCode = run(new String[]{ "-testInterrupt", d.toString() });
        // processing a file throws an interrupt, it should blow on file
        // after descent into dir
        Assert.assertEquals(2, TestFsShellReturnCode.InterruptCommand.processed);
        Assert.assertEquals(130, exitCode);
    }

    /**
     * Faked Chown class for {@link testChownUserAndGroupValidity()}.
     *
     * The test only covers argument parsing, so override to skip processing.
     */
    private static class FakeChown extends FsShellPermissions.Chown {
        public static String NAME = "chown";

        @Override
        protected void processArgument(PathData item) {
        }
    }

    /**
     * Tests combinations of valid and invalid user and group arguments to chown.
     */
    @Test
    public void testChownUserAndGroupValidity() {
        testChownUserAndGroupValidity(true);
        testChownUserAndGroupValidity(false);
    }

    /**
     * Faked Chgrp class for {@link testChgrpGroupValidity()}.
     * The test only covers argument parsing, so override to skip processing.
     */
    private static class FakeChgrp extends FsShellPermissions.Chgrp {
        public static String NAME = "chgrp";

        @Override
        protected void processArgument(PathData item) {
        }
    }

    /**
     * Tests valid and invalid group arguments to chgrp.
     */
    @Test
    public void testChgrpGroupValidity() {
        testChgrpGroupValidity(true);
        testChgrpGroupValidity(false);
    }

    static class LocalFileSystemExtn extends LocalFileSystem {
        public LocalFileSystemExtn() {
            super(new TestFsShellReturnCode.RawLocalFileSystemExtn());
        }
    }

    static class RawLocalFileSystemExtn extends RawLocalFileSystem {
        protected static HashMap<String, String> owners = new HashMap<String, String>();

        protected static HashMap<String, String> groups = new HashMap<String, String>();

        @Override
        public FSDataOutputStream create(Path p) throws IOException {
            // owners.remove(p);
            // groups.remove(p);
            return super.create(p);
        }

        @Override
        public void setOwner(Path p, String username, String groupname) throws IOException {
            String f = makeQualified(p).toString();
            if (username != null) {
                TestFsShellReturnCode.RawLocalFileSystemExtn.owners.put(f, username);
            }
            if (groupname != null) {
                TestFsShellReturnCode.RawLocalFileSystemExtn.groups.put(f, groupname);
            }
        }

        @Override
        public FileStatus getFileStatus(Path p) throws IOException {
            String f = makeQualified(p).toString();
            FileStatus stat = super.getFileStatus(p);
            stat.getPermission();
            if (TestFsShellReturnCode.RawLocalFileSystemExtn.owners.containsKey(f)) {
                stat.setOwner(("STUB-" + (TestFsShellReturnCode.RawLocalFileSystemExtn.owners.get(f))));
            } else {
                stat.setOwner(("REAL-" + (stat.getOwner())));
            }
            if (TestFsShellReturnCode.RawLocalFileSystemExtn.groups.containsKey(f)) {
                stat.setGroup(("STUB-" + (TestFsShellReturnCode.RawLocalFileSystemExtn.groups.get(f))));
            } else {
                stat.setGroup(("REAL-" + (stat.getGroup())));
            }
            return stat;
        }
    }

    static class MyFsShell extends FsShell {
        @Override
        protected void registerCommands(CommandFactory factory) {
            factory.addClass(TestFsShellReturnCode.InterruptCommand.class, "-testInterrupt");
        }
    }

    static class InterruptCommand extends FsCommand {
        static int processed = 0;

        InterruptCommand() {
            TestFsShellReturnCode.InterruptCommand.processed = 0;
            setRecursive(true);
        }

        @Override
        protected void processPath(PathData item) throws IOException {
            System.out.println(("processing: " + item));
            (TestFsShellReturnCode.InterruptCommand.processed)++;
            if (item.stat.isFile()) {
                System.out.println("throw interrupt");
                throw new InterruptedIOException();
            }
        }
    }
}

