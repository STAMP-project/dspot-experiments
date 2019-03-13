/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.test.agent;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.flume.test.util.StagedInstall;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFileChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestFileChannel.class);

    private static final Collection<File> tempResources = new ArrayList<File>();

    private Properties agentProps;

    private File sinkOutputDir;

    /**
     * File channel in/out test. Verifies that all events inserted into the
     * file channel are received by the sink in order.
     * <p>
     * The EXEC source creates 100 events where the event bodies have
     * sequential numbers. The source puts those events into the file channel,
     * and the FILE_ROLL The sink is expected to take all 100 events in FIFO
     * order.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInOut() throws Exception {
        TestFileChannel.LOGGER.debug("testInOut() started.");
        StagedInstall.getInstance().startAgent("a1", agentProps);
        TimeUnit.SECONDS.sleep(10);// Wait for source and sink to finish

        // TODO make this more deterministic
        /* Create expected output */
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= 100; i++) {
            sb.append(i).append("\n");
        }
        String expectedOutput = sb.toString();
        TestFileChannel.LOGGER.info(("Created expected output: " + expectedOutput));
        /* Create actual output file */
        File[] sinkOutputDirChildren = sinkOutputDir.listFiles();
        // Only 1 file should be in FILE_ROLL sink's dir (rolling is disabled)
        Assert.assertEquals(((("Expected FILE_ROLL sink's dir to have only 1 child," + " but found ") + (sinkOutputDirChildren.length)) + " children."), 1, sinkOutputDirChildren.length);
        File actualOutput = sinkOutputDirChildren[0];
        if (!(Files.toString(actualOutput, Charsets.UTF_8).equals(expectedOutput))) {
            TestFileChannel.LOGGER.error("Actual output doesn\'t match expected output.\n");
            throw new AssertionError(("FILE_ROLL sink's actual output doesn't " + "match expected output."));
        }
        TestFileChannel.LOGGER.debug("testInOut() ended.");
    }
}

