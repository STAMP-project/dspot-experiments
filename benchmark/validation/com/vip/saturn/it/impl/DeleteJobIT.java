package com.vip.saturn.it.impl;


import ConfigurationNode.TO_DELETE;
import JobType.JAVA_JOB;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.it.job.SimpleJavaJob;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.executor.Main;
import com.vip.saturn.job.internal.execution.ExecutionNode;
import com.vip.saturn.job.internal.server.ServerNode;
import com.vip.saturn.job.internal.storage.JobNodePath;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteJobIT extends AbstractSaturnIT {
    /**
     * ??Executor
     */
    @Test
    public void test_A_multiExecutor() throws Exception {
        AbstractSaturnIT.startExecutorList(3);
        int shardCount = 3;
        final String jobName = "test_A_multiExecutor";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("*/2 * * * * ?");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        addJob(jobConfig);
        Thread.sleep(1000);
        enableReport(jobName);
        enableJob(jobName);
        Thread.sleep((4 * 1000));
        disableJob(jobName);
        Thread.sleep(1000);
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            assertThat(SimpleJavaJob.statusMap.get(key)).isGreaterThanOrEqualTo(1);
        }
        removeJob(jobName);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (Main executor : AbstractSaturnIT.saturnExecutorList) {
                        if (executor == null) {
                            continue;
                        }
                        if (AbstractSaturnIT.regCenter.isExisted(ServerNode.getServerNode(jobName, executor.getExecutorName()))) {
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
    }

    /**
     * ????Executor
     */
    @Test
    public void test_B_oneExecutor() throws Exception {
        AbstractSaturnIT.startOneNewExecutorList();
        final int shardCount = 3;
        final String jobName = "test_B_oneExecutor";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("*/2 * * * * ?");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        addJob(jobConfig);
        Thread.sleep(1000);
        enableReport(jobName);
        enableJob(jobName);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (int i = 0; i < shardCount; i++) {
                        String key = (jobName + "_") + i;
                        if ((SimpleJavaJob.statusMap.get(key)) < 1) {
                            return false;
                        }
                    }
                    return true;
                }
            }, 4);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        disableJob(jobName);
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
            }, 3);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        removeJob(jobName);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    for (Main executor : AbstractSaturnIT.saturnExecutorList) {
                        if (executor == null) {
                            continue;
                        }
                        if (AbstractSaturnIT.regCenter.isExisted(ServerNode.getServerNode(jobName, executor.getExecutorName()))) {
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
    }

    /**
     * ??????????Executor??toDelete???IT? ??????Executor??????toDelete?????????$Jobs/jobName/servers/executorName
     */
    @Test
    public void test_C_alreadyExistsToDelete() throws Exception {
        final int shardCount = 3;
        final String jobName = "test_C_alreadyExistsToDelete";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("*/2 * * * * ?");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        addJob(jobConfig);
        Thread.sleep(1000);
        enableReport(jobName);
        // ??hack????????toDelete??
        zkUpdateJobNode(jobName, TO_DELETE, "1");
        final String serverNodePath = JobNodePath.getServerNodePath(jobName, "executorName0");
        AbstractSaturnIT.regCenter.persist(serverNodePath, "");
        AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        try {
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    if (AbstractSaturnIT.regCenter.isExisted(serverNodePath)) {
                        return false;
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

