/**
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.sample;


import ExitStatus.COMPLETED;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.sample.config.JobRunnerConfiguration;
import org.springframework.batch.sample.remotechunking.MasterConfiguration;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * The master step of the job under test will read data and send chunks to the worker
 * (started in {@link RemoteChunkingJobFunctionalTests#setUp()}) for processing and writing.
 *
 * @author Mahmoud Ben Hassine
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { JobRunnerConfiguration.class, MasterConfiguration.class })
@PropertySource("classpath:remote-chunking.properties")
public class RemoteChunkingJobFunctionalTests {
    private static final String BROKER_DATA_DIRECTORY = "build/activemq-data";

    @Value("${broker.url}")
    private String brokerUrl;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private BrokerService brokerService;

    private AnnotationConfigApplicationContext workerApplicationContext;

    @Test
    public void testRemoteChunkingJob() throws Exception {
        // when
        JobExecution jobExecution = this.jobLauncherTestUtils.launchJob();
        // then
        Assert.assertEquals(COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
        // the master sent 2 chunks ({1, 2, 3} and {4, 5, 6}) to workers
        Assert.assertEquals("Waited for 2 results.", jobExecution.getExitStatus().getExitDescription());
    }
}

