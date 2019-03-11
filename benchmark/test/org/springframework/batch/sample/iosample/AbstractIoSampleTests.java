/**
 * Copyright 2008-2010 the original author or authors.
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
import CustomerCreditIncreaseProcessor.FIXED_AMOUNT;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.sample.domain.trade.CustomerCredit;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


/**
 * Base class for IoSample tests that increase input customer credit by fixed
 * amount. Assumes inputs and outputs are in the same format and uses the job's
 * {@link ItemReader} to parse the outputs.
 *
 * @author Robert Kasanicky
 */
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/job-runner-context.xml", "/jobs/ioSampleJob.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class })
public abstract class AbstractIoSampleTests {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ItemReader<CustomerCredit> reader;

    /**
     * Check the resulting credits correspond to inputs increased by fixed
     * amount.
     */
    @Test
    public void testUpdateCredit() throws Exception {
        open(reader);
        List<CustomerCredit> inputs = getCredits(reader);
        close(reader);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(getUniqueJobParameters());
        Assert.assertEquals(COMPLETED, jobExecution.getStatus());
        pointReaderToOutput(reader);
        open(reader);
        List<CustomerCredit> outputs = getCredits(reader);
        close(reader);
        Assert.assertEquals(inputs.size(), outputs.size());
        int itemCount = inputs.size();
        Assert.assertTrue((itemCount > 0));
        for (int i = 0; i < itemCount; i++) {
            Assert.assertEquals(inputs.get(i).getCredit().add(FIXED_AMOUNT).intValue(), outputs.get(i).getCredit().intValue());
        }
    }
}

