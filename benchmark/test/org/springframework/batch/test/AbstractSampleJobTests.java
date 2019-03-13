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
package org.springframework.batch.test;


import BatchStatus.COMPLETED;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.sample.SampleTasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;


/**
 * This is an abstract test class to be used by test classes to test the
 * {@link AbstractJobTests} class.
 *
 * @author Dan Garrette
 * @since 2.0
 */
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/job-runner-context.xml" })
public abstract class AbstractSampleJobTests {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @Qualifier("tasklet2")
    private SampleTasklet tasklet2;

    @Test
    public void testJob() throws Exception {
        Assert.assertEquals(COMPLETED, jobLauncherTestUtils.launchJob().getStatus());
        this.verifyTasklet(1);
        this.verifyTasklet(2);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonExistentStep() {
        jobLauncherTestUtils.launchStep("nonExistent");
    }

    @Test
    public void testStep1Execution() {
        Assert.assertEquals(COMPLETED, jobLauncherTestUtils.launchStep("step1").getStatus());
        this.verifyTasklet(1);
    }

    @Test
    public void testStep2Execution() {
        Assert.assertEquals(COMPLETED, jobLauncherTestUtils.launchStep("step2").getStatus());
        this.verifyTasklet(2);
    }

    @Test
    @Repeat(10)
    public void testStep3Execution() throws Exception {
        // logging only, may complete in < 1ms (repeat so that it's likely to for at least one of those times)
        Assert.assertEquals(COMPLETED, jobLauncherTestUtils.launchStep("step3").getStatus());
    }

    @Test
    public void testStepLaunchJobContextEntry() {
        ExecutionContext jobContext = new ExecutionContext();
        jobContext.put("key1", "value1");
        Assert.assertEquals(COMPLETED, jobLauncherTestUtils.launchStep("step2", jobContext).getStatus());
        this.verifyTasklet(2);
        Assert.assertTrue(tasklet2.jobContextEntryFound);
    }
}

