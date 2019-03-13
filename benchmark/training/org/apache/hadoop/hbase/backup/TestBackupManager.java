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
package org.apache.hadoop.hbase.backup;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongArray;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.backup.impl.BackupManager;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hbase.thirdparty.com.google.common.util.concurrent.Uninterruptibles;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestBackupManager {
    private static final Logger LOG = LoggerFactory.getLogger(TestBackupManager.class);

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupManager.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static Configuration conf = TestBackupManager.UTIL.getConfiguration();

    protected static MiniHBaseCluster cluster;

    protected static Connection conn;

    protected BackupManager backupManager;

    AtomicLongArray startTimes = new AtomicLongArray(2);

    AtomicLongArray stopTimes = new AtomicLongArray(2);

    @Test
    public void testStartBackupExclusiveOperation() {
        long sleepTime = 2000;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    backupManager.startBackupSession();
                    boolean result = startTimes.compareAndSet(0, 0, System.currentTimeMillis());
                    if (!result) {
                        result = startTimes.compareAndSet(1, 0, System.currentTimeMillis());
                        if (!result) {
                            throw new IOException("PANIC! Unreachable code");
                        }
                    }
                    Thread.sleep(sleepTime);
                    result = stopTimes.compareAndSet(0, 0, System.currentTimeMillis());
                    if (!result) {
                        result = stopTimes.compareAndSet(1, 0, System.currentTimeMillis());
                        if (!result) {
                            throw new IOException("PANIC! Unreachable code");
                        }
                    }
                    backupManager.finishBackupSession();
                } catch (IOException | InterruptedException e) {
                    Assert.fail(("Unexpected exception: " + (e.getMessage())));
                }
            }
        };
        Thread[] workers = new Thread[2];
        for (int i = 0; i < (workers.length); i++) {
            workers[i] = new Thread(r);
            workers[i].start();
        }
        for (int i = 0; i < (workers.length); i++) {
            Uninterruptibles.joinUninterruptibly(workers[i]);
        }
        TestBackupManager.LOG.info((("Diff start time=" + ((startTimes.get(1)) - (startTimes.get(0)))) + "ms"));
        TestBackupManager.LOG.info((("Diff finish time=" + ((stopTimes.get(1)) - (stopTimes.get(0)))) + "ms"));
        Assert.assertTrue((((startTimes.get(1)) - (startTimes.get(0))) >= sleepTime));
        Assert.assertTrue((((stopTimes.get(1)) - (stopTimes.get(0))) >= sleepTime));
    }
}

