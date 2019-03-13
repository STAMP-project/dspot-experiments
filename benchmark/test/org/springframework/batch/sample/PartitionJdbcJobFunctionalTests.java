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
package org.springframework.batch.sample;


import BatchStatus.COMPLETED;
import CustomerCreditIncreaseProcessor.FIXED_AMOUNT;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.sample.domain.trade.CustomerCredit;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/jobs/partitionJdbcJob.xml", "/job-runner-context.xml" })
public class PartitionJdbcJobFunctionalTests implements ApplicationContextAware {
    @Autowired
    @Qualifier("inputTestReader")
    private ItemReader<CustomerCredit> inputReader;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private ApplicationContext applicationContext;

    /**
     * Check the resulting credits correspond to inputs increased by fixed
     * amount.
     */
    @Test
    public void testUpdateCredit() throws Exception {
        Assert.assertTrue("Define a prototype bean called 'outputTestReader' to check the output", applicationContext.containsBeanDefinition("outputTestReader"));
        open(inputReader);
        List<CustomerCredit> inputs = new java.util.ArrayList(getCredits(inputReader));
        close(inputReader);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        @SuppressWarnings("unchecked")
        ItemReader<CustomerCredit> outputReader = ((ItemReader<CustomerCredit>) (applicationContext.getBean("outputTestReader")));
        open(outputReader);
        List<CustomerCredit> outputs = new java.util.ArrayList(getCredits(outputReader));
        close(outputReader);
        Assert.assertEquals(inputs.size(), outputs.size());
        int itemCount = inputs.size();
        Assert.assertTrue((itemCount > 0));
        inputs.iterator();
        for (int i = 0; i < itemCount; i++) {
            Assert.assertEquals(inputs.get(i).getCredit().add(FIXED_AMOUNT).intValue(), outputs.get(i).getCredit().intValue());
        }
    }
}

