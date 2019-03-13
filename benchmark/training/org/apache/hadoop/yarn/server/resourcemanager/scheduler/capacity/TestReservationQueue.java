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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerDynamicEditException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for dynamic auto created leaf queues.
 *
 * @see ReservationQueue
 */
public class TestReservationQueue {
    private CapacitySchedulerConfiguration csConf;

    private CapacitySchedulerContext csContext;

    static final int DEF_MAX_APPS = 10000;

    static final int GB = 1024;

    private final ResourceCalculator resourceCalculator = new DefaultResourceCalculator();

    private ReservationQueue autoCreatedLeafQueue;

    @Test
    public void testAddSubtractCapacity() throws Exception {
        // verify that setting, adding, subtracting capacity works
        autoCreatedLeafQueue.setCapacity(1.0F);
        validateAutoCreatedLeafQueue(1);
        autoCreatedLeafQueue.setEntitlement(new QueueEntitlement(0.9F, 1.0F));
        validateAutoCreatedLeafQueue(0.9);
        autoCreatedLeafQueue.setEntitlement(new QueueEntitlement(1.0F, 1.0F));
        validateAutoCreatedLeafQueue(1);
        autoCreatedLeafQueue.setEntitlement(new QueueEntitlement(0.0F, 1.0F));
        validateAutoCreatedLeafQueue(0);
        try {
            autoCreatedLeafQueue.setEntitlement(new QueueEntitlement(1.1F, 1.0F));
            Assert.fail();
        } catch (SchedulerDynamicEditException iae) {
            // expected
            validateAutoCreatedLeafQueue(1);
        }
        try {
            autoCreatedLeafQueue.setEntitlement(new QueueEntitlement((-0.1F), 1.0F));
            Assert.fail();
        } catch (SchedulerDynamicEditException iae) {
            // expected
            validateAutoCreatedLeafQueue(1);
        }
    }
}

