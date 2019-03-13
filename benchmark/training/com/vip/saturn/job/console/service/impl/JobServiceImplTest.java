package com.vip.saturn.job.console.service.impl;


import CuratorFrameworkOp.CuratorTransactionOp;
import ExecutionStatus.COMPLETED;
import ExecutionStatus.FAILED;
import ExecutionStatus.RUNNING;
import ExecutionStatus.TIMEOUT;
import JobMode.SYSTEM_PREFIX;
import JobStatus.READY;
import JobType.JAVA_JOB;
import JobType.MSG_JOB;
import JobType.PASSIVE_JAVA_JOB;
import JobType.PASSIVE_SHELL_JOB;
import JobType.SHELL_JOB;
import SystemConfigProperties.MAX_JOB_NUM;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.mybatis.entity.JobConfig4DB;
import com.vip.saturn.job.console.mybatis.service.CurrentJobConfigService;
import com.vip.saturn.job.console.repository.zookeeper.CuratorRepository.CuratorFrameworkOp;
import com.vip.saturn.job.console.service.RegistryCenterService;
import com.vip.saturn.job.console.service.SystemConfigService;
import com.vip.saturn.job.console.utils.ExecutorNodePath;
import com.vip.saturn.job.console.utils.JobNodePath;
import com.vip.saturn.job.console.utils.SaturnConstants;
import com.vip.saturn.job.sharding.node.SaturnExecutorsNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobServiceImplTest {
    @Mock
    private CuratorFrameworkOp curatorFrameworkOp;

    @Mock
    private CuratorTransactionOp curatorTransactionOp;

    @Mock
    private CurrentJobConfigService currentJobConfigService;

    @Mock
    private RegistryCenterService registryCenterService;

    @Mock
    private SystemConfigService systemConfigService;

    @InjectMocks
    private JobServiceImpl.JobServiceImpl jobService;

    private String namespace = "saturn-job-test.vip.com";

    private String jobName = "testJob";

    private String userName = "weicong01.li";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetGroup() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(new JobConfig4DB()));
        Assert.assertEquals(jobService.getGroups(namespace).size(), 1);
    }

    @Test
    public void testEnableJobFailByJobNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s??????????", jobName));
        jobService.enableJob(namespace, jobName, userName);
    }

    @Test
    public void testEnableJobFailByJobHasEnabled() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.TRUE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????%s?????????", jobName));
        jobService.enableJob(namespace, jobName, userName);
    }

    @Test
    public void testEnabledJobFailByJobHasFinished() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.FALSE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(false);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(true);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s??????????STOPPED??", jobName));
        jobService.enableJob(namespace, jobName, userName);
    }

    @Test
    public void testEnabledJobSuccess() throws Exception {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.FALSE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
        jobService.enableJob(namespace, jobName, userName);
        Mockito.verify(currentJobConfigService).updateByPrimaryKey(jobConfig4DB);
        Mockito.verify(curatorFrameworkOp).update(ArgumentMatchers.eq(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_ENABLED)), ArgumentMatchers.eq(true));
    }

    @Test
    public void testDisableJobFailByJobNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s??????????", jobName));
        jobService.disableJob(namespace, jobName, userName);
    }

    @Test
    public void testDisableJobFailByJobHasDisabled() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.FALSE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????%s?????????", jobName));
        jobService.disableJob(namespace, jobName, userName);
    }

    @Test
    public void testDisableJobFailByUpdateError() throws Exception {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.TRUE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(currentJobConfigService.updateByPrimaryKey(jobConfig4DB)).thenThrow(new SaturnJobConsoleException("update error"));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("update error");
        jobService.disableJob(namespace, jobName, userName);
    }

    @Test
    public void testDisableJobSuccess() throws Exception {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.TRUE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        jobService.disableJob(namespace, jobName, userName);
        Mockito.verify(currentJobConfigService).updateByPrimaryKey(jobConfig4DB);
        Mockito.verify(curatorFrameworkOp).update(ArgumentMatchers.eq(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_ENABLED)), ArgumentMatchers.eq(false));
    }

    @Test
    public void testRemoveJobFailByNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.<JobConfig4DB>emptyList());
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s??????????", jobName));
        jobService.removeJob(namespace, jobName);
    }

    @Test
    public void testRemoveJobFailByHaveUpStream() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setUpStream("upStreamJob");
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s??????????????%s??????????????", jobName, "upStreamJob"));
        jobService.removeJob(namespace, jobName);
    }

    @Test
    public void testRemoveJobFailByHaveDownStream() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setDownStream("downStreamJob");
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s??????????????%s??????????????", jobName, "downStreamJob"));
        jobService.removeJob(namespace, jobName);
    }

    @Test
    public void testRemoveJobFailByNotStopped() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.TRUE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???????(%s)?????????STOPPED??", jobName));
        jobService.removeJob(namespace, jobName);
    }

    @Test
    public void testRemoveJobFailByLimitTime() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.FALSE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
        Stat stat = new Stat();
        stat.setCtime(System.currentTimeMillis());
        Mockito.when(curatorFrameworkOp.getStat(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobName)))).thenReturn(stat);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???????(%s)?????????????????%d??", jobName, ((SaturnConstants.JOB_CAN_BE_DELETE_TIME_LIMIT) / 60000)));
        jobService.removeJob(namespace, jobName);
    }

    @Test
    public void testRemoveJobFailByDeleteDBError() throws Exception {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setId(1L);
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.FALSE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
        Stat stat = new Stat();
        stat.setCtime(((System.currentTimeMillis()) - ((3 * 60) * 1000)));
        Mockito.when(curatorFrameworkOp.getStat(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobName)))).thenReturn(stat);
        Mockito.when(currentJobConfigService.deleteByPrimaryKey(jobConfig4DB.getId())).thenThrow(new SaturnJobConsoleException("delete error"));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("delete error");
        jobService.removeJob(namespace, jobName);
    }

    @Test
    public void testRemoveJobSuccess() throws Exception {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setId(1L);
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(Boolean.FALSE);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
        Stat stat = new Stat();
        stat.setCtime(((System.currentTimeMillis()) - ((3 * 60) * 1000)));
        Mockito.when(curatorFrameworkOp.getStat(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobName)))).thenReturn(stat);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getConfigNodePath(jobName, "toDelete")))).thenReturn(true);
        jobService.removeJob(namespace, jobName);
        Mockito.verify(currentJobConfigService).deleteByPrimaryKey(jobConfig4DB.getId());
        Mockito.verify(curatorFrameworkOp).deleteRecursive(ArgumentMatchers.eq(JobNodePath.getConfigNodePath(jobName, "toDelete")));
        Mockito.verify(curatorFrameworkOp).create(ArgumentMatchers.eq(JobNodePath.getConfigNodePath(jobName, "toDelete")));
    }

    @Test
    public void testGetCandidateExecutorsFailByNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s???????Executor?????????", jobName));
        jobService.getCandidateExecutors(namespace, jobName);
    }

    @Test
    public void testGetCandidaExecutorsByExecutorPathNotExist() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(SaturnExecutorsNode.getExecutorsNodePath()))).thenReturn(false);
        Assert.assertTrue(jobService.getCandidateExecutors(namespace, jobName).isEmpty());
    }

    @Test
    public void testGetCandidateExecutors() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(SaturnExecutorsNode.getExecutorsNodePath()))).thenReturn(true);
        String executor = "executor";
        Mockito.when(curatorFrameworkOp.getChildren(ArgumentMatchers.eq(SaturnExecutorsNode.getExecutorsNodePath()))).thenReturn(Lists.newArrayList(executor));
        Assert.assertEquals(jobService.getCandidateExecutors(namespace, jobName).size(), 1);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PREFER_LIST))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PREFER_LIST))).thenReturn("preferExecutor2,@preferExecutor3");
        Assert.assertEquals(jobService.getCandidateExecutors(namespace, jobName).size(), 3);
    }

    @Test
    public void testSetPreferListFailByNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("??????%s???Executor???????????", jobName));
        jobService.setPreferList(namespace, jobName, "preferList", userName);
    }

    @Test
    public void testSetPreferListFailByLocalModeNotStop() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(true);
        jobConfig4DB.setLocalMode(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???????????(%s)???????Executor??????", jobName));
        jobService.setPreferList(namespace, jobName, "preferList", userName);
    }

    @Test
    public void testSetPreferListSuccess() throws Exception {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(false);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        String preferList = "preferList";
        jobService.setPreferList(namespace, jobName, preferList, userName);
        Mockito.verify(currentJobConfigService).updateNewAndSaveOld2History(ArgumentMatchers.any(JobConfig4DB.class), ArgumentMatchers.eq(jobConfig4DB), ArgumentMatchers.eq(userName));
        Mockito.verify(curatorFrameworkOp).update(ArgumentMatchers.eq(SaturnExecutorsNode.getJobConfigPreferListNodePath(jobName)), ArgumentMatchers.eq(preferList));
        Mockito.verify(curatorFrameworkOp).delete(ArgumentMatchers.eq(SaturnExecutorsNode.getJobConfigForceShardNodePath(jobName)));
        Mockito.verify(curatorFrameworkOp).create(ArgumentMatchers.eq(SaturnExecutorsNode.getJobConfigForceShardNodePath(jobName)));
    }

    @Test
    public void testAddJobFailByWithoutJobName() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("?????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByJobNameInvalid() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName("!@#aa");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("???????????0-9?????a-z?????A-Z????_");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByDependingJobNameInvalid() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setDependencies("12!@@");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("?????????????0-9?????a-z?????A-Z????_?????,");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByWithoutJobType() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByJobTypeInvalid() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType("unknown");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByJavaJobWithoutClass() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??java??????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByPassiveJavaJobWithoutClass() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(PASSIVE_JAVA_JOB.name());
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??java??????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByVMSJobWithoutClass() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??java??????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByShellJobWithoutCron() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(SHELL_JOB.name());
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??cron???cron?????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByShellJobCronInvalid() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(SHELL_JOB.name());
        jobConfig.setCron("xxxxx");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("cron???????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByMsgJobWithoutQueue() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(MSG_JOB.name());
        jobConfig.setJobClass("testCLass");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("???????queue??");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByLocalModeJobWithoutShardingItem() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setLocalMode(true);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("????????????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByNoLocalModeJobWithoutShardingItem() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setLocalMode(false);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??????????????1");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByNoLocalModeJoShardingItemInvalid() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setLocalMode(false);
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("001");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????'%s'????", "001"));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByNoLocalModeJoShardingItemInvalidNumber() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setLocalMode(false);
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("x=x");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????'%s'????", "x=x"));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByAddSystemJob() throws SaturnJobConsoleException {
        JobConfig jobConfig = createValidJob();
        jobConfig.setJobMode(SYSTEM_PREFIX);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("???????????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByJobIsExist() throws SaturnJobConsoleException {
        JobConfig jobConfig = createValidJob();
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(ArgumentMatchers.eq(namespace), ArgumentMatchers.eq(jobName))).thenReturn(jobConfig4DB);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???(%s)????", jobName));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByLimitNum() throws SaturnJobConsoleException {
        JobConfig jobConfig = createValidJob();
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName("abc");
        Mockito.when(systemConfigService.getIntegerValue(ArgumentMatchers.eq(MAX_JOB_NUM), ArgumentMatchers.eq(100))).thenReturn(1);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("??????????(%d)????%s????", 1, jobName));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByDeleting() throws SaturnJobConsoleException {
        JobConfig jobConfig = createValidJob();
        String server = "e1";
        Mockito.when(registryCenterService.getCuratorFrameworkOp(ArgumentMatchers.eq(namespace))).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobConfig.getJobName())))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getServerNodePath(jobConfig.getJobName())))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.getChildren(ArgumentMatchers.eq(JobNodePath.getServerNodePath(jobConfig.getJobName())))).thenReturn(Lists.newArrayList(server));
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(ExecutorNodePath.getExecutorNodePath(server, "ip")))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getServerStatus(jobConfig.getJobName(), server)))).thenReturn(true);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???(%s)???????????", jobName));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobSuccess() throws Exception {
        JobConfig jobConfig = createValidJob();
        Mockito.when(registryCenterService.getCuratorFrameworkOp(ArgumentMatchers.eq(namespace))).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.inTransaction()).thenReturn(curatorTransactionOp);
        Mockito.when(curatorTransactionOp.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(curatorTransactionOp);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobConfig.getJobName())))).thenReturn(true);
        jobService.addJob(namespace, jobConfig, userName);
        Mockito.verify(curatorFrameworkOp).deleteRecursive(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobConfig.getJobName())));
        Mockito.verify(currentJobConfigService).create(ArgumentMatchers.any(JobConfig4DB.class));
    }

    @Test
    public void testCopyJobSuccess() throws Exception {
        JobConfig jobConfig = createValidJob();
        Mockito.when(registryCenterService.getCuratorFrameworkOp(ArgumentMatchers.eq(namespace))).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.inTransaction()).thenReturn(curatorTransactionOp);
        Mockito.when(curatorFrameworkOp.checkExists(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobConfig.getJobName())))).thenReturn(true);
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(ArgumentMatchers.eq(namespace), ArgumentMatchers.eq("copyJob"))).thenReturn(jobConfig4DB);
        Mockito.when(curatorFrameworkOp.inTransaction()).thenReturn(curatorTransactionOp);
        Mockito.when(curatorTransactionOp.replaceIfChanged(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(curatorTransactionOp);
        Mockito.when(curatorTransactionOp.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(curatorTransactionOp);
        jobService.copyJob(namespace, jobConfig, "copyJob", userName);
        Mockito.verify(curatorFrameworkOp).deleteRecursive(ArgumentMatchers.eq(JobNodePath.getJobNodePath(jobConfig.getJobName())));
        Mockito.verify(currentJobConfigService).create(ArgumentMatchers.any(JobConfig4DB.class));
    }

    @Test
    public void testAddJobFailByNoLocalModeJoShardingItemLess() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setLocalMode(false);
        jobConfig.setShardingTotalCount(2);
        jobConfig.setShardingItemParameters("0=1");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("????????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByLocalModeJobShardingItemInvalid() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setLocalMode(true);
        jobConfig.setShardingItemParameters("test");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??????????????????*=xx?");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByLocalModeJobHasDownStream() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setLocalMode(true);
        jobConfig.setShardingItemParameters("*=xx");
        jobConfig.setDownStream("test");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("????????????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByHasDownStreamButShardingTotalCountIsNotOne() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setShardingTotalCount(2);
        jobConfig.setShardingItemParameters("0=0,1=1");
        jobConfig.setDownStream("test");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("????1?????????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByHasDownStreamButIsSelf() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setDownStream(jobName);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage((("????(" + (jobName)) + ")????????"));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByHasDownStreamButNotExisting() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setDownStream("test");
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("????(test)???");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByHasDownStreamButIsAncestor() throws Exception {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(PASSIVE_SHELL_JOB.name());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setUpStream("test1");
        jobConfig.setDownStream("test1");
        JobConfig4DB test1 = new JobConfig4DB();
        test1.setJobName("test1");
        test1.setJobType(PASSIVE_SHELL_JOB.name());
        test1.setShardingTotalCount(1);
        test1.setShardingItemParameters("0=0");
        Mockito.when(currentJobConfigService.findConfigsByNamespace(ArgumentMatchers.eq(namespace))).thenReturn(Arrays.asList(test1));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.inTransaction()).thenReturn(curatorTransactionOp);
        Mockito.when(curatorTransactionOp.replaceIfChanged(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(curatorTransactionOp);
        Mockito.when(curatorTransactionOp.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(curatorTransactionOp);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("??(%s)??????????: %s", namespace, "[testJob, test1, testJob]"));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByHasDownStreamButIsAncestor2() throws Exception {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(PASSIVE_SHELL_JOB.name());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setUpStream("test2");
        jobConfig.setDownStream("test1");
        JobConfig4DB test1 = new JobConfig4DB();
        test1.setJobName("test1");
        test1.setJobType(PASSIVE_SHELL_JOB.name());
        test1.setShardingTotalCount(1);
        test1.setShardingItemParameters("0=0");
        test1.setDownStream("test2");
        JobConfig4DB test2 = new JobConfig4DB();
        test2.setJobName("test2");
        test2.setJobType(PASSIVE_SHELL_JOB.name());
        test2.setShardingTotalCount(1);
        test2.setShardingItemParameters("0=0");
        test2.setUpStream("test1");
        Mockito.when(currentJobConfigService.findConfigsByNamespace(ArgumentMatchers.eq(namespace))).thenReturn(Arrays.asList(test1, test2));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.inTransaction()).thenReturn(curatorTransactionOp);
        Mockito.when(curatorTransactionOp.replaceIfChanged(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(curatorTransactionOp);
        Mockito.when(curatorTransactionOp.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(curatorTransactionOp);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("??(%s)??????????: %s", namespace, "[testJob, test2, test1, testJob]"));
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testAddJobFailByHasDownStreamButIsNotPassive() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setJobType(JAVA_JOB.name());
        jobConfig.setCron("0 */2 * * * ?");
        jobConfig.setJobClass("testCLass");
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setDownStream("test1");
        JobConfig4DB test1 = new JobConfig4DB();
        test1.setJobName("test1");
        Mockito.when(currentJobConfigService.findConfigsByNamespace(ArgumentMatchers.eq(namespace))).thenReturn(Arrays.asList(test1));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("????(test1)??????");
        jobService.addJob(namespace, jobConfig, userName);
    }

    @Test
    public void testGetUnSystemJobsWithCondition() throws SaturnJobConsoleException {
        String namespace = "ns1";
        String jobName = "testJob";
        int count = 4;
        Map<String, Object> condition = buildCondition(null);
        Mockito.when(currentJobConfigService.findConfigsByNamespaceWithCondition(ArgumentMatchers.eq(namespace), ArgumentMatchers.eq(condition), Matchers.<Pageable>anyObject())).thenReturn(buildJobConfig4DBList(namespace, jobName, count));
        Assert.assertTrue(((jobService.getUnSystemJobsWithCondition(namespace, condition, 1, 25).size()) == count));
    }

    @Test
    public void testGetMaxJobNum() {
        Assert.assertEquals(jobService.getMaxJobNum(), 100);
    }

    @Test
    public void testGetUnSystemJob() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(new JobConfig4DB()));
        Assert.assertEquals(jobService.getUnSystemJobs(namespace).size(), 1);
    }

    @Test
    public void testGetUnSystemJobWithConditionAndStatus() throws SaturnJobConsoleException {
        String namespace = "ns1";
        String jobName = "testJob";
        int count = 4;
        List<JobConfig4DB> jobConfig4DBList = buildJobConfig4DBList(namespace, jobName, count);
        Map<String, Object> condition = buildCondition(READY);
        Mockito.when(currentJobConfigService.findConfigsByNamespaceWithCondition(ArgumentMatchers.eq(namespace), ArgumentMatchers.eq(condition), Matchers.<Pageable>anyObject())).thenReturn(jobConfig4DBList);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        for (int i = 0; i < count; i++) {
            JobConfig4DB jobConfig4DB = jobConfig4DBList.get(i);
            // ?? index ????job enabled ? true
            jobConfig4DB.setEnabled(((i % 2) == 1));
            Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(ArgumentMatchers.eq(namespace), ArgumentMatchers.eq(jobConfig4DB.getJobName()))).thenReturn(jobConfig4DB);
            Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobConfig4DB.getJobName()))).thenReturn(null);
        }
        Assert.assertTrue(((jobService.getUnSystemJobsWithCondition(namespace, condition, 1, 25).size()) == (count / 2)));
    }

    @Test
    public void testGetUnSystemJobNames() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Assert.assertEquals(jobService.getUnSystemJobs(namespace).size(), 1);
    }

    @Test
    public void testGetJobName() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigNamesByNamespace(namespace)).thenReturn(null);
        Assert.assertTrue(jobService.getJobNames(namespace).isEmpty());
        Mockito.when(currentJobConfigService.findConfigNamesByNamespace(namespace)).thenReturn(Lists.newArrayList(jobName));
        Assert.assertEquals(jobService.getJobNames(namespace).size(), 1);
    }

    @Test
    public void testPersistJobFromDb() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        jobService.persistJobFromDB(namespace, jobConfig);
        jobService.persistJobFromDB(jobConfig, curatorFrameworkOp);
    }

    @Test
    public void testImportFailByWithoutJobName() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("??????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByJobNameInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName("!@avb");
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????????0-9?????a-z?????A-Z????_?"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByWithoutJobType() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByUnknownJobType() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn("xxx");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByJavaJobWithoutClass() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JAVA_JOB.name());
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("??java???????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByPassiveJavaJobWithoutClass() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(PASSIVE_JAVA_JOB.name());
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("??java???????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByShellJobWithoutCron() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("??cron???cron??????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByShellJobCronInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("xxxx");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("cron????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByWithoutShardingCount() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("?????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByShardingCountInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("xxx");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("?????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByShardingCountLess4One() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("0");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????1"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByTimeoutSecondsInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIMEOUT_SECONDS))).thenReturn("error");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???Kill??/????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByLocalJobWithoutShardingParam() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("true");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("????????????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByLocalJobShardingParamInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("true");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("??????????????????*=xx?"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByShardingParamLess4Count() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("2");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("?????????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByProcessCountIntervalSecondsInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PROCESS_COUNT_INTERVAL_SECONDS))).thenReturn("error");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByLoadLevelInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOAD_LEVEL))).thenReturn("error");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByJobDegreeInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_DEGREE))).thenReturn("error");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("?????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByJobModeInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_MODE))).thenReturn(SYSTEM_PREFIX);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByDependenciesInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_DEPENDENCIES))).thenReturn("!@error");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("?????????????0-9?????a-z?????A-Z????_?????,"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByTimeout4AlarmSecondsInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIMEOUT_4_ALARM_SECONDS))).thenReturn("error");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByTimeZoneInvalid() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIME_ZONE))).thenReturn("error");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("????"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByLocalJobNotSupportFailover() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("*=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("true");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_FAILOVER))).thenReturn("true");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????failover"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByVMSJobNotSupportFailover() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(MSG_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_QUEUE_NAME))).thenReturn("queue");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("*=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("false");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_FAILOVER))).thenReturn("true");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????failover"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailByVMSJobNotSupportRerun() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(MSG_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_QUEUE_NAME))).thenReturn("queue");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_RERUN))).thenReturn("true");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(StringContains.containsString("???????rerun"));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportFailTotalCountLimit() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        Mockito.when(systemConfigService.getIntegerValue(MAX_JOB_NUM, 100)).thenReturn(1);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("??????????(%d)?????", 1));
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testImportSuccess() throws SaturnJobConsoleException, IOException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("0 */2 * * * ?");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        Mockito.when(systemConfigService.getIntegerValue(MAX_JOB_NUM, 100)).thenReturn(100);
        jobService.importJobs(namespace, data, userName);
    }

    @Test
    public void testExport() throws SaturnJobConsoleException, IOException, BiffException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        File file = jobService.exportJobs(namespace);
        MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
        Workbook workbook = Workbook.getWorkbook(data.getInputStream());
        Assert.assertNotNull(workbook);
        Sheet[] sheets = workbook.getSheets();
        Assert.assertEquals(sheets.length, 1);
    }

    @Test
    public void testIsJobShardingAllocatedExecutor() throws SaturnJobConsoleException {
        String namespace = "ns1";
        String jobName = "testJob";
        String executor = "executor1";
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executor));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executor, "sharding"))).thenReturn("true");
        Assert.assertTrue(jobService.isJobShardingAllocatedExecutor(namespace, jobName));
    }

    @Test
    public void testGetExecutionStatusSuccessfully() throws Exception {
        String namespace = "ns1";
        String jobName = "jobA";
        String executorName = "exec1";
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(buildJobConfig4DB(namespace, jobName));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        List<String> shardItems = Lists.newArrayList("0", "1", "2", "3", "4");
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(shardItems);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executorName));
        // 3???
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getServerSharding(jobName, executorName))).thenReturn("0,1,2,3,4");
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getEnabledReportNodePath(jobName))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getEnabledReportNodePath(jobName))).thenReturn("true");
        // 0???running
        mockExecutionStatusNode(true, false, false, false, false, executorName, jobName, "0");
        // 1???completed
        mockExecutionStatusNode(false, true, false, false, false, executorName, jobName, "1");
        // 2???fail
        mockExecutionStatusNode(false, true, false, true, false, executorName, jobName, "2");
        // 3???failover
        mockExecutionStatusNode(true, false, true, false, false, executorName, jobName, "3");
        // 4???timeout
        mockExecutionStatusNode(false, true, false, false, true, executorName, jobName, "4");
        mockJobMessage(jobName, "0", "this is message");
        mockJobMessage(jobName, "1", "this is message");
        mockJobMessage(jobName, "2", "this is message");
        mockJobMessage(jobName, "3", "this is message");
        mockJobMessage(jobName, "4", "this is message");
        mockTimezone(jobName, "Asia/Shanghai");
        mockExecutionNodeData(jobName, "0", "lastBeginTime", "0");
        mockExecutionNodeData(jobName, "0", "nextFireTime", "2000");
        mockExecutionNodeData(jobName, "0", "lastCompleteTime", "1000");
        mockExecutionNodeData(jobName, "1", "lastBeginTime", "0");
        mockExecutionNodeData(jobName, "1", "nextFireTime", "2000");
        mockExecutionNodeData(jobName, "1", "lastCompleteTime", "1000");
        mockExecutionNodeData(jobName, "2", "lastBeginTime", "0");
        mockExecutionNodeData(jobName, "2", "nextFireTime", "2000");
        mockExecutionNodeData(jobName, "2", "lastCompleteTime", "1000");
        mockExecutionNodeData(jobName, "3", "lastBeginTime", "0");
        mockExecutionNodeData(jobName, "3", "nextFireTime", "2000");
        mockExecutionNodeData(jobName, "3", "lastCompleteTime", "1000");
        mockExecutionNodeData(jobName, "4", "nextFireTime", "2000");
        mockExecutionNodeData(jobName, "4", "lastCompleteTime", "1000");
        List<ExecutionInfo> result = jobService.getExecutionStatus(namespace, jobName);
        Assert.assertEquals("size should be 5", 5, result.size());
        // verify 0???
        ExecutionInfo executionInfo = result.get(0);
        Assert.assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
        Assert.assertEquals("jobName not equal", jobName, executionInfo.getJobName());
        Assert.assertEquals("status not equal", RUNNING, executionInfo.getStatus());
        Assert.assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
        Assert.assertFalse("failover should be false", executionInfo.getFailover());
        Assert.assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
        Assert.assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
        Assert.assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
        // verify 1???
        executionInfo = result.get(1);
        Assert.assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
        Assert.assertEquals("jobName not equal", jobName, executionInfo.getJobName());
        Assert.assertEquals("status not equal", COMPLETED, executionInfo.getStatus());
        Assert.assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
        Assert.assertFalse("failover should be false", executionInfo.getFailover());
        Assert.assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
        Assert.assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
        Assert.assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
        // verify 2???
        executionInfo = result.get(2);
        Assert.assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
        Assert.assertEquals("jobName not equal", jobName, executionInfo.getJobName());
        Assert.assertEquals("status not equal", FAILED, executionInfo.getStatus());
        Assert.assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
        Assert.assertFalse("failover should be false", executionInfo.getFailover());
        Assert.assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
        Assert.assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
        Assert.assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
        // verify 3???
        executionInfo = result.get(3);
        Assert.assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
        Assert.assertEquals("jobName not equal", jobName, executionInfo.getJobName());
        Assert.assertEquals("status not equal", RUNNING, executionInfo.getStatus());
        Assert.assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
        Assert.assertTrue("failover should be false", executionInfo.getFailover());
        Assert.assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
        Assert.assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
        Assert.assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
        // verify 4???
        executionInfo = result.get(4);
        Assert.assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
        Assert.assertEquals("jobName not equal", jobName, executionInfo.getJobName());
        Assert.assertEquals("status not equal", TIMEOUT, executionInfo.getStatus());
        Assert.assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
        Assert.assertFalse("failover should be false", executionInfo.getFailover());
        Assert.assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
        Assert.assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
    }

    @Test
    public void testGetJobConfigFromZK() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(SHELL_JOB.name());
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PROCESS_COUNT_INTERVAL_SECONDS))).thenReturn("100");
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIMEOUT_SECONDS))).thenReturn("100");
        Assert.assertNotNull(jobService.getJobConfigFromZK(namespace, jobName));
    }

    @Test
    public void testGetJobConfigFailByJobNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???(%s)???", jobName));
        jobService.getJobConfig(namespace, jobName);
    }

    @Test
    public void testGetJobConfigSuccess() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(new JobConfig4DB());
        Assert.assertNotNull(jobService.getJobConfig(namespace, jobName));
    }

    @Test
    public void testGetJobStatusFailByJobNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("????????%s?????????????", jobName));
        jobService.getJobStatus(namespace, jobName);
    }

    @Test
    public void testGetJobStatusSuccess() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Assert.assertEquals(jobService.getJobStatus(namespace, jobName), READY);
        JobConfig jobConfig = new JobConfig();
        jobConfig.setEnabled(true);
        Assert.assertEquals(jobService.getJobStatus(namespace, jobConfig), READY);
    }

    @Test
    public void testGetServerList() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(null);
        Assert.assertTrue(jobService.getJobServerList(namespace, jobName).isEmpty());
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
        Assert.assertEquals(jobService.getJobServerList(namespace, jobName).size(), 1);
    }

    @Test
    public void testGetJobConfigVoFailByJobNotExist() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???(%s)???", jobName));
        jobService.getJobConfigVo(namespace, jobName);
    }

    @Test
    public void testGetJobConfigVoSuccess() throws SaturnJobConsoleException {
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(new JobConfig4DB());
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Assert.assertNotNull(jobService.getJobConfigVo(namespace, jobName));
    }

    @Test
    public void testUpdateJobConfigFailByJobNotExist() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobConfig.getJobName())).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???(%s)???", jobName));
        jobService.getJobConfigVo(namespace, jobName);
    }

    @Test
    public void testUpdateJobConfigSuccess() throws SaturnJobConsoleException {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobConfig.getJobName())).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        jobService.getJobConfigVo(namespace, jobName);
    }

    @Test
    public void testGetAllJobNamesFromZK() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.get$JobsNodePath())).thenReturn(null);
        Assert.assertTrue(jobService.getAllJobNamesFromZK(namespace).isEmpty());
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.get$JobsNodePath())).thenReturn(Lists.newArrayList(jobName));
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName))).thenReturn(true);
        Assert.assertEquals(jobService.getAllJobNamesFromZK(namespace).size(), 1);
    }

    @Test
    public void testUpdateJobCronFailByCronInvalid() throws SaturnJobConsoleException {
        String cron = "error";
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("The cron expression is invalid: %s", cron));
        jobService.updateJobCron(namespace, jobName, cron, null, userName);
    }

    @Test
    public void testUpdateJobCronFailByJobNotExist() throws SaturnJobConsoleException {
        String cron = "0 */2 * * * ?";
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName))).thenReturn(false);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("The job does not exists: %s", jobName));
        jobService.updateJobCron(namespace, jobName, cron, null, userName);
    }

    @Test
    public void testUpdateJobCronSuccess() throws SaturnJobConsoleException {
        String cron = "0 */2 * * * ?";
        Map<String, String> customContext = Maps.newHashMap();
        customContext.put("test", "test");
        CuratorFramework curatorFramework = Mockito.mock(CuratorFramework.class);
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        Mockito.when(curatorFrameworkOp.getCuratorFramework()).thenReturn(curatorFramework);
        Mockito.when(curatorFramework.getNamespace()).thenReturn(namespace);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName))).thenReturn(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        jobService.updateJobCron(namespace, jobName, cron, customContext, userName);
    }

    @Test
    public void testGetJobServers() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getLeaderNodePath(jobName, "election/host"))).thenReturn("127.0.0.1");
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Assert.assertEquals(jobService.getJobServers(namespace, jobName).size(), 1);
    }

    @Test
    public void testGetJobServerStatus() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
        Assert.assertEquals(jobService.getJobServersStatus(namespace, jobName).size(), 1);
    }

    @Test
    public void testRunAtOneFailByNotReady() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(false);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???(%s)???READY?????????", jobName));
        jobService.runAtOnce(namespace, jobName);
    }

    @Test
    public void testRunAtOnceFailByNoExecutor() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(null);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("??executor?????(%s)???????", jobName));
        jobService.runAtOnce(namespace, jobName);
    }

    @Test
    public void testRunAtOnceFailByNoOnlineExecutor() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage("??ONLINE?executor???????");
        jobService.runAtOnce(namespace, jobName);
    }

    @Test
    public void testRunAtOnceSuccess() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(true);
        String executor = "executor";
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executor));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executor, "status"))).thenReturn("true");
        jobService.runAtOnce(namespace, jobName);
        Mockito.verify(curatorFrameworkOp).create(JobNodePath.getRunOneTimePath(jobName, executor), "null");
    }

    @Test
    public void testStopAtOneFailByNotStopping() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("???(%s)???STOPPING?????????", jobName));
        jobService.stopAtOnce(namespace, jobName);
    }

    @Test
    public void testStopAtOnceFailByNoExecutor() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(false);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath(jobName, "1", "running"))).thenReturn(true);
        expectedException.expect(SaturnJobConsoleException.class);
        expectedException.expectMessage(String.format("??executor?????(%s)???????", jobName));
        jobService.stopAtOnce(namespace, jobName);
    }

    @Test
    public void testStopAtOnceSuccess() throws SaturnJobConsoleException {
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(false);
        String executor = "executor";
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        Mockito.when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath(jobName, "1", "running"))).thenReturn(true);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executor));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executor, "status"))).thenReturn("true");
        jobService.stopAtOnce(namespace, jobName);
        Mockito.verify(curatorFrameworkOp).create(JobNodePath.getStopOneTimePath(jobName, executor));
    }

    @Test
    public void testGetExecutionStatusByJobHasStopped() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        // test get execution status by job has stopped
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setJobName(jobName);
        jobConfig4DB.setEnabled(false);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Assert.assertTrue(jobService.getExecutionStatus(namespace, jobName).isEmpty());
    }

    @Test
    public void testGetExecutionStatusByWithoutItem() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setEnabled(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(null);
        Assert.assertTrue(jobService.getExecutionStatus(namespace, jobName).isEmpty());
    }

    @Test
    public void testGetExecutionStatus() throws SaturnJobConsoleException {
        Mockito.when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
        JobConfig4DB jobConfig4DB = new JobConfig4DB();
        jobConfig4DB.setEnabled(true);
        Mockito.when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
        Mockito.when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("server"));
        Mockito.when(curatorFrameworkOp.getData(JobNodePath.getServerSharding(jobName, "server"))).thenReturn("0");
    }
}

