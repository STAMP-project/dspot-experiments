package com.vip.saturn.it.impl;


import JobType.JAVA_JOB;
import SystemEnvProperties.NAME_VIP_SATURN_INIT_JOB_BY_GROUPS;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.job.InitByGroupsJob;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.utils.SystemEnvProperties;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InitJobByGroupsIT extends AbstractSaturnIT {
    @Test
    public void testA_ExecutorNotConfigGroups() throws Exception {
        System.clearProperty(NAME_VIP_SATURN_INIT_JOB_BY_GROUPS);
        SystemEnvProperties.loadProperties();
        InitByGroupsJob.inited = false;
        AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName("testA_ExecutorNotConfigGroups");
        jobConfig.setCron("*/2 * * * * ?");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(InitByGroupsJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setGroups("");
        addJob(jobConfig);
        Thread.sleep(1000);
        assertThat(InitByGroupsJob.inited).isTrue();
        removeJob(jobConfig.getJobName());
    }

    @Test
    public void testB_ExecutorConfigGroupsAndInitJobSuccessfully() throws Exception {
        try {
            System.setProperty(NAME_VIP_SATURN_INIT_JOB_BY_GROUPS, "group1, group2");
            SystemEnvProperties.loadProperties();
            InitByGroupsJob.inited = false;
            AbstractSaturnIT.startOneNewExecutorList();
            Thread.sleep(1000);
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName("testB_ExecutorConfigGroupsAndInitJobSuccessfully");
            jobConfig.setCron("*/2 * * * * ?");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(InitByGroupsJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(1);
            jobConfig.setShardingItemParameters("0=0");
            jobConfig.setGroups("group2");
            addJob(jobConfig);
            Thread.sleep(1000);
            assertThat(InitByGroupsJob.inited).isTrue();
            removeJob(jobConfig.getJobName());
        } finally {
            System.clearProperty(NAME_VIP_SATURN_INIT_JOB_BY_GROUPS);
            SystemEnvProperties.loadProperties();
        }
    }

    @Test
    public void testC_ExecutorConfigGroupsAndInitJobFailed() throws Exception {
        try {
            System.setProperty(NAME_VIP_SATURN_INIT_JOB_BY_GROUPS, "group1, group2");
            SystemEnvProperties.loadProperties();
            InitByGroupsJob.inited = false;
            AbstractSaturnIT.startOneNewExecutorList();
            Thread.sleep(1000);
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName("testC_ExecutorConfigGroupsAndInitJobFailed");
            jobConfig.setCron("*/2 * * * * ?");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(InitByGroupsJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(1);
            jobConfig.setShardingItemParameters("0=0");
            jobConfig.setGroups("");
            addJob(jobConfig);
            Thread.sleep(1000);
            assertThat(InitByGroupsJob.inited).isFalse();
            removeJob(jobConfig.getJobName());
        } finally {
            System.clearProperty(NAME_VIP_SATURN_INIT_JOB_BY_GROUPS);
            SystemEnvProperties.loadProperties();
        }
    }
}

