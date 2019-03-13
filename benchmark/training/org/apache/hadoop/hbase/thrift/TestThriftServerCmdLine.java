/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.thrift;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.thrift.server.TServer;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Start the HBase Thrift server on a random port through the command-line
 * interface and talk to it from client side.
 */
@Category({ ClientTests.class, LargeTests.class })
@RunWith(Parameterized.class)
public class TestThriftServerCmdLine {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestThriftServerCmdLine.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestThriftServerCmdLine.class);

    protected final ImplType implType;

    protected boolean specifyFramed;

    protected boolean specifyBindIP;

    protected boolean specifyCompact;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Thread cmdLineThread;

    private volatile Exception cmdLineException;

    private Exception clientSideException;

    private volatile ThriftServer thriftServer;

    protected int port;

    public TestThriftServerCmdLine(ImplType implType, boolean specifyFramed, boolean specifyBindIP, boolean specifyCompact) {
        this.implType = implType;
        this.specifyFramed = specifyFramed;
        this.specifyBindIP = specifyBindIP;
        this.specifyCompact = specifyCompact;
        TestThriftServerCmdLine.LOG.debug(getParametersString());
    }

    @Test
    public void testRunThriftServer() throws Exception {
        List<String> args = new ArrayList<>();
        if ((implType) != null) {
            String serverTypeOption = implType.toString();
            Assert.assertTrue(serverTypeOption.startsWith("-"));
            args.add(serverTypeOption);
        }
        port = HBaseTestingUtility.randomFreePort();
        args.add(("-" + (Constants.PORT_OPTION)));
        args.add(String.valueOf(port));
        args.add(("-" + (Constants.INFOPORT_OPTION)));
        int infoPort = HBaseTestingUtility.randomFreePort();
        args.add(String.valueOf(infoPort));
        if (specifyFramed) {
            args.add(("-" + (Constants.FRAMED_OPTION)));
        }
        if (specifyBindIP) {
            args.add(("-" + (Constants.BIND_OPTION)));
            args.add(InetAddress.getLocalHost().getHostName());
        }
        if (specifyCompact) {
            args.add(("-" + (Constants.COMPACT_OPTION)));
        }
        args.add("start");
        thriftServer = createThriftServer();
        startCmdLineThread(args.toArray(new String[args.size()]));
        // wait up to 10s for the server to start
        for (int i = 0; (i < 100) && ((thriftServer.tserver) == null); i++) {
            Thread.sleep(100);
        }
        Class<? extends TServer> expectedClass = ((implType) != null) ? implType.serverClass : TBoundedThreadPoolServer.class;
        Assert.assertEquals(expectedClass, thriftServer.tserver.getClass());
        try {
            talkToThriftServer();
        } catch (Exception ex) {
            clientSideException = ex;
        } finally {
            stopCmdLineThread();
        }
        if ((clientSideException) != null) {
            TestThriftServerCmdLine.LOG.error(("Thrift client threw an exception. Parameters:" + (getParametersString())), clientSideException);
            throw new Exception(clientSideException);
        }
    }

    protected static volatile boolean tableCreated = false;
}

