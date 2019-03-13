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


import org.apache.flume.test.util.SyslogAgent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestSyslogSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSyslogSource.class);

    private SyslogAgent agent;

    private SyslogAgent.SyslogSourceType sourceType;

    public TestSyslogSource(SyslogAgent.SyslogSourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Test
    public void testKeepFields() throws Exception {
        TestSyslogSource.LOGGER.debug("testKeepFields() started.");
        agent.start("all");
        agent.runKeepFieldsTest();
        TestSyslogSource.LOGGER.debug("testKeepFields() ended.");
    }

    @Test
    public void testRemoveFields() throws Exception {
        TestSyslogSource.LOGGER.debug("testRemoveFields() started.");
        agent.start("none");
        agent.runKeepFieldsTest();
        TestSyslogSource.LOGGER.debug("testRemoveFields() ended.");
    }

    @Test
    public void testKeepTimestampAndHostname() throws Exception {
        TestSyslogSource.LOGGER.debug("testKeepTimestampAndHostname() started.");
        agent.start("timestamp hostname");
        agent.runKeepFieldsTest();
        TestSyslogSource.LOGGER.debug("testKeepTimestampAndHostname() ended.");
    }
}

