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
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Base class for remote partitioning tests.
 *
 * @author Mahmoud Ben Hassine
 */
@RunWith(SpringRunner.class)
@PropertySource("classpath:remote-partitioning.properties")
public abstract class RemotePartitioningJobFunctionalTests {
    private static final String BROKER_DATA_DIRECTORY = "build/activemq-data";

    @Value("${broker.url}")
    private String brokerUrl;

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    private BrokerService brokerService;

    private EmbeddedDatabase embeddedDatabase;

    private AnnotationConfigApplicationContext workerApplicationContext;

    @Test
    public void testRemotePartitioningJob() throws Exception {
        // when
        JobExecution jobExecution = this.jobLauncherTestUtils.launchJob();
        // then
        Assert.assertEquals(COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
        Assert.assertEquals(4, jobExecution.getStepExecutions().size());// master + 3 workers

    }
}

