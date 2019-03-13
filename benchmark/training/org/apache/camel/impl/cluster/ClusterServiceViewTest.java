/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.impl.cluster;


import ServiceStatus.Started;
import ServiceStatus.Stopped;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.camel.cluster.CamelClusterEventListener;
import org.apache.camel.cluster.CamelClusterMember;
import org.apache.camel.cluster.CamelClusterService;
import org.apache.camel.support.cluster.AbstractCamelClusterService;
import org.apache.camel.support.cluster.AbstractCamelClusterView;
import org.junit.Assert;
import org.junit.Test;


public class ClusterServiceViewTest {
    @Test
    public void testViewEquality() throws Exception {
        ClusterServiceViewTest.TestClusterService service = new ClusterServiceViewTest.TestClusterService(UUID.randomUUID().toString());
        ClusterServiceViewTest.TestClusterView view1 = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        ClusterServiceViewTest.TestClusterView view2 = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        ClusterServiceViewTest.TestClusterView view3 = getView("ns2").unwrap(ClusterServiceViewTest.TestClusterView.class);
        Assert.assertEquals(view1, view2);
        Assert.assertNotEquals(view1, view3);
    }

    @Test
    public void testViewReferences() throws Exception {
        ClusterServiceViewTest.TestClusterService service = new ClusterServiceViewTest.TestClusterService(UUID.randomUUID().toString());
        start();
        ClusterServiceViewTest.TestClusterView view1 = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        ClusterServiceViewTest.TestClusterView view2 = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        ClusterServiceViewTest.TestClusterView view3 = getView("ns2").unwrap(ClusterServiceViewTest.TestClusterView.class);
        Assert.assertEquals(Started, getStatus());
        Assert.assertEquals(Started, getStatus());
        Assert.assertEquals(Started, getStatus());
        releaseView(view1);
        Assert.assertEquals(Started, getStatus());
        Assert.assertEquals(Started, getStatus());
        Assert.assertEquals(Started, getStatus());
        releaseView(view2);
        Assert.assertEquals(Stopped, getStatus());
        Assert.assertEquals(Stopped, getStatus());
        Assert.assertEquals(Started, getStatus());
        releaseView(view3);
        ClusterServiceViewTest.TestClusterView newView1 = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        ClusterServiceViewTest.TestClusterView newView2 = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        Assert.assertEquals(newView1, newView2);
        Assert.assertEquals(view1, newView1);
        Assert.assertEquals(view1, newView2);
        Assert.assertEquals(Started, getStatus());
        Assert.assertEquals(Started, getStatus());
        Assert.assertEquals(Stopped, getStatus());
        stop();
        Assert.assertEquals(Stopped, getStatus());
        Assert.assertEquals(Stopped, getStatus());
        Assert.assertEquals(Stopped, getStatus());
        Assert.assertEquals(Stopped, getStatus());
        Assert.assertEquals(Stopped, getStatus());
    }

    @Test
    public void testViewForceOperations() throws Exception {
        ClusterServiceViewTest.TestClusterService service = new ClusterServiceViewTest.TestClusterService(UUID.randomUUID().toString());
        ClusterServiceViewTest.TestClusterView view = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        Assert.assertEquals(Stopped, getStatus());
        // This should not start the view as the service has not yet started.
        service.startView(getNamespace());
        Assert.assertEquals(Stopped, getStatus());
        // This should start the view.
        start();
        Assert.assertEquals(Started, getStatus());
        service.stopView(getNamespace());
        Assert.assertEquals(Stopped, getStatus());
        service.startView(getNamespace());
        Assert.assertEquals(Started, getStatus());
        releaseView(view);
        Assert.assertEquals(Stopped, getStatus());
    }

    @Test
    public void testMultipleViewListeners() throws Exception {
        final ClusterServiceViewTest.TestClusterService service = new ClusterServiceViewTest.TestClusterService(UUID.randomUUID().toString());
        final ClusterServiceViewTest.TestClusterView view = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        final int events = 1 + (new Random().nextInt(10));
        final Set<Integer> results = new HashSet<>();
        final CountDownLatch latch = new CountDownLatch(events);
        IntStream.range(0, events).forEach(( i) -> addEventListener(((CamelClusterEventListener.Leadership) (( v, l) -> {
            results.add(i);
            latch.countDown();
        }))));
        start();
        view.setLeader(true);
        latch.await(10, TimeUnit.SECONDS);
        IntStream.range(0, events).forEach(( i) -> Assert.assertTrue(results.contains(i)));
    }

    @Test
    public void testLateViewListeners() throws Exception {
        final ClusterServiceViewTest.TestClusterService service = new ClusterServiceViewTest.TestClusterService(UUID.randomUUID().toString());
        final ClusterServiceViewTest.TestClusterView view = getView("ns1").unwrap(ClusterServiceViewTest.TestClusterView.class);
        final int events = 1 + (new Random().nextInt(10));
        final Set<Integer> results = new HashSet<>();
        final CountDownLatch latch = new CountDownLatch((events * 2));
        IntStream.range(0, events).forEach(( i) -> addEventListener(((CamelClusterEventListener.Leadership) (( v, l) -> {
            results.add(i);
            latch.countDown();
        }))));
        start();
        view.setLeader(true);
        IntStream.range(events, (events * 2)).forEach(( i) -> addEventListener(((CamelClusterEventListener.Leadership) (( v, l) -> {
            results.add(i);
            latch.countDown();
        }))));
        latch.await(10, TimeUnit.SECONDS);
        IntStream.range(0, (events * 2)).forEach(( i) -> Assert.assertTrue(results.contains(i)));
    }

    // *********************************
    // Helpers
    // *********************************
    private static class TestClusterView extends AbstractCamelClusterView {
        private boolean leader;

        public TestClusterView(CamelClusterService cluster, String namespace) {
            super(cluster, namespace);
        }

        @Override
        public Optional<CamelClusterMember> getLeader() {
            return leader ? Optional.of(getLocalMember()) : Optional.empty();
        }

        @Override
        public CamelClusterMember getLocalMember() {
            return new CamelClusterMember() {
                @Override
                public boolean isLeader() {
                    return leader;
                }

                @Override
                public boolean isLocal() {
                    return true;
                }

                @Override
                public String getId() {
                    return getClusterService().getId();
                }
            };
        }

        @Override
        public List<CamelClusterMember> getMembers() {
            return Collections.emptyList();
        }

        @Override
        protected void doStart() throws Exception {
        }

        @Override
        protected void doStop() throws Exception {
        }

        public boolean isLeader() {
            return leader;
        }

        public void setLeader(boolean leader) {
            this.leader = leader;
            if (isRunAllowed()) {
                fireLeadershipChangedEvent(getLeader());
            }
        }
    }

    private static class TestClusterService extends AbstractCamelClusterService<ClusterServiceViewTest.TestClusterView> {
        public TestClusterService(String id) {
            super(id);
        }

        @Override
        protected ClusterServiceViewTest.TestClusterView createView(String namespace) throws Exception {
            return new ClusterServiceViewTest.TestClusterView(this, namespace);
        }
    }
}

