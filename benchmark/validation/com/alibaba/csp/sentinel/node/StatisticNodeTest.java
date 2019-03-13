/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.node;


import Constants.TIME_DROP_VALVE;
import com.alibaba.csp.sentinel.util.TimeUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link StatisticNode}.
 *
 * @author cdfive
 */
public class StatisticNodeTest {
    private static final String LOG_PREFIX = "[StatisticNodeTest]";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-HH-dd HH:mm:ss");

    private static final Random RANDOM = new Random();

    private static final int THREAD_COUNT = 20;

    /**
     * A simple test for statistic threadNum and qps by using StatisticNode
     *
     * <p>
     * 20 threads, 30 tasks, every task execute 10 times of bizMethod
     * one bizMethod execute within 1 second, and within 0.5 second interval to exceute next bizMthod
     * so that the total time cost will be within 1 minute
     * </p>
     *
     * <p>
     * Print the statistic info of StatisticNode and verify some results
     * </p>
     */
    @Test
    public void testStatisticThreadNumAndQps() {
        long testStartTime = TimeUtil.currentTimeMillis();
        int taskCount = 30;
        int taskBizExecuteCount = 10;
        StatisticNode node = new StatisticNode();
        ExecutorService bizEs = Executors.newFixedThreadPool(StatisticNodeTest.THREAD_COUNT);
        ExecutorService tickEs = Executors.newSingleThreadExecutor();
        tickEs.submit(new StatisticNodeTest.TickTask(node));
        List<StatisticNodeTest.BizTask> bizTasks = new ArrayList<StatisticNodeTest.BizTask>(taskBizExecuteCount);
        for (int i = 0; i < taskCount; i++) {
            bizTasks.add(new StatisticNodeTest.BizTask(node, taskBizExecuteCount));
        }
        try {
            bizEs.invokeAll(bizTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StatisticNodeTest.log("====================================================");
        StatisticNodeTest.log("all biz task done, waiting 3 second to exit");
        StatisticNodeTest.sleep(3000);
        bizEs.shutdown();
        tickEs.shutdown();
        // now no biz method execute, so there is no curThreadNum,passQps,successQps
        Assert.assertEquals(0, node.curThreadNum());
        Assert.assertEquals(0, node.passQps());
        Assert.assertEquals(0, node.successQps());
        // note: total time cost should be controlled within 1 minute,
        // as the node.totalRequest() holding statistics of recent 60 seconds
        int totalRequest = taskCount * taskBizExecuteCount;
        // verify totalRequest
        Assert.assertEquals(totalRequest, node.totalRequest());
        // as all execute success, totalRequest should equal to totalSuccess
        Assert.assertEquals(totalRequest, node.totalSuccess());
        // now there are no data in time span, so the minRT should be equals to TIME_DROP_VALVE
        Assert.assertEquals(node.minRt(), TIME_DROP_VALVE);
        StatisticNodeTest.log("====================================================");
        StatisticNodeTest.log((("testStatisticThreadNumAndQps done, cost " + ((TimeUtil.currentTimeMillis()) - testStartTime)) + "ms"));
    }

    private static class BizTask implements Callable<Object> {
        private StatisticNode node;

        private Integer bizExecuteCount;

        public BizTask(StatisticNode node, Integer bizExecuteCount) {
            this.node = node;
            this.bizExecuteCount = bizExecuteCount;
        }

        @Override
        public Object call() throws Exception {
            while (true) {
                node.increaseThreadNum();
                node.addPassRequest(1);
                long startTime = TimeUtil.currentTimeMillis();
                StatisticNodeTest.bizMethod();
                node.decreaseThreadNum();
                // decrease one ThreadNum, so curThreadNum should less than THREAD_COUNT
                Assert.assertTrue(((node.curThreadNum()) < (StatisticNodeTest.THREAD_COUNT)));
                long rt = (TimeUtil.currentTimeMillis()) - startTime;
                node.addRtAndSuccess(rt, 1);
                // wait random 0.5 second for simulate method call interval,
                // otherwise the curThreadNum will always be THREAD_COUNT at the beginning
                StatisticNodeTest.sleep(StatisticNodeTest.RANDOM.nextInt(500));
                (bizExecuteCount)--;
                if ((bizExecuteCount) <= 0) {
                    break;
                }
            } 
            return null;
        }
    }

    private static class TickTask implements Runnable {
        private StatisticNode node;

        public TickTask(StatisticNode node) {
            this.node = node;
        }

        @Override
        public void run() {
            while (true) {
                // print statistic info every 1 second
                StatisticNodeTest.sleep(1000);
                // the curThreadNum should not greater than THREAD_COUNT
                Assert.assertTrue(((node.curThreadNum()) <= (StatisticNodeTest.THREAD_COUNT)));
                StatisticNodeTest.logNode(node);
            } 
        }
    }
}

