/**
 * Copyright 2006-2014 the original author or authors.
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
package org.springframework.batch.sample.iosample;


import BatchStatus.COMPLETED;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.sample.domain.trade.CustomerCredit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 * @since 2.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/jobs/ioSampleJob.xml", "/jobs/iosample/delimited.xml" })
public class TwoJobInstancesDelimitedFunctionalTests {
    @Autowired
    private JobLauncher launcher;

    @Autowired
    private Job job;

    @Autowired
    private ItemReader<CustomerCredit> reader;

    @Autowired
    @Qualifier("itemReader")
    private ItemStream readerStream;

    @Test
    public void testLaunchJobTwice() throws Exception {
        JobExecution jobExecution = launcher.run(this.job, getJobParameters("data/iosample/input/delimited.csv"));
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        verifyOutput(6);
        jobExecution = launcher.run(this.job, getJobParameters("data/iosample/input/delimited2.csv"));
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        verifyOutput(2);
    }
}

