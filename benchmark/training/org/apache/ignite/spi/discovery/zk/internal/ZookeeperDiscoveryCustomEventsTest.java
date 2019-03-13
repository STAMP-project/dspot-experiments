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
package org.apache.ignite.spi.discovery.zk.internal;


import java.util.Objects;
import org.apache.ignite.Ignite;
import org.apache.ignite.internal.managers.discovery.DiscoCache;
import org.apache.ignite.internal.managers.discovery.DiscoveryCustomMessage;
import org.apache.ignite.internal.managers.discovery.GridDiscoveryManager;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgniteUuid;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * Tests for Zookeeper SPI discovery.
 */
public class ZookeeperDiscoveryCustomEventsTest extends ZookeeperDiscoverySpiTestBase {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomEventsSimple1_SingleNode() throws Exception {
        ZookeeperDiscoverySpiTestHelper.ackEveryEventSystemProperty();
        Ignite srv0 = startGrid(0);
        srv0.createCache(new org.apache.ignite.configuration.CacheConfiguration("c1"));
        helper.waitForEventsAcks(srv0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomEventsSimple1_5_Nodes() throws Exception {
        ZookeeperDiscoverySpiTestHelper.ackEveryEventSystemProperty();
        Ignite srv0 = startGrids(5);
        srv0.createCache(new org.apache.ignite.configuration.CacheConfiguration("c1"));
        awaitPartitionMapExchange();
        helper.waitForEventsAcks(srv0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomEvents_FastStopProcess_1() throws Exception {
        customEvents_FastStopProcess(1, 0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomEvents_FastStopProcess_2() throws Exception {
        customEvents_FastStopProcess(5, 5);
    }

    /**
     *
     */
    private static class TestFastStopProcessCustomMessage implements DiscoveryCustomMessage {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        private final IgniteUuid id = IgniteUuid.randomUuid();

        /**
         *
         */
        private final boolean createAck;

        /**
         *
         */
        private final int payload;

        /**
         *
         *
         * @param createAck
         * 		Create ack message flag.
         * @param payload
         * 		Payload.
         */
        TestFastStopProcessCustomMessage(boolean createAck, int payload) {
            this.createAck = createAck;
            this.payload = payload;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public IgniteUuid id() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        public DiscoveryCustomMessage ackMessage() {
            return createAck ? new ZookeeperDiscoveryCustomEventsTest.TestFastStopProcessCustomMessageAck(payload) : null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isMutable() {
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean stopProcess() {
            return true;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public DiscoCache createDiscoCache(GridDiscoveryManager mgr, AffinityTopologyVersion topVer, DiscoCache discoCache) {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            ZookeeperDiscoveryCustomEventsTest.TestFastStopProcessCustomMessage that = ((ZookeeperDiscoveryCustomEventsTest.TestFastStopProcessCustomMessage) (o));
            return ((createAck) == (that.createAck)) && ((payload) == (that.payload));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return Objects.hash(createAck, payload);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(ZookeeperDiscoveryCustomEventsTest.TestFastStopProcessCustomMessage.class, this);
        }
    }

    /**
     *
     */
    private static class TestFastStopProcessCustomMessageAck implements DiscoveryCustomMessage {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        private final IgniteUuid id = IgniteUuid.randomUuid();

        /**
         *
         */
        private final int payload;

        /**
         *
         *
         * @param payload
         * 		Payload.
         */
        TestFastStopProcessCustomMessageAck(int payload) {
            this.payload = payload;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public IgniteUuid id() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        @Nullable
        public DiscoveryCustomMessage ackMessage() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isMutable() {
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean stopProcess() {
            return true;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public DiscoCache createDiscoCache(GridDiscoveryManager mgr, AffinityTopologyVersion topVer, DiscoCache discoCache) {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            ZookeeperDiscoveryCustomEventsTest.TestFastStopProcessCustomMessageAck that = ((ZookeeperDiscoveryCustomEventsTest.TestFastStopProcessCustomMessageAck) (o));
            return (payload) == (that.payload);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return Objects.hash(payload);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(ZookeeperDiscoveryCustomEventsTest.TestFastStopProcessCustomMessageAck.class, this);
        }
    }
}

