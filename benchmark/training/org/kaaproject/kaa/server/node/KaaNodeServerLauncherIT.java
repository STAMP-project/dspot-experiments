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
package org.kaaproject.kaa.server.node;


import KaaNodeThriftService.Client;
import KaaThriftService.KAA_NODE_SERVICE;
import com.google.common.io.Closeables;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingCluster;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;
import org.kaaproject.kaa.common.avro.AvroByteArrayConverter;
import org.kaaproject.kaa.server.common.thrift.gen.node.KaaNodeThriftService;
import org.kaaproject.kaa.server.common.utils.KaaUncaughtExceptionHandler;
import org.kaaproject.kaa.server.common.zk.gen.OperationsNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * The Class KaaNodeServerLauncherIT.
 */
public class KaaNodeServerLauncherIT {
    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(KaaNodeServerLauncherIT.class);

    /**
     * The Constant HOST.
     */
    private static final String HOST = "localhost";

    /**
     * The Constant PORT.
     */
    private static final int PORT = 10090;

    private static final String OPERATIONS_SERVER_NODE_PATH = "/operationsServerNodes";

    private static final AvroByteArrayConverter<OperationsNodeInfo> OPERATIONS_NODE_INFO_CONVERTER = new AvroByteArrayConverter(OperationsNodeInfo.class);

    private static final int KAA_NODE_START_TIMEOUT_SEC = 60;

    private static final int KAA_NODE_STOP_TIMEOUT_SEC = 30;

