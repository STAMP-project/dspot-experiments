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


import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.flume.test.util.StagedInstall;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);

    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private Properties agentProps;

    private Map<String, String> agentEnv;

    private Map<String, String> agentOptions;

    private File sinkOutputDir1;

    private File sinkOutputDir2;

    private File sinkOutputDir3;

    private File hadoopCredStore;

    @Test
    public void testConfigReplacement() throws Exception {
        TestConfig.LOGGER.debug("testConfigReplacement() started.");
        StagedInstall.getInstance().startAgent("agent", agentProps, agentEnv, agentOptions);
        TimeUnit.SECONDS.sleep(10);// Wait for sources and sink to process files

        // Ensure we received all events.
        validateSeenEvents(sinkOutputDir1, 1, 100);
        validateSeenEvents(sinkOutputDir2, 1, 100);
        validateSeenEvents(sinkOutputDir3, 1, 100);
        TestConfig.LOGGER.debug("Processed all the events!");
        TestConfig.LOGGER.debug("testConfigReplacement() ended.");
    }

    @Test
    public void testConfigReload() throws Exception {
        TestConfig.LOGGER.debug("testConfigReplacement() started.");
        agentProps.put("agent.channels.mem-01.transactionCapacity", "10");
        agentProps.put("agent.sinks.roll-01.sink.batchSize", "20");
        StagedInstall.getInstance().startAgent("agent", agentProps, agentEnv, agentOptions);
        TimeUnit.SECONDS.sleep(10);// Wait for sources and sink to process files

        // This directory is empty due to misconfiguration
        validateSeenEvents(sinkOutputDir1, 0, 0);
        // These are well configured
        validateSeenEvents(sinkOutputDir2, 1, 100);
        validateSeenEvents(sinkOutputDir3, 1, 100);
        TestConfig.LOGGER.debug("Processed all the events!");
        // repair the config
        agentProps.put("agent.channels.mem-01.transactionCapacity", "20");
        StagedInstall.getInstance().reconfigure(agentProps);
        TimeUnit.SECONDS.sleep(40);// Wait for sources and sink to process files

        // Ensure we received all events.
        validateSeenEvents(sinkOutputDir1, 1, 100);
        TestConfig.LOGGER.debug("testConfigReplacement() ended.");
    }
}

