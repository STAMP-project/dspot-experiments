/**
 * Copyright 2009-2014 the original author or authors.
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
package org.springframework.batch.core.launch;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class JobLauncherIntegrationTests {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    public void testLaunchAndRelaunch() throws Exception {
        int before = jdbcTemplate.queryForObject("select count(*) from BATCH_JOB_INSTANCE", Integer.class);
        JobExecution jobExecution = launch(true, 0);
        launch(false, jobExecution.getId());
        launch(false, jobExecution.getId());
        int after = jdbcTemplate.queryForObject("select count(*) from BATCH_JOB_INSTANCE", Integer.class);
        Assert.assertEquals((before + 1), after);
    }
}

