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
package org.springframework.batch.core.step.item;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.FatalStepExecutionException;
import org.springframework.batch.core.step.factory.FaultTolerantStepFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.support.transaction.TransactionAwareProxyFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeEditor;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link FaultTolerantStepFactoryBean}.
 */
public class FaultTolerantStepFactoryBeanRollbackTests {
    protected final Log logger = LogFactory.getLog(getClass());

    private FaultTolerantStepFactoryBean<String, String> factory;

    private SkipReaderStub<String> reader;

    private SkipProcessorStub<String> processor;

    private SkipWriterStub<String> writer;

    private JobExecution jobExecution;

    private StepExecution stepExecution;

    private JobRepository repository;

    @Test
    public void testBeforeChunkListenerException() throws Exception {
        factory.setListeners(new StepListener[]{ new FaultTolerantStepFactoryBeanRollbackTests.ExceptionThrowingChunkListener(1) });
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Assert.assertEquals(BatchStatus.FAILED.toString(), stepExecution.getExitStatus().getExitCode());
        Assert.assertTrue(((stepExecution.getCommitCount()) == 0));// Make sure exception was thrown in after, not before

        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertThat(e, CoreMatchers.instanceOf(FatalStepExecutionException.class));
        Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void testAfterChunkListenerException() throws Exception {
        factory.setListeners(new StepListener[]{ new FaultTolerantStepFactoryBeanRollbackTests.ExceptionThrowingChunkListener(2) });
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Assert.assertEquals(BatchStatus.FAILED.toString(), stepExecution.getExitStatus().getExitCode());
        Assert.assertTrue(((stepExecution.getCommitCount()) > 0));// Make sure exception was thrown in after, not before

        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertThat(e, CoreMatchers.instanceOf(FatalStepExecutionException.class));
        Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void testOverrideWithoutChangingRollbackRules() throws Exception {
        TransactionAttributeEditor editor = new TransactionAttributeEditor();
        editor.setAsText("-RuntimeException");
        TransactionAttribute attr = ((TransactionAttribute) (editor.getValue()));
        Assert.assertTrue(attr.rollbackOn(new RuntimeException("")));
        Assert.assertFalse(attr.rollbackOn(new Exception("")));
    }

    @Test
    public void testChangeRollbackRules() throws Exception {
        TransactionAttributeEditor editor = new TransactionAttributeEditor();
        editor.setAsText("+RuntimeException");
        TransactionAttribute attr = ((TransactionAttribute) (editor.getValue()));
        Assert.assertFalse(attr.rollbackOn(new RuntimeException("")));
        Assert.assertFalse(attr.rollbackOn(new Exception("")));
    }

    @Test
    public void testNonDefaultRollbackRules() throws Exception {
        TransactionAttributeEditor editor = new TransactionAttributeEditor();
        editor.setAsText("+RuntimeException,+SkippableException");
        RuleBasedTransactionAttribute attr = ((RuleBasedTransactionAttribute) (editor.getValue()));
        attr.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
        Assert.assertTrue(attr.rollbackOn(new Exception("")));
        Assert.assertFalse(attr.rollbackOn(new RuntimeException("")));
        Assert.assertFalse(attr.rollbackOn(new SkippableException("")));
    }

    /**
     * Scenario: Exception in reader that should not cause rollback
     */
    @Test
    public void testReaderDefaultNoRollbackOnCheckedException() throws Exception {
        reader.setItems("1", "2", "3", "4");
        reader.setFailures("2", "3");
        reader.setExceptionType(SkippableException.class);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getRollbackCount());
    }

    /**
     * Scenario: Exception in reader that should not cause rollback
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testReaderAttributesOverrideSkippableNoRollback() throws Exception {
        reader.setFailures("2", "3");
        reader.setItems("1", "2", "3", "4");
        reader.setExceptionType(SkippableException.class);
        // No skips by default
        factory.setSkippableExceptionClasses(getExceptionMap(RuntimeException.class));
        // But this one is explicit in the tx-attrs so it should be skipped
        factory.setNoRollbackExceptionClasses(getExceptionList(SkippableException.class));
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getRollbackCount());
    }

    /**
     * Scenario: Exception in processor that should cause rollback because of
     * checked exception
     */
    @Test
    public void testProcessorDefaultRollbackOnCheckedException() throws Exception {
        reader.setItems("1", "2", "3", "4");
        processor.setFailures("1", "3");
        processor.setExceptionType(SkippableException.class);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        Assert.assertEquals(2, stepExecution.getRollbackCount());
    }

    /**
     * Scenario: Exception in processor that should cause rollback
     */
    @Test
    public void testProcessorDefaultRollbackOnRuntimeException() throws Exception {
        reader.setItems("1", "2", "3", "4");
        processor.setFailures("1", "3");
        processor.setExceptionType(SkippableRuntimeException.class);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        Assert.assertEquals(2, stepExecution.getRollbackCount());
    }

    @Test
    public void testNoRollbackInProcessorWhenSkipExceeded() throws Throwable {
        jobExecution = repository.createJobExecution("noRollbackJob", new JobParameters());
        factory.setSkipLimit(0);
        reader.clear();
        reader.setItems("1", "2", "3", "4", "5");
        factory.setItemReader(reader);
        writer.clear();
        factory.setItemWriter(writer);
        processor.clear();
        factory.setItemProcessor(processor);
        @SuppressWarnings("unchecked")
        List<Class<? extends Throwable>> exceptions = Arrays.asList(Exception.class);
        factory.setNoRollbackExceptionClasses(exceptions);
        @SuppressWarnings("unchecked")
        Map<Class<? extends Throwable>, Boolean> skippable = getExceptionMap(Exception.class);
        factory.setSkippableExceptionClasses(skippable);
        processor.setFailures("2");
        Step step = factory.getObject();
        stepExecution = jobExecution.createStepExecution(factory.getName());
        repository.add(stepExecution);
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 3, 4, 5]", writer.getCommitted().toString());
        // No rollback on 2 so processor has side effect
        Assert.assertEquals("[1, 2, 3, 4, 5]", processor.getCommitted().toString());
        List<String> processed = new ArrayList<>(processor.getProcessed());
        Collections.sort(processed);
        Assert.assertEquals("[1, 2, 3, 4, 5]", processed.toString());
        Assert.assertEquals(0, stepExecution.getSkipCount());
    }

