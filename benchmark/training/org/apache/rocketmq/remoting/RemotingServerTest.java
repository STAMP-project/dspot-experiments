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
package org.apache.rocketmq.remoting;


import LanguageCode.JAVA;
import java.util.concurrent.CountDownLatch;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.remoting.exception.RemotingTooMuchRequestException;
import org.apache.rocketmq.remoting.netty.ResponseFuture;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.junit.Assert;
import org.junit.Test;


public class RemotingServerTest {
    private static RemotingServer remotingServer;

    private static RemotingClient remotingClient;

    @Test
    public void testInvokeSync() throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setCount(1);
        requestHeader.setMessageTitle("Welcome");
        RemotingCommand request = RemotingCommand.createRequestCommand(0, requestHeader);
        RemotingCommand response = RemotingServerTest.remotingClient.invokeSync("localhost:8888", request, (1000 * 3));
        Assert.assertTrue((response != null));
        assertThat(response.getLanguage()).isEqualTo(JAVA);
        assertThat(response.getExtFields()).hasSize(2);
    }

    @Test
    public void testInvokeOneway() throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, RemotingTooMuchRequestException {
        RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
        request.setRemark("messi");
        RemotingServerTest.remotingClient.invokeOneway("localhost:8888", request, (1000 * 3));
    }

    @Test
    public void testInvokeAsync() throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException, RemotingTooMuchRequestException {
        final CountDownLatch latch = new CountDownLatch(1);
        RemotingCommand request = RemotingCommand.createRequestCommand(0, null);
        request.setRemark("messi");
        RemotingServerTest.remotingClient.invokeAsync("localhost:8888", request, (1000 * 3), new InvokeCallback() {
            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                latch.countDown();
                Assert.assertTrue((responseFuture != null));
                assertThat(responseFuture.getResponseCommand().getLanguage()).isEqualTo(JAVA);
                assertThat(responseFuture.getResponseCommand().getExtFields()).hasSize(2);
            }
        });
        latch.await();
    }
}

