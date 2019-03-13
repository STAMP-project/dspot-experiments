/**
 * Copyright 2006-2013 the original author or authors.
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


import BatchStatus.COMPLETED;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class TablePrefixTests {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    private JdbcTemplate jdbcTemplate;

    @Test
    public void testJobLaunch() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "PREFIX_JOB_INSTANCE"));
    }

    public static class TestTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            return RepeatStatus.FINISHED;
        }
    }
}

