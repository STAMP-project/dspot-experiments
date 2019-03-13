package com.vip.saturn.it.impl;


import JobType.JAVA_JOB;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.it.job.LongtimeJavaJob;
import com.vip.saturn.it.job.SimpleJavaJob;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.executor.Main;
import com.vip.saturn.job.internal.execution.ExecutionNode;
import com.vip.saturn.job.internal.storage.JobNodePath;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author hebelala
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShardingWithTrafficIT extends AbstractSaturnIT {
    /**
     * ????????????<br> ?????????????A?B?<br> ??B??????B???????A?<br> ??B??????????<br> ??B??????????<br>
     * ??B?????????????A?B?
     */
    @Test
    public void test_A_NormalFlow() throws Exception {
        String jobName = "test_A_NormalFlow";
        String jobName2 = "test_A_NormalFlow2";
        final JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(2);
        jobConfig.setShardingItemParameters("0=0,1=1");
        final JobConfig jobConfig2 = new JobConfig();
        jobConfig2.setJobName(jobName2);
        jobConfig2.setCron("9 9 9 9 9 ? 2099");
        jobConfig2.setJobType(JAVA_JOB.toString());
        jobConfig2.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig2.setShardingTotalCount(2);
        jobConfig2.setShardingItemParameters("0=0,1=1");
        addJob(jobConfig);
        Thread.sleep(1000L);
        addJob(jobConfig2);
        Thread.sleep(1000L);
        enableJob(jobName);
        Thread.sleep(1000L);
        enableJob(jobName2);
        Thread.sleep(1000L);
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();
        String executorName1 = executor1.getExecutorName();
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();
        String executorName2 = executor2.getExecutorName();
        runAtOnceAndWaitShardingCompleted(jobName);
        runAtOnceAndWaitShardingCompleted(jobName2);
        isItemsBalanceOk(jobName, jobName2, executorName1, executorName2);
        extractTraffic(executorName2);
        Thread.sleep(1000L);
        runAtOnceAndWaitShardingCompleted(jobName);
        runAtOnceAndWaitShardingCompleted(jobName2);
        isItemsToExecutor1(jobName, jobName2, executorName1, executorName2);
        AbstractSaturnIT.stopExecutorGracefully(1);
        Thread.sleep(1000L);
        runAtOnceAndWaitShardingCompleted(jobName);
        runAtOnceAndWaitShardingCompleted(jobName2);
        isItemsToExecutor1(jobName, jobName2, executorName1, executorName2);
        executor2 = startExecutor(1);
        executorName2 = executor2.getExecutorName();
        runAtOnceAndWaitShardingCompleted(jobName);
        runAtOnceAndWaitShardingCompleted(jobName2);
        isItemsToExecutor1(jobName, jobName2, executorName1, executorName2);
        recoverTraffic(executorName2);
        Thread.sleep(1000L);
        runAtOnceAndWaitShardingCompleted(jobName);
        runAtOnceAndWaitShardingCompleted(jobName2);
        isItemsBalanceOk(jobName, jobName2, executorName1, executorName2);
        // ????????Test
        disableJob(jobName);
        disableJob(jobName2);
        Thread.sleep(1000L);
        removeJob(jobName);
        removeJob(jobName2);
        Thread.sleep(1000L);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    @Test
    public void test_B_NoTrafficNotTakeOverFailoverShard() throws Exception {
        final String jobName = "test_B_NoTrafficNotTakeOverFailoverShard";
        LongtimeJavaJob.JobStatus status = new LongtimeJavaJob.JobStatus();
        status.runningCount = 0;
        status.sleepSeconds = 10;
        status.finished = false;
        status.timeout = false;
        LongtimeJavaJob.statusMap.put((jobName + "_0"), status);
        final JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(LongtimeJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        addJob(jobConfig);
        Thread.sleep(1000L);
        enableJob(jobName);
        Thread.sleep(1000L);
        AbstractSaturnIT.startOneNewExecutorList();
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();
        String executorName2 = executor2.getExecutorName();
        extractTraffic(executorName2);
        Thread.sleep(1000L);
        runAtOnceAndWaitShardingCompleted(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return AbstractSaturnIT.regCenter.isExisted(JobNodePath.getNodeFullPath(jobName, ExecutionNode.getRunningNode(0)));
            }
        }, 4);
        stopExecutor(0);
        Thread.sleep(100L);
        List<String> items = AbstractSaturnIT.regCenter.getChildrenKeys(JobNodePath.getNodeFullPath(jobName, "leader/failover/items"));
        assertThat(items).isNotNull().containsOnly("0");
        assertThat((!(isFailoverAssigned(jobName, 0))));
        LongtimeJavaJob.statusMap.clear();
        disableJob(jobName);
        Thread.sleep(1000L);
        removeJob(jobName);
        Thread.sleep(1000L);
        AbstractSaturnIT.stopExecutorListGracefully();
    }
}

