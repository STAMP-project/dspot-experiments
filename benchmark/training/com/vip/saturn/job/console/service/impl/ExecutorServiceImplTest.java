package com.vip.saturn.job.console.service.impl;


import CuratorRepository.CuratorFrameworkOp;
import JobType.JAVA_JOB;
import JobType.MSG_JOB;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.console.domain.ServerRunningInfo;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.service.JobService;
import com.vip.saturn.job.console.service.RegistryCenterService;
import com.vip.saturn.job.console.utils.JobNodePath;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ExecutorServiceImplTest {
    @Mock
    private CuratorFrameworkOp curatorFrameworkOp;

    @Mock
    private RegistryCenterService registryCenterService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private ExecutorServiceImpl executorService;

    @Test
    public void getExecutorRunningInfo() throws SaturnJobConsoleException {
        String namespace = "www.abc.com";
        String executorName = "exec01";
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        JobConfig jobConfigA = new JobConfig();
        jobConfigA.setJobName("jobA");
        jobConfigA.setJobType(JAVA_JOB.name());
        jobConfigA.setEnabledReport(Boolean.TRUE);
        jobConfigA.setFailover(Boolean.TRUE);
        jobConfigA.setLocalMode(Boolean.FALSE);
        JobConfig jobConfigB = new JobConfig();
        jobConfigB.setJobName("jobB");
        jobConfigB.setJobType(MSG_JOB.name());
        jobConfigB.setEnabledReport(Boolean.FALSE);
        jobConfigB.setFailover(Boolean.FALSE);
        jobConfigB.setLocalMode(Boolean.FALSE);
        JobConfig jobConfigC = new JobConfig();
        jobConfigC.setJobName("jobC");
        jobConfigC.setJobType(JAVA_JOB.name());
        jobConfigC.setEnabledReport(Boolean.TRUE);
        jobConfigC.setFailover(Boolean.FALSE);
        jobConfigC.setLocalMode(Boolean.FALSE);
        Mockito.when(jobService.getUnSystemJobs(namespace)).thenReturn(Lists.newArrayList(jobConfigA, jobConfigB, jobConfigC));
        Mockito.when(curatorFrameworkOp.checkExists(String.format("/%s/%s/servers", JobNodePath.$JOBS_NODE_NAME, jobConfigA.getJobName()))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(String.format("/%s/%s/servers", JobNodePath.$JOBS_NODE_NAME, jobConfigB.getJobName()))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(String.format("/%s/%s/servers", JobNodePath.$JOBS_NODE_NAME, jobConfigC.getJobName()))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.getData(String.format("%s/%s/%s", JobNodePath.getServerNodePath(jobConfigA.getJobName()), executorName, "sharding"))).thenReturn("1,2");
        Mockito.when(curatorFrameworkOp.getData(String.format("%s/%s/%s", JobNodePath.getServerNodePath(jobConfigB.getJobName()), executorName, "sharding"))).thenReturn("2,3");
        Mockito.when(curatorFrameworkOp.getData(String.format("%s/%s/%s", JobNodePath.getServerNodePath(jobConfigC.getJobName()), executorName, "sharding"))).thenReturn("0,1");
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath("jobA"))).thenReturn(Lists.newArrayList("0", "1", "2", "3"));
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath("jobB"))).thenReturn(Lists.newArrayList("0", "1", "2", "3"));
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath("jobC"))).thenReturn(Lists.newArrayList("0", "1", "2", "3"));
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobA", "0", "running"))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath("jobA", "0", "failover"))).thenReturn(executorName);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobA", "1", "running"))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobA", "2", "running"))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobA", "3", "running"))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath("jobA", "3", "failover"))).thenReturn("other-exec");
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobC", "0", "running"))).thenReturn(false);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobC", "1", "running"))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobC", "2", "running"))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath("jobC", "3", "running"))).thenReturn(true);
        ServerRunningInfo serverRunningInfo = executorService.getExecutorRunningInfo(namespace, executorName);
        Assert.assertEquals(2, serverRunningInfo.getRunningJobItems().size());
        Assert.assertEquals("0,1,2", serverRunningInfo.getRunningJobItems().get("jobA"));
        Assert.assertEquals("1", serverRunningInfo.getRunningJobItems().get("jobC"));
        Assert.assertEquals(0, serverRunningInfo.getPotentialRunningJobItems().size());
        Assert.assertEquals(null, serverRunningInfo.getPotentialRunningJobItems().get("jobB"));
    }
}

