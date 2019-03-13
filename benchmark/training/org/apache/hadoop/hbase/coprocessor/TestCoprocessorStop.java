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
package org.apache.hadoop.hbase.coprocessor;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for master and regionserver coprocessor stop method
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestCoprocessorStop {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorStop.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCoprocessorStop.class);

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final String MASTER_FILE = "master" + (System.currentTimeMillis());

    private static final String REGIONSERVER_FILE = "regionserver" + (System.currentTimeMillis());

    public static class FooCoprocessor implements MasterCoprocessor , RegionServerCoprocessor {
        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            String where = null;
            if (env instanceof MasterCoprocessorEnvironment) {
                // if running on HMaster
                where = "master";
            } else
                if (env instanceof RegionServerCoprocessorEnvironment) {
                    where = "regionserver";
                } else
                    if (env instanceof RegionCoprocessorEnvironment) {
                        TestCoprocessorStop.LOG.error("on RegionCoprocessorEnvironment!!");
                    }


            TestCoprocessorStop.LOG.info(("start coprocessor on " + where));
        }

        @Override
        public void stop(CoprocessorEnvironment env) throws IOException {
            String fileName = null;
            if (env instanceof MasterCoprocessorEnvironment) {
                // if running on HMaster
                fileName = TestCoprocessorStop.MASTER_FILE;
            } else
                if (env instanceof RegionServerCoprocessorEnvironment) {
                    fileName = TestCoprocessorStop.REGIONSERVER_FILE;
                } else
                    if (env instanceof RegionCoprocessorEnvironment) {
                        TestCoprocessorStop.LOG.error("on RegionCoprocessorEnvironment!!");
                    }


            Configuration conf = TestCoprocessorStop.UTIL.getConfiguration();
            Path resultFile = new Path(TestCoprocessorStop.UTIL.getDataTestDirOnTestFS(), fileName);
            FileSystem fs = FileSystem.get(conf);
            boolean result = fs.createNewFile(resultFile);
            TestCoprocessorStop.LOG.info(((("create file " + resultFile) + " return rc ") + result));
        }
    }

    @Test
    public void testStopped() throws Exception {
        // shutdown hbase only. then check flag file.
        MiniHBaseCluster cluster = TestCoprocessorStop.UTIL.getHBaseCluster();
        TestCoprocessorStop.LOG.info("shutdown hbase cluster...");
        cluster.shutdown();
        TestCoprocessorStop.LOG.info("wait for the hbase cluster shutdown...");
        cluster.waitUntilShutDown();
        Configuration conf = TestCoprocessorStop.UTIL.getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        Path resultFile = new Path(TestCoprocessorStop.UTIL.getDataTestDirOnTestFS(), TestCoprocessorStop.MASTER_FILE);
        Assert.assertTrue("Master flag file should have been created", fs.exists(resultFile));
        resultFile = new Path(TestCoprocessorStop.UTIL.getDataTestDirOnTestFS(), TestCoprocessorStop.REGIONSERVER_FILE);
        Assert.assertTrue("RegionServer flag file should have been created", fs.exists(resultFile));
    }
}

