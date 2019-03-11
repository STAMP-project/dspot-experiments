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
package org.apache.hadoop.yarn.server;


import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify if NodeManager's in-memory good local dirs list and good log dirs list
 * get updated properly when disks(nm-local-dirs and nm-log-dirs) fail. Also
 * verify if the overall health status of the node gets updated properly when
 * specified percentage of disks fail.
 */
public class TestDiskFailures {
    private static final Logger LOG = LoggerFactory.getLogger(TestDiskFailures.class);

    /* Set disk check interval high enough so that it never runs during the test.
    Checks will be called manually if necessary.
     */
    private static final long TOO_HIGH_DISK_HEALTH_CHECK_INTERVAL = ((1000 * 60) * 60) * 24;

    private static FileContext localFS = null;

    private static final File testDir = new File("target", TestDiskFailures.class.getName()).getAbsoluteFile();

    private static final File localFSDirBase = new File(TestDiskFailures.testDir, ((TestDiskFailures.class.getName()) + "-localDir"));

    private static final int numLocalDirs = 4;

    private static final int numLogDirs = 4;

    private static MiniYARNCluster yarnCluster;

    LocalDirsHandlerService dirsHandler;

    /**
     * Make local-dirs fail/inaccessible and verify if NodeManager can
     * recognize the disk failures properly and can update the list of
     * local-dirs accordingly with good disks. Also verify the overall
     * health status of the node.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testLocalDirsFailures() throws IOException {
        testDirsFailures(true);
    }

    /**
     * Make log-dirs fail/inaccessible and verify if NodeManager can
     * recognize the disk failures properly and can update the list of
     * log-dirs accordingly with good disks. Also verify the overall health
     * status of the node.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testLogDirsFailures() throws IOException {
        testDirsFailures(false);
    }

    /**
     * Make a local and log directory inaccessible during initialization
     * and verify those bad directories are recognized and removed from
     * the list of available local and log directories.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDirFailuresOnStartup() throws IOException {
        Configuration conf = new YarnConfiguration();
        String localDir1 = new File(TestDiskFailures.testDir, "localDir1").getPath();
        String localDir2 = new File(TestDiskFailures.testDir, "localDir2").getPath();
        String logDir1 = new File(TestDiskFailures.testDir, "logDir1").getPath();
        String logDir2 = new File(TestDiskFailures.testDir, "logDir2").getPath();
        conf.set(NM_LOCAL_DIRS, ((localDir1 + ",") + localDir2));
        conf.set(NM_LOG_DIRS, ((logDir1 + ",") + logDir2));
        prepareDirToFail(localDir1);
        prepareDirToFail(logDir2);
        LocalDirsHandlerService dirSvc = new LocalDirsHandlerService();
        dirSvc.init(conf);
        List<String> localDirs = dirSvc.getLocalDirs();
        Assert.assertEquals(1, localDirs.size());
        Assert.assertEquals(new Path(localDir2).toString(), localDirs.get(0));
        List<String> logDirs = dirSvc.getLogDirs();
        Assert.assertEquals(1, logDirs.size());
        Assert.assertEquals(new Path(logDir1).toString(), logDirs.get(0));
    }
}

