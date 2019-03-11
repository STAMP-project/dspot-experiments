package com.netflix.conductor.core.events.sqs;


import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.netflix.conductor.contribs.queue.sqs.SQSObservableQueue;
import com.netflix.conductor.core.config.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestSQSEventQueueProvider {
    private AmazonSQSClient amazonSQSClient;

    private Configuration configuration;

    @Test
    public void testGetQueueWithDefaultConfiguration() {
        Mockito.when(configuration.getIntProperty(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenAnswer(( invocation) -> invocation.getArguments()[1]);
        ListQueuesResult listQueuesResult = new ListQueuesResult().withQueueUrls("test_queue_1");
        Mockito.when(amazonSQSClient.listQueues(ArgumentMatchers.any(ListQueuesRequest.class))).thenReturn(listQueuesResult);
        SQSEventQueueProvider sqsEventQueueProvider = new SQSEventQueueProvider(amazonSQSClient, configuration);
        SQSObservableQueue sqsObservableQueue = ((SQSObservableQueue) (sqsEventQueueProvider.getQueue("test_queue_1")));
        Assert.assertNotNull(sqsObservableQueue);
        Assert.assertEquals(1, sqsObservableQueue.getBatchSize());
        Assert.assertEquals(100, sqsObservableQueue.getPollTimeInMS());
        Assert.assertEquals(60, sqsObservableQueue.getVisibilityTimeoutInSeconds());
    }

    @Test
    public void testGetQueueWithCustomConfiguration() {
        Mockito.when(configuration.getIntProperty(ArgumentMatchers.eq("workflow.event.queues.sqs.batchSize"), ArgumentMatchers.anyInt())).thenReturn(10);
        Mockito.when(configuration.getIntProperty(ArgumentMatchers.eq("workflow.event.queues.sqs.pollTimeInMS"), ArgumentMatchers.anyInt())).thenReturn(50);
        Mockito.when(configuration.getIntProperty(ArgumentMatchers.eq("workflow.event.queues.sqs.visibilityTimeoutInSeconds"), ArgumentMatchers.anyInt())).thenReturn(30);
        ListQueuesResult listQueuesResult = new ListQueuesResult().withQueueUrls("test_queue_1");
        Mockito.when(amazonSQSClient.listQueues(ArgumentMatchers.any(ListQueuesRequest.class))).thenReturn(listQueuesResult);
        SQSEventQueueProvider sqsEventQueueProvider = new SQSEventQueueProvider(amazonSQSClient, configuration);
        SQSObservableQueue sqsObservableQueue = ((SQSObservableQueue) (sqsEventQueueProvider.getQueue("test_queue_1")));
        Assert.assertNotNull(sqsObservableQueue);
        Assert.assertEquals(10, sqsObservableQueue.getBatchSize());
        Assert.assertEquals(50, sqsObservableQueue.getPollTimeInMS());
        Assert.assertEquals(30, sqsObservableQueue.getVisibilityTimeoutInSeconds());
    }
}

