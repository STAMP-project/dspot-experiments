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
package org.apache.hadoop.hdds.scm.command;


import GenericTestUtils.LogCapturer;
import java.util.Collections;
import org.apache.hadoop.hdds.scm.server.SCMDatanodeHeartbeatDispatcher.CommandStatusReportFromDatanode;
import org.apache.hadoop.hdds.server.events.EventPublisher;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for command status report handler.
 */
public class TestCommandStatusReportHandler implements EventPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(TestCommandStatusReportHandler.class);

    private CommandStatusReportHandler cmdStatusReportHandler;

    @Test
    public void testCommandStatusReport() {
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(TestCommandStatusReportHandler.LOG);
        CommandStatusReportFromDatanode report = this.getStatusReport(Collections.emptyList());
        cmdStatusReportHandler.onMessage(report, this);
        Assert.assertFalse(logCapturer.getOutput().contains("Delete_Block_Status"));
        Assert.assertFalse(logCapturer.getOutput().contains("Replicate_Command_Status"));
        report = this.getStatusReport(this.getCommandStatusList());
        cmdStatusReportHandler.onMessage(report, this);
        Assert.assertTrue(logCapturer.getOutput().contains(("firing event of type " + "Delete_Block_Status")));
        Assert.assertTrue(logCapturer.getOutput().contains(("firing event of type " + "Replicate_Command_Status")));
        Assert.assertTrue(logCapturer.getOutput().contains(("type: " + "deleteBlocksCommand")));
        Assert.assertTrue(logCapturer.getOutput().contains(("type: " + "replicateContainerCommand")));
    }
}

