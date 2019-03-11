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
package org.apache.hadoop.hdfs;


import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test covers privilege related aspects of FsShell
 */
public class TestFsShellPermission {
    private static final String TEST_ROOT = "/testroot";

    private class FileEntry {
        private String path;

        private boolean isDir;

        private String owner;

        private String group;

        private String permission;

        public FileEntry(String path, boolean isDir, String owner, String group, String permission) {
            this.path = path;
            this.isDir = isDir;
            this.owner = owner;
            this.group = group;
            this.permission = permission;
        }

        String getPath() {
            return path;
        }

        boolean isDirectory() {
            return isDir;
        }

        String getOwner() {
            return owner;
        }

        String getGroup() {
            return group;
        }

        String getPermission() {
            return permission;
        }
    }

    /* Each instance of TestDeleteHelper captures one testing scenario.

    To create all files listed in fileEntries, and then delete as user
    doAsuser the deleteEntry with command+options specified in cmdAndOptions.

    When expectedToDelete is true, the deleteEntry is expected to be deleted;
    otherwise, it's not expected to be deleted. At the end of test,
    the existence of deleteEntry is checked against expectedToDelete
    to ensure the command is finished with expected result
     */
    private class TestDeleteHelper {
        private TestFsShellPermission.FileEntry[] fileEntries;

        private TestFsShellPermission.FileEntry deleteEntry;

        private String cmdAndOptions;

        private boolean expectedToDelete;

        final String doAsGroup;

        final UserGroupInformation userUgi;

        public TestDeleteHelper(TestFsShellPermission.FileEntry[] fileEntries, TestFsShellPermission.FileEntry deleteEntry, String cmdAndOptions, String doAsUser, boolean expectedToDelete) {
            this.fileEntries = fileEntries;
            this.deleteEntry = deleteEntry;
            this.cmdAndOptions = cmdAndOptions;
            this.expectedToDelete = expectedToDelete;
            doAsGroup = (doAsUser.equals("hdfs")) ? "supergroup" : "users";
            userUgi = TestFsShellPermission.createUGI(doAsUser, doAsGroup);
        }

        public void execute(Configuration conf, FileSystem fs) throws Exception {
            fs.mkdirs(new Path(TestFsShellPermission.TEST_ROOT));
            createFiles(fs, TestFsShellPermission.TEST_ROOT, fileEntries);
            final FsShell fsShell = new FsShell(conf);
            final String deletePath = ((TestFsShellPermission.TEST_ROOT) + "/") + (deleteEntry.getPath());
            String[] tmpCmdOpts = StringUtils.split(cmdAndOptions);
            ArrayList<String> tmpArray = new ArrayList<String>(Arrays.asList(tmpCmdOpts));
            tmpArray.add(deletePath);
            final String[] cmdOpts = tmpArray.toArray(new String[tmpArray.size()]);
            userUgi.doAs(new PrivilegedExceptionAction<String>() {
                public String run() throws Exception {
                    return TestFsShellPermission.execCmd(fsShell, cmdOpts);
                }
            });
            boolean deleted = !(fs.exists(new Path(deletePath)));
            Assert.assertEquals(expectedToDelete, deleted);
            TestFsShellPermission.deldir(fs, TestFsShellPermission.TEST_ROOT);
        }
    }

    @Test
    public void testDelete() throws Exception {
        Configuration conf = null;
        MiniDFSCluster cluster = null;
        try {
            conf = new Configuration();
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
            String nnUri = FileSystem.getDefaultUri(conf).toString();
            FileSystem fs = FileSystem.get(URI.create(nnUri), conf);
            ArrayList<TestFsShellPermission.TestDeleteHelper> ta = new ArrayList<TestFsShellPermission.TestDeleteHelper>();
            // Add empty dir tests
            ta.add(genRmrEmptyDirWithReadPerm());
            ta.add(genRmrEmptyDirWithNoPerm());
            ta.add(genRmrfEmptyDirWithNoPerm());
            // Add non-empty dir tests
            ta.add(genRmrNonEmptyDirWithReadPerm());
            ta.add(genRmrNonEmptyDirWithNoPerm());
            ta.add(genRmrNonEmptyDirWithAllPerm());
            ta.add(genRmrfNonEmptyDirWithNoPerm());
            // Add single tile test
            ta.add(genDeleteSingleFileNotAsOwner());
            // Run all tests
            for (TestFsShellPermission.TestDeleteHelper t : ta) {
                t.execute(conf, fs);
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

