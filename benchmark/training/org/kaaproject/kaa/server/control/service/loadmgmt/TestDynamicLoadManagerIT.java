/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.control.service.loadmgmt;


import KaaThriftService.BOOTSTRAP_SERVICE;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.thrift.gen.bootstrap.BootstrapThriftService;
import org.kaaproject.kaa.server.common.zk.control.ControlNode;
import org.kaaproject.kaa.server.common.zk.gen.BootstrapNodeInfo;
import org.kaaproject.kaa.server.common.zk.gen.ConnectionInfo;
import org.kaaproject.kaa.server.common.zk.gen.OperationsNodeInfo;
import org.kaaproject.kaa.server.control.service.zk.ControlZkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DynamicLoadManager Integration test, emulates new Bootstrap node adding.
 *
 * @author Andrey Panasenko
 */
public class TestDynamicLoadManagerIT {
    private static final int DEFAULT_PRIORITY = 10;

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TestDynamicLoadManagerIT.class);

    /**
     * Thrift host for Bootstrap test service
     */
    private static final String thriftHost = "localhost";

    /**
     * Thrift port for Bootstrap test service
     */
    private static final int thriftPort = 9819;

    /**
     * Thread executor
     */
    private static ExecutorService executor = null;

    private static LoadDistributionService ldServiceMock;

    private static ControlZkService zkServiceMock;

    private static ControlNode pNodeMock;

    /**
     * Bootstrap thrift test service runner
     */
    private TestDynamicLoadManagerIT.ThriftRunner bootstrapThrift;

    /**
     * Test Bootstrap node add.
     */
    @Test
    public void bootstrapNodeAddTest() {
        TestDynamicLoadManagerIT.LOG.info("bootstrapNodeAddTest started");
        DynamicLoadManager dm = getDynamicLoadManager();
        ConnectionInfo bsConnectionInfo = new ConnectionInfo(TestDynamicLoadManagerIT.thriftHost, TestDynamicLoadManagerIT.thriftPort, ByteBuffer.wrap("Just array".getBytes()));
        BootstrapNodeInfo bsNode = getBootstrapNodeInfo(bsConnectionInfo);
        dm.onNodeAdded(bsNode);
        checkBSNode();
    }

    /**
     * Test Bootstrap Node update
     */
    @Test
    public void bootstrapNodeUpdateTest() {
        TestDynamicLoadManagerIT.LOG.info("BootstrapNodeUpdateTest started");
        DynamicLoadManager dm = getDynamicLoadManager();
        ConnectionInfo bsErrConnectionInfo = new ConnectionInfo(TestDynamicLoadManagerIT.thriftHost, ((TestDynamicLoadManagerIT.thriftPort) + 1), ByteBuffer.wrap("Just array".getBytes()));
        BootstrapNodeInfo bsErrNode = getBootstrapNodeInfo(bsErrConnectionInfo);
        dm.onNodeAdded(bsErrNode);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Assert.fail(e.toString());
        }
        ConnectionInfo bsConnectionInfo = new ConnectionInfo(TestDynamicLoadManagerIT.thriftHost, TestDynamicLoadManagerIT.thriftPort, ByteBuffer.wrap("Just array".getBytes()));
        BootstrapNodeInfo bsNode = getBootstrapNodeInfo(bsConnectionInfo);
        dm.onNodeUpdated(bsNode);
        dm.recalculate();
        checkBSNode();
    }

    /**
     * Test Bootstrap Node remove
     */
    @Test
    public void bootstrapNodeDeleteTest() {
        TestDynamicLoadManagerIT.LOG.info("BootstrapNodeUpdateTest started");
        DynamicLoadManager dm = getDynamicLoadManager();
        ConnectionInfo bsErrConnectionInfo = new ConnectionInfo(TestDynamicLoadManagerIT.thriftHost, ((TestDynamicLoadManagerIT.thriftPort) + 1), ByteBuffer.wrap("Just array".getBytes()));
        BootstrapNodeInfo bsErrNode = getBootstrapNodeInfo(bsErrConnectionInfo);
        dm.onNodeAdded(bsErrNode);
        ConnectionInfo bsConnectionInfo = new ConnectionInfo(TestDynamicLoadManagerIT.thriftHost, TestDynamicLoadManagerIT.thriftPort, ByteBuffer.wrap("Just array".getBytes()));
        BootstrapNodeInfo bsNode = getBootstrapNodeInfo(bsConnectionInfo);
        dm.onNodeAdded(bsNode);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Assert.fail(e.toString());
        }
        dm.onNodeRemoved(bsErrNode);
        dm.recalculate();
        checkBSNode();
    }

    /**
     * Test Operations Node Update Update with two phases, one with same
     * ConnectionInfo DNS Name, second with changed ConnectionInfo DNS Name
     */
    @Test
    public void operationsNodeUpdateTest() {
        TestDynamicLoadManagerIT.LOG.info("BootstrapNodeUpdateTest started");
        DynamicLoadManager dm = getDynamicLoadManager();
        OperationsNodeInfo nodeInfo = generateOperationsNodeInfo("localhost", 1200, 9898, ByteBuffer.wrap("Just array modified".getBytes()), 1);
        dm.onNodeUpdated(nodeInfo);
        TestDynamicLoadManagerIT.LOG.info("BootstrapNodeTest Operations Node {} updated.", nodeInfo.toString());
        OperationsNodeInfo nodeInfo2 = generateOperationsNodeInfo("localhost", 1201, 9899, ByteBuffer.wrap("Just array modified".getBytes()), 1);
        dm.onNodeUpdated(nodeInfo2);
        TestDynamicLoadManagerIT.LOG.info("BootstrapNodeTest Operations Node {} updated.", nodeInfo.toString());
        ConnectionInfo bsConnectionInfo = new ConnectionInfo(TestDynamicLoadManagerIT.thriftHost, TestDynamicLoadManagerIT.thriftPort, ByteBuffer.wrap("Just array".getBytes()));
        BootstrapNodeInfo bsNode = getBootstrapNodeInfo(bsConnectionInfo);
        dm.onNodeAdded(bsNode);
        Assert.assertNotNull(bootstrapThrift.getBootstrapThriftServiceImpl());
        Assert.assertNotNull(bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap());
        Assert.assertEquals(2, bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap().size());
        Assert.assertNotNull(bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap().get("localhost:1200"));
        Assert.assertNotNull(bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap().get("localhost:1201"));
        Assert.assertEquals(((long) (10)), ((long) (bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap().get("localhost:1200").getPriority())));
        Assert.assertEquals(((long) (10)), ((long) (bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap().get("localhost:1201").getPriority())));
    }

    /**
     * Test Operations Node Remove
     */
    @Test
    public void operationsNodeRemoveTest() {
        TestDynamicLoadManagerIT.LOG.info("BootstrapNodeRemoveTest started");
        bootstrapThrift.getBootstrapThriftServiceImpl().reset();
        DynamicLoadManager dm = getDynamicLoadManager();
        ConnectionInfo bsConnectionInfo = new ConnectionInfo(TestDynamicLoadManagerIT.thriftHost, TestDynamicLoadManagerIT.thriftPort, ByteBuffer.wrap("Just array".getBytes()));
        BootstrapNodeInfo bsNode = getBootstrapNodeInfo(bsConnectionInfo);
        dm.onNodeAdded(bsNode);
        checkBSNode();
        OperationsNodeInfo nodeInfo = generateOperationsNodeInfo("localhost", 1201, 9899, ByteBuffer.wrap("Just".getBytes()), 1);
        bootstrapThrift.getBootstrapThriftServiceImpl().reset();
        dm.onNodeAdded(nodeInfo);
        Assert.assertNotNull(bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap());
        Assert.assertEquals(2, bootstrapThrift.getBootstrapThriftServiceImpl().getOperatonsServerMap().size());
        bootstrapThrift.getBootstrapThriftServiceImpl().reset();
        dm.onNodeRemoved(nodeInfo);
        checkBSNode();
    }

    /**
     * ThriftRunner Class. Used to run thrift servers.
     */
    public class ThriftRunner implements Runnable {
        private final String thriftHost;

        private final int thriftPort;

        private final BootstrapThriftServiceImpl bootstrapThriftService;

        private final Object stopSync;

        private final Object startSync;

        private boolean stopComplete = false;

        private boolean startComplete = false;

        /**
         * The server.
         */
        private TServer server;

        public ThriftRunner(String thriftHost, int thriftPort) {
            this.thriftHost = thriftHost;
            this.thriftPort = thriftPort;
            this.stopSync = new Object();
            this.startSync = new Object();
            bootstrapThriftService = new BootstrapThriftServiceImpl();
        }

        /* (non-Javadoc)

        @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            TestDynamicLoadManagerIT.LOG.info("Initializing Thrift Service for Bootstrap Server....");
            TestDynamicLoadManagerIT.LOG.info("thrift host: {}", thriftHost);
            TestDynamicLoadManagerIT.LOG.info("thrift port: {}", thriftPort);
            try {
                TMultiplexedProcessor processor = new TMultiplexedProcessor();
                BootstrapThriftService.Processor<BootstrapThriftService.Iface> bootstrapProcessor = new BootstrapThriftService.Processor<BootstrapThriftService.Iface>(bootstrapThriftService);
                processor.registerProcessor(BOOTSTRAP_SERVICE.getServiceName(), bootstrapProcessor);
                TServerTransport serverTransport = new TServerSocket(new InetSocketAddress(thriftHost, thriftPort));
                server = new org.apache.thrift.server.TThreadPoolServer(new org.apache.thrift.server.TThreadPoolServer.Args(serverTransport).processor(processor));
                TestDynamicLoadManagerIT.LOG.info("Bootstrap test Server {}:{} Started.", thriftHost, thriftPort);
                synchronized(startSync) {
                    startComplete = true;
                    startSync.notify();
                }
                server.serve();
                TestDynamicLoadManagerIT.LOG.info("Bootstrap test Server {}:{} Stopped.", thriftHost, thriftPort);
            } catch (TTransportException e) {
                TestDynamicLoadManagerIT.LOG.error("TTransportException", e);
            } finally {
                synchronized(stopSync) {
                    stopComplete = true;
                    bootstrapThriftService.reset();
                    stopSync.notify();
                }
            }
        }

        public void waitStart() {
            TestDynamicLoadManagerIT.LOG.info("Bootstrap test Server {}:{} waitStart()", thriftHost, thriftPort);
            synchronized(startSync) {
                if (!(startComplete)) {
                    try {
                        startSync.wait(60000);
                    } catch (InterruptedException e) {
                        TestDynamicLoadManagerIT.LOG.error("Interupted ThiftRunner startWait()", e);
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                TestDynamicLoadManagerIT.LOG.error("Interupted ThiftRunner startWait() in sleep", e);
            }
        }

        public void shutdown() {
            TestDynamicLoadManagerIT.LOG.info("Bootstrap test Server {}:{} shutdown()", thriftHost, thriftPort);
            server.stop();
            synchronized(stopSync) {
                if (!(stopComplete)) {
                    try {
                        stopSync.wait(60000);
                    } catch (InterruptedException e) {
                        TestDynamicLoadManagerIT.LOG.error("Interupted ThiftRunner shutdown", e);
                    }
                }
            }
        }

        public BootstrapThriftServiceImpl getBootstrapThriftServiceImpl() {
            return bootstrapThriftService;
        }
    }
}

