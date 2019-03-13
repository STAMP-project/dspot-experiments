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
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.Assert;
import org.apache.ambari.server.events.listeners.upgrade.HostVersionOutOfSyncListener;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.StackId;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Tests AMBARI-9738 which produced a deadlock during read and writes between
 * {@link ClustersImpl} and {@link ClusterImpl}.
 */
public class ClustersDeadlockTest {
    private static final String CLUSTER_NAME = "c1";

    private static final int NUMBER_OF_HOSTS = 100;

    private static final int NUMBER_OF_THREADS = 3;

    private final AtomicInteger hostNameCounter = new AtomicInteger(0);

    private CountDownLatch writerStoppedSignal;

    private CountDownLatch readerStoppedSignal;

    private StackId stackId = new StackId("HDP-0.1");

    private String REPO_VERSION = "0.1-1234";

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
    private OrmTestHelper helper;

    private Cluster cluster;

    /**
     * Tests that no deadlock exists when adding hosts while reading from the
     * cluster.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 40000)
    public void testDeadlockWhileMappingHosts() throws Exception {
        Provider<ClustersDeadlockTest.ClustersHostMapperThread> clustersHostMapperThreadFactory = new Provider<ClustersDeadlockTest.ClustersHostMapperThread>() {
            @Override
            public ClustersDeadlockTest.ClustersHostMapperThread get() {
                return new ClustersDeadlockTest.ClustersHostMapperThread();
            }
        };
        doLoadTest(new ClustersDeadlockTest.ClusterReaderThreadFactory(), clustersHostMapperThreadFactory, ClustersDeadlockTest.NUMBER_OF_THREADS, writerStoppedSignal, readerStoppedSignal);
        Assert.assertEquals(((ClustersDeadlockTest.NUMBER_OF_THREADS) * (ClustersDeadlockTest.NUMBER_OF_HOSTS)), clusters.getHostsForCluster(ClustersDeadlockTest.CLUSTER_NAME).size());
    }

    /**
     * Tests that no deadlock exists when adding hosts while reading from the
     * cluster. This test ensures that there are service components installed on
     * the hosts so that the cluster health report does some more work.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 40000)
    public void testDeadlockWhileMappingHostsWithExistingServices() throws Exception {
        Provider<ClustersDeadlockTest.ClustersHostAndComponentMapperThread> clustersHostAndComponentMapperThreadFactory = new Provider<ClustersDeadlockTest.ClustersHostAndComponentMapperThread>() {
            @Override
            public ClustersDeadlockTest.ClustersHostAndComponentMapperThread get() {
                return new ClustersDeadlockTest.ClustersHostAndComponentMapperThread();
            }
        };
        doLoadTest(new ClustersDeadlockTest.ClusterReaderThreadFactory(), clustersHostAndComponentMapperThreadFactory, ClustersDeadlockTest.NUMBER_OF_THREADS, writerStoppedSignal, readerStoppedSignal);
    }

    /**
     * Tests that no deadlock exists when adding hosts while reading from the
     * cluster.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 40000)
    public void testDeadlockWhileUnmappingHosts() throws Exception {
        Provider<ClustersDeadlockTest.ClustersHostUnMapperThread> clustersHostUnMapperThreadFactory = new Provider<ClustersDeadlockTest.ClustersHostUnMapperThread>() {
            @Override
            public ClustersDeadlockTest.ClustersHostUnMapperThread get() {
                return new ClustersDeadlockTest.ClustersHostUnMapperThread();
            }
        };
        doLoadTest(new ClustersDeadlockTest.ClusterReaderThreadFactory(), clustersHostUnMapperThreadFactory, ClustersDeadlockTest.NUMBER_OF_THREADS, writerStoppedSignal, readerStoppedSignal);
        Assert.assertEquals(0, clusters.getHostsForCluster(ClustersDeadlockTest.CLUSTER_NAME).size());
    }

    private final class ClusterReaderThreadFactory implements Provider<ClustersDeadlockTest.ClusterReaderThread> {
        @Override
        public ClustersDeadlockTest.ClusterReaderThread get() {
            return new ClustersDeadlockTest.ClusterReaderThread();
        }
    }

    /**
     * The {@link ClusterReaderThread} reads from a cluster over and over again
     * with a slight pause.
     */
    private final class ClusterReaderThread extends Thread {
        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            try {
                // Repeat until writer threads exist
                while (true) {
                    if ((writerStoppedSignal.getCount()) == 0) {
                        break;
                    }
                    cluster.convertToResponse();
                    Thread.sleep(10);
                } 
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            } finally {
                // Notify that one reader was stopped
                readerStoppedSignal.countDown();
            }
        }
    }

    /**
     * The {@link ClustersHostMapperThread} is used to map hosts to a cluster over
     * and over.
     */
    private final class ClustersHostMapperThread extends Thread {
        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            try {
                for (int i = 0; i < (ClustersDeadlockTest.NUMBER_OF_HOSTS); i++) {
                    String hostName = "c64-" + (hostNameCounter.getAndIncrement());
                    clusters.addHost(hostName);
                    setOsFamily(clusters.getHost(hostName), "redhat", "6.4");
                    clusters.mapHostToCluster(hostName, ClustersDeadlockTest.CLUSTER_NAME);
                    Thread.sleep(10);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * The {@link ClustersHostAndComponentMapperThread} is used to map hosts to a
     * cluster over and over. This will also add components to the hosts that are
     * being mapped to further exercise the cluster health report concurrency.
     */
    private final class ClustersHostAndComponentMapperThread extends Thread {
        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            try {
                for (int i = 0; i < (ClustersDeadlockTest.NUMBER_OF_HOSTS); i++) {
                    String hostName = "c64-" + (hostNameCounter.getAndIncrement());
                    clusters.addHost(hostName);
                    setOsFamily(clusters.getHost(hostName), "redhat", "6.4");
                    clusters.mapHostToCluster(hostName, ClustersDeadlockTest.CLUSTER_NAME);
                    // create DATANODE on this host so that we end up exercising the
                    // cluster health report since we need a service component host
                    createNewServiceComponentHost("HDFS", "DATANODE", hostName);
                    Thread.sleep(10);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * The {@link ClustersHostUnMapperThread} is used to unmap hosts to a cluster
     * over and over.
     */
    private final class ClustersHostUnMapperThread extends Thread {
        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            List<String> hostNames = new ArrayList<>(100);
            try {
                // pre-map the hosts
                for (int i = 0; i < (ClustersDeadlockTest.NUMBER_OF_HOSTS); i++) {
                    String hostName = "c64-" + (hostNameCounter.getAndIncrement());
                    hostNames.add(hostName);
                    clusters.addHost(hostName);
                    setOsFamily(clusters.getHost(hostName), "redhat", "6.4");
                    clusters.mapHostToCluster(hostName, ClustersDeadlockTest.CLUSTER_NAME);
                }
                // unmap them all now
                for (String hostName : hostNames) {
                    clusters.unmapHostFromCluster(hostName, ClustersDeadlockTest.CLUSTER_NAME);
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

