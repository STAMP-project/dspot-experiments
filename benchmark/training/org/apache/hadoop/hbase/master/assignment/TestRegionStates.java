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
package org.apache.hadoop.hbase.master.assignment;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.procedure2.util.StringUtils;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestRegionStates {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionStates.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionStates.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static ThreadPoolExecutor threadPool;

    private static ExecutorCompletionService executorService;

    // ==========================================================================
    // Regions related
    // ==========================================================================
    @Test
    public void testRegionDoubleCreation() throws Exception {
        // NOTE: RegionInfo sort by table first, so we are relying on that
        final TableName TABLE_NAME_A = TableName.valueOf("testOrderedByTableA");
        final TableName TABLE_NAME_B = TableName.valueOf("testOrderedByTableB");
        final TableName TABLE_NAME_C = TableName.valueOf("testOrderedByTableC");
        final RegionStates stateMap = new RegionStates();
        final int NRUNS = 1000;
        final int NSMALL_RUNS = 3;
        // add some regions for table B
        for (int i = 0; i < NRUNS; ++i) {
            addRegionNode(stateMap, TABLE_NAME_B, i);
        }
        // re-add the regions for table B
        for (int i = 0; i < NRUNS; ++i) {
            addRegionNode(stateMap, TABLE_NAME_B, i);
        }
        TestRegionStates.waitExecutorService((NRUNS * 2));
        // add two other tables A and C that will be placed before and after table B (sort order)
        for (int i = 0; i < NSMALL_RUNS; ++i) {
            addRegionNode(stateMap, TABLE_NAME_A, i);
            addRegionNode(stateMap, TABLE_NAME_C, i);
        }
        TestRegionStates.waitExecutorService((NSMALL_RUNS * 2));
        // check for the list of regions of the 3 tables
        checkTableRegions(stateMap, TABLE_NAME_A, NSMALL_RUNS);
        checkTableRegions(stateMap, TABLE_NAME_B, NRUNS);
        checkTableRegions(stateMap, TABLE_NAME_C, NSMALL_RUNS);
    }

    @Test
    public void testPerf() throws Exception {
        final TableName TABLE_NAME = TableName.valueOf("testPerf");
        final int NRUNS = 1000000;// 1M

        final RegionStates stateMap = new RegionStates();
        long st = System.currentTimeMillis();
        for (int i = 0; i < NRUNS; ++i) {
            final int regionId = i;
            TestRegionStates.executorService.submit(new Callable<Object>() {
                @Override
                public Object call() {
                    RegionInfo hri = createRegionInfo(TABLE_NAME, regionId);
                    return stateMap.getOrCreateRegionStateNode(hri);
                }
            });
        }
        TestRegionStates.waitExecutorService(NRUNS);
        long et = System.currentTimeMillis();
        TestRegionStates.LOG.info(String.format("PERF STATEMAP INSERT: %s %s/sec", StringUtils.humanTimeDiff((et - st)), StringUtils.humanSize((NRUNS / ((et - st) / 1000.0F)))));
        st = System.currentTimeMillis();
        for (int i = 0; i < NRUNS; ++i) {
            final int regionId = i;
            TestRegionStates.executorService.submit(new Callable<Object>() {
                @Override
                public Object call() {
                    RegionInfo hri = createRegionInfo(TABLE_NAME, regionId);
                    return stateMap.getRegionState(hri);
                }
            });
        }
        TestRegionStates.waitExecutorService(NRUNS);
        et = System.currentTimeMillis();
        TestRegionStates.LOG.info(String.format("PERF STATEMAP GET: %s %s/sec", StringUtils.humanTimeDiff((et - st)), StringUtils.humanSize((NRUNS / ((et - st) / 1000.0F)))));
    }

    @Test
    public void testPerfSingleThread() {
        final TableName TABLE_NAME = TableName.valueOf("testPerf");
        final int NRUNS = 1 * 1000000;// 1M

        final RegionStates stateMap = new RegionStates();
        long st = System.currentTimeMillis();
        for (int i = 0; i < NRUNS; ++i) {
            stateMap.createRegionStateNode(createRegionInfo(TABLE_NAME, i));
        }
        long et = System.currentTimeMillis();
        TestRegionStates.LOG.info(String.format("PERF SingleThread: %s %s/sec", StringUtils.humanTimeDiff((et - st)), StringUtils.humanSize((NRUNS / ((et - st) / 1000.0F)))));
    }
}

