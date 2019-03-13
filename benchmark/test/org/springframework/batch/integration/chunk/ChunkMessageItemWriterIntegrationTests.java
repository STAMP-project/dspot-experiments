package org.springframework.batch.integration.chunk;


import BatchStatus.FAILED;
import ChunkMessageChannelItemWriter.ACTUAL;
import ChunkMessageChannelItemWriter.EXPECTED;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.factory.SimpleStepFactoryBean;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ChunkMessageItemWriterIntegrationTests {
    private final ChunkMessageChannelItemWriter<Object> writer = new ChunkMessageChannelItemWriter();

    @Autowired
    @Qualifier("requests")
    private MessageChannel requests;

    @Autowired
    @Qualifier("replies")
    private PollableChannel replies;

    private final SimpleStepFactoryBean<Object, Object> factory = new SimpleStepFactoryBean();

    private SimpleJobRepository jobRepository;

    private static long jobCounter;

    @Test
    public void testOpenWithNoState() throws Exception {
        writer.open(new ExecutionContext());
    }

    @Test
    public void testUpdateAndOpenWithState() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        writer.update(executionContext);
        writer.open(executionContext);
        Assert.assertEquals(0, executionContext.getInt(EXPECTED));
        Assert.assertEquals(0, executionContext.getInt(ACTUAL));
    }

    @Test
    public void testVanillaIteration() throws Exception {
        factory.setItemReader(new org.springframework.batch.item.support.ListItemReader(Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,2,3,4,5,6"))));
        Step step = factory.getObject();
        StepExecution stepExecution = getStepExecution(step);
        step.execute(stepExecution);
        waitForResults(6, 10);
        Assert.assertEquals(6, TestItemWriter.count);
        Assert.assertEquals(6, stepExecution.getReadCount());
    }

    @Test
    public void testSimulatedRestart() throws Exception {
        factory.setItemReader(new org.springframework.batch.item.support.ListItemReader(Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,2,3,4,5,6"))));
        Step step = factory.getObject();
        StepExecution stepExecution = getStepExecution(step);
        // Set up context with two messages (chunks) in the backlog
        stepExecution.getExecutionContext().putInt(EXPECTED, 6);
        stepExecution.getExecutionContext().putInt(ACTUAL, 4);
        // And make the back log real
        requests.send(getSimpleMessage("foo", stepExecution.getJobExecution().getJobId()));
        requests.send(getSimpleMessage("bar", stepExecution.getJobExecution().getJobId()));
        step.execute(stepExecution);
        waitForResults(8, 10);
        Assert.assertEquals(8, TestItemWriter.count);
        Assert.assertEquals(6, stepExecution.getReadCount());
    }

    @Test
    public void testSimulatedRestartWithBadMessagesFromAnotherJob() throws Exception {
        factory.setItemReader(new org.springframework.batch.item.support.ListItemReader(Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,2,3,4,5,6"))));
        Step step = factory.getObject();
        StepExecution stepExecution = getStepExecution(step);
        // Set up context with two messages (chunks) in the backlog
        stepExecution.getExecutionContext().putInt(EXPECTED, 3);
        stepExecution.getExecutionContext().putInt(ACTUAL, 2);
        // Speed up the eventual failure
        writer.setMaxWaitTimeouts(2);
        // And make the back log real
        requests.send(getSimpleMessage("foo", 4321L));
        step.execute(stepExecution);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(ExitStatus.FAILED.getExitCode(), stepExecution.getExitStatus().getExitCode());
        String message = stepExecution.getExitStatus().getExitDescription();
        Assert.assertTrue(("Message does not contain 'wrong job': " + message), message.contains("wrong job"));
        waitForResults(1, 10);
        Assert.assertEquals(1, TestItemWriter.count);
        Assert.assertEquals(0, stepExecution.getReadCount());
    }

    @Test
    public void testEarlyCompletionSignalledInHandler() throws Exception {
        factory.setItemReader(new org.springframework.batch.item.support.ListItemReader(Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,fail,3,4,5,6"))));
        factory.setCommitInterval(2);
        Step step = factory.getObject();
        StepExecution stepExecution = getStepExecution(step);
        step.execute(stepExecution);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(ExitStatus.FAILED.getExitCode(), stepExecution.getExitStatus().getExitCode());
        String message = stepExecution.getExitStatus().getExitDescription();
        Assert.assertTrue(("Message does not contain 'fail': " + message), message.contains("fail"));
        waitForResults(2, 10);
        // The number of items processed is actually between 1 and 6, because
        // the one that failed might have been processed out of order.
        Assert.assertTrue((1 <= (TestItemWriter.count)));
        Assert.assertTrue((6 >= (TestItemWriter.count)));
        // But it should fail the step in any case
        Assert.assertEquals(FAILED, stepExecution.getStatus());
    }

    @Test
    public void testSimulatedRestartWithNoBacklog() throws Exception {
        factory.setItemReader(new org.springframework.batch.item.support.ListItemReader(Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,2,3,4,5,6"))));
        Step step = factory.getObject();
        StepExecution stepExecution = getStepExecution(step);
        // Set up expectation of three messages (chunks) in the backlog
        stepExecution.getExecutionContext().putInt(EXPECTED, 6);
        stepExecution.getExecutionContext().putInt(ACTUAL, 3);
        writer.setMaxWaitTimeouts(2);
        /* With no backlog we process all the items, but the listener can't
        reconcile the expected number of items with the actual. An infinite
        loop would be bad, so the best we can do is fail as fast as possible.
         */
        step.execute(stepExecution);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(ExitStatus.FAILED.getExitCode(), stepExecution.getExitStatus().getExitCode());
        String message = stepExecution.getExitStatus().getExitDescription();
        Assert.assertTrue(("Message did not contain 'timed out': " + message), message.toLowerCase().contains("timed out"));
        Assert.assertEquals(0, TestItemWriter.count);
        Assert.assertEquals(0, stepExecution.getReadCount());
    }

    /**
     * This one is flakey - we try to force it to wait until after the step to
     * finish processing just by waiting for long enough.
     */
    @Test
    public void testFailureInStepListener() throws Exception {
        factory.setItemReader(new org.springframework.batch.item.support.ListItemReader(Arrays.asList(StringUtils.commaDelimitedListToStringArray("wait,fail,3,4,5,6"))));
        Step step = factory.getObject();
        StepExecution stepExecution = getStepExecution(step);
        step.execute(stepExecution);
        waitForResults(2, 10);
        // The number of items processed is actually between 1 and 6, because
        // the one that failed might have been processed out of order.
        Assert.assertTrue((1 <= (TestItemWriter.count)));
        Assert.assertTrue((6 >= (TestItemWriter.count)));
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(ExitStatus.FAILED.getExitCode(), stepExecution.getExitStatus().getExitCode());
        String exitDescription = stepExecution.getExitStatus().getExitDescription();
        Assert.assertTrue(("Exit description does not contain exception type name: " + exitDescription), exitDescription.contains(AsynchronousFailureException.class.getName()));
    }
}

