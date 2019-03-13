package com.vip.saturn.it.impl;


import JobType.SHELL_JOB;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.internal.execution.ExecutionNode;
import com.vip.saturn.job.internal.storage.JobNodePath;
import com.vip.saturn.job.utils.ScriptPidUtils;
import java.util.Random;
import org.apache.commons.exec.OS;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptJobIT extends AbstractSaturnIT {
    public static String NORMAL_SH_PATH;

    public static String LONG_TIME_SH_PATH;

    @Test
    public void test_A_Normalsh() throws Exception {
        if (!(OS.isFamilyUnix())) {
            return;
        }
        AbstractSaturnIT.startOneNewExecutorList();
        final String jobName = "test_A_Normalsh";
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("*/4 * * * * ?");
        jobConfig.setJobType(SHELL_JOB.toString());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setProcessCountIntervalSeconds(1);
        jobConfig.setShardingItemParameters(("0=sh " + (ScriptJobIT.NORMAL_SH_PATH)));
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        Thread.sleep(1000);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    String count = zkGetJobNode(jobName, (("servers/" + (AbstractSaturnIT.saturnExecutorList.get(0).getExecutorName())) + "/processSuccessCount"));
                    AbstractSaturnIT.log.info("success count: {}", count);
                    int cc = Integer.parseInt(count);
                    if (cc > 0) {
                        return true;
                    }
                    return false;
                }
            }, 15);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
    }

    /**
     * ?????????Executor??????
     */
    @Test
    public void test_B_ForceStop() throws Exception {
        // bacause ScriptPidUtils.isPidRunning don't support mac
        if ((!(OS.isFamilyUnix())) || (OS.isFamilyMac())) {
            return;
        }
        final int shardCount = 3;
        final String jobName = "test_B_ForceStop_" + (new Random().nextInt(100));// ????IT??????

        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(SHELL_JOB.toString());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters(((((("0=sh " + (ScriptJobIT.LONG_TIME_SH_PATH)) + ",1=sh ") + (ScriptJobIT.LONG_TIME_SH_PATH)) + ",2=sh ") + (ScriptJobIT.LONG_TIME_SH_PATH)));
        addJob(jobConfig);
        Thread.sleep(1000);
        AbstractSaturnIT.startOneNewExecutorList();// ??????????pid????

        Thread.sleep(1000);
        final String executorName = AbstractSaturnIT.saturnExecutorList.get(0).getExecutorName();
        enableJob(jobName);
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(2000);
        // ??????????
        stopExecutor(0);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (int j = 0; j < shardCount; j++) {
                        long pid = ScriptPidUtils.getFirstPidFromFile(executorName, jobName, ("" + j));
                        if ((pid > 0) && (ScriptPidUtils.isPidRunning(pid))) {
                            return false;
                        }
                    }
                    return true;
                }
            }, 10);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
    }

    /**
     * ?????????Executor????????? ????Executor????????????????????running?????????????????running??????completed???
     */
    @Test
    public void test_C_ReuseItem() throws Exception {
        // because ScriptPidUtils.isPidRunning don't supoort mac
        if ((!(OS.isFamilyUnix())) || (OS.isFamilyMac())) {
            return;
        }
        final int shardCount = 3;
        final String jobName = "test_C_ReuseItem" + (new Random().nextInt(100));// ????IT??????

        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(SHELL_JOB.toString());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters(((((("0=sh " + (ScriptJobIT.LONG_TIME_SH_PATH)) + ",1=sh ") + (ScriptJobIT.LONG_TIME_SH_PATH)) + ",2=sh ") + (ScriptJobIT.LONG_TIME_SH_PATH)));
        addJob(jobConfig);
        Thread.sleep(1000);
        AbstractSaturnIT.startOneNewExecutorList();// ??????????pid????

        Thread.sleep(1000);
        final String executorName = AbstractSaturnIT.saturnExecutorList.get(0).getExecutorName();
        enableJob(jobName);
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        disableJob(jobName);
        Thread.sleep(1000);
        // ??????????
        stopExecutor(0);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isOnline(executorName));
                }
            }, 10);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        for (int j = 0; j < shardCount; j++) {
            long pid = ScriptPidUtils.getFirstPidFromFile(executorName, jobName, ("" + j));
            if ((pid < 0) || (!(ScriptPidUtils.isPidRunning(pid)))) {
                fail((((("item " + j) + ", pid ") + pid) + " should running"));
            }
        }
        AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(2000);
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
            }, 10);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        forceStopJob(jobName);
        Thread.sleep(1000);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (int j = 0; j < shardCount; j++) {
                        if (!(AbstractSaturnIT.regCenter.isExisted(JobNodePath.getNodeFullPath(jobName, ExecutionNode.getCompletedNode(j))))) {
                            return false;
                        }
                    }
                    return true;
                }
            }, 10);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        removeJob(jobName);
    }
}

