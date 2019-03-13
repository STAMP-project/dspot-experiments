/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.streamstatus;


import StreamStatus.ACTIVE;
import StreamStatus.IDLE;
import org.junit.Assert;
import org.junit.Test;

import static StreamStatus.ACTIVE;
import static StreamStatus.ACTIVE_STATUS;
import static StreamStatus.IDLE_STATUS;


/**
 * Tests for {@link StreamStatus}.
 */
public class StreamStatusTest {
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalCreationThrowsException() {
        new StreamStatus(32);
    }

    @Test
    public void testEquals() {
        StreamStatus idleStatus = new StreamStatus(IDLE_STATUS);
        StreamStatus activeStatus = new StreamStatus(ACTIVE_STATUS);
        Assert.assertEquals(IDLE, idleStatus);
        Assert.assertTrue(idleStatus.isIdle());
        Assert.assertFalse(idleStatus.isActive());
        Assert.assertEquals(ACTIVE, activeStatus);
        Assert.assertTrue(activeStatus.isActive());
        Assert.assertFalse(activeStatus.isIdle());
    }

    @Test
    public void testTypeCasting() {
        StreamStatus status = ACTIVE;
        Assert.assertTrue(status.isStreamStatus());
        Assert.assertFalse(status.isRecord());
        Assert.assertFalse(status.isWatermark());
        Assert.assertFalse(status.isLatencyMarker());
        try {
            status.asWatermark();
            Assert.fail("should throw an exception");
        } catch (Exception e) {
            // expected
        }
        try {
            status.asRecord();
            Assert.fail("should throw an exception");
        } catch (Exception e) {
            // expected
        }
        try {
            status.asLatencyMarker();
            Assert.fail("should throw an exception");
        } catch (Exception e) {
            // expected
        }
    }
}

