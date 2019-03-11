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
package com.alipay.remoting.rpc.connectionmanage;


import com.alipay.remoting.Connection;
import com.alipay.remoting.RemotingAddressParser;
import com.alipay.remoting.Url;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcAddressParser;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.SimpleClientUserProcessor;
import com.alipay.remoting.rpc.common.SimpleServerUserProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author yueliang
 * @version $Id: ReconnectManagerTest.java, v 0.1 2017-03-16 PM1:03 yueliang Exp $
 */
public class ReconnectManagerTest {
    static Logger logger = LoggerFactory.getLogger(ReconnectManagerTest.class);

    BoltServer server;

    RpcClient client;

    int port = 2014;

    SimpleServerUserProcessor serverUserProcessor = new SimpleServerUserProcessor();

    SimpleClientUserProcessor clientUserProcessor = new SimpleClientUserProcessor();

    CONNECTEventProcessor clientConnectProcessor = new CONNECTEventProcessor();

    CONNECTEventProcessor serverConnectProcessor = new CONNECTEventProcessor();

    DISCONNECTEventProcessor clientDisConnectProcessor = new DISCONNECTEventProcessor();

    DISCONNECTEventProcessor serverDisConnectProcessor = new DISCONNECTEventProcessor();

    /**
     * parser
     */
    private RemotingAddressParser addressParser = new RpcAddressParser();

    @Test
    public void testReconnectionBySysetmSetting() throws RemotingException, InterruptedException {
        doInit(true, false);
        String addr = "127.0.0.1:2014?zone=RZONE&_CONNECTIONNUM=1";
        Url url = addressParser.parse(addr);
        Connection connection = client.getConnection(url, 1000);
        Assert.assertEquals(0, clientDisConnectProcessor.getDisConnectTimes());
        Assert.assertEquals(1, clientConnectProcessor.getConnectTimes());
        connection.close();
        Thread.sleep(2000);
        Assert.assertEquals(1, clientDisConnectProcessor.getDisConnectTimes());
        Assert.assertEquals(2, clientConnectProcessor.getConnectTimes());
    }

    @Test
    public void testReconnectionByUserSetting() throws RemotingException, InterruptedException {
        doInit(false, true);
        client.enableReconnectSwitch();
        String addr = "127.0.0.1:2014?zone=RZONE&_CONNECTIONNUM=1";
        Url url = addressParser.parse(addr);
        Connection connection = client.getConnection(url, 1000);
        Assert.assertEquals(0, clientDisConnectProcessor.getDisConnectTimes());
        Assert.assertEquals(1, clientConnectProcessor.getConnectTimes());
        connection.close();
        Thread.sleep(1000);
        Assert.assertEquals(1, clientDisConnectProcessor.getDisConnectTimes());
        Assert.assertEquals(2, clientConnectProcessor.getConnectTimes());
    }
}

