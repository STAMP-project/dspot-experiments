package com.vip.saturn.job.console.service.impl;


import CuratorRepository.CuratorFrameworkOp;
import JobStatus.READY;
import JobStatus.RUNNING;
import JobStatus.STOPPED;
import JobStatus.STOPPING;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.exception.SaturnJobConsoleHttpException;
import com.vip.saturn.job.console.mybatis.service.CurrentJobConfigService;
import com.vip.saturn.job.console.repository.zookeeper.CuratorRepository;
import com.vip.saturn.job.console.service.JobService;
import com.vip.saturn.job.console.service.RegistryCenterService;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Created by kfchu on 31/05/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class RestApiServiceImplTest {
    private static final String TEST_NAME_SPACE_NAME = "testDomain";

    @Mock
    private RegistryCenterService registryCenterService;

    @Mock
    private CuratorRepository curatorRepository;

    @Mock
    private JobService jobService;

    @Mock
    private CurrentJobConfigService currentJobConfigService;

    @Mock
    private CuratorFrameworkOp curatorFrameworkOp;

    @Mock
    private CuratorFramework curatorFramework;

    @InjectMocks
    private RestApiServiceImpl restApiService;

    @Test
    public void testRunAtOnceSuccessfully() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(READY);
        List<JobServer> servers = Lists.newArrayList();
        JobServer jobServer = createJobServer("job1");
        servers.add(jobServer);
        Mockito.when(jobService.getJobServers(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(servers);
        List<JobServerStatus> jobServerStatusList = getJobServerStatus(servers);
        Mockito.when(jobService.getJobServersStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(jobServerStatusList);
        // run
        restApiService.runJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName, null);
    }

    @Test
    public void testRunAtOnceFailAsJobStatusIsNotReady() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(RUNNING);
        // run
        try {
            restApiService.runJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName, null);
        } catch (SaturnJobConsoleHttpException e) {
            Assert.assertEquals("status code is not 400", 400, e.getStatusCode());
            Assert.assertEquals("error message is not equals", "job's status is not {READY}", e.getMessage());
        }
    }

    @Test
    public void testRunAtOnceFailAsNoExecutorFound() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(READY);
        List<JobServer> servers = Lists.newArrayList();
        Mockito.when(jobService.getJobServers(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(servers);
        // run
        try {
            restApiService.runJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName, null);
        } catch (SaturnJobConsoleHttpException e) {
            Assert.assertEquals("status code is not 400", 400, e.getStatusCode());
            Assert.assertEquals("error message is not equals", "no executor found for this job", e.getMessage());
        }
    }

    @Test
    public void testStopAtOnceSuccessfullyWhenJobIsAlreadyStopped() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPED);
        // run
        restApiService.stopJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
    }

    @Test
    public void testStopAtOnceSuccessfullyWhenJobStatusIsStoppingAndJobTypeIsMsg() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPING);
        List<JobServer> servers = Lists.newArrayList();
        JobServer jobServer = createJobServer("job1");
        servers.add(jobServer);
        Mockito.when(jobService.getJobServers(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(servers);
        List<String> serverNameList = getJobServerNameList(servers);
        Mockito.when(jobService.getJobServerList(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(serverNameList);
        // run
        restApiService.stopJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
    }

    @Test
    public void testStopAtOnceSuccessfullyWhenJobStatusIsStoppingAndJobTypeIsJava() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPING);
        List<JobServer> servers = Lists.newArrayList();
        JobServer jobServer = createJobServer("job1");
        servers.add(jobServer);
        Mockito.when(jobService.getJobServers(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(servers);
        List<String> serverNameList = getJobServerNameList(servers);
        Mockito.when(jobService.getJobServerList(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(serverNameList);
        // run
        restApiService.stopJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
    }

    @Test
    public void testStopAtOnceFailForMsgJobAsJobIsEnable() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPING);
        List<JobServer> servers = Lists.newArrayList();
        JobServer jobServer = createJobServer("job1");
        servers.add(jobServer);
        Mockito.when(jobService.getJobServers(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(servers);
        List<String> serverNameList = getJobServerNameList(servers);
        Mockito.when(jobService.getJobServerList(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(serverNameList);
        // run
        try {
            restApiService.stopJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
        } catch (SaturnJobConsoleHttpException e) {
            Assert.assertEquals("status code is not 400", 400, e.getStatusCode());
            Assert.assertEquals("error message is not equals", "job cannot be stopped while it is enable", e.getMessage());
        }
    }

    @Test
    public void testStopAtOnceFailForJavaJobAsNoExecutor() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPING);
        List<JobServer> servers = Lists.newArrayList();
        Mockito.when(jobService.getJobServers(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(servers);
        // run
        try {
            restApiService.stopJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
        } catch (SaturnJobConsoleHttpException e) {
            Assert.assertEquals("status code is not 400", 400, e.getStatusCode());
            Assert.assertEquals("error message is not equals", "no executor found for this job", e.getMessage());
        }
    }

    @Test
    public void testStopAtOnceFailForJavaJobAsStatusIsNotStopping() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(READY);
        List<JobServer> servers = Lists.newArrayList();
        Mockito.when(jobService.getJobServers(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(servers);
        // run
        try {
            restApiService.stopJobAtOnce(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
        } catch (SaturnJobConsoleHttpException e) {
            Assert.assertEquals("status code is not 400", 400, e.getStatusCode());
            Assert.assertEquals("error message is not equals", "job cannot be stopped while its status is READY or RUNNING", e.getMessage());
        }
    }

    @Test
    public void testDeleteJobSuccessfully() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPED);
        // run
        restApiService.deleteJob(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
        // verify
        Mockito.verify(jobService).removeJob(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
    }

    @Test
    public void testDeleteJobFailAsStatusIsNotStopped() throws SaturnJobConsoleException {
        // prepare
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPING);
        // run
        try {
            restApiService.deleteJob(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
        } catch (SaturnJobConsoleHttpException e) {
            Assert.assertEquals("status code is not 400", 400, e.getStatusCode());
            Assert.assertEquals("error message is not equals", "job's status is not {STOPPED}", e.getMessage());
        }
        // verify
        Mockito.verify(jobService, Mockito.times(0)).removeJob(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName);
    }

    @Test
    public void testUpdateJobSuccessfully() throws SaturnJobConsoleException {
        String jobName = "testJob";
        JobConfig jobConfig = buildUpdateJobConfig(jobName);
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPED);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(buildJobConfig4DB(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName));
        // run
        restApiService.updateJob(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName, jobConfig);
    }

    @Test
    public void testUpdateJobFailAsSaturnsIsNotStopped() throws SaturnJobConsoleException {
        String jobName = "testJob";
        Mockito.when(jobService.getJobStatus(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName)).thenReturn(STOPPING);
        // run
        try {
            restApiService.updateJob(RestApiServiceImplTest.TEST_NAME_SPACE_NAME, jobName, buildUpdateJobConfig(jobName));
        } catch (SaturnJobConsoleHttpException e) {
            Assert.assertEquals("status code is not 400", 400, e.getStatusCode());
            Assert.assertEquals("error message is not equals", "job's status is not {STOPPED}", e.getMessage());
        }
    }
}