    @Test
    public void testProcessSkipWithNoRollbackForCheckedException() throws Exception {
        processor.setFailures("4");
        processor.setExceptionType(SkippableException.class);
        factory.setNoRollbackExceptionClasses(getExceptionList(SkippableException.class));
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(1, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(5, stepExecution.getReadCount());
        Assert.assertEquals(1, stepExecution.getProcessSkipCount());
        Assert.assertEquals(0, stepExecution.getRollbackCount());
        // skips "4"
        Assert.assertTrue(reader.getRead().contains("4"));
        Assert.assertFalse(writer.getCommitted().contains("4"));
        List<String> expectedOutput = Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,2,3,5"));
        Assert.assertEquals(expectedOutput, writer.getCommitted());
    }

    /**
     * Scenario: Exception in writer that should not cause rollback and scan
     */
    @Test
    public void testWriterDefaultRollbackOnCheckedException() throws Exception {
        writer.setFailures("2", "3");
        writer.setExceptionType(SkippableException.class);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        Assert.assertEquals(4, stepExecution.getRollbackCount());
    }

    /**
     * Scenario: Exception in writer that should not cause rollback and scan
     */
    @Test
    public void testWriterDefaultRollbackOnError() throws Exception {
        writer.setFailures("2", "3");
        writer.setExceptionType(AssertionError.class);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(1, stepExecution.getRollbackCount());
    }

    /**
     * Scenario: Exception in writer that should not cause rollback and scan
     */
    @Test
    public void testWriterDefaultRollbackOnRuntimeException() throws Exception {
        writer.setFailures("2", "3");
        writer.setExceptionType(SkippableRuntimeException.class);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        Assert.assertEquals(4, stepExecution.getRollbackCount());
    }

    /**
     * Scenario: Exception in writer that should not cause rollback and scan
     */
    @Test
    public void testWriterNoRollbackOnRuntimeException() throws Exception {
        writer.setFailures("2", "3");
        writer.setExceptionType(SkippableRuntimeException.class);
        factory.setNoRollbackExceptionClasses(getExceptionList(SkippableRuntimeException.class));
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        // Two multi-item chunks rolled back. When the item was encountered on
        // its own it can proceed
        Assert.assertEquals(2, stepExecution.getRollbackCount());
    }

    /**
     * Scenario: Exception in writer that should not cause rollback and scan
     */
    @Test
    public void testWriterNoRollbackOnCheckedException() throws Exception {
        writer.setFailures("2", "3");
        writer.setExceptionType(SkippableException.class);
        factory.setNoRollbackExceptionClasses(getExceptionList(SkippableException.class));
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getSkipCount());
        // Two multi-item chunks rolled back. When the item was encountered on
        // its own it can proceed
        Assert.assertEquals(2, stepExecution.getRollbackCount());
    }

