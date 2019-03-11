/**
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.batch.core.step.item;


import BatchStatus.COMPLETED;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.step.factory.FaultTolerantStepFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.support.transaction.TransactionAwareProxyFactory;
import org.springframework.util.StringUtils;


public class FaultTolerantStepFactoryBeanNonBufferingTests {
    protected final Log logger = LogFactory.getLog(getClass());

    private FaultTolerantStepFactoryBean<String, String> factory = new FaultTolerantStepFactoryBean();

    private List<String> items = Arrays.asList("1", "2", "3", "4", "5");

    private ListItemReader<String> reader = new ListItemReader(TransactionAwareProxyFactory.createTransactionalList(items));

    private FaultTolerantStepFactoryBeanNonBufferingTests.SkipWriterStub writer = new FaultTolerantStepFactoryBeanNonBufferingTests.SkipWriterStub();

    private JobExecution jobExecution;

    private static final SkippableRuntimeException exception = new SkippableRuntimeException("exception in writer");

    int count = 0;

    /**
     * Check items causing errors are skipped as expected.
     */
    @Test
    @SuppressWarnings("rawtypes")
    public void testSkip() throws Exception {
        @SuppressWarnings("unchecked")
        SkipListener<Integer, String> skipListener = Mockito.mock(SkipListener.class);
        skipListener.onSkipInWrite("3", FaultTolerantStepFactoryBeanNonBufferingTests.exception);
        skipListener.onSkipInWrite("4", FaultTolerantStepFactoryBeanNonBufferingTests.exception);
        factory.setListeners(new SkipListener[]{ skipListener });
        Step step = factory.getObject();
        StepExecution stepExecution = new StepExecution(step.getName(), jobExecution);
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(2, stepExecution.getWriteSkipCount());
        // only one exception caused rollback, and only once in this case
        // because all items in that chunk were skipped immediately
        Assert.assertEquals(1, stepExecution.getRollbackCount());
        Assert.assertFalse(writer.written.contains("4"));
        List<String> expectedOutput = Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,2,5"));
        Assert.assertEquals(expectedOutput, writer.written);
        // 5 items + 1 rollbacks reading 2 items each time
        Assert.assertEquals(7, stepExecution.getReadCount());
    }

    /**
     * Simple item writer that supports skip functionality.
     */
    private static class SkipWriterStub implements ItemWriter<String> {
        protected final Log logger = LogFactory.getLog(getClass());

        // simulate transactional output
        private List<Object> written = TransactionAwareProxyFactory.createTransactionalList();

        private final Collection<String> failures;

        public SkipWriterStub() {
            this(Arrays.asList("4"));
        }

        /**
         *
         *
         * @param failures
         * 		commaDelimitedListToSet
         */
        public SkipWriterStub(Collection<String> failures) {
            this.failures = failures;
        }

        @Override
        public void write(List<? extends String> items) throws Exception {
            logger.debug(("Writing: " + items));
            for (String item : items) {
                if (failures.contains(item)) {
                    logger.debug((("Throwing write exception on [" + item) + "]"));
                    throw FaultTolerantStepFactoryBeanNonBufferingTests.exception;
                }
                written.add(item);
            }
        }
    }
}

