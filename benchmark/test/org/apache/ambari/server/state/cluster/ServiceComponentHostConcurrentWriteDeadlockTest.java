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
package org.apache.ambari.server.state.cluster;


import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.List;
import org.apache.ambari.server.events.listeners.upgrade.HostVersionOutOfSyncListener;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.State;
import org.apache.ambari.server.state.org.apache.ambari.server.state.State;
import org.apache.ambari.server.testing.DeadlockWarningThread;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests AMBARI-12526 which produced a deadlock during concurrent writes of
 * service component host version and state.
 */
public class ServiceComponentHostConcurrentWriteDeadlockTest {
    private static final int NUMBER_OF_THREADS = 3;

    @Inject
    private Injector injector;

    @Inject
    private Clusters clusters;

    @Inject
    private ServiceFactory serviceFactory;

    @Inject
    private ServiceComponentFactory serviceComponentFactory;

    @Inject
    private ServiceComponentHostFactory serviceComponentHostFactory;

    @Inject
    private ConfigFactory configFactory;

    @Inject
    private OrmTestHelper helper;

    private StackId stackId = new StackId("HDP-0.1");

    private final String REPO_VERSION = "0.1-1234";

    private RepositoryVersionEntity m_repositoryVersion;

    /**
     * The cluster.
     */
    private Cluster cluster;

    /**
     *
     */
    @Test
    public void testConcurrentWriteDeadlock() throws Exception {
        ServiceComponentHost nameNodeSCH = createNewServiceComponentHost("HDFS", "NAMENODE", "c6401");
        ServiceComponentHost dataNodeSCH = createNewServiceComponentHost("HDFS", "DATANODE", "c6401");
        List<ServiceComponentHost> serviceComponentHosts = new ArrayList<>();
        serviceComponentHosts.add(nameNodeSCH);
        serviceComponentHosts.add(dataNodeSCH);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < (ServiceComponentHostConcurrentWriteDeadlockTest.NUMBER_OF_THREADS); i++) {
            ServiceComponentHostConcurrentWriteDeadlockTest.ServiceComponentHostDeadlockWriter thread = new ServiceComponentHostConcurrentWriteDeadlockTest.ServiceComponentHostDeadlockWriter();
            thread.setServiceComponentHosts(serviceComponentHosts);
            thread.start();
            threads.add(thread);
        }
        DeadlockWarningThread wt = new DeadlockWarningThread(threads);
        while (true) {
            if (!(wt.isAlive())) {
                break;
            }
        } 
        if (wt.isDeadlocked()) {
            Assert.assertFalse(wt.getErrorMessages().toString(), wt.isDeadlocked());
        } else {
            Assert.assertFalse(wt.isDeadlocked());
        }
    }

    /**
     * Tests AMBARI-12526 by constantly writing version and state to service
     * component hosts. The deadlock this is testing for occurred when different
     * rows were concurrently being updated by different threads and different
     * transactions.
     */
    private static final class ServiceComponentHostDeadlockWriter extends Thread {
        private List<ServiceComponentHost> serviceComponentHosts;

        public void setServiceComponentHosts(List<ServiceComponentHost> serviceComponentHosts) {
            this.serviceComponentHosts = serviceComponentHosts;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            try {
                for (int i = 0; i < 1000; i++) {
                    org.apache.ambari.server.state.State state = ((i % 2) == 0) ? State.INSTALLING : State.INSTALL_FAILED;
                    String version = ((i % 2) == 0) ? "UNKNOWN" : "2.2.0.0-1234";
                    for (ServiceComponentHost serviceComponentHost : serviceComponentHosts) {
                        serviceComponentHost.setState(state);
                        serviceComponentHost.setVersion(version);
                    }
                    Thread.sleep(10);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     *
     */
    private class MockModule implements Module {
        /**
         *
         */
        @Override
        public void configure(Binder binder) {
            // this listener gets in the way of actually testing the concurrency
            // between the threads; it slows them down too much, so mock it out
            binder.bind(HostVersionOutOfSyncListener.class).toInstance(EasyMock.createNiceMock(HostVersionOutOfSyncListener.class));
        }
    }
}

