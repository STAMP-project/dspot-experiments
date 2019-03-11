/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.network;


import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.RemoveInfo;
import org.apache.activemq.command.Response;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportListener;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


public class NetworkRouteTest {
    private IMocksControl control;

    private BrokerService brokerService;

    private Transport localBroker;

    private Transport remoteBroker;

    private TransportListener localListener;

    private TransportListener remoteListener;

    private MessageDispatch msgDispatch;

    private ActiveMQMessage path1Msg;

    private ActiveMQMessage path2Msg;

    private ActiveMQMessage removePath1Msg;

    private ActiveMQMessage removePath2Msg;

    // this sort of mockery is very brittle but it is fast!
    @Test
    public void verifyNoRemoveOnOneConduitRemove() throws Exception {
        localBroker.oneway(EasyMock.isA(ConsumerInfo.class));
        control.replay();
        remoteListener.onCommand(path2Msg);
        remoteListener.onCommand(path1Msg);
        remoteListener.onCommand(removePath2Msg);
        control.verify();
    }

    @Test
    public void addAndRemoveOppositeOrder() throws Exception {
        // from (1)
        localBroker.oneway(EasyMock.isA(ConsumerInfo.class));
        NetworkRouteTest.ArgHolder localConsumer = NetworkRouteTest.ArgHolder.holdArgsForLastObjectCall();
        // from (2a)
        remoteBroker.asyncRequest(EasyMock.isA(ActiveMQMessage.class), EasyMock.isA(ResponseCallback.class));
        NetworkRouteTest.ArgHolder firstMessageFuture = NetworkRouteTest.ArgHolder.holdArgsForLastFutureRequestCall();
        localBroker.oneway(EasyMock.isA(MessageAck.class));
        // from (2b)
        remoteBroker.asyncRequest(EasyMock.isA(ActiveMQMessage.class), EasyMock.isA(ResponseCallback.class));
        NetworkRouteTest.ArgHolder secondMessageFuture = NetworkRouteTest.ArgHolder.holdArgsForLastFutureRequestCall();
        localBroker.oneway(EasyMock.isA(MessageAck.class));
        // from (3)
        localBroker.oneway(EasyMock.isA(RemoveInfo.class));
        NetworkRouteTest.ExpectationWaiter waitForRemove = NetworkRouteTest.ExpectationWaiter.waiterForLastVoidCall();
        control.replay();
        // (1) send advisory of path 1
        remoteListener.onCommand(path1Msg);
        msgDispatch.setConsumerId(getConsumerId());
        // send advisory of path 2, doesn't send a ConsumerInfo to localBroker
        remoteListener.onCommand(path2Msg);
        // (2a) send a message
        localListener.onCommand(msgDispatch);
        ResponseCallback callback = ((ResponseCallback) (firstMessageFuture.arguments[1]));
        FutureResponse response = new FutureResponse(callback);
        response.set(new Response());
        // send advisory of path 2 remove, doesn't send a RemoveInfo to localBroker
        remoteListener.onCommand(removePath2Msg);
        // (2b) send a message
        localListener.onCommand(msgDispatch);
        callback = ((ResponseCallback) (secondMessageFuture.arguments[1]));
        response = new FutureResponse(callback);
        response.set(new Response());
        // (3) send advisory of path 1 remove, sends a RemoveInfo to localBroker
        remoteListener.onCommand(removePath1Msg);
        waitForRemove.assertHappens(5, TimeUnit.SECONDS);
        // send a message, does not send message as in 2a and 2b
        localListener.onCommand(msgDispatch);
        control.verify();
    }

