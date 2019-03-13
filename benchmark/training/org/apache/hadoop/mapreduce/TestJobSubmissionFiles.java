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
package org.apache.hadoop.mapreduce;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for JobSubmissionFiles Utility class.
 */
public class TestJobSubmissionFiles {
    private static final String USER_1 = "user1@HADOOP.APACHE.ORG";

    private static final String USER_1_SHORT_NAME = "user1";

    private static final String GROUP1_NAME = "group1";

    private static final String GROUP2_NAME = "group2";

    private static final String GROUP3_NAME = "group3";

    private static final String[] GROUP_NAMES = new String[]{ TestJobSubmissionFiles.GROUP1_NAME, TestJobSubmissionFiles.GROUP2_NAME, TestJobSubmissionFiles.GROUP3_NAME };

    @Test
    public void testGetStagingDirWhenFullFileOwnerNameAndFullUserName() throws IOException, InterruptedException {
        Cluster cluster = Mockito.mock(Cluster.class);
        Configuration conf = new Configuration();
        Path stagingPath = Mockito.mock(Path.class);
        UserGroupInformation user = UserGroupInformation.createUserForTesting(TestJobSubmissionFiles.USER_1, TestJobSubmissionFiles.GROUP_NAMES);
        Assert.assertEquals(TestJobSubmissionFiles.USER_1, user.getUserName());
        FileSystem fs = new FileSystemTestHelper.MockFileSystem();
        Mockito.when(cluster.getStagingAreaDir()).thenReturn(stagingPath);
        Mockito.when(stagingPath.getFileSystem(conf)).thenReturn(fs);
        // Staging directory owner full principal name is in lower case.
        String stagingDirOwner = TestJobSubmissionFiles.USER_1.toLowerCase();
        FileStatus fileStatus = new FileStatus(1, true, 1, 1, 100L, 100L, FsPermission.getDefault(), stagingDirOwner, stagingDirOwner, stagingPath);
        Mockito.when(fs.getFileStatus(stagingPath)).thenReturn(fileStatus);
        Assert.assertEquals(stagingPath, JobSubmissionFiles.getStagingDir(cluster, conf, user));
        // Staging directory owner full principal name in upper and lower case
        stagingDirOwner = TestJobSubmissionFiles.USER_1;
        fileStatus = new FileStatus(1, true, 1, 1, 100L, 100L, FsPermission.getDefault(), stagingDirOwner, stagingDirOwner, stagingPath);
        Mockito.when(fs.getFileStatus(stagingPath)).thenReturn(fileStatus);
        Assert.assertEquals(stagingPath, JobSubmissionFiles.getStagingDir(cluster, conf, user));
    }

    @Test(expected = IOException.class)
    public void testGetStagingWhenFileOwnerNameAndCurrentUserNameDoesNotMatch() throws IOException, InterruptedException {
        Cluster cluster = Mockito.mock(Cluster.class);
        Configuration conf = new Configuration();
        String stagingDirOwner = "someuser";
        Path stagingPath = Mockito.mock(Path.class);
        UserGroupInformation user = UserGroupInformation.createUserForTesting(TestJobSubmissionFiles.USER_1, TestJobSubmissionFiles.GROUP_NAMES);
        Assert.assertEquals(TestJobSubmissionFiles.USER_1, user.getUserName());
        FileSystem fs = new FileSystemTestHelper.MockFileSystem();
        FileStatus fileStatus = new FileStatus(1, true, 1, 1, 100L, 100L, FsPermission.getDefault(), stagingDirOwner, stagingDirOwner, stagingPath);
        Mockito.when(stagingPath.getFileSystem(conf)).thenReturn(fs);
        Mockito.when(fs.getFileStatus(stagingPath)).thenReturn(fileStatus);
        Mockito.when(cluster.getStagingAreaDir()).thenReturn(stagingPath);
        Assert.assertEquals(stagingPath, JobSubmissionFiles.getStagingDir(cluster, conf, user));
    }

    @Test
    public void testGetStagingDirWhenShortFileOwnerNameAndFullUserName() throws IOException, InterruptedException {
        Cluster cluster = Mockito.mock(Cluster.class);
        Configuration conf = new Configuration();
        String stagingDirOwner = TestJobSubmissionFiles.USER_1_SHORT_NAME;
        Path stagingPath = Mockito.mock(Path.class);
        UserGroupInformation user = UserGroupInformation.createUserForTesting(TestJobSubmissionFiles.USER_1, TestJobSubmissionFiles.GROUP_NAMES);
        Assert.assertEquals(TestJobSubmissionFiles.USER_1, user.getUserName());
        FileSystem fs = new FileSystemTestHelper.MockFileSystem();
        FileStatus fileStatus = new FileStatus(1, true, 1, 1, 100L, 100L, FsPermission.getDefault(), stagingDirOwner, stagingDirOwner, stagingPath);
        Mockito.when(stagingPath.getFileSystem(conf)).thenReturn(fs);
        Mockito.when(fs.getFileStatus(stagingPath)).thenReturn(fileStatus);
        Mockito.when(cluster.getStagingAreaDir()).thenReturn(stagingPath);
        Assert.assertEquals(stagingPath, JobSubmissionFiles.getStagingDir(cluster, conf, user));
    }

    @Test
    public void testGetStagingDirWhenShortFileOwnerNameAndShortUserName() throws IOException, InterruptedException {
        Cluster cluster = Mockito.mock(Cluster.class);
        Configuration conf = new Configuration();
        String stagingDirOwner = TestJobSubmissionFiles.USER_1_SHORT_NAME;
        Path stagingPath = Mockito.mock(Path.class);
        UserGroupInformation user = UserGroupInformation.createUserForTesting(TestJobSubmissionFiles.USER_1_SHORT_NAME, TestJobSubmissionFiles.GROUP_NAMES);
        Assert.assertEquals(TestJobSubmissionFiles.USER_1_SHORT_NAME, user.getUserName());
        FileSystem fs = new FileSystemTestHelper.MockFileSystem();
        FileStatus fileStatus = new FileStatus(1, true, 1, 1, 100L, 100L, FsPermission.getDefault(), stagingDirOwner, stagingDirOwner, stagingPath);
        Mockito.when(stagingPath.getFileSystem(conf)).thenReturn(fs);
        Mockito.when(fs.getFileStatus(stagingPath)).thenReturn(fileStatus);
        Mockito.when(cluster.getStagingAreaDir()).thenReturn(stagingPath);
        Assert.assertEquals(stagingPath, JobSubmissionFiles.getStagingDir(cluster, conf, user));
    }
}

