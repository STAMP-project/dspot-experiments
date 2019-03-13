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


import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.flume.test.util.StagedInstall;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSpooldirSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSpooldirSource.class);

    private Properties agentProps;

    private File sinkOutputDir;

    private List<File> spoolDirs = Lists.newArrayList();

    @Test
    public void testManySpooldirs() throws Exception {
        TestSpooldirSource.LOGGER.debug("testManySpooldirs() started.");
        StagedInstall.getInstance().startAgent("agent", agentProps);
        final int NUM_FILES_PER_DIR = 10;
        createInputTestFiles(spoolDirs, NUM_FILES_PER_DIR, 0);
        TimeUnit.SECONDS.sleep(10);// Wait for sources and sink to process files

        // Ensure we received all events.
        validateSeenEvents(sinkOutputDir, 1, spoolDirs.size(), NUM_FILES_PER_DIR);
        TestSpooldirSource.LOGGER.debug("Processed all the events!");
        TestSpooldirSource.LOGGER.debug("testManySpooldirs() ended.");
    }
}

