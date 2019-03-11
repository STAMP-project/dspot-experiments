/**
 * Copyright 2009-2019 the original author or authors.
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
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.UnexpectedRollbackException;


/**
 *
 *
 * @author Dan Garrette
 * @author Mahmoud Ben Hassine
 * @since 2.0.2
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class FaultTolerantExceptionClassesTests implements ApplicationContextAware {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private SkipReaderStub<String> reader;

    @Autowired
    private SkipWriterStub<String> writer;

    @Autowired
    private ExceptionThrowingTaskletStub tasklet;

    private ApplicationContext applicationContext;

    @Test
    public void testNonSkippable() throws Exception {
        writer.setExceptionType(RuntimeException.class);
        StepExecution stepExecution = launchStep("nonSkippableStep");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testNonSkippableChecked() throws Exception {
        writer.setExceptionType(Exception.class);
        StepExecution stepExecution = launchStep("nonSkippableStep");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testSkippable() throws Exception {
        writer.setExceptionType(SkippableRuntimeException.class);
        StepExecution stepExecution = launchStep("skippableStep");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 4]", writer.getCommitted().toString());
    }

    @Test
    public void testRegularRuntimeExceptionNotSkipped() throws Exception {
        writer.setExceptionType(RuntimeException.class);
        StepExecution stepExecution = launchStep("skippableStep");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        // BATCH-1327:
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        // BATCH-1327:
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testFatalOverridesSkippable() throws Exception {
        writer.setExceptionType(FatalRuntimeException.class);
        StepExecution stepExecution = launchStep("skippableFatalStep");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testDefaultFatalChecked() throws Exception {
        writer.setExceptionType(Exception.class);
        StepExecution stepExecution = launchStep("skippableFatalStep");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        // BATCH-1327:
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        // BATCH-1327:
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testSkippableChecked() throws Exception {
        writer.setExceptionType(SkippableException.class);
        StepExecution stepExecution = launchStep("skippableStep");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 4]", writer.getCommitted().toString());
    }

    @Test
    public void testNonSkippableUnchecked() throws Exception {
        writer.setExceptionType(UnexpectedRollbackException.class);
        StepExecution stepExecution = launchStep("skippableStep");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testFatalChecked() throws Exception {
        writer.setExceptionType(FatalSkippableException.class);
        StepExecution stepExecution = launchStep("skippableFatalStep");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testRetryableButNotSkippable() throws Exception {
        writer.setExceptionType(RuntimeException.class);
        StepExecution stepExecution = launchStep("retryable");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 1, 2, 3]", writer.getWritten().toString());
        // BATCH-1327:
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testRetryableSkippable() throws Exception {
        writer.setExceptionType(SkippableRuntimeException.class);
        StepExecution stepExecution = launchStep("retryable");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 4]", writer.getCommitted().toString());
    }

    @Test
    public void testRetryableFatal() throws Exception {
        // User wants all exceptions to be retried, but only some are skippable
        // FatalRuntimeException is not skippable because it is fatal, but is a
        // subclass of another skippable
        writer.setExceptionType(FatalRuntimeException.class);
        StepExecution stepExecution = launchStep("retryable");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        // BATCH-1333:
        Assert.assertEquals("[1, 2, 3, 1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testRetryableButNotSkippableChecked() throws Exception {
        writer.setExceptionType(Exception.class);
        StepExecution stepExecution = launchStep("retryable");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 1, 2, 3]", writer.getWritten().toString());
        // BATCH-1327:
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testRetryableSkippableChecked() throws Exception {
        writer.setExceptionType(SkippableException.class);
        StepExecution stepExecution = launchStep("retryable");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 4]", writer.getCommitted().toString());
    }

    @Test
    public void testRetryableFatalChecked() throws Exception {
        writer.setExceptionType(FatalSkippableException.class);
        StepExecution stepExecution = launchStep("retryable");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        // BATCH-1333:
        Assert.assertEquals("[1, 2, 3, 1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
    }

    @Test
    public void testNoRollbackDefaultRollbackException() throws Exception {
        // Exception is neither no-rollback nor skippable
        writer.setExceptionType(Exception.class);
        StepExecution stepExecution = launchStep("noRollbackDefault");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        // BATCH-1318:
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        // BATCH-1318:
        Assert.assertEquals("[]", writer.getCommitted().toString());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
    }

    @Test
    public void testNoRollbackDefaultNoRollbackException() throws Exception {
        // Exception is no-rollback and not skippable
        writer.setExceptionType(IllegalStateException.class);
        StepExecution stepExecution = launchStep("noRollbackDefault");
        Assert.assertNotNull(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        // BATCH-1334:
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        // BATCH-1334:
        Assert.assertEquals("[1, 2, 3, 4]", writer.getCommitted().toString());
        // BATCH-1334:
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
    }

    @Test
    public void testNoRollbackPathology() throws Exception {
        // Exception is neither no-rollback nor skippable and no-rollback is
        // RuntimeException (potentially pathological because other obviously
        // rollback signalling Exceptions also extend RuntimeException)
        writer.setExceptionType(Exception.class);
        StepExecution stepExecution = launchStep("noRollbackPathology");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        // BATCH-1335:
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        // BATCH-1335:
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testNoRollbackSkippableRollbackException() throws Exception {
        writer.setExceptionType(SkippableRuntimeException.class);
        StepExecution stepExecution = launchStep("noRollbackSkippable");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        Assert.assertEquals("[1, 2, 4]", writer.getCommitted().toString());
    }

    @Test
    public void testNoRollbackSkippableNoRollbackException() throws Exception {
        writer.setExceptionType(FatalRuntimeException.class);
        StepExecution stepExecution = launchStep("noRollbackSkippable");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        // BATCH-1332:
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        // BATCH-1334:
        // Skipped but also committed (because it was marked as no-rollback)
        Assert.assertEquals("[1, 2, 3, 4]", writer.getCommitted().toString());
        Assert.assertEquals(1, stepExecution.getWriteSkipCount());
    }

    @Test
    public void testNoRollbackFatalRollbackException() throws Exception {
        writer.setExceptionType(SkippableRuntimeException.class);
        StepExecution stepExecution = launchStep("noRollbackFatal");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[1, 2, 3]", writer.getWritten().toString());
        Assert.assertEquals("[]", writer.getCommitted().toString());
    }

    @Test
    public void testNoRollbackFatalNoRollbackException() throws Exception {
        // User has asked for no rollback on a fatal exception. What should the
        // outcome be?  As per BATCH-1333 it is interpreted as not skippable, but
        // retryable if requested.  Here it was not requested to be retried, but
        // it was marked as no-rollback.  As per BATCH-1334 this has to be ignored
        // so that the failed item can be isolated.
        writer.setExceptionType(FatalRuntimeException.class);
        StepExecution stepExecution = launchStep("noRollbackFatal");
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        // BATCH-1331:
        Assert.assertEquals("[1, 2, 3, 1, 2, 3, 4]", writer.getWritten().toString());
        // BATCH-1331:
        Assert.assertEquals("[1, 2, 3, 4]", writer.getCommitted().toString());
    }

    @Test
    @DirtiesContext
    public void testNoRollbackTaskletRollbackException() throws Exception {
        tasklet.setExceptionType(RuntimeException.class);
        StepExecution stepExecution = launchStep("noRollbackTasklet");
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals("[]", tasklet.getCommitted().toString());
    }

    @Test
    @DirtiesContext
    public void testNoRollbackTaskletNoRollbackException() throws Exception {
        tasklet.setExceptionType(SkippableRuntimeException.class);
        StepExecution stepExecution = launchStep("noRollbackTasklet");
        // assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        // BATCH-1298:
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("[1, 1, 1, 1]", tasklet.getCommitted().toString());
    }
}