    /**
     * Test start kaa node server application.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testStartKaaNodeServerApplication() throws Exception {
        TestingCluster zkCluster = null;
        TTransport transport = null;
        Thread kaaNodeServerLauncherThread = null;
        KaaNodeThriftService.Client client = null;
        CuratorFramework zkClient = null;
        CountDownLatch latch = new CountDownLatch(1);
        boolean kaaNodeStarted = false;
        KaaNodeServerLauncherIT.TestKaaNodeLauncher launcher = new KaaNodeServerLauncherIT.TestKaaNodeLauncher();
        try {
            zkCluster = new TestingCluster(new InstanceSpec(null, 2185, (-1), (-1), true, (-1), (-1), (-1)));
            zkCluster.start();
            zkClient = CuratorFrameworkFactory.newClient(zkCluster.getConnectString(), new RetryOneTime(100));
            zkClient.start();
            kaaNodeServerLauncherThread = new Thread(launcher);
            kaaNodeServerLauncherThread.start();
            KaaNodeServerLauncherIT.OperationsNodeStartupListener operationsNodeStartupListener = new KaaNodeServerLauncherIT.OperationsNodeStartupListener();
            zkClient.getCuratorListenable().addListener(operationsNodeStartupListener);
            zkClient.getChildren().inBackground(latch).forPath(KaaNodeServerLauncherIT.OPERATIONS_SERVER_NODE_PATH);
            // Wait for operations service to start
            kaaNodeStarted = latch.await(KaaNodeServerLauncherIT.KAA_NODE_START_TIMEOUT_SEC, TimeUnit.SECONDS);
            zkClient.getCuratorListenable().removeListener(operationsNodeStartupListener);
            transport = new TSocket(KaaNodeServerLauncherIT.HOST, KaaNodeServerLauncherIT.PORT);
            TProtocol protocol = new org.apache.thrift.protocol.TBinaryProtocol(transport);
            TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, KAA_NODE_SERVICE.getServiceName());
            client = new KaaNodeThriftService.Client(mp);
            transport.open();
            client.shutdown();
        } finally {
            boolean shutdownFailed = false;
            Closeables.close(zkClient, true);
            if ((transport != null) && (transport.isOpen())) {
                Closeables.close(transport, true);
            }
            if (kaaNodeServerLauncherThread != null) {
                kaaNodeServerLauncherThread.join(30000);
                shutdownFailed = kaaNodeServerLauncherThread.isAlive();
            }
            Closeables.close(zkCluster, true);
            if (launcher != null) {
                ConfigurableApplicationContext appContext = launcher.getApplicationContext();
                if (appContext.isActive()) {
                    Closeables.close(appContext, true);
                }
            }
            if (!kaaNodeStarted) {
                throw new TimeoutException((("Timeout (" + (KaaNodeServerLauncherIT.KAA_NODE_START_TIMEOUT_SEC)) + " sec) occured while waiting kaa node server to start!"));
            } else
                if (shutdownFailed) {
                    throw new TimeoutException((("Timeout (" + (KaaNodeServerLauncherIT.KAA_NODE_STOP_TIMEOUT_SEC)) + " sec) occured while waiting kaa node server shutdown thread!"));
                }

        }
    }

    private class OperationsNodeStartupListener implements CuratorListener {
        @Override
        public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
            if ((event.getType()) == (CuratorEventType.CHILDREN)) {
                if (event.getChildren().isEmpty()) {
                    client.getChildren().inBackground(event.getContext()).forPath(event.getPath());
                } else {
                    String path = ((event.getPath()) + "/") + (event.getChildren().get(0));
                    KaaNodeServerLauncherIT.LOG.info("Operations Node registered in ZK. Waiting for transports configration");
                    client.getData().inBackground(event.getContext()).forPath(path);
                }
            } else
                if ((event.getType()) == (CuratorEventType.GET_DATA)) {
                    if ((event.getData()) == null) {
                        client.getData().inBackground(event.getContext()).forPath(event.getPath());
                    } else {
                        OperationsNodeInfo nodeInfo = KaaNodeServerLauncherIT.OPERATIONS_NODE_INFO_CONVERTER.fromByteArray(event.getData());
                        boolean isTransportInitialized = !(nodeInfo.getTransports().isEmpty());
                        if (isTransportInitialized) {
                            KaaNodeServerLauncherIT.LOG.info("Operations Node updated tarnsports configuration in ZK");
                            ((CountDownLatch) (event.getContext())).countDown();
                        } else {
                            client.getData().inBackground(event.getContext()).forPath(event.getPath());
                        }
                    }
                }

        }
    }

    private static class TestKaaNodeApplication extends KaaNodeApplication {
        private static final String[] DEFAULT_APPLICATION_CONTEXT_XMLS = new String[]{ "kaaNodeContext.xml" };

        private static final String[] DEFAULT_APPLICATION_CONFIGURATION_FILES = new String[]{ "kaa-node.properties", "sql-dao.properties", "nosql-dao.properties" };

        ConfigurableApplicationContext applicationContext;

        public TestKaaNodeApplication() {
            super(KaaNodeServerLauncherIT.TestKaaNodeApplication.DEFAULT_APPLICATION_CONTEXT_XMLS, KaaNodeServerLauncherIT.TestKaaNodeApplication.DEFAULT_APPLICATION_CONFIGURATION_FILES);
        }

        @Override
        protected void init(ApplicationContext applicationContext) {
            this.applicationContext = ((ConfigurableApplicationContext) (applicationContext));
            super.init(applicationContext);
        }
    }

    private class TestKaaNodeLauncher implements Runnable {
        private KaaNodeServerLauncherIT.TestKaaNodeApplication app;

        @Override
        public void run() {
            KaaNodeServerLauncherIT.LOG.info("Starting Kaa Node Server ...");
            Thread.setDefaultUncaughtExceptionHandler(new KaaUncaughtExceptionHandler());
            app = new KaaNodeServerLauncherIT.TestKaaNodeApplication();
            startAndWait(new String[]{ "common-test-context.xml", "kaa-node-test.properties" });
            KaaNodeServerLauncherIT.LOG.info("Kaa Node Server Stopped");
        }

        private ConfigurableApplicationContext getApplicationContext() {
            if ((app) != null) {
                return app.applicationContext;
            }
            return null;
        }
    }
}

