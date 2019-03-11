/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.jita.allocator.concurrency;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.jita.conf.Configuration;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class DeviceAllocationsTrackerTest {
    private static Configuration configuration = new Configuration();

    @Test
    public void testGetAllocatedSize1() throws Exception {
        DeviceAllocationsTracker tracker = new DeviceAllocationsTracker(DeviceAllocationsTrackerTest.configuration);
        tracker.addToAllocation(1L, 0, 100L);
        Assert.assertEquals(100, tracker.getAllocatedSize(0));
        tracker.subFromAllocation(1L, 0, 100L);
        Assert.assertEquals(0, tracker.getAllocatedSize(0));
    }

    @Test
    public void testGetAllocatedSize2() throws Exception {
        DeviceAllocationsTracker tracker = new DeviceAllocationsTracker(DeviceAllocationsTrackerTest.configuration);
        tracker.addToAllocation(1L, 0, 100L);
        tracker.addToAllocation(2L, 0, 100L);
        Assert.assertEquals(200, tracker.getAllocatedSize(0));
        tracker.subFromAllocation(1L, 0, 100L);
        Assert.assertEquals(100, tracker.getAllocatedSize(0));
    }

    @Test
    public void testGetAllocatedSize3() throws Exception {
        DeviceAllocationsTracker tracker = new DeviceAllocationsTracker(DeviceAllocationsTrackerTest.configuration);
        tracker.addToAllocation(1L, 0, 100L);
        tracker.addToAllocation(2L, 1, 100L);
        Assert.assertEquals(100, tracker.getAllocatedSize(0));
        Assert.assertEquals(100, tracker.getAllocatedSize(1));
        tracker.subFromAllocation(1L, 0, 100L);
        Assert.assertEquals(0, tracker.getAllocatedSize(0));
        Assert.assertEquals(100, tracker.getAllocatedSize(1));
    }

    @Test
    public void testGetAllocatedSize4() throws Exception {
        DeviceAllocationsTracker tracker = new DeviceAllocationsTracker(DeviceAllocationsTrackerTest.configuration);
        tracker.addToAllocation(1L, 0, 100L);
        tracker.addToAllocation(2L, 0, 150L);
        Assert.assertEquals(250, tracker.getAllocatedSize(0));
        Assert.assertEquals(100, tracker.getAllocatedSize(1L, 0));
        Assert.assertEquals(150, tracker.getAllocatedSize(2L, 0));
        tracker.subFromAllocation(1L, 0, 100L);
        Assert.assertEquals(150, tracker.getAllocatedSize(0));
    }

    @Test
    public void testReservedSpace1() throws Exception {
        DeviceAllocationsTracker tracker = new DeviceAllocationsTracker(DeviceAllocationsTrackerTest.configuration);
        tracker.addToReservedSpace(0, 1000L);
        Assert.assertEquals(1000L, tracker.getReservedSpace(0));
        tracker.subFromReservedSpace(0, 1000L);
        Assert.assertEquals(0L, tracker.getReservedSpace(0));
    }
}