    @Test
    public void testSkipInProcessor() throws Exception {
        processor.setFailures("4");
        factory.setCommitInterval(30);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 4, 1, 2, 3, 5]", processor.getProcessed().toString());
        Assert.assertEquals("[1, 2, 3, 5]", processor.getCommitted().toString());
        Assert.assertEquals("[1, 2, 3, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 3, 5]", writer.getCommitted().toString());
    }

    @Test
    public void testMultipleSkipsInProcessor() throws Exception {
        processor.setFailures("2", "4");
        factory.setCommitInterval(30);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 3, 5]", processor.getCommitted().toString());
        Assert.assertEquals("[1, 3, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 3, 5]", writer.getCommitted().toString());
        Assert.assertEquals("[1, 2, 1, 3, 4, 1, 3, 5]", processor.getProcessed().toString());
    }

    @Test
    public void testMultipleSkipsInNonTransactionalProcessor() throws Exception {
        processor.setFailures("2", "4");
        factory.setCommitInterval(30);
        factory.setProcessorTransactional(false);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 3, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 3, 5]", writer.getCommitted().toString());
        // If non-transactional, we should only process each item once
        Assert.assertEquals("[1, 2, 3, 4, 5]", processor.getProcessed().toString());
    }

    @Test
    public void testFilterInProcessor() throws Exception {
        processor.setFailures("4");
        processor.setFilter(true);
        factory.setCommitInterval(30);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 4, 5]", processor.getProcessed().toString());
        Assert.assertEquals("[1, 2, 3, 4, 5]", processor.getCommitted().toString());
        Assert.assertEquals("[1, 2, 3, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 3, 5]", writer.getCommitted().toString());
    }

    @Test
    public void testSkipInWriter() throws Exception {
        writer.setFailures("4");
        factory.setCommitInterval(30);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 5]", processor.getCommitted().toString());
        Assert.assertEquals("[1, 2, 3, 5]", writer.getCommitted().toString());
        Assert.assertEquals("[1, 2, 3, 4, 1, 2, 3, 4, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 3, 4, 5, 1, 2, 3, 4, 5]", processor.getProcessed().toString());
        Assert.assertEquals(1, stepExecution.getWriteSkipCount());
        Assert.assertEquals(5, stepExecution.getReadCount());
        Assert.assertEquals(4, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
    }

    @Test
    public void testSkipInWriterNonTransactionalProcessor() throws Exception {
        writer.setFailures("4");
        factory.setCommitInterval(30);
        factory.setProcessorTransactional(false);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 5]", writer.getCommitted().toString());
        Assert.assertEquals("[1, 2, 3, 4, 1, 2, 3, 4, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 3, 4, 5]", processor.getProcessed().toString());
    }

    @Test
    public void testSkipInWriterTransactionalReader() throws Exception {
        writer.setFailures("4");
        ItemReader<String> reader = new org.springframework.batch.item.support.ListItemReader(TransactionAwareProxyFactory.createTransactionalList(Arrays.asList("1", "2", "3", "4", "5")));
        factory.setItemReader(reader);
        factory.setCommitInterval(30);
        factory.setSkipLimit(10);
        factory.setIsReaderTransactionalQueue(true);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[]", writer.getCommitted().toString());
        Assert.assertEquals("[1, 2, 3, 4]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 3, 4, 5, 1, 2, 3, 4, 5]", processor.getProcessed().toString());
    }

    @Test
    public void testMultithreadedSkipInWriter() throws Exception {
        writer.setFailures("1", "2", "3", "4", "5");
        factory.setCommitInterval(3);
        factory.setSkipLimit(10);
        factory.setTaskExecutor(new SimpleAsyncTaskExecutor());
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[]", writer.getCommitted().toString());
        Assert.assertEquals("[]", processor.getCommitted().toString());
        Assert.assertEquals(5, stepExecution.getSkipCount());
    }

    @Test
    public void testMultipleSkipsInWriter() throws Exception {
        writer.setFailures("2", "4");
        factory.setCommitInterval(30);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 3, 5]", writer.getCommitted().toString());
        Assert.assertEquals("[1, 2, 1, 2, 3, 4, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 3, 5]", processor.getCommitted().toString());
        Assert.assertEquals("[1, 2, 3, 4, 5, 1, 2, 3, 4, 5]", processor.getProcessed().toString());
        Assert.assertEquals(2, stepExecution.getWriteSkipCount());
        Assert.assertEquals(5, stepExecution.getReadCount());
        Assert.assertEquals(3, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
    }

    @Test
    public void testMultipleSkipsInWriterNonTransactionalProcessor() throws Exception {
        writer.setFailures("2", "4");
        factory.setCommitInterval(30);
        factory.setProcessorTransactional(false);
        Step step = factory.getObject();
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 3, 5]", writer.getCommitted().toString());
        Assert.assertEquals("[1, 2, 1, 2, 3, 4, 5]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 3, 4, 5]", processor.getProcessed().toString());
    }

    class ExceptionThrowingChunkListener implements ChunkListener {
        private int phase = -1;

        public ExceptionThrowingChunkListener(int throwPhase) {
            this.phase = throwPhase;
        }

        @Override
        public void beforeChunk(ChunkContext context) {
            if ((phase) == 1) {
                throw new IllegalArgumentException("Planned exception");
            }
        }

        @Override
        public void afterChunk(ChunkContext context) {
            if ((phase) == 2) {
                throw new IllegalArgumentException("Planned exception");
            }
        }

        @Override
        public void afterChunkError(ChunkContext context) {
            if ((phase) == 3) {
                throw new IllegalArgumentException("Planned exception");
            }
        }
    }
}

