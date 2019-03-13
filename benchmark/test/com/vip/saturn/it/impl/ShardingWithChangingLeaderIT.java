package com.vip.saturn.it.impl;


import JobType.JAVA_JOB;
import SaturnExecutorsNode.LEADER_HOSTNODE_PATH;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.it.job.SimpleJavaJob;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.executor.Main;
import com.vip.saturn.job.internal.sharding.ShardingNode;
import com.vip.saturn.job.internal.storage.JobNodePath;
import com.vip.saturn.job.utils.ItemUtils;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author hebelala
 */
public class ShardingWithChangingLeaderIT extends AbstractSaturnIT {
    @Test
    public void test_A_StopNamespaceShardingManagerLeader() throws Exception {
        AbstractSaturnIT.startSaturnConsoleList(2);
        Thread.sleep(1000);
        final String jobName = "test_A_StopNamespaceShardingManagerLeader";
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        addJob(jobConfig);
        Thread.sleep(1000);
        Main executor = AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        String hostValue = AbstractSaturnIT.regCenter.get(LEADER_HOSTNODE_PATH);
        assertThat(hostValue).isNotNull();
        AbstractSaturnIT.stopSaturnConsole(0);
        Thread.sleep(1000);
        String hostValue2 = AbstractSaturnIT.regCenter.get(LEADER_HOSTNODE_PATH);
        assertThat(hostValue2).isNotNull().isNotEqualTo(hostValue);
        enableJob(jobName);
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor.getExecutorName()))));
        assertThat(items).contains(0);
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        Thread.sleep(1000);
        AbstractSaturnIT.stopExecutorListGracefully();
        AbstractSaturnIT.stopSaturnConsoleList();
    }
}

