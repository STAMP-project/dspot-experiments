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
package com.alipay.remoting.rpc.heartbeat;


import CommonCommandCode.HEARTBEAT;
import RpcProtocol.PROTOCOL_CODE;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.PortScan;
import com.alipay.remoting.rpc.common.SimpleClientUserProcessor;
import com.alipay.remoting.rpc.common.SimpleServerUserProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Client heart beat test
 *
 * @author xiaomin.cxm
 * @version $Id: ClientHeartBeatTest.java, v 0.1 Apr 12, 2016 11:13:10 AM xiaomin.cxm Exp $
 */
public class ClientHeartBeatTest {
    static Logger logger = LoggerFactory.getLogger(ClientHeartBeatTest.class);

    BoltServer server;

    RpcClient client;

    int port = PortScan.select();

    String ip = "127.0.0.1";

    String addr = "127.0.0.1:" + (port);

    int invokeTimes = 5;

    SimpleServerUserProcessor serverUserProcessor = new SimpleServerUserProcessor();

    SimpleClientUserProcessor clientUserProcessor = new SimpleClientUserProcessor();

    CONNECTEventProcessor clientConnectProcessor = new CONNECTEventProcessor();

    CONNECTEventProcessor serverConnectProcessor = new CONNECTEventProcessor();

    DISCONNECTEventProcessor clientDisConnectProcessor = new DISCONNECTEventProcessor();

    DISCONNECTEventProcessor serverDisConnectProcessor = new DISCONNECTEventProcessor();

    CustomHeartBeatProcessor heartBeatProcessor = new CustomHeartBeatProcessor();

    /**
     * test heartbeat trigger
     */
    @Test
    public void testClientHeartBeatTrigger() throws InterruptedException {
        server.getRpcServer().registerProcessor(PROTOCOL_CODE, HEARTBEAT, heartBeatProcessor);
        try {
            client.createStandaloneConnection(addr, 1000);
        } catch (RemotingException e) {
            ClientHeartBeatTest.logger.error("", e);
        }
        Thread.sleep(500);
        Assert.assertTrue(((heartBeatProcessor.getHeartBeatTimes()) > 1));
        Assert.assertEquals(1, clientConnectProcessor.getConnectTimes());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
    }

    /**
     * test heartbeat no response, close the connection from client side
     */
    @Test
    public void testClientHeartBeatTriggerExceed3Times() throws InterruptedException {
        server.getRpcServer().registerProcessor(PROTOCOL_CODE, HEARTBEAT, heartBeatProcessor);
        try {
            client.createStandaloneConnection(addr, 1000);
        } catch (RemotingException e) {
            ClientHeartBeatTest.logger.error("", e);
        }
        Thread.sleep(3000);
        Assert.assertTrue(((heartBeatProcessor.getHeartBeatTimes()) > 1));
        Assert.assertEquals(1, clientConnectProcessor.getConnectTimes());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(1, clientDisConnectProcessor.getDisConnectTimes());
        Assert.assertEquals(1, serverDisConnectProcessor.getDisConnectTimes());
    }

    /**
     * test basic heartbeat and ack
     */
    @Test
    public void testClientHeartBeatAck() throws InterruptedException {
        try {
            client.createStandaloneConnection(addr, 1000);
        } catch (RemotingException e) {
            ClientHeartBeatTest.logger.error("", e);
        }
        Thread.sleep(1000);
        Assert.assertEquals(1, clientConnectProcessor.getConnectTimes());
        Assert.assertEquals(1, serverConnectProcessor.getConnectTimes());
        Assert.assertEquals(0, clientDisConnectProcessor.getDisConnectTimes());
        Assert.assertEquals(0, serverDisConnectProcessor.getDisConnectTimes());
    }
}

