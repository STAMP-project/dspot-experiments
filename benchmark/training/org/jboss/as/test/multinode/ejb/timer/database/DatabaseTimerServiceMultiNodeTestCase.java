/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.multinode.ejb.timer.database;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.naming.Context;
import org.h2.tools.Server;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that timers are never doubled up
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(DatabaseTimerServiceMultiNodeTestCase.DatabaseTimerServiceTestCaseServerSetup.class)
public class DatabaseTimerServiceMultiNodeTestCase {
    public static final String ARCHIVE_NAME = "testTimerServiceSimple";

    public static final int TIMER_COUNT = 100;

    private static Server server;

    private static final int TIMER_DELAY = 400;

    static class DatabaseTimerServiceTestCaseServerSetup implements ServerSetupTask {
        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
            if ((DatabaseTimerServiceMultiNodeTestCase.server) == null) {
                // we need a TCP server that can be shared between the two servers
                DatabaseTimerServiceMultiNodeTestCase.server = Server.createTcpServer().start();
            }
            final ModelNode address = new ModelNode();
            address.add("subsystem", "datasources");
            address.add("data-source", "MyNewDs");
            address.protect();
            final ModelNode operation = new ModelNode();
            operation.get(OP).set("add");
            operation.get(OP_ADDR).set(address);
            operation.get("name").set("MyNewDs");
            operation.get("jndi-name").set("java:jboss/datasources/TimeDs");
            operation.get("enabled").set(true);
            operation.get("driver-name").set("h2");
            operation.get("pool-name").set("MyNewDs_Pool");
            operation.get("connection-url").set((("jdbc:h2:" + (DatabaseTimerServiceMultiNodeTestCase.server.getURL())) + "/mem:testdb;DB_CLOSE_DELAY=-1"));
            operation.get("user-name").set("sa");
            operation.get("password").set("sa");
            ManagementOperations.executeOperation(managementClient.getControllerClient(), operation);
            ModelNode op = new ModelNode();
            op.get(OP).set(ADD);
            op.get(OP_ADDR).add(SUBSYSTEM, "ejb3");
            op.get(OP_ADDR).add("service", "timer-service");
            op.get(OP_ADDR).add("database-data-store", "dbstore");
            op.get("datasource-jndi-name").set("java:jboss/datasources/TimeDs");
            op.get("database").set("postgresql");
            op.get("refresh-interval").set(100);
            op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            ModelNode op = new ModelNode();
            op.get(OP).set(REMOVE);
            op.get(OP_ADDR).add(SUBSYSTEM, "ejb3");
            op.get(OP_ADDR).add("service", "timer-service");
            op.get(OP_ADDR).add("database-data-store", "dbstore");
            op.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
            op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
            op = new ModelNode();
            op.get(OP).set(REMOVE);
            op.get(OP_ADDR).add(SUBSYSTEM, "datasources");
            op.get(OP_ADDR).add("data-source", "MyNewDs");
            op.get(OPERATION_HEADERS, ROLLBACK_ON_RUNTIME_FAILURE).set(false);
            op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), op);
        }
    }

    @ContainerResource("multinode-server")
    private ManagementClient serverClient;

    @ContainerResource("multinode-client")
    private ManagementClient clientClient;

    @Test
    public void testEjbTimeoutOnOtherNode() throws Exception {
        Context clientContext = getRemoteContext(clientClient);
        try {
            RemoteTimedBean clientBean = ((RemoteTimedBean) (clientContext.lookup((((((DatabaseTimerServiceMultiNodeTestCase.ARCHIVE_NAME) + "/") + (TimedObjectTimerServiceBean.class.getSimpleName())) + "!") + (RemoteTimedBean.class.getName())))));
            Set<String> names = new HashSet<>();
            long time = (System.currentTimeMillis()) + (DatabaseTimerServiceMultiNodeTestCase.TIMER_DELAY);
            for (int i = 0; i < (DatabaseTimerServiceMultiNodeTestCase.TIMER_COUNT); ++i) {
                String name = "timer" + i;
                clientBean.scheduleTimer(time, name);
                names.add(name);
            }
            final Context remoteContext = getRemoteContext(serverClient);
            try {
                Collector serverBean = ((Collector) (remoteContext.lookup((((((DatabaseTimerServiceMultiNodeTestCase.ARCHIVE_NAME) + "/") + (CollectionSingleton.class.getSimpleName())) + "!") + (Collector.class.getName())))));
                List<TimerData> res = serverBean.collect(DatabaseTimerServiceMultiNodeTestCase.TIMER_COUNT);
                Assert.assertEquals(((((("Expected " + (DatabaseTimerServiceMultiNodeTestCase.TIMER_COUNT)) + " was ") + (res.size())) + " ") + res), DatabaseTimerServiceMultiNodeTestCase.TIMER_COUNT, res.size());
                boolean server = false;
                boolean client = false;
                final Set<String> newNames = new HashSet<>(names);
                for (TimerData r : res) {
                    if (!(newNames.remove(r.getInfo()))) {
                        if (!(names.contains(r.getInfo()))) {
                            throw new RuntimeException(((("Timer " + (r.getInfo())) + " not run ") + res));
                        } else {
                            throw new RuntimeException(((("Timer " + (r.getInfo())) + " run twice ") + res));
                        }
                    }
                    if (r.getNode().equals("client")) {
                        client = true;
                    } else
                        if (r.getNode().equals("server")) {
                            server = true;
                        }

                }
                Assert.assertTrue(client);
                Assert.assertTrue(server);
            } finally {
                remoteContext.close();
            }
        } finally {
            clientContext.close();
        }
    }
}

