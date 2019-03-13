/**
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.repository.dao;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "sql-dao-test.xml")
public class JdbcJobInstanceDaoTests extends AbstractJobInstanceDaoTests {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobInstanceDao jobInstanceDao;

    @Autowired
    private JobExecutionDao jobExecutionDao;

    @Transactional
    @Test
    public void testFindJobInstanceByExecution() {
        JobParameters jobParameters = new JobParameters();
        JobInstance jobInstance = dao.createJobInstance("testInstance", jobParameters);
        JobExecution jobExecution = new JobExecution(jobInstance, 2L, jobParameters, null);
        jobExecutionDao.saveJobExecution(jobExecution);
        JobInstance returnedInstance = dao.getJobInstance(jobExecution);
        Assert.assertEquals(jobInstance, returnedInstance);
    }

    @Test
    public void testHexing() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] bytes = digest.digest("f78spx".getBytes("UTF-8"));
        StringBuilder output = new StringBuilder();
        for (byte bite : bytes) {
            output.append(String.format("%02x", bite));
        }
        Assert.assertEquals(("Wrong hash: " + output), 32, output.length());
        String value = String.format("%032x", new BigInteger(1, bytes));
        Assert.assertEquals(("Wrong hash: " + value), 32, value.length());
        Assert.assertEquals(value, output.toString());
    }

    @Test
    public void testJobInstanceWildcard() {
        dao.createJobInstance("anotherJob", new JobParameters());
        dao.createJobInstance("someJob", new JobParameters());
        List<JobInstance> jobInstances = dao.findJobInstancesByName("*Job", 0, 2);
        Assert.assertEquals(2, jobInstances.size());
        for (JobInstance instance : jobInstances) {
            Assert.assertTrue(instance.getJobName().contains("Job"));
        }
        jobInstances = dao.getJobInstances("Job*", 0, 2);
        Assert.assertTrue(jobInstances.isEmpty());
    }
}