    @Test
    public void addAndRemoveSameOrder() throws Exception {
        // from (1)
        localBroker.oneway(EasyMock.isA(ConsumerInfo.class));
        NetworkRouteTest.ArgHolder localConsumer = NetworkRouteTest.ArgHolder.holdArgsForLastObjectCall();
        // from (2a)
        remoteBroker.asyncRequest(EasyMock.isA(ActiveMQMessage.class), EasyMock.isA(ResponseCallback.class));
        NetworkRouteTest.ArgHolder firstMessageFuture = NetworkRouteTest.ArgHolder.holdArgsForLastFutureRequestCall();
        localBroker.oneway(EasyMock.isA(MessageAck.class));
        // from (2b)
        remoteBroker.asyncRequest(EasyMock.isA(ActiveMQMessage.class), EasyMock.isA(ResponseCallback.class));
        NetworkRouteTest.ArgHolder secondMessageFuture = NetworkRouteTest.ArgHolder.holdArgsForLastFutureRequestCall();
        localBroker.oneway(EasyMock.isA(MessageAck.class));
        // from (3)
        localBroker.oneway(EasyMock.isA(RemoveInfo.class));
        NetworkRouteTest.ExpectationWaiter waitForRemove = NetworkRouteTest.ExpectationWaiter.waiterForLastVoidCall();
        control.replay();
        // (1) send advisory of path 1
        remoteListener.onCommand(path1Msg);
        msgDispatch.setConsumerId(getConsumerId());
        // send advisory of path 2, doesn't send a ConsumerInfo to localBroker
        remoteListener.onCommand(path2Msg);
        // (2a) send a message
        localListener.onCommand(msgDispatch);
        ResponseCallback callback = ((ResponseCallback) (firstMessageFuture.arguments[1]));
        FutureResponse response = new FutureResponse(callback);
        response.set(new Response());
        // send advisory of path 1 remove, shouldn't send a RemoveInfo to localBroker
        remoteListener.onCommand(removePath1Msg);
        // (2b) send a message, should send the message as in 2a
        localListener.onCommand(msgDispatch);
        callback = ((ResponseCallback) (secondMessageFuture.arguments[1]));
        response = new FutureResponse(callback);
        response.set(new Response());
        // (3) send advisory of path 1 remove, should send a RemoveInfo to localBroker
        remoteListener.onCommand(removePath2Msg);
        waitForRemove.assertHappens(5, TimeUnit.SECONDS);
        // send a message, does not send message as in 2a
        localListener.onCommand(msgDispatch);
        control.verify();
    }

    private static class ArgHolder {
        public Object[] arguments;

        public static NetworkRouteTest.ArgHolder holdArgsForLastVoidCall() {
            final NetworkRouteTest.ArgHolder holder = new NetworkRouteTest.ArgHolder();
            EasyMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Object>() {
                @Override
                public Object answer() throws Throwable {
                    Object[] args = EasyMock.getCurrentArguments();
                    holder.arguments = Arrays.copyOf(args, args.length);
                    return null;
                }
            });
            return holder;
        }

        public static NetworkRouteTest.ArgHolder holdArgsForLastObjectCall() {
            final NetworkRouteTest.ArgHolder holder = new NetworkRouteTest.ArgHolder();
            EasyMock.expect(new Object()).andAnswer(new org.easymock.IAnswer<Object>() {
                @Override
                public Object answer() throws Throwable {
                    Object[] args = EasyMock.getCurrentArguments();
                    holder.arguments = Arrays.copyOf(args, args.length);
                    return null;
                }
            });
            return holder;
        }

        public static NetworkRouteTest.ArgHolder holdArgsForLastFutureRequestCall() {
            final NetworkRouteTest.ArgHolder holder = new NetworkRouteTest.ArgHolder();
            EasyMock.expect(new FutureResponse(null)).andAnswer(new org.easymock.IAnswer<FutureResponse>() {
                @Override
                public FutureResponse answer() throws Throwable {
                    Object[] args = EasyMock.getCurrentArguments();
                    holder.arguments = Arrays.copyOf(args, args.length);
                    return null;
                }
            });
            return holder;
        }

        public Object[] getArguments() {
            Assert.assertNotNull(arguments);
            return arguments;
        }
    }

    private static class ExpectationWaiter {
        private CountDownLatch latch = new CountDownLatch(1);

        public static NetworkRouteTest.ExpectationWaiter waiterForLastVoidCall() {
            final NetworkRouteTest.ExpectationWaiter waiter = new NetworkRouteTest.ExpectationWaiter();
            EasyMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Object>() {
                @Override
                public Object answer() throws Throwable {
                    waiter.latch.countDown();
                    return null;
                }
            });
            return waiter;
        }

        public void assertHappens(long timeout, TimeUnit unit) throws InterruptedException {
            Assert.assertTrue(latch.await(timeout, unit));
        }
    }
}

