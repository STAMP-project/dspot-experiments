/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.chillmode;


import SCMChillModeManager.ChillModeStatus;
import SCMEvents.CHILL_MODE_STATUS;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.block.BlockManager;
import org.apache.hadoop.hdds.scm.container.replication.ReplicationActivityStatus;
import org.apache.hadoop.hdds.scm.server.SCMClientProtocolServer;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests ChillModeHandler behavior.
 */
public class TestChillModeHandler {
    private OzoneConfiguration configuration;

    private SCMClientProtocolServer scmClientProtocolServer;

    private ReplicationActivityStatus replicationActivityStatus;

    private BlockManager blockManager;

    private ChillModeHandler chillModeHandler;

    private EventQueue eventQueue;

    private ChillModeStatus chillModeStatus;

    @Test
    public void testChillModeHandlerWithChillModeEnabled() throws Exception {
        setup(true);
        Assert.assertTrue(chillModeHandler.getChillModeStatus());
        eventQueue.fireEvent(CHILL_MODE_STATUS, chillModeStatus);
        GenericTestUtils.waitFor(() -> !(chillModeHandler.getChillModeStatus()), 1000, 5000);
        Assert.assertFalse(scmClientProtocolServer.getChillModeStatus());
        Assert.assertFalse(isScmInChillMode());
        GenericTestUtils.waitFor(() -> replicationActivityStatus.isReplicationEnabled(), 1000, 5000);
    }

    @Test
    public void testChillModeHandlerWithChillModeDisbaled() throws Exception {
        setup(false);
        Assert.assertFalse(chillModeHandler.getChillModeStatus());
        eventQueue.fireEvent(CHILL_MODE_STATUS, chillModeStatus);
        Assert.assertFalse(chillModeHandler.getChillModeStatus());
        Assert.assertFalse(scmClientProtocolServer.getChillModeStatus());
        Assert.assertFalse(isScmInChillMode());
        GenericTestUtils.waitFor(() -> replicationActivityStatus.isReplicationEnabled(), 1000, 5000);
    }
}

