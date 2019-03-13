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
package org.apache.flink.mesos.util;


import Protos.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.flink.mesos.Utils;
import org.apache.flink.util.TestLogger;
import org.apache.mesos.Protos;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link MesosResourceAllocation}.
 */
// endregion
public class MesosResourceAllocationTest extends TestLogger {
    // possible roles
    private static final String ROLE_A = "A";

    private static final String ROLE_B = "B";

    // possible framework configurations
    private static final Set<String> AS_ROLE_A = Collections.singleton(MesosResourceAllocationTest.ROLE_A);

    private static final Set<String> AS_NO_ROLE = Collections.emptySet();

    // region Reservations
    /**
     * Tests that reserved resources are prioritized.
     */
    @Test
    public void testReservationPrioritization() {
        MesosResourceAllocation allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(MesosResourceAllocationTest.ROLE_A, 1.0), Utils.cpus(Utils.UNRESERVED_ROLE, 1.0), Utils.cpus(MesosResourceAllocationTest.ROLE_B, 1.0)));
        Assert.assertEquals(Utils.resources(Utils.cpus(MesosResourceAllocationTest.ROLE_A, 1.0), Utils.cpus(MesosResourceAllocationTest.ROLE_B, 1.0), Utils.cpus(Utils.UNRESERVED_ROLE, 1.0)), allocation.getRemaining());
    }

    /**
     * Tests that resources are filtered according to the framework role (if any).
     */
    @Test
    public void testReservationFiltering() {
        MesosResourceAllocation allocation;
        // unreserved resources
        allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(Utils.UNRESERVED_ROLE, 1.0), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(80, 80))));
        Assert.assertEquals(Utils.resources(Utils.cpus(Utils.UNRESERVED_ROLE, 1.0)), allocation.takeScalar("cpus", 1.0, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(80, 80))), allocation.takeRanges("ports", 1, MesosResourceAllocationTest.AS_NO_ROLE));
        allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(Utils.UNRESERVED_ROLE, 1.0), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(80, 80))));
        Assert.assertEquals(Utils.resources(Utils.cpus(Utils.UNRESERVED_ROLE, 1.0)), allocation.takeScalar("cpus", 1.0, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(80, 80))), allocation.takeRanges("ports", 1, MesosResourceAllocationTest.AS_ROLE_A));
        // reserved for the framework role
        allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(MesosResourceAllocationTest.ROLE_A, 1.0), Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 80))));
        Assert.assertEquals(Utils.resources(), allocation.takeScalar("cpus", 1.0, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.takeRanges("ports", 1, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(Utils.cpus(MesosResourceAllocationTest.ROLE_A, 1.0)), allocation.takeScalar("cpus", 1.0, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 80))), allocation.takeRanges("ports", 1, MesosResourceAllocationTest.AS_ROLE_A));
        // reserved for a different role
        allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(MesosResourceAllocationTest.ROLE_B, 1.0), Utils.ports(MesosResourceAllocationTest.ROLE_B, Utils.range(80, 80))));
        Assert.assertEquals(Utils.resources(), allocation.takeScalar("cpus", 1.0, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.takeRanges("ports", 1, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.takeScalar("cpus", 1.0, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(), allocation.takeRanges("ports", 1, MesosResourceAllocationTest.AS_ROLE_A));
    }

    // endregion
    // region General
    /**
     * Tests resource naming and typing.
     */
    @Test
    public void testResourceSpecificity() {
        MesosResourceAllocation allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(1.0), Utils.ports(Utils.range(80, 80))));
        // mismatched name
        Assert.assertEquals(Utils.resources(), allocation.takeScalar("other", 1.0, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.takeRanges("other", 1, MesosResourceAllocationTest.AS_NO_ROLE));
        // mismatched type
        Assert.assertEquals(Utils.resources(), allocation.takeScalar("ports", 1.0, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.takeRanges("cpus", 1, MesosResourceAllocationTest.AS_NO_ROLE));
        // nothing lost
        Assert.assertEquals(Utils.resources(Utils.cpus(1.0), Utils.ports(Utils.range(80, 80))), allocation.getRemaining());
    }

    // endregion
    // region Scalar Resources
    /**
     * Tests scalar resource accounting.
     */
    @Test
    public void testScalarResourceAccounting() {
        MesosResourceAllocation allocation;
        // take part of a resource
        allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(1.0)));
        Assert.assertEquals(Utils.resources(Utils.cpus(0.25)), allocation.takeScalar("cpus", 0.25, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(Utils.cpus(0.75)), allocation.getRemaining());
        // take a whole resource
        allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(1.0)));
        Assert.assertEquals(Utils.resources(Utils.cpus(1.0)), allocation.takeScalar("cpus", 1.0, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.getRemaining());
        // take multiple resources
        allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(MesosResourceAllocationTest.ROLE_A, 1.0), Utils.cpus(Utils.UNRESERVED_ROLE, 1.0)));
        Assert.assertEquals(Utils.resources(Utils.cpus(MesosResourceAllocationTest.ROLE_A, 1.0), Utils.cpus(Utils.UNRESERVED_ROLE, 0.25)), allocation.takeScalar("cpus", 1.25, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.cpus(Utils.UNRESERVED_ROLE, 0.75)), allocation.getRemaining());
    }

    /**
     * Tests scalar resource exhaustion (i.e. insufficient resources).
     */
    @Test
    public void testScalarResourceExhaustion() {
        MesosResourceAllocation allocation = new MesosResourceAllocation(Utils.resources(Utils.cpus(1.0)));
        Assert.assertEquals(Utils.resources(Utils.cpus(1.0)), allocation.takeScalar("cpus", 2.0, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.getRemaining());
    }

    // endregion
    // region Range Resources
    /**
     * Tests range resource accounting.
     */
    @Test
    public void testRangeResourceAccounting() {
        MesosResourceAllocation allocation;
        List<Protos.Resource> ports = Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 81), Utils.range(443, 444)), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(1024, 1025), Utils.range(8080, 8081)));
        // take a partial range of one resource
        allocation = new MesosResourceAllocation(ports);
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 80))), allocation.takeRanges("ports", 1, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(81, 81), Utils.range(443, 444)), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(1024, 1025), Utils.range(8080, 8081))), allocation.getRemaining());
        // take a whole range of one resource
        allocation = new MesosResourceAllocation(ports);
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 81))), allocation.takeRanges("ports", 2, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(443, 444)), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(1024, 1025), Utils.range(8080, 8081))), allocation.getRemaining());
        // take numerous ranges of one resource
        allocation = new MesosResourceAllocation(ports);
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 81), Utils.range(443, 443))), allocation.takeRanges("ports", 3, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(444, 444)), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(1024, 1025), Utils.range(8080, 8081))), allocation.getRemaining());
        // take a whole resource
        allocation = new MesosResourceAllocation(ports);
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 81), Utils.range(443, 444))), allocation.takeRanges("ports", 4, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(1024, 1025), Utils.range(8080, 8081))), allocation.getRemaining());
        // take numerous resources
        allocation = new MesosResourceAllocation(ports);
        Assert.assertEquals(Utils.resources(Utils.ports(MesosResourceAllocationTest.ROLE_A, Utils.range(80, 81), Utils.range(443, 444)), Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(1024, 1024))), allocation.takeRanges("ports", 5, MesosResourceAllocationTest.AS_ROLE_A));
        Assert.assertEquals(Utils.resources(Utils.ports(Utils.UNRESERVED_ROLE, Utils.range(1025, 1025), Utils.range(8080, 8081))), allocation.getRemaining());
    }

    /**
     * Tests range resource exhaustion (i.e. insufficient resources).
     */
    @Test
    public void testRangeResourceExhaustion() {
        MesosResourceAllocation allocation = new MesosResourceAllocation(Utils.resources(Utils.ports(Utils.range(80, 80))));
        Assert.assertEquals(Utils.resources(Utils.ports(Utils.range(80, 80))), allocation.takeRanges("ports", 2, MesosResourceAllocationTest.AS_NO_ROLE));
        Assert.assertEquals(Utils.resources(), allocation.getRemaining());
    }
}

