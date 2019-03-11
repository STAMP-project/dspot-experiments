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
package org.springframework.batch.core.configuration.xml;


import BatchStatus.COMPLETED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 * @author Josh Long
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PartitionStepWithLateBindingParserTests {
    @Autowired
    @Qualifier("job1")
    private Job job1;

    @Autowired
    @Qualifier("nameStoringTasklet")
    private NameStoringTasklet nameStoringTasklet;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean;

    private List<String> savedStepNames = new ArrayList<>();

    @Test
    public void testExplicitHandlerStep() throws Exception {
        Assert.assertNotNull(job1);
        JobExecution jobExecution = jobRepository.createJobExecution(job1.getName(), new JobParametersBuilder().addLong("gridSize", 1L).toJobParameters());
        job1.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        Collections.sort(savedStepNames);
        Assert.assertEquals("[s1:partition0]", savedStepNames.toString());
        List<String> stepNames = getStepNames(jobExecution);
        Assert.assertEquals(2, stepNames.size());
        Assert.assertEquals("[s1, s1:partition0]", stepNames.toString());
    }
}

