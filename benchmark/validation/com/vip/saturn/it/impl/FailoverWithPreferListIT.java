/**
 * vips Inc. Copyright (c) 2016 All Rights Reserved.
 */
package com.vip.saturn.it.impl;


import JobType.JAVA_JOB;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.it.job.LongtimeJavaJob;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.internal.execution.ExecutionNode;
import com.vip.saturn.job.internal.sharding.ShardingNode;
import com.vip.saturn.job.internal.storage.JobNodePath;
import com.vip.saturn.job.utils.ItemUtils;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FailoverWithPreferListIT extends AbstractSaturnIT {
    @Test
    public void test_A_JavaJob() throws Exception {
        final int shardCount = 2;
        final String jobName = "failoverWithPreferITJobJava";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            LongtimeJavaJob.JobStatus status = new LongtimeJavaJob.JobStatus();
            status.runningCount = 0;
            status.sleepSeconds = 10;
            status.finished = false;
            status.timeout = false;
            LongtimeJavaJob.statusMap.put(key, status);
        }
        // 1 ?????????10S????????????????preferList?executor0???????????
        final JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(LongtimeJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setPreferList(AbstractSaturnIT.saturnExecutorList.get(0).getExecutorName());
        jobConfig.setUseDispreferList(false);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        addJob(jobConfig);
        Thread.sleep(1000);
        // 2 ???????????
        enableJob(jobConfig.getJobName());
        Thread.sleep(2000);
        runAtOnce(jobName);
        // 3 ?????????????
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (int j = 0; j < shardCount; j++) {
                        if (!(AbstractSaturnIT.regCenter.isExisted(JobNodePath.getNodeFullPath(jobName, ExecutionNode.getRunningNode(j))))) {
                            return false;
                        }
                    }
                    return true;
                }
            }, 30);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        Thread.sleep(2000);
        final List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(AbstractSaturnIT.saturnExecutorList.get(0).getExecutorName()))));
        // 4 ?????executor???executor????????????(?????????????)
        stopExecutor(0);
        System.out.println(("items:" + items));
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (int j = 0; j < shardCount; j++) {
                        if (AbstractSaturnIT.regCenter.isExisted(JobNodePath.getNodeFullPath(jobName, ExecutionNode.getRunningNode(j)))) {
                            return false;
                        }
                    }
                    return true;
                }
            }, 20);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        // 5 ???????executor0????????????
        for (Integer item : items) {
            String key = (jobName + "_") + item;
            LongtimeJavaJob.JobStatus status = LongtimeJavaJob.statusMap.get(key);
            assertThat(status.runningCount).isEqualTo(0);
        }
        disableJob(jobConfig.getJobName());
        Thread.sleep(1000);
        removeJob(jobConfig.getJobName());
        Thread.sleep(2000);
        LongtimeJavaJob.statusMap.clear();
    }
}

