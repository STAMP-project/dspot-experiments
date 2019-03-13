/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.rpc.control;


import BitControl.FinishedReceiver;
import BitControl.InitializeFragments;
import DrillbitEndpoint.State.STARTUP;
import ExecProtos.FragmentHandle;
import GeneralRPCProtos.Ack;
import UserBitShared.QueryId;
import UserBitShared.QueryProfile;
import io.netty.buffer.ByteBuf;
import java.util.concurrent.CountDownLatch;
import org.apache.drill.exec.proto.BitControl;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.exec.proto.ExecProtos;
import org.apache.drill.exec.proto.GeneralRPCProtos;
import org.apache.drill.exec.proto.UserBitShared;
import org.apache.drill.exec.rpc.Acks;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.exec.rpc.RpcOutcomeListener;
import org.apache.drill.exec.work.batch.ControlMessageHandler;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class TestLocalControlConnectionManager {
    private static final DrillbitEndpoint localEndpoint = DrillbitEndpoint.newBuilder().setAddress("10.0.0.1").setControlPort(31011).setState(STARTUP).build();

    private static ControlConnectionConfig mockConfig;

    private static ControlMessageHandler mockHandler;

    private static ControlTunnel controlTunnel;

    private static CountDownLatch latch;

    private static final String NEGATIVE_ACK_MESSAGE = "Negative Ack received";

    private static final RpcOutcomeListener<GeneralRPCProtos.Ack> outcomeListener = new RpcOutcomeListener<GeneralRPCProtos.Ack>() {
        @Override
        public void failed(RpcException ex) {
            throw new IllegalStateException(ex);
        }

        @Override
        public void success(GeneralRPCProtos.Ack value, ByteBuf buffer) {
            if (value.getOk()) {
                TestLocalControlConnectionManager.latch.countDown();
            } else {
                throw new IllegalStateException(TestLocalControlConnectionManager.NEGATIVE_ACK_MESSAGE);
            }
        }

        @Override
        public void interrupted(InterruptedException e) {
            // Do nothing
        }
    };

    @Rule
    public ExpectedException exceptionThrown = ExpectedException.none();

    /**
     * Verify that SendFragmentStatus is handled correctly using ControlTunnel with LocalControlConnectionManager
     */
    @Test
    public void testLocalSendFragmentStatus_Success() throws Exception {
        final UserBitShared.QueryId mockQueryId = QueryId.getDefaultInstance();
        final UserBitShared.QueryProfile mockProfile = QueryProfile.getDefaultInstance();
        Mockito.when(TestLocalControlConnectionManager.mockHandler.requestQueryStatus(mockQueryId)).thenReturn(mockProfile);
        final UserBitShared.QueryProfile returnedProfile = TestLocalControlConnectionManager.controlTunnel.requestQueryProfile(mockQueryId).checkedGet();
        Assert.assertEquals(returnedProfile, mockProfile);
    }

    /**
     * Verify that SendFragmentStatus failure scenario is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testLocalSendFragmentStatus_Failure() throws Exception {
        final UserBitShared.QueryId mockQueryId = QueryId.getDefaultInstance();
        final String exceptionMessage = "Testing failure case";
        exceptionThrown.expect(RpcException.class);
        exceptionThrown.expectMessage(exceptionMessage);
        Mockito.when(TestLocalControlConnectionManager.mockHandler.requestQueryStatus(mockQueryId)).thenThrow(new RpcException(exceptionMessage));
        TestLocalControlConnectionManager.controlTunnel.requestQueryProfile(mockQueryId).checkedGet();
    }

    /**
     * Verify that CancelFragment with positive ack is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testLocalCancelFragment_PositiveAck() throws Exception {
        final ExecProtos.FragmentHandle mockHandle = FragmentHandle.getDefaultInstance();
        TestLocalControlConnectionManager.latch = new CountDownLatch(1);
        final GeneralRPCProtos.Ack mockResponse = Acks.OK;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.cancelFragment(mockHandle)).thenReturn(mockResponse);
        TestLocalControlConnectionManager.controlTunnel.cancelFragment(TestLocalControlConnectionManager.outcomeListener, mockHandle);
        TestLocalControlConnectionManager.latch.await();
    }

    /**
     * Verify that CancelFragment with negative ack is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testLocalCancelFragment_NegativeAck() throws Exception {
        final ExecProtos.FragmentHandle mockHandle = FragmentHandle.getDefaultInstance();
        TestLocalControlConnectionManager.latch = new CountDownLatch(1);
        exceptionThrown.expect(IllegalStateException.class);
        exceptionThrown.expectMessage(TestLocalControlConnectionManager.NEGATIVE_ACK_MESSAGE);
        final GeneralRPCProtos.Ack mockResponse = Acks.FAIL;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.cancelFragment(mockHandle)).thenReturn(mockResponse);
        TestLocalControlConnectionManager.controlTunnel.cancelFragment(TestLocalControlConnectionManager.outcomeListener, mockHandle);
        TestLocalControlConnectionManager.latch.await();
    }

    /**
     * Verify that InitializeFragments with positive ack is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testLocalSendFragments_PositiveAck() throws Exception {
        final BitControl.InitializeFragments mockFragments = InitializeFragments.getDefaultInstance();
        TestLocalControlConnectionManager.latch = new CountDownLatch(1);
        final GeneralRPCProtos.Ack mockResponse = Acks.OK;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.initializeFragment(mockFragments)).thenReturn(mockResponse);
        TestLocalControlConnectionManager.controlTunnel.sendFragments(TestLocalControlConnectionManager.outcomeListener, mockFragments);
        TestLocalControlConnectionManager.latch.await();
    }

    /**
     * Verify that InitializeFragments with negative ack is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testLocalSendFragments_NegativeAck() throws Exception {
        final BitControl.InitializeFragments mockFragments = InitializeFragments.getDefaultInstance();
        TestLocalControlConnectionManager.latch = new CountDownLatch(1);
        exceptionThrown.expect(IllegalStateException.class);
        exceptionThrown.expectMessage(TestLocalControlConnectionManager.NEGATIVE_ACK_MESSAGE);
        final GeneralRPCProtos.Ack mockResponse = Acks.FAIL;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.initializeFragment(mockFragments)).thenReturn(mockResponse);
        TestLocalControlConnectionManager.controlTunnel.sendFragments(TestLocalControlConnectionManager.outcomeListener, mockFragments);
        TestLocalControlConnectionManager.latch.await();
    }

    /**
     * Verify that InitializeFragments failure case is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testLocalSendFragments_Failure() throws Exception {
        final BitControl.InitializeFragments mockFragments = InitializeFragments.getDefaultInstance();
        TestLocalControlConnectionManager.latch = new CountDownLatch(1);
        exceptionThrown.expect(IllegalStateException.class);
        exceptionThrown.expectCause(new TypeSafeMatcher<Throwable>(RpcException.class) {
            @Override
            protected boolean matchesSafely(Throwable throwable) {
                return (throwable != null) && (throwable instanceof RpcException);
            }

            @Override
            public void describeTo(Description description) {
                // Do nothing
            }
        });
        Mockito.when(TestLocalControlConnectionManager.mockHandler.initializeFragment(mockFragments)).thenThrow(new RpcException("Failed to initialize"));
        TestLocalControlConnectionManager.controlTunnel.sendFragments(TestLocalControlConnectionManager.outcomeListener, mockFragments);
        TestLocalControlConnectionManager.latch.await();
    }

    /**
     * Verify that UnpauseFragment is handled correctly using ControlTunnel with LocalControlConnectionManager
     */
    @Test
    public void testUnpauseFragments() throws Exception {
        final ExecProtos.FragmentHandle mockHandle = FragmentHandle.getDefaultInstance();
        TestLocalControlConnectionManager.latch = new CountDownLatch(1);
        final GeneralRPCProtos.Ack mockResponse = Acks.OK;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.resumeFragment(mockHandle)).thenReturn(mockResponse);
        TestLocalControlConnectionManager.controlTunnel.unpauseFragment(TestLocalControlConnectionManager.outcomeListener, mockHandle);
        TestLocalControlConnectionManager.latch.await();
    }

    /**
     * Verify that RequestQueryStatus is handled correctly using ControlTunnel with LocalControlConnectionManager
     */
    @Test
    public void testRequestQueryStatus() throws Exception {
        final UserBitShared.QueryId mockQueryId = QueryId.getDefaultInstance();
        final UserBitShared.QueryProfile mockProfile = QueryProfile.getDefaultInstance();
        Mockito.when(TestLocalControlConnectionManager.mockHandler.requestQueryStatus(mockQueryId)).thenReturn(mockProfile);
        final UserBitShared.QueryProfile returnedProfile = TestLocalControlConnectionManager.controlTunnel.requestQueryProfile(mockQueryId).checkedGet();
        Assert.assertEquals(returnedProfile, mockProfile);
    }

    /**
     * Verify that CancelQuery with positive ack is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testCancelQuery_PositiveAck() throws Exception {
        final UserBitShared.QueryId mockQueryId = QueryId.getDefaultInstance();
        final GeneralRPCProtos.Ack mockResponse = Acks.OK;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.requestQueryCancel(mockQueryId)).thenReturn(mockResponse);
        GeneralRPCProtos.Ack response = TestLocalControlConnectionManager.controlTunnel.requestCancelQuery(mockQueryId).checkedGet();
        Assert.assertEquals(response, mockResponse);
    }

    /**
     * Verify that CancelQuery with negative ack is handled correctly using ControlTunnel with
     * LocalControlConnectionManager
     */
    @Test
    public void testCancelQuery_NegativeAck() throws Exception {
        final UserBitShared.QueryId mockQueryId = QueryId.getDefaultInstance();
        final GeneralRPCProtos.Ack mockResponse = Acks.FAIL;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.requestQueryCancel(mockQueryId)).thenReturn(mockResponse);
        GeneralRPCProtos.Ack response = TestLocalControlConnectionManager.controlTunnel.requestCancelQuery(mockQueryId).checkedGet();
        Assert.assertEquals(response, mockResponse);
    }

    /**
     * Verify that FinishedReceiver is handled correctly using ControlTunnel with LocalControlConnectionManager
     */
    @Test
    public void testInformReceiverFinished_success() throws Exception {
        final BitControl.FinishedReceiver finishedReceiver = FinishedReceiver.getDefaultInstance();
        TestLocalControlConnectionManager.latch = new CountDownLatch(1);
        final GeneralRPCProtos.Ack mockResponse = Acks.OK;
        Mockito.when(TestLocalControlConnectionManager.mockHandler.receivingFragmentFinished(finishedReceiver)).thenReturn(mockResponse);
        TestLocalControlConnectionManager.controlTunnel.informReceiverFinished(TestLocalControlConnectionManager.outcomeListener, finishedReceiver);
        TestLocalControlConnectionManager.latch.await();
    }
}

