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
package org.apache.flink.mesos.scheduler;


import Protos.FrameworkID;
import Protos.OfferID;
import Protos.SlaveID;
import java.util.Arrays;
import java.util.Collections;
import org.apache.flink.mesos.Utils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Offer} which adapts a Mesos offer as a lease for use with Fenzo.
 */
// endregion
public class OfferTest extends TestLogger {
    private static final double EPSILON = 1.0E-5;

    private static final FrameworkID FRAMEWORK_ID = FrameworkID.newBuilder().setValue("framework-1").build();

    private static final OfferID OFFER_ID = OfferID.newBuilder().setValue("offer-1").build();

    private static final String HOSTNAME = "host-1";

    private static final SlaveID AGENT_ID = SlaveID.newBuilder().setValue("agent-1").build();

    private static final String ROLE_A = "A";

    private static final String ATTR_1 = "A1";

    // region Resources
    /**
     * Tests basic properties (other than those of specific resources, covered elsewhere).
     */
    @Test
    public void testResourceProperties() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(), OfferTest.attrs()));
        Assert.assertNotNull(offer.getResources());
        Assert.assertEquals(OfferTest.HOSTNAME, offer.hostname());
        Assert.assertEquals(OfferTest.AGENT_ID.getValue(), offer.getVMID());
        Assert.assertNotNull(offer.getOffer());
        Assert.assertEquals(OfferTest.OFFER_ID.getValue(), offer.getId());
        Assert.assertNotEquals(0L, offer.getOfferedTime());
        Assert.assertNotNull(offer.getAttributeMap());
        Assert.assertNotNull(offer.toString());
    }

    /**
     * Tests aggregation of resources in the presence of unreserved plus reserved resources.
     */
    @Test
    public void testResourceAggregation() {
        Offer offer;
        offer = new Offer(OfferTest.offer(Utils.resources(), OfferTest.attrs()));
        Assert.assertEquals(0.0, offer.cpuCores(), OfferTest.EPSILON);
        Assert.assertEquals(Arrays.asList(), OfferTest.ranges(offer.portRanges()));
        offer = new Offer(OfferTest.offer(Utils.resources(Utils.cpus(OfferTest.ROLE_A, 1.0), Utils.cpus(Utils.UNRESERVED_ROLE, 1.0), Utils.ports(OfferTest.ROLE_A, Utils.range(80, 80), Utils.range(443, 444)), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(8080, 8081)), OfferTest.otherScalar(42.0)), OfferTest.attrs()));
        Assert.assertEquals(2.0, offer.cpuCores(), OfferTest.EPSILON);
        Assert.assertEquals(Arrays.asList(Utils.range(80, 80), Utils.range(443, 444), Utils.range(8080, 8081)), OfferTest.ranges(offer.portRanges()));
    }

    @Test
    public void testCpuCores() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(Utils.cpus(1.0)), OfferTest.attrs()));
        Assert.assertEquals(1.0, offer.cpuCores(), OfferTest.EPSILON);
    }

    @Test
    public void testGPUs() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(Utils.gpus(1.0)), OfferTest.attrs()));
        Assert.assertEquals(1.0, offer.gpus(), OfferTest.EPSILON);
    }

    @Test
    public void testMemoryMB() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(Utils.mem(1024.0)), OfferTest.attrs()));
        Assert.assertEquals(1024.0, offer.memoryMB(), OfferTest.EPSILON);
    }

    @Test
    public void testNetworkMbps() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(Utils.network(10.0)), OfferTest.attrs()));
        Assert.assertEquals(10.0, offer.networkMbps(), OfferTest.EPSILON);
    }

    @Test
    public void testDiskMB() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(Utils.disk(1024.0)), OfferTest.attrs()));
        Assert.assertEquals(1024.0, offer.diskMB(), OfferTest.EPSILON);
    }

    @Test
    public void testPortRanges() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(Utils.ports(Utils.range(8080, 8081))), OfferTest.attrs()));
        Assert.assertEquals(Collections.singletonList(Utils.range(8080, 8081)), OfferTest.ranges(offer.portRanges()));
    }

    // endregion
    // region Attributes
    @Test
    public void testAttributeIndexing() {
        Offer offer = new Offer(OfferTest.offer(Utils.resources(), OfferTest.attrs(OfferTest.attr(OfferTest.ATTR_1, 42.0))));
        Assert.assertEquals(OfferTest.attr(OfferTest.ATTR_1, 42.0), offer.getAttributeMap().get(OfferTest.ATTR_1));
    }
}

