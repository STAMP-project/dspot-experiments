package com.netflix.eureka.cluster;


import Action.Heartbeat;
import ProcessingResult.Congestion;
import ProcessingResult.PermanentError;
import ProcessingResult.Success;
import ProcessingResult.TransientError;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.util.InstanceInfoGenerator;
import com.netflix.eureka.util.batcher.TaskProcessor.ProcessingResult;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static com.netflix.eureka.cluster.TestableInstanceReplicationTask.ProcessingState.Failed;
import static com.netflix.eureka.cluster.TestableInstanceReplicationTask.ProcessingState.Finished;
import static com.netflix.eureka.cluster.TestableInstanceReplicationTask.ProcessingState.Pending;


/**
 *
 *
 * @author Tomasz Bak
 */
public class ReplicationTaskProcessorTest {
    private final TestableHttpReplicationClient replicationClient = new TestableHttpReplicationClient();

    private ReplicationTaskProcessor replicationTaskProcessor;

    @Test
    public void testNonBatchableTaskExecution() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().withAction(Heartbeat).withReplyStatusCode(200).build();
        ProcessingResult status = replicationTaskProcessor.process(task);
        Assert.assertThat(status, CoreMatchers.is(Success));
    }

    @Test
    public void testNonBatchableTaskCongestionFailureHandling() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().withAction(Heartbeat).withReplyStatusCode(503).build();
        ProcessingResult status = replicationTaskProcessor.process(task);
        Assert.assertThat(status, CoreMatchers.is(Congestion));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Pending));
    }

    @Test
    public void testNonBatchableTaskNetworkFailureHandling() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().withAction(Heartbeat).withNetworkFailures(1).build();
        ProcessingResult status = replicationTaskProcessor.process(task);
        Assert.assertThat(status, CoreMatchers.is(TransientError));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Pending));
    }

    @Test
    public void testNonBatchableTaskPermanentFailureHandling() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().withAction(Heartbeat).withReplyStatusCode(406).build();
        ProcessingResult status = replicationTaskProcessor.process(task);
        Assert.assertThat(status, CoreMatchers.is(PermanentError));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Failed));
    }

    @Test
    public void testBatchableTaskListExecution() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().build();
        replicationClient.withBatchReply(200);
        replicationClient.withNetworkStatusCode(200);
        ProcessingResult status = replicationTaskProcessor.process(Collections.<ReplicationTask>singletonList(task));
        Assert.assertThat(status, CoreMatchers.is(Success));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Finished));
    }

    @Test
    public void testBatchableTaskCongestionFailureHandling() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().build();
        replicationClient.withNetworkStatusCode(503);
        ProcessingResult status = replicationTaskProcessor.process(Collections.<ReplicationTask>singletonList(task));
        Assert.assertThat(status, CoreMatchers.is(Congestion));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Pending));
    }

    @Test
    public void testBatchableTaskNetworkReadTimeOutHandling() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().build();
        replicationClient.withReadtimeOut(1);
        ProcessingResult status = replicationTaskProcessor.process(Collections.<ReplicationTask>singletonList(task));
        Assert.assertThat(status, CoreMatchers.is(Congestion));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Pending));
    }

    @Test
    public void testBatchableTaskNetworkFailureHandling() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().build();
        replicationClient.withNetworkError(1);
        ProcessingResult status = replicationTaskProcessor.process(Collections.<ReplicationTask>singletonList(task));
        Assert.assertThat(status, CoreMatchers.is(TransientError));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Pending));
    }

    @Test
    public void testBatchableTaskPermanentFailureHandling() throws Exception {
        TestableInstanceReplicationTask task = TestableInstanceReplicationTask.aReplicationTask().build();
        InstanceInfo instanceInfoFromPeer = InstanceInfoGenerator.takeOne();
        replicationClient.withNetworkStatusCode(200);
        replicationClient.withBatchReply(400);
        replicationClient.withInstanceInfo(instanceInfoFromPeer);
        ProcessingResult status = replicationTaskProcessor.process(Collections.<ReplicationTask>singletonList(task));
        Assert.assertThat(status, CoreMatchers.is(Success));
        Assert.assertThat(task.getProcessingState(), CoreMatchers.is(Failed));
    }
}

