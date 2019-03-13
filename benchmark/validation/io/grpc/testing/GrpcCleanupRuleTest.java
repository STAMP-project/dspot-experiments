/**
 * Copyright 2018 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.testing;


import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.internal.FakeClock;
import io.grpc.testing.GrpcCleanupRule.Resource;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Unit tests for {@link GrpcCleanupRule}.
 */
@RunWith(JUnit4.class)
public class GrpcCleanupRuleTest {
    public static final FakeClock fakeClock = new FakeClock();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void registerChannelReturnSameChannel() {
        ManagedChannel channel = Mockito.mock(ManagedChannel.class);
        Assert.assertSame(channel, new GrpcCleanupRule().register(channel));
    }

    @Test
    public void registerServerReturnSameServer() {
        Server server = Mockito.mock(Server.class);
        Assert.assertSame(server, new GrpcCleanupRule().register(server));
    }

    @Test
    public void registerNullChannelThrowsNpe() {
        ManagedChannel channel = null;
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("channel");
        grpcCleanup.register(channel);
    }

    @Test
    public void registerNullServerThrowsNpe() {
        Server server = null;
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("server");
        grpcCleanup.register(server);
    }

    @Test
    public void singleChannelCleanup() throws Throwable {
        // setup
        ManagedChannel channel = Mockito.mock(ManagedChannel.class);
        Statement statement = Mockito.mock(Statement.class);
        InOrder inOrder = Mockito.inOrder(statement, channel);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        // run
        grpcCleanup.register(channel);
        boolean awaitTerminationFailed = false;
        try {
            // will throw because channel.awaitTermination(long, TimeUnit) will return false;
            /* description */
            grpcCleanup.apply(statement, null).evaluate();
        } catch (AssertionError e) {
            awaitTerminationFailed = true;
        }
        // verify
        Assert.assertTrue(awaitTerminationFailed);
        inOrder.verify(statement).evaluate();
        inOrder.verify(channel).shutdown();
        inOrder.verify(channel).awaitTermination(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(channel).shutdownNow();
    }

    @Test
    public void singleServerCleanup() throws Throwable {
        // setup
        Server server = Mockito.mock(Server.class);
        Statement statement = Mockito.mock(Statement.class);
        InOrder inOrder = Mockito.inOrder(statement, server);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        // run
        grpcCleanup.register(server);
        boolean awaitTerminationFailed = false;
        try {
            // will throw because channel.awaitTermination(long, TimeUnit) will return false;
            /* description */
            grpcCleanup.apply(statement, null).evaluate();
        } catch (AssertionError e) {
            awaitTerminationFailed = true;
        }
        // verify
        Assert.assertTrue(awaitTerminationFailed);
        inOrder.verify(statement).evaluate();
        inOrder.verify(server).shutdown();
        inOrder.verify(server).awaitTermination(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(server).shutdownNow();
    }

    @Test
    public void multiResource_cleanupGracefully() throws Throwable {
        // setup
        Resource resource1 = Mockito.mock(Resource.class);
        Resource resource2 = Mockito.mock(Resource.class);
        Resource resource3 = Mockito.mock(Resource.class);
        Mockito.doReturn(true).when(resource1).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Mockito.doReturn(true).when(resource2).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Mockito.doReturn(true).when(resource3).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Statement statement = Mockito.mock(Statement.class);
        InOrder inOrder = Mockito.inOrder(statement, resource1, resource2, resource3);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        // run
        grpcCleanup.register(resource1);
        grpcCleanup.register(resource2);
        grpcCleanup.register(resource3);
        /* description */
        grpcCleanup.apply(statement, null).evaluate();
        // Verify.
        inOrder.verify(statement).evaluate();
        inOrder.verify(resource3).cleanUp();
        inOrder.verify(resource2).cleanUp();
        inOrder.verify(resource1).cleanUp();
        inOrder.verify(resource3).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(resource2).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(resource1).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(resource1, Mockito.never()).forceCleanUp();
        Mockito.verify(resource2, Mockito.never()).forceCleanUp();
        Mockito.verify(resource3, Mockito.never()).forceCleanUp();
    }

    @Test
    public void baseTestFails() throws Throwable {
        // setup
        Resource resource = Mockito.mock(Resource.class);
        Statement statement = Mockito.mock(Statement.class);
        Mockito.doThrow(new Exception()).when(statement).evaluate();
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        // run
        grpcCleanup.register(resource);
        boolean baseTestFailed = false;
        try {
            /* description */
            grpcCleanup.apply(statement, null).evaluate();
        } catch (Exception e) {
            baseTestFailed = true;
        }
        // verify
        Assert.assertTrue(baseTestFailed);
        Mockito.verify(resource).forceCleanUp();
        Mockito.verifyNoMoreInteractions(resource);
        Mockito.verify(resource, Mockito.never()).cleanUp();
        Mockito.verify(resource, Mockito.never()).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
    }

    @Test
    public void multiResource_awaitReleasedFails() throws Throwable {
        // setup
        Resource resource1 = Mockito.mock(Resource.class);
        Resource resource2 = Mockito.mock(Resource.class);
        Resource resource3 = Mockito.mock(Resource.class);
        Mockito.doReturn(true).when(resource1).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Mockito.doReturn(false).when(resource2).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Mockito.doReturn(true).when(resource3).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Statement statement = Mockito.mock(Statement.class);
        InOrder inOrder = Mockito.inOrder(statement, resource1, resource2, resource3);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        // run
        grpcCleanup.register(resource1);
        grpcCleanup.register(resource2);
        grpcCleanup.register(resource3);
        boolean cleanupFailed = false;
        try {
            /* description */
            grpcCleanup.apply(statement, null).evaluate();
        } catch (AssertionError e) {
            cleanupFailed = true;
        }
        // verify
        Assert.assertTrue(cleanupFailed);
        inOrder.verify(statement).evaluate();
        inOrder.verify(resource3).cleanUp();
        inOrder.verify(resource2).cleanUp();
        inOrder.verify(resource1).cleanUp();
        inOrder.verify(resource3).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(resource2).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(resource2).forceCleanUp();
        inOrder.verify(resource1).forceCleanUp();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(resource3, Mockito.never()).forceCleanUp();
        Mockito.verify(resource1, Mockito.never()).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
    }

    @Test
    public void multiResource_awaitReleasedInterrupted() throws Throwable {
        // setup
        Resource resource1 = Mockito.mock(Resource.class);
        Resource resource2 = Mockito.mock(Resource.class);
        Resource resource3 = Mockito.mock(Resource.class);
        Mockito.doReturn(true).when(resource1).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Mockito.doThrow(new InterruptedException()).when(resource2).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Mockito.doReturn(true).when(resource3).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Statement statement = Mockito.mock(Statement.class);
        InOrder inOrder = Mockito.inOrder(statement, resource1, resource2, resource3);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        // run
        grpcCleanup.register(resource1);
        grpcCleanup.register(resource2);
        grpcCleanup.register(resource3);
        boolean cleanupFailed = false;
        try {
            /* description */
            grpcCleanup.apply(statement, null).evaluate();
        } catch (InterruptedException e) {
            cleanupFailed = true;
        }
        // verify
        Assert.assertTrue(cleanupFailed);
        Assert.assertTrue(Thread.interrupted());
        inOrder.verify(statement).evaluate();
        inOrder.verify(resource3).cleanUp();
        inOrder.verify(resource2).cleanUp();
        inOrder.verify(resource1).cleanUp();
        inOrder.verify(resource3).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(resource2).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        inOrder.verify(resource2).forceCleanUp();
        inOrder.verify(resource1).forceCleanUp();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(resource3, Mockito.never()).forceCleanUp();
        Mockito.verify(resource1, Mockito.never()).awaitReleased(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
    }

    @Test
    public void multiResource_timeoutCalculation() throws Throwable {
        // setup
        Resource resource1 = Mockito.mock(GrpcCleanupRuleTest.FakeResource.class, AdditionalAnswers.delegatesTo(/* cleanupNanos */
        /* awaitReleaseNanos */
        new GrpcCleanupRuleTest.FakeResource(1, 10)));
        Resource resource2 = Mockito.mock(GrpcCleanupRuleTest.FakeResource.class, AdditionalAnswers.delegatesTo(/* cleanupNanos */
        /* awaitReleaseNanos */
        new GrpcCleanupRuleTest.FakeResource(100, 1000)));
        Statement statement = Mockito.mock(Statement.class);
        InOrder inOrder = Mockito.inOrder(statement, resource1, resource2);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule().setTicker(GrpcCleanupRuleTest.fakeClock.getTicker());
        // run
        grpcCleanup.register(resource1);
        grpcCleanup.register(resource2);
        /* description */
        grpcCleanup.apply(statement, null).evaluate();
        // verify
        inOrder.verify(statement).evaluate();
        inOrder.verify(resource2).cleanUp();
        inOrder.verify(resource1).cleanUp();
        inOrder.verify(resource2).awaitReleased((((TimeUnit.SECONDS.toNanos(10)) - 100) - 1), TimeUnit.NANOSECONDS);
        inOrder.verify(resource1).awaitReleased(((((TimeUnit.SECONDS.toNanos(10)) - 100) - 1) - 1000), TimeUnit.NANOSECONDS);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(resource2, Mockito.never()).forceCleanUp();
        Mockito.verify(resource1, Mockito.never()).forceCleanUp();
    }

    @Test
    public void multiResource_timeoutCalculation_customTimeout() throws Throwable {
        // setup
        Resource resource1 = Mockito.mock(GrpcCleanupRuleTest.FakeResource.class, AdditionalAnswers.delegatesTo(/* cleanupNanos */
        /* awaitReleaseNanos */
        new GrpcCleanupRuleTest.FakeResource(1, 10)));
        Resource resource2 = Mockito.mock(GrpcCleanupRuleTest.FakeResource.class, AdditionalAnswers.delegatesTo(/* cleanupNanos */
        /* awaitReleaseNanos */
        new GrpcCleanupRuleTest.FakeResource(100, 1000)));
        Statement statement = Mockito.mock(Statement.class);
        InOrder inOrder = Mockito.inOrder(statement, resource1, resource2);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule().setTicker(GrpcCleanupRuleTest.fakeClock.getTicker()).setTimeout(3000, TimeUnit.NANOSECONDS);
        // run
        grpcCleanup.register(resource1);
        grpcCleanup.register(resource2);
        /* description */
        grpcCleanup.apply(statement, null).evaluate();
        // verify
        inOrder.verify(statement).evaluate();
        inOrder.verify(resource2).cleanUp();
        inOrder.verify(resource1).cleanUp();
        inOrder.verify(resource2).awaitReleased(((3000 - 100) - 1), TimeUnit.NANOSECONDS);
        inOrder.verify(resource1).awaitReleased((((3000 - 100) - 1) - 1000), TimeUnit.NANOSECONDS);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(resource2, Mockito.never()).forceCleanUp();
        Mockito.verify(resource1, Mockito.never()).forceCleanUp();
    }

    @Test
    public void baseTestFailsThenCleanupFails() throws Throwable {
        // setup
        Exception baseTestFailure = new Exception();
        Statement statement = Mockito.mock(Statement.class);
        Mockito.doThrow(baseTestFailure).when(statement).evaluate();
        Resource resource1 = Mockito.mock(Resource.class);
        Resource resource2 = Mockito.mock(Resource.class);
        Resource resource3 = Mockito.mock(Resource.class);
        Mockito.doThrow(new RuntimeException()).when(resource2).forceCleanUp();
        InOrder inOrder = Mockito.inOrder(statement, resource1, resource2, resource3);
        GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
        // run
        grpcCleanup.register(resource1);
        grpcCleanup.register(resource2);
        grpcCleanup.register(resource3);
        Throwable failure = null;
        try {
            /* description */
            grpcCleanup.apply(statement, null).evaluate();
        } catch (Throwable e) {
            failure = e;
        }
        // verify
        assertThat(failure).isInstanceOf(MultipleFailureException.class);
        Assert.assertSame(baseTestFailure, ((MultipleFailureException) (failure)).getFailures().get(0));
        inOrder.verify(statement).evaluate();
        inOrder.verify(resource3).forceCleanUp();
        inOrder.verify(resource2).forceCleanUp();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(resource1, Mockito.never()).cleanUp();
        Mockito.verify(resource2, Mockito.never()).cleanUp();
        Mockito.verify(resource3, Mockito.never()).cleanUp();
        Mockito.verify(resource1, Mockito.never()).forceCleanUp();
    }

    public static class FakeResource implements Resource {
        private final long cleanupNanos;

        private final long awaitReleaseNanos;

        private FakeResource(long cleanupNanos, long awaitReleaseNanos) {
            this.cleanupNanos = cleanupNanos;
            this.awaitReleaseNanos = awaitReleaseNanos;
        }

        @Override
        public void cleanUp() {
            GrpcCleanupRuleTest.fakeClock.forwardTime(cleanupNanos, TimeUnit.NANOSECONDS);
        }

        @Override
        public void forceCleanUp() {
        }

        @Override
        public boolean awaitReleased(long duration, TimeUnit timeUnit) {
            GrpcCleanupRuleTest.fakeClock.forwardTime(awaitReleaseNanos, TimeUnit.NANOSECONDS);
            return true;
        }
    }
}

