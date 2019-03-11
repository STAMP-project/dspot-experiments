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
package org.apache.hadoop.tools;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.HarFs;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestHadoopArchiveLogsRunner {
    private static final int FILE_SIZE_INCREMENT = 4096;

    private static final int[] FILE_SIZES = new int[]{ 3, 4, 2 };

    private static final int FILE_COUNT = TestHadoopArchiveLogsRunner.FILE_SIZES.length;

    private static final byte[] DUMMY_DATA = new byte[TestHadoopArchiveLogsRunner.FILE_SIZE_INCREMENT];

    static {
        new Random().nextBytes(TestHadoopArchiveLogsRunner.DUMMY_DATA);
    }

    private Configuration conf;

    private MiniDFSCluster dfsCluster;

    private MiniYARNCluster yarnCluster;

    private FileSystem fs;

    private ApplicationId app1;

    private Path app1Path;

    private Path workingDir;

    private Path remoteRootLogDir;

    private String suffix;

    @Rule
    public Timeout globalTimeout = new Timeout(50000);

    @Test
    public void testHadoopArchiveLogs() throws Exception {
        String[] args = getArgs();
        final HadoopArchiveLogsRunner halr = new HadoopArchiveLogsRunner(conf);
        Assert.assertEquals(0, ToolRunner.run(halr, args));
        fs = FileSystem.get(conf);
        FileStatus[] app1Files = fs.listStatus(app1Path);
        Assert.assertEquals(1, app1Files.length);
        FileStatus harFile = app1Files[0];
        Assert.assertEquals(((app1.toString()) + ".har"), harFile.getPath().getName());
        Path harPath = new Path(("har:///" + (harFile.getPath().toUri().getRawPath())));
        FileStatus[] harLogs = HarFs.get(harPath.toUri(), conf).listStatus(harPath);
        Assert.assertEquals(TestHadoopArchiveLogsRunner.FILE_COUNT, harLogs.length);
        Arrays.sort(harLogs, new Comparator<FileStatus>() {
            @Override
            public int compare(FileStatus o1, FileStatus o2) {
                return o1.getPath().getName().compareTo(o2.getPath().getName());
            }
        });
        for (int i = 0; i < (TestHadoopArchiveLogsRunner.FILE_COUNT); i++) {
            FileStatus harLog = harLogs[i];
            Assert.assertEquals(("log" + (i + 1)), harLog.getPath().getName());
            Assert.assertEquals(((TestHadoopArchiveLogsRunner.FILE_SIZES[i]) * (TestHadoopArchiveLogsRunner.FILE_SIZE_INCREMENT)), harLog.getLen());
            Assert.assertEquals(new org.apache.hadoop.fs.permission.FsPermission(FsAction.READ_WRITE, FsAction.READ, FsAction.NONE), harLog.getPermission());
            Assert.assertEquals(System.getProperty("user.name"), harLog.getOwner());
        }
        Assert.assertEquals(0, fs.listStatus(workingDir).length);
    }

    @Test
    public void testHadoopArchiveLogsWithArchiveError() throws Exception {
        String[] args = getArgs();
        final HadoopArchiveLogsRunner halr = new HadoopArchiveLogsRunner(conf);
        HadoopArchives mockHadoopArchives = Mockito.mock(HadoopArchives.class);
        Mockito.when(mockHadoopArchives.run(Mockito.<String[]>any())).thenReturn((-1));
        halr.hadoopArchives = mockHadoopArchives;
        Assert.assertNotEquals(0, ToolRunner.run(halr, args));
        // Make sure the original log files are intact
        FileStatus[] app1Files = fs.listStatus(app1Path);
        Assert.assertEquals(TestHadoopArchiveLogsRunner.FILE_COUNT, app1Files.length);
        for (int i = 0; i < (TestHadoopArchiveLogsRunner.FILE_COUNT); i++) {
            Assert.assertEquals(((TestHadoopArchiveLogsRunner.FILE_SIZES[i]) * (TestHadoopArchiveLogsRunner.FILE_SIZE_INCREMENT)), app1Files[i].getLen());
        }
    }
}

