/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.core.partition;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 */
@ContextConfiguration(locations = "launch-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class VanillaIntegrationTests {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    private JdbcTemplate jdbcTemplate;

    @Test
    public void testSimpleProperties() throws Exception {
        Assert.assertNotNull(jobLauncher);
    }

    @Test
    public void testLaunchJob() throws Exception {
        int beforeMaster = jdbcTemplate.queryForObject("SELECT COUNT(*) from BATCH_STEP_EXECUTION where STEP_NAME='step1:master'", Integer.class);
        int beforePartition = jdbcTemplate.queryForObject("SELECT COUNT(*) from BATCH_STEP_EXECUTION where STEP_NAME like 'step1:partition%'", Integer.class);
        Assert.assertNotNull(jobLauncher.run(job, new JobParameters()));
        int afterMaster = jdbcTemplate.queryForObject("SELECT COUNT(*) from BATCH_STEP_EXECUTION where STEP_NAME='step1:master'", Integer.class);
        int afterPartition = jdbcTemplate.queryForObject("SELECT COUNT(*) from BATCH_STEP_EXECUTION where STEP_NAME like 'step1:partition%'", Integer.class);
        Assert.assertEquals(1, (afterMaster - beforeMaster));
        // Should be same as grid size in step splitter
        Assert.assertEquals(2, (afterPartition - beforePartition));
    }
}

