/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.coordination;


import ServerType.BRIDGE;
import ServerType.HISTORICAL;
import ServerType.INDEXER_EXECUTOR;
import ServerType.REALTIME;
import org.junit.Assert;
import org.junit.Test;


public class ServerTypeTest {
    @Test
    public void testAssignable() {
        Assert.assertTrue(HISTORICAL.isSegmentReplicationTarget());
        Assert.assertTrue(BRIDGE.isSegmentReplicationTarget());
        Assert.assertFalse(REALTIME.isSegmentReplicationTarget());
        Assert.assertFalse(INDEXER_EXECUTOR.isSegmentReplicationTarget());
    }

    @Test
    public void testFromString() {
        Assert.assertEquals(HISTORICAL, ServerType.fromString("historical"));
        Assert.assertEquals(BRIDGE, ServerType.fromString("bridge"));
        Assert.assertEquals(REALTIME, ServerType.fromString("realtime"));
        Assert.assertEquals(INDEXER_EXECUTOR, ServerType.fromString("indexer-executor"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(HISTORICAL.toString(), "historical");
        Assert.assertEquals(BRIDGE.toString(), "bridge");
        Assert.assertEquals(REALTIME.toString(), "realtime");
        Assert.assertEquals(INDEXER_EXECUTOR.toString(), "indexer-executor");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidName() {
        ServerType.fromString("invalid");
    }
}

