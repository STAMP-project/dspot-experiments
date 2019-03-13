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
package com.alipay.remoting.rpc.addressargs;


import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.common.BoltServer;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.PortScan;
import com.alipay.remoting.rpc.common.RequestBody;
import com.alipay.remoting.rpc.common.SimpleClientUserProcessor;
import com.alipay.remoting.rpc.common.SimpleServerUserProcessor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alipay.remoting.rpc.common.RequestBody.InvokeType.values;


/**
 * address args test [_CONNECTIONNUM]
 *
 * @author xiaomin.cxm
 * @version $Id: AddressArgs_CONNECTIONNUM_Test.java, v 0.1 Feb 17, 2016 2:01:54 PM xiaomin.cxm Exp $
 */
public class AddressArgs_CONNECTIONNUM_Test {
    static Logger logger = LoggerFactory.getLogger(AddressArgs_CONNECTIONNUM_Test.class);

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

    @Test
    public void test_connNum_10_warmup_True_invoke_1times() {
        String url = (addr) + "?_CONNECTTIMEOUT=1000&_TIMEOUT=5000&_CONNECTIONNUM=10&_CONNECTIONWARMUP=true";
        for (RequestBody.InvokeType type : values()) {
            doResetTimes();
            doTest(url, type, 1, 10, (-1));
        }
    }

    @Test
    public void test_connNum_10_warmup_False_invoke_1times() {
        String url = (addr) + "?_CONNECTTIMEOUT=1000&_TIMEOUT=5000&_CONNECTIONNUM=10&_CONNECTIONWARMUP=false";
        for (RequestBody.InvokeType type : values()) {
            doResetTimes();
            doTest(url, type, 1, 1, (-1));
        }
    }

    @Test
    public void test_connNum_1_warmup_True_invoke_1times() {
        String url = (addr) + "?_CONNECTTIMEOUT=1000&_TIMEOUT=5000&_CONNECTIONNUM=1&_CONNECTIONWARMUP=true";
        for (RequestBody.InvokeType type : values()) {
            doResetTimes();
            doTest(url, type, 1, 1, (-1));
        }
    }

    @Test
    public void test_connNum_1_warmup_False_invoke_3times() {
        String url = (addr) + "?_CONNECTTIMEOUT=1000&_TIMEOUT=5000&_CONNECTIONNUM=1&_CONNECTIONWARMUP=false";
        for (RequestBody.InvokeType type : values()) {
            doResetTimes();
            doTest(url, type, 1, 1, (-1));
        }
    }

    @Test
    public void test_connNum_2_warmup_False_invoke_3times() {
        String url = (addr) + "?_CONNECTTIMEOUT=1000&_TIMEOUT=5000&_CONNECTIONNUM=2&_CONNECTIONWARMUP=false";
        for (RequestBody.InvokeType type : values()) {
            doResetTimes();
            doTest(url, type, 1, 1, (-1));
        }
    }

    @Test
    public void test_connNum_2_warmup_True_invoke_3times() {
        String url = (addr) + "?_CONNECTTIMEOUT=1000&_TIMEOUT=5000&_CONNECTIONNUM=2&_CONNECTIONWARMUP=true";
        for (RequestBody.InvokeType type : values()) {
            doResetTimes();
            doTest(url, type, 1, 2, (-1));
        }
    }
}

