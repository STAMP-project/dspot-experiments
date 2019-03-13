package com.vip.saturn.it.impl;


import JobType.JAVA_JOB;
import ServerNode.RUNONETIME;
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
public class RunAtOnceJobIT extends AbstractSaturnIT {
    /**
     * ??STOPPING???????
     */
    @Test
    public void test_C_normalTrigger() throws Exception {
        final int shardCount = 3;
        final String jobName = "test_C_normalTrigger";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setTimeoutSeconds(0);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        Thread.sleep(1000);
        runAtOnce(jobName);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (int i = 0; i < shardCount; i++) {
                        String key = (jobName + "_") + i;
                        if ((SimpleJavaJob.statusMap.get(key)) != 1) {
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
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        SimpleJavaJob.statusMap.clear();
    }

    @Test
    public void test_B_ignoreWhenIsRunning() throws Exception {
        final int shardCount = 1;
        final String jobName = "test_B_ignoreWhenIsRunning";
        LongtimeJavaJob.JobStatus status = new LongtimeJavaJob.JobStatus();
        status.runningCount = 0;
        status.sleepSeconds = 3;
        status.finished = false;
        status.timeout = false;
        LongtimeJavaJob.statusMap.put(((jobName + "_") + 0), status);
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
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
        Thread.sleep(1000);
        // suppose to be ignored.
        try {
            runAtOnce(jobName);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo((("???(" + jobName) + ")???READY?????????"));
        }
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    String path = JobNodePath.getNodeFullPath(jobName, String.format(RUNONETIME, "executorName0"));
                    if (AbstractSaturnIT.regCenter.isExisted(path)) {
                        return false;
                    }
                    return true;
                }
            }, 20);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    if ((LongtimeJavaJob.statusMap.get(((jobName + "_") + 0)).runningCount) < 1) {
                        return false;
                    }
                    return true;
                }
            }, 30);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        LongtimeJavaJob.statusMap.clear();
    }
}

