/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime;


import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.apache.kafka.connect.util.ThreadedTest;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;


@RunWith(PowerMockRunner.class)
public class SourceTaskOffsetCommitterTest extends ThreadedTest {
    private final ConcurrentHashMap<ConnectorTaskId, ScheduledFuture<?>> committers = new ConcurrentHashMap<>();

    @Mock
    private ScheduledExecutorService executor;

    @Mock
    private Logger mockLog;

    @Mock
    private ScheduledFuture<?> commitFuture;

    @Mock
    private ScheduledFuture<?> taskFuture;

    @Mock
    private ConnectorTaskId taskId;

    @Mock
    private WorkerSourceTask task;

    private SourceTaskOffsetCommitter committer;

    private static final long DEFAULT_OFFSET_COMMIT_INTERVAL_MS = 1000;

    @SuppressWarnings("unchecked")
    @Test
    public void testSchedule() {
        Capture<Runnable> taskWrapper = EasyMock.newCapture();
        EasyMock.expect(executor.scheduleWithFixedDelay(EasyMock.capture(taskWrapper), eq(SourceTaskOffsetCommitterTest.DEFAULT_OFFSET_COMMIT_INTERVAL_MS), eq(SourceTaskOffsetCommitterTest.DEFAULT_OFFSET_COMMIT_INTERVAL_MS), eq(TimeUnit.MILLISECONDS))).andReturn(((ScheduledFuture) (commitFuture)));
        PowerMock.replayAll();
        committer.schedule(taskId, task);
        Assert.assertTrue(taskWrapper.hasCaptured());
        Assert.assertNotNull(taskWrapper.getValue());
        Assert.assertEquals(Collections.singletonMap(taskId, commitFuture), committers);
        PowerMock.verifyAll();
    }

    @Test
    public void testClose() throws Exception {
        long timeoutMs = 1000;
        // Normal termination, where termination times out.
        executor.shutdown();
        PowerMock.expectLastCall();
        EasyMock.expect(executor.awaitTermination(eq(timeoutMs), eq(TimeUnit.MILLISECONDS))).andReturn(false);
        mockLog.error(EasyMock.anyString());
        PowerMock.expectLastCall();
        PowerMock.replayAll();
        committer.close(timeoutMs);
        PowerMock.verifyAll();
        PowerMock.resetAll();
        // Termination interrupted
        executor.shutdown();
        PowerMock.expectLastCall();
        EasyMock.expect(executor.awaitTermination(eq(timeoutMs), eq(TimeUnit.MILLISECONDS))).andThrow(new InterruptedException());
        PowerMock.replayAll();
        committer.close(timeoutMs);
        PowerMock.verifyAll();
    }

    @Test
    public void testRemove() throws Exception {
        // Try to remove a non-existing task
        PowerMock.replayAll();
        Assert.assertTrue(committers.isEmpty());
        committer.remove(taskId);
        Assert.assertTrue(committers.isEmpty());
        PowerMock.verifyAll();
        PowerMock.resetAll();
        // Try to remove an existing task
        EasyMock.expect(taskFuture.cancel(eq(false))).andReturn(false);
        EasyMock.expect(taskFuture.isDone()).andReturn(false);
        EasyMock.expect(taskFuture.get()).andReturn(null);
        PowerMock.replayAll();
        committers.put(taskId, taskFuture);
        committer.remove(taskId);
        Assert.assertTrue(committers.isEmpty());
        PowerMock.verifyAll();
        PowerMock.resetAll();
        // Try to remove a cancelled task
        EasyMock.expect(taskFuture.cancel(eq(false))).andReturn(false);
        EasyMock.expect(taskFuture.isDone()).andReturn(false);
        EasyMock.expect(taskFuture.get()).andThrow(new CancellationException());
        mockLog.trace(EasyMock.anyString(), EasyMock.<Object>anyObject());
        PowerMock.expectLastCall();
        PowerMock.replayAll();
        committers.put(taskId, taskFuture);
        committer.remove(taskId);
        Assert.assertTrue(committers.isEmpty());
        PowerMock.verifyAll();
        PowerMock.resetAll();
        // Try to remove an interrupted task
        EasyMock.expect(taskFuture.cancel(eq(false))).andReturn(false);
        EasyMock.expect(taskFuture.isDone()).andReturn(false);
        EasyMock.expect(taskFuture.get()).andThrow(new InterruptedException());
        PowerMock.replayAll();
        try {
            committers.put(taskId, taskFuture);
            committer.remove(taskId);
            Assert.fail("Expected ConnectException to be raised");
        } catch (ConnectException e) {
            // ignore
        }
        PowerMock.verifyAll();
    }
}

