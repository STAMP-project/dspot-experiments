package com.vip.saturn.it.impl;


import JobType.JAVA_JOB;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.it.job.LongtimeJavaJob;
import com.vip.saturn.it.job.SimpleJavaJob;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.internal.storage.JobNodePath;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecutionIT extends AbstractSaturnIT {
    @Test
    public void test_A_report() throws Exception {
        AbstractSaturnIT.startExecutorList(1);
        final JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName("test_A_report");
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        addJob(jobConfig);
        zkUpdateJobNode(jobConfig.getJobName(), "config/enabledReport", "true");
        Thread.sleep(1000);
        enableJob(jobConfig.getJobName());
        Thread.sleep(1000);
        runAtOnce(jobConfig.getJobName());
        Thread.sleep(1000);
        assertThat(zkGetJobNode(jobConfig.getJobName(), "execution/0/lastBeginTime")).isNull();
        assertThat(zkGetJobNode(jobConfig.getJobName(), "execution/0/nextFireTime")).isNull();
        assertThat(zkGetJobNode(jobConfig.getJobName(), "execution/0/lastCompleteTime")).isNull();
        assertThat(zkGetJobNode(jobConfig.getJobName(), "execution/0/jobMsg")).isNull();
        assertThat(zkGetJobNode(jobConfig.getJobName(), "execution/0/jobLog")).isNull();
        assertThat(zkGetJobNode(jobConfig.getJobName(), "execution/0/completed")).isNotNull();
        doReport(jobConfig.getJobName());
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if ((((((zkGetJobNode(jobConfig.getJobName(), "execution/0/lastBeginTime")) != null) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/nextFireTime")) != null)) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/lastCompleteTime")) != null)) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/jobMsg")) != null)) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/jobLog")) != null)) {
                    return true;
                }
                return false;
            }
        }, 3);
        zkRemoveJobNode(jobConfig.getJobName(), "execution/0/lastBeginTime");
        zkRemoveJobNode(jobConfig.getJobName(), "execution/0/nextFireTime");
        zkRemoveJobNode(jobConfig.getJobName(), "execution/0/lastCompleteTime");
        zkRemoveJobNode(jobConfig.getJobName(), "execution/0/jobMsg");
        zkRemoveJobNode(jobConfig.getJobName(), "execution/0/jobLog");
        zkRemoveJobNode(jobConfig.getJobName(), "execution/0/completed");
        disableJob(jobConfig.getJobName());
        Thread.sleep(1000);
        disableReport(jobConfig.getJobName());
        Thread.sleep(1000);
        enableJob(jobConfig.getJobName());
        Thread.sleep(1000);
        runAtOnce(jobConfig.getJobName());
        Thread.sleep(1000);
        assertThat(zkGetJobNode(jobConfig.getJobName(), "execution/0/completed")).isNull();
        doReport(jobConfig.getJobName());
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if ((((((zkGetJobNode(jobConfig.getJobName(), "execution/0/lastBeginTime")) != null) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/nextFireTime")) != null)) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/lastCompleteTime")) != null)) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/jobMsg")) != null)) && ((zkGetJobNode(jobConfig.getJobName(), "execution/0/jobLog")) != null)) {
                    return true;
                }
                return false;
            }
        }, 3);
        disableJob(jobConfig.getJobName());
        Thread.sleep(1000);
        removeJob(jobConfig.getJobName());
        Thread.sleep(1000);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    @Test
    public void test_B_executionStatus() throws Exception {
        AbstractSaturnIT.startExecutorList(1);
        final String jobName = "test_B_executionStatus";
        final int shardCount = 1;
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            LongtimeJavaJob.JobStatus status = new LongtimeJavaJob.JobStatus();
            status.runningCount = 0;
            status.sleepSeconds = 20;
            status.finished = false;
            status.timeout = false;
            LongtimeJavaJob.statusMap.put(key, status);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("0 */6 * * * ? 2099");// ????5?????????failover

        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(LongtimeJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setTimeoutSeconds(0);
        jobConfig.setShardingItemParameters("0=0");
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(2000);
        assertThat(zkGetJobNode(jobName, "execution/0/running")).isEqualTo("executorName0");
        zkRemoveJobNode(jobName, "execution/0/running");
        Thread.sleep(1000);
        // itself take over 0 item
        assertThat(zkGetJobNode(jobName, "execution/0/failover")).isEqualTo("executorName0");
        // wait the last finish, until the failover lifecycle begin
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return AbstractSaturnIT.regCenter.isExisted(JobNodePath.getNodeFullPath(jobName, "execution/0/running"));
            }
        }, 30);
        assertThat(zkGetJobNode(jobName, "execution/0/running")).isEqualTo("executorName0");
        // wait the failover lifecycle finish
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return AbstractSaturnIT.regCenter.isExisted(JobNodePath.getNodeFullPath(jobName, "execution/0/completed"));
            }
        }, 30);
        assertThat(zkGetJobNode(jobName, "execution/0/completed")).isEqualTo("executorName0");
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        Thread.sleep(2000);
        LongtimeJavaJob.statusMap.clear();
        AbstractSaturnIT.stopExecutorListGracefully();
    }
}

