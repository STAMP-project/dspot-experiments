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
package org.apache.hadoop.hdfs.server.namenode;


import FSEditLog.LOG;
import com.google.common.base.Supplier;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem.NameNodeEditLogRoller;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


@RunWith(Parameterized.class)
public class TestEditLogAutoroll {
    static {
        GenericTestUtils.setLogLevel(FSEditLog.LOG, Level.DEBUG);
    }

    private static boolean useAsyncEditLog;

    public TestEditLogAutoroll(Boolean async) {
        TestEditLogAutoroll.useAsyncEditLog = async;
    }

    private Configuration conf;

    private MiniDFSCluster cluster;

    private NameNode nn0;

    private FileSystem fs;

    private FSEditLog editLog;

    private final Random random = new Random();

    public static final Logger LOG = LoggerFactory.getLogger(FSEditLog.class);

    @Test(timeout = 60000)
    public void testEditLogAutoroll() throws Exception {
        // Make some edits
        final long startTxId = editLog.getCurSegmentTxId();
        for (int i = 0; i < 11; i++) {
            fs.mkdirs(new Path(("testEditLogAutoroll-" + i)));
        }
        // Wait for the NN to autoroll
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (editLog.getCurSegmentTxId()) > startTxId;
            }
        }, 1000, 5000);
        // Transition to standby and make sure the roller stopped
        nn0.transitionToStandby();
        GenericTestUtils.assertNoThreadsMatching(((".*" + (NameNodeEditLogRoller.class.getSimpleName())) + ".*"));
    }
}

