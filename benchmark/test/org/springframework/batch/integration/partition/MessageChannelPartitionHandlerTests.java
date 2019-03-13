package org.springframework.batch.integration.partition;


import BatchStatus.COMPLETED;
import BatchStatus.STARTED;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.partition.StepExecutionSplitter;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;


/**
 *
 *
 * @author Will Schipp
 * @author Michael Minella
 */
@SuppressWarnings("raw")
public class MessageChannelPartitionHandlerTests {
    private MessageChannelPartitionHandler messageChannelPartitionHandler;

    @Test
    public void testNoPartitions() throws Exception {
        // execute with no default set
        messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        // mock
        StepExecution masterStepExecution = Mockito.mock(StepExecution.class);
        StepExecutionSplitter stepExecutionSplitter = Mockito.mock(StepExecutionSplitter.class);
        // execute
        Collection<StepExecution> executions = messageChannelPartitionHandler.handle(stepExecutionSplitter, masterStepExecution);
        // verify
        Assert.assertTrue(executions.isEmpty());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testHandleNoReply() throws Exception {
        // execute with no default set
        messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        // mock
        StepExecution masterStepExecution = Mockito.mock(StepExecution.class);
        StepExecutionSplitter stepExecutionSplitter = Mockito.mock(StepExecutionSplitter.class);
        MessagingTemplate operations = Mockito.mock(MessagingTemplate.class);
        Message message = Mockito.mock(Message.class);
        // when
        HashSet<StepExecution> stepExecutions = new HashSet<>();
        stepExecutions.add(new StepExecution("step1", new JobExecution(5L)));
        Mockito.when(stepExecutionSplitter.split(ArgumentMatchers.any(StepExecution.class), ArgumentMatchers.eq(1))).thenReturn(stepExecutions);
        Mockito.when(message.getPayload()).thenReturn(Collections.emptyList());
        Mockito.when(operations.receive(((PollableChannel) (ArgumentMatchers.any())))).thenReturn(message);
        // set
        messageChannelPartitionHandler.setMessagingOperations(operations);
        // execute
        Collection<StepExecution> executions = messageChannelPartitionHandler.handle(stepExecutionSplitter, masterStepExecution);
        // verify
        Assert.assertNotNull(executions);
        Assert.assertTrue(executions.isEmpty());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testHandleWithReplyChannel() throws Exception {
        // execute with no default set
        messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        // mock
        StepExecution masterStepExecution = Mockito.mock(StepExecution.class);
        StepExecutionSplitter stepExecutionSplitter = Mockito.mock(StepExecutionSplitter.class);
        MessagingTemplate operations = Mockito.mock(MessagingTemplate.class);
        Message message = Mockito.mock(Message.class);
        PollableChannel replyChannel = Mockito.mock(PollableChannel.class);
        // when
        HashSet<StepExecution> stepExecutions = new HashSet<>();
        stepExecutions.add(new StepExecution("step1", new JobExecution(5L)));
        Mockito.when(stepExecutionSplitter.split(ArgumentMatchers.any(StepExecution.class), ArgumentMatchers.eq(1))).thenReturn(stepExecutions);
        Mockito.when(message.getPayload()).thenReturn(Collections.emptyList());
        Mockito.when(operations.receive(replyChannel)).thenReturn(message);
        // set
        messageChannelPartitionHandler.setMessagingOperations(operations);
        messageChannelPartitionHandler.setReplyChannel(replyChannel);
        // execute
        Collection<StepExecution> executions = messageChannelPartitionHandler.handle(stepExecutionSplitter, masterStepExecution);
        // verify
        Assert.assertNotNull(executions);
        Assert.assertTrue(executions.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test(expected = MessageTimeoutException.class)
    public void messageReceiveTimeout() throws Exception {
        // execute with no default set
        messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        // mock
        StepExecution masterStepExecution = Mockito.mock(StepExecution.class);
        StepExecutionSplitter stepExecutionSplitter = Mockito.mock(StepExecutionSplitter.class);
        MessagingTemplate operations = Mockito.mock(MessagingTemplate.class);
        Message message = Mockito.mock(Message.class);
        // when
        HashSet<StepExecution> stepExecutions = new HashSet<>();
        stepExecutions.add(new StepExecution("step1", new JobExecution(5L)));
        Mockito.when(stepExecutionSplitter.split(ArgumentMatchers.any(StepExecution.class), ArgumentMatchers.eq(1))).thenReturn(stepExecutions);
        Mockito.when(message.getPayload()).thenReturn(Collections.emptyList());
        // set
        messageChannelPartitionHandler.setMessagingOperations(operations);
        // execute
        messageChannelPartitionHandler.handle(stepExecutionSplitter, masterStepExecution);
    }

    @Test
    public void testHandleWithJobRepositoryPolling() throws Exception {
        // execute with no default set
        messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        // mock
        JobExecution jobExecution = new JobExecution(5L, new JobParameters());
        StepExecution masterStepExecution = new StepExecution("step1", jobExecution, 1L);
        StepExecutionSplitter stepExecutionSplitter = Mockito.mock(StepExecutionSplitter.class);
        MessagingTemplate operations = Mockito.mock(MessagingTemplate.class);
        JobExplorer jobExplorer = Mockito.mock(JobExplorer.class);
        // when
        HashSet<StepExecution> stepExecutions = new HashSet<>();
        StepExecution partition1 = new StepExecution("step1:partition1", jobExecution, 2L);
        StepExecution partition2 = new StepExecution("step1:partition2", jobExecution, 3L);
        StepExecution partition3 = new StepExecution("step1:partition3", jobExecution, 4L);
        StepExecution partition4 = new StepExecution("step1:partition3", jobExecution, 4L);
        partition1.setStatus(COMPLETED);
        partition2.setStatus(COMPLETED);
        partition3.setStatus(STARTED);
        partition4.setStatus(COMPLETED);
        stepExecutions.add(partition1);
        stepExecutions.add(partition2);
        stepExecutions.add(partition3);
        Mockito.when(stepExecutionSplitter.split(ArgumentMatchers.any(StepExecution.class), ArgumentMatchers.eq(1))).thenReturn(stepExecutions);
        Mockito.when(jobExplorer.getStepExecution(ArgumentMatchers.eq(5L), ArgumentMatchers.any(Long.class))).thenReturn(partition2, partition1, partition3, partition3, partition3, partition3, partition4);
        // set
        messageChannelPartitionHandler.setMessagingOperations(operations);
        messageChannelPartitionHandler.setJobExplorer(jobExplorer);
        messageChannelPartitionHandler.setStepName("step1");
        messageChannelPartitionHandler.setPollInterval(500L);
        messageChannelPartitionHandler.afterPropertiesSet();
        // execute
        Collection<StepExecution> executions = messageChannelPartitionHandler.handle(stepExecutionSplitter, masterStepExecution);
        // verify
        Assert.assertNotNull(executions);
        Assert.assertEquals(3, executions.size());
        Assert.assertTrue(executions.contains(partition1));
        Assert.assertTrue(executions.contains(partition2));
        Assert.assertTrue(executions.contains(partition4));
        // verify
        Mockito.verify(operations, Mockito.times(3)).send(ArgumentMatchers.any(Message.class));
    }

    @Test(expected = TimeoutException.class)
    public void testHandleWithJobRepositoryPollingTimeout() throws Exception {
        // execute with no default set
        messageChannelPartitionHandler = new MessageChannelPartitionHandler();
        // mock
        JobExecution jobExecution = new JobExecution(5L, new JobParameters());
        StepExecution masterStepExecution = new StepExecution("step1", jobExecution, 1L);
        StepExecutionSplitter stepExecutionSplitter = Mockito.mock(StepExecutionSplitter.class);
        MessagingTemplate operations = Mockito.mock(MessagingTemplate.class);
        JobExplorer jobExplorer = Mockito.mock(JobExplorer.class);
        // when
        HashSet<StepExecution> stepExecutions = new HashSet<>();
        StepExecution partition1 = new StepExecution("step1:partition1", jobExecution, 2L);
        StepExecution partition2 = new StepExecution("step1:partition2", jobExecution, 3L);
        StepExecution partition3 = new StepExecution("step1:partition3", jobExecution, 4L);
        partition1.setStatus(COMPLETED);
        partition2.setStatus(COMPLETED);
        partition3.setStatus(STARTED);
        stepExecutions.add(partition1);
        stepExecutions.add(partition2);
        stepExecutions.add(partition3);
        Mockito.when(stepExecutionSplitter.split(ArgumentMatchers.any(StepExecution.class), ArgumentMatchers.eq(1))).thenReturn(stepExecutions);
        Mockito.when(jobExplorer.getStepExecution(ArgumentMatchers.eq(5L), ArgumentMatchers.any(Long.class))).thenReturn(partition2, partition1, partition3);
        // set
        messageChannelPartitionHandler.setMessagingOperations(operations);
        messageChannelPartitionHandler.setJobExplorer(jobExplorer);
        messageChannelPartitionHandler.setStepName("step1");
        messageChannelPartitionHandler.setTimeout(1000L);
        messageChannelPartitionHandler.afterPropertiesSet();
        // execute
        messageChannelPartitionHandler.handle(stepExecutionSplitter, masterStepExecution);
    }
}

