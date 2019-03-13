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
package org.apache.hadoop.hbase;


import CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY;
import CoprocessorHost.REGIONSERVER_COPROCESSOR_CONF_KEY;
import java.io.IOException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.naming.ServiceUnavailableException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.security.AccessDeniedException;
import org.apache.hadoop.hbase.security.access.AccessController;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test case for JMX Connector Server.
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestJMXConnectorServer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestJMXConnectorServer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestJMXConnectorServer.class);

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static Configuration conf = null;

    private static Admin admin;

    // RMI registry port
    private static int rmiRegistryPort = 61120;

    // Switch for customized Accesscontroller to throw ACD exception while executing test case
    static boolean hasAccess;

    /**
     * This tests to validate the HMaster's ConnectorServer after unauthorised stopMaster call.
     */
    @Test
    public void testHMConnectorServerWhenStopMaster() throws Exception {
        TestJMXConnectorServer.conf.set(MASTER_COPROCESSOR_CONF_KEY, (((JMXListener.class.getName()) + ",") + (TestJMXConnectorServer.MyAccessController.class.getName())));
        TestJMXConnectorServer.conf.setInt("master.rmi.registry.port", TestJMXConnectorServer.rmiRegistryPort);
        TestJMXConnectorServer.UTIL.startMiniCluster();
        TestJMXConnectorServer.admin = TestJMXConnectorServer.UTIL.getConnection().getAdmin();
        // try to stop master
        boolean accessDenied = false;
        try {
            TestJMXConnectorServer.hasAccess = false;
            TestJMXConnectorServer.LOG.info("Stopping HMaster...");
            TestJMXConnectorServer.admin.stopMaster();
        } catch (AccessDeniedException e) {
            TestJMXConnectorServer.LOG.info("Exception occurred while stopping HMaster. ", e);
            accessDenied = true;
        }
        Assert.assertTrue(accessDenied);
        // Check whether HMaster JMX Connector server can be connected
        JMXConnector connector = null;
        try {
            connector = JMXConnectorFactory.connect(JMXListener.buildJMXServiceURL(TestJMXConnectorServer.rmiRegistryPort, TestJMXConnectorServer.rmiRegistryPort));
        } catch (IOException e) {
            if ((e.getCause()) instanceof ServiceUnavailableException) {
                Assert.fail("Can't connect to HMaster ConnectorServer.");
            }
        }
        Assert.assertNotNull("JMXConnector should not be null.", connector);
        connector.close();
    }

    /**
     * This tests to validate the RegionServer's ConnectorServer after unauthorised stopRegionServer
     * call.
     */
    @Test
    public void testRSConnectorServerWhenStopRegionServer() throws Exception {
        TestJMXConnectorServer.conf.set(REGIONSERVER_COPROCESSOR_CONF_KEY, (((JMXListener.class.getName()) + ",") + (TestJMXConnectorServer.MyAccessController.class.getName())));
        TestJMXConnectorServer.conf.setInt("regionserver.rmi.registry.port", TestJMXConnectorServer.rmiRegistryPort);
        TestJMXConnectorServer.UTIL.startMiniCluster();
        TestJMXConnectorServer.admin = TestJMXConnectorServer.UTIL.getConnection().getAdmin();
        TestJMXConnectorServer.hasAccess = false;
        ServerName serverName = TestJMXConnectorServer.UTIL.getHBaseCluster().getRegionServer(0).getServerName();
        TestJMXConnectorServer.LOG.info("Stopping Region Server...");
        TestJMXConnectorServer.admin.stopRegionServer((((serverName.getHostname()) + ":") + (serverName.getPort())));
        // Check whether Region Sever JMX Connector server can be connected
        JMXConnector connector = null;
        try {
            connector = JMXConnectorFactory.connect(JMXListener.buildJMXServiceURL(TestJMXConnectorServer.rmiRegistryPort, TestJMXConnectorServer.rmiRegistryPort));
        } catch (IOException e) {
            if ((e.getCause()) instanceof ServiceUnavailableException) {
                Assert.fail("Can't connect to Region Server ConnectorServer.");
            }
        }
        Assert.assertNotNull("JMXConnector should not be null.", connector);
        connector.close();
    }

    /**
     * This tests to validate the HMaster's ConnectorServer after unauthorised shutdown call.
     */
    @Test
    public void testHMConnectorServerWhenShutdownCluster() throws Exception {
        TestJMXConnectorServer.conf.set(MASTER_COPROCESSOR_CONF_KEY, (((JMXListener.class.getName()) + ",") + (TestJMXConnectorServer.MyAccessController.class.getName())));
        TestJMXConnectorServer.conf.setInt("master.rmi.registry.port", TestJMXConnectorServer.rmiRegistryPort);
        TestJMXConnectorServer.UTIL.startMiniCluster();
        TestJMXConnectorServer.admin = TestJMXConnectorServer.UTIL.getConnection().getAdmin();
        boolean accessDenied = false;
        try {
            TestJMXConnectorServer.hasAccess = false;
            TestJMXConnectorServer.LOG.info("Stopping HMaster...");
            TestJMXConnectorServer.admin.shutdown();
        } catch (AccessDeniedException e) {
            TestJMXConnectorServer.LOG.error("Exception occurred while stopping HMaster. ", e);
            accessDenied = true;
        }
        Assert.assertTrue(accessDenied);
        // Check whether HMaster JMX Connector server can be connected
        JMXConnector connector = null;
        try {
            connector = JMXConnectorFactory.connect(JMXListener.buildJMXServiceURL(TestJMXConnectorServer.rmiRegistryPort, TestJMXConnectorServer.rmiRegistryPort));
        } catch (IOException e) {
            if ((e.getCause()) instanceof ServiceUnavailableException) {
                Assert.fail("Can't connect to HMaster ConnectorServer.");
            }
        }
        Assert.assertNotNull("JMXConnector should not be null.", connector);
        connector.close();
    }

    /* Customized class for test case execution which will throw ACD exception while executing
    stopMaster/preStopRegionServer/preShutdown explicitly.
     */
    public static class MyAccessController extends AccessController {
        @Override
        public void postStartMaster(ObserverContext<MasterCoprocessorEnvironment> ctx) throws IOException {
            // Do nothing. In particular, stop the creation of the hbase:acl table. It makes the
            // shutdown take time.
        }

        @Override
        public void preStopMaster(ObserverContext<MasterCoprocessorEnvironment> c) throws IOException {
            if (!(TestJMXConnectorServer.hasAccess)) {
                throw new AccessDeniedException("Insufficient permissions to stop master");
            }
        }

        @Override
        public void preStopRegionServer(ObserverContext<RegionServerCoprocessorEnvironment> ctx) throws IOException {
            if (!(TestJMXConnectorServer.hasAccess)) {
                throw new AccessDeniedException("Insufficient permissions to stop region server.");
            }
        }

        @Override
        public void preShutdown(ObserverContext<MasterCoprocessorEnvironment> c) throws IOException {
            if (!(TestJMXConnectorServer.hasAccess)) {
                throw new AccessDeniedException("Insufficient permissions to shut down cluster.");
            }
        }

        @Override
        public void preExecuteProcedures(ObserverContext<RegionServerCoprocessorEnvironment> ctx) throws IOException {
            // FIXME: ignore the procedure permission check since in our UT framework master is neither
            // the systemuser nor the superuser so we can not call executeProcedures...
        }
    }
}

