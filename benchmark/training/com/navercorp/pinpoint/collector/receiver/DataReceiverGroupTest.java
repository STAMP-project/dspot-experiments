/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.receiver;


import com.navercorp.pinpoint.collector.config.DataReceiverGroupConfiguration;
import com.navercorp.pinpoint.collector.receiver.thrift.TCPReceiverBean;
import com.navercorp.pinpoint.collector.receiver.thrift.UDPReceiverBean;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.profiler.sender.DataSender;
import com.navercorp.pinpoint.profiler.sender.TcpDataSender;
import com.navercorp.pinpoint.rpc.client.PinpointClientFactory;
import com.navercorp.pinpoint.thrift.dto.TResult;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Taejin Koo
 */
public class DataReceiverGroupTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataReceiverGroupTest.class);

    @Test
    public void receiverGroupTest1() throws Exception {
        DataReceiverGroupConfiguration mockConfig = createMockConfig(true, true);
        DataReceiverGroupTest.TestDispatchHandler dispatchHandler = new DataReceiverGroupTest.TestDispatchHandler(2, 1);
        UDPReceiverBean udpReceiverBean = createUdpReceiverBean(mockConfig, dispatchHandler);
        TCPReceiverBean tcpReceiverBean = createTcpReceiverBean(mockConfig, dispatchHandler);
        DataSender udpDataSender = null;
        TcpDataSender tcpDataSender = null;
        PinpointClientFactory pinpointClientFactory = null;
        try {
            udpReceiverBean.afterPropertiesSet();
            tcpReceiverBean.afterPropertiesSet();
            udpDataSender = newUdpDataSender(mockConfig);
            pinpointClientFactory = createPinpointClientFactory();
            tcpDataSender = new TcpDataSender(this.getClass().getName(), "127.0.0.1", mockConfig.getTcpBindPort(), pinpointClientFactory);
            udpDataSender.send(new TResult());
            tcpDataSender.send(new TResult());
            tcpDataSender.request(new TResult());
            Assert.assertTrue(tcpDataSender.isConnected());
            Assert.assertTrue(dispatchHandler.getSendLatch().await(1000, TimeUnit.MILLISECONDS));
            Assert.assertTrue(dispatchHandler.getRequestLatch().await(1000, TimeUnit.MILLISECONDS));
        } finally {
            closeDataSender(udpDataSender);
            closeDataSender(tcpDataSender);
            closeClientFactory(pinpointClientFactory);
            closeBean(udpReceiverBean);
            closeBean(tcpReceiverBean);
        }
    }

    @Test
    public void receiverGroupTest2() throws Exception {
        DataReceiverGroupConfiguration mockConfig = createMockConfig(true, false);
        DataReceiverGroupTest.TestDispatchHandler testDispatchHandler = new DataReceiverGroupTest.TestDispatchHandler(1, 1);
        TCPReceiverBean receiver = createTcpReceiverBean(mockConfig, testDispatchHandler);
        DataSender udpDataSender = null;
        TcpDataSender tcpDataSender = null;
        PinpointClientFactory pinpointClientFactory = null;
        try {
            receiver.afterPropertiesSet();
            udpDataSender = newUdpDataSender(mockConfig);
            udpDataSender.send(new TResult());
            Assert.assertFalse(testDispatchHandler.getSendLatch().await(1000, TimeUnit.MILLISECONDS));
            pinpointClientFactory = createPinpointClientFactory();
            tcpDataSender = new TcpDataSender(this.getClass().getName(), "127.0.0.1", mockConfig.getTcpBindPort(), pinpointClientFactory);
            Assert.assertTrue(tcpDataSender.isConnected());
            tcpDataSender.send(new TResult());
            tcpDataSender.request(new TResult());
            Assert.assertTrue(testDispatchHandler.getSendLatch().await(1000, TimeUnit.MILLISECONDS));
            Assert.assertTrue(testDispatchHandler.getRequestLatch().await(1000, TimeUnit.MILLISECONDS));
        } finally {
            closeDataSender(udpDataSender);
            closeDataSender(tcpDataSender);
            closeClientFactory(pinpointClientFactory);
            closeBean(receiver);
        }
    }

    @Test
    public void receiverGroupTest3() throws Exception {
        DataReceiverGroupConfiguration mockConfig = createMockConfig(false, true);
        DataReceiverGroupTest.TestDispatchHandler testDispatchHandler = new DataReceiverGroupTest.TestDispatchHandler(1, 1);
        UDPReceiverBean receiver = createUdpReceiverBean(mockConfig, testDispatchHandler);
        DataSender udpDataSender = null;
        TcpDataSender tcpDataSender = null;
        PinpointClientFactory pinpointClientFactory = null;
        try {
            receiver.afterPropertiesSet();
            udpDataSender = newUdpDataSender(mockConfig);
            udpDataSender.send(new TResult());
            Assert.assertTrue(testDispatchHandler.getSendLatch().await(1000, TimeUnit.MILLISECONDS));
            pinpointClientFactory = createPinpointClientFactory();
            tcpDataSender = new TcpDataSender(this.getClass().getName(), "127.0.0.1", mockConfig.getTcpBindPort(), pinpointClientFactory);
            Assert.assertFalse(tcpDataSender.isConnected());
        } finally {
            closeDataSender(udpDataSender);
            closeDataSender(tcpDataSender);
            closeClientFactory(pinpointClientFactory);
            closeBean(receiver);
        }
    }

    private static class TestDispatchHandler implements DispatchHandler {
        private final CountDownLatch sendLatch;

        private final CountDownLatch requestLatch;

        public TestDispatchHandler(int sendLatchCount, int requestLatchCount) {
            this.sendLatch = new CountDownLatch(sendLatchCount);
            this.requestLatch = new CountDownLatch(requestLatchCount);
        }

        public CountDownLatch getSendLatch() {
            return sendLatch;
        }

        public CountDownLatch getRequestLatch() {
            return requestLatch;
        }

        @Override
        public void dispatchSendMessage(ServerRequest serverRequest) {
            DataReceiverGroupTest.LOGGER.debug("===================================== send {}", serverRequest);
            sendLatch.countDown();
        }

        @Override
        public void dispatchRequestMessage(ServerRequest serverRequest, ServerResponse serverResponse) {
            DataReceiverGroupTest.LOGGER.debug("===================================== request {}", serverRequest);
            requestLatch.countDown();
            Object tResult = new TResult();
            serverResponse.write(tResult);
        }
    }
}

