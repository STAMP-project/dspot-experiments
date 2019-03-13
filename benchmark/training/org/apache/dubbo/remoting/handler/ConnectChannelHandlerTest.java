/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.handler;


import java.util.concurrent.ThreadPoolExecutor;
import org.apache.dubbo.remoting.ExecutionException;
import org.apache.dubbo.remoting.RemotingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ConnectChannelHandlerTest extends WrappedChannelHandlerTest {
    @Test
    public void test_Connect_Blocked() throws RemotingException {
        handler = new org.apache.dubbo.remoting.transport.dispatcher.connection.ConnectionOrderedChannelHandler(new WrappedChannelHandlerTest.BizChannelHander(false), url);
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (getField(handler, "connectionExecutor", 1)));
        Assertions.assertEquals(1, executor.getMaximumPoolSize());
        int runs = 20;
        int taskCount = runs * 2;
        for (int i = 0; i < runs; i++) {
            handler.connected(new MockedChannel());
            handler.disconnected(new MockedChannel());
            Assertions.assertTrue(((executor.getActiveCount()) <= 1), ((executor.getActiveCount()) + " must <=1"));
        }
        // queue.size
        Assertions.assertEquals((taskCount - 1), executor.getQueue().size());
        for (int i = 0; i < taskCount; i++) {
            if ((executor.getCompletedTaskCount()) < taskCount) {
                sleep(100);
            }
        }
        Assertions.assertEquals(taskCount, executor.getCompletedTaskCount());
    }

    // biz error should not throw and affect biz thread.
    @Test
    public void test_Connect_Biz_Error() throws RemotingException {
        handler = new org.apache.dubbo.remoting.transport.dispatcher.connection.ConnectionOrderedChannelHandler(new WrappedChannelHandlerTest.BizChannelHander(true), url);
        handler.connected(new MockedChannel());
    }

    // biz error should not throw and affect biz thread.
    @Test
    public void test_Disconnect_Biz_Error() throws RemotingException {
        handler = new org.apache.dubbo.remoting.transport.dispatcher.connection.ConnectionOrderedChannelHandler(new WrappedChannelHandlerTest.BizChannelHander(true), url);
        handler.disconnected(new MockedChannel());
    }

    @Test
    public void test_Connect_Execute_Error() throws RemotingException {
        Assertions.assertThrows(ExecutionException.class, () -> {
            handler = new org.apache.dubbo.remoting.transport.dispatcher.connection.ConnectionOrderedChannelHandler(new WrappedChannelHandlerTest.BizChannelHander(false), url);
            ThreadPoolExecutor executor = ((ThreadPoolExecutor) (getField(handler, "connectionExecutor", 1)));
            executor.shutdown();
            handler.connected(new MockedChannel());
        });
    }

    @Test
    public void test_Disconnect_Execute_Error() throws RemotingException {
        Assertions.assertThrows(ExecutionException.class, () -> {
            handler = new org.apache.dubbo.remoting.transport.dispatcher.connection.ConnectionOrderedChannelHandler(new WrappedChannelHandlerTest.BizChannelHander(false), url);
            ThreadPoolExecutor executor = ((ThreadPoolExecutor) (getField(handler, "connectionExecutor", 1)));
            executor.shutdown();
            handler.disconnected(new MockedChannel());
        });
    }

    // throw  ChannelEventRunnable.runtimeExeception(int logger) not in execute exception
    // (expected = RemotingException.class)
    @Test
    public void test_MessageReceived_Biz_Error() throws RemotingException {
        handler.received(new MockedChannel(), "");
    }

    // throw  ChannelEventRunnable.runtimeExeception(int logger) not in execute exception
    @Test
    public void test_Caught_Biz_Error() throws RemotingException {
        handler.caught(new MockedChannel(), new WrappedChannelHandlerTest.BizException());
    }

    @Test
    public void test_Received_InvokeInExecuter() throws RemotingException {
        Assertions.assertThrows(ExecutionException.class, () -> {
            handler = new org.apache.dubbo.remoting.transport.dispatcher.connection.ConnectionOrderedChannelHandler(new WrappedChannelHandlerTest.BizChannelHander(false), url);
            ThreadPoolExecutor executor = ((ThreadPoolExecutor) (getField(handler, "SHARED_EXECUTOR", 1)));
            executor.shutdown();
            executor = ((ThreadPoolExecutor) (getField(handler, "executor", 1)));
            executor.shutdown();
            handler.received(new MockedChannel(), "");
        });
    }
}

