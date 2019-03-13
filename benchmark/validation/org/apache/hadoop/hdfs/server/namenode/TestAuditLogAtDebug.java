/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import java.util.Arrays;
import java.util.Optional;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem.DefaultAuditLogger;
import org.apache.log4j.Level;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the HDFS Audit logger respects DFS_NAMENODE_AUDIT_LOG_DEBUG_CMDLIST.
 */
public class TestAuditLogAtDebug {
    static final Logger LOG = LoggerFactory.getLogger(TestAuditLogAtDebug.class);

    @Rule
    public Timeout timeout = new Timeout(300000);

    private static final String DUMMY_COMMAND_1 = "dummycommand1";

    private static final String DUMMY_COMMAND_2 = "dummycommand2";

    @Test
    public void testDebugCommandNotLoggedAtInfo() {
        DefaultAuditLogger logger = makeSpyLogger(Level.INFO, Optional.of(Arrays.asList(TestAuditLogAtDebug.DUMMY_COMMAND_1)));
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_1);
        Mockito.verify(logger, Mockito.never()).logAuditMessage(ArgumentMatchers.anyString());
    }

    @Test
    public void testDebugCommandLoggedAtDebug() {
        DefaultAuditLogger logger = makeSpyLogger(Level.DEBUG, Optional.of(Arrays.asList(TestAuditLogAtDebug.DUMMY_COMMAND_1)));
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_1);
        Mockito.verify(logger, Mockito.times(1)).logAuditMessage(ArgumentMatchers.anyString());
    }

    @Test
    public void testInfoCommandLoggedAtInfo() {
        DefaultAuditLogger logger = makeSpyLogger(Level.INFO, Optional.of(Arrays.asList(TestAuditLogAtDebug.DUMMY_COMMAND_1)));
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_2);
        Mockito.verify(logger, Mockito.times(1)).logAuditMessage(ArgumentMatchers.anyString());
    }

    @Test
    public void testMultipleDebugCommandsNotLoggedAtInfo() {
        DefaultAuditLogger logger = makeSpyLogger(Level.INFO, Optional.of(Arrays.asList(TestAuditLogAtDebug.DUMMY_COMMAND_1, TestAuditLogAtDebug.DUMMY_COMMAND_2)));
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_1);
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_2);
        Mockito.verify(logger, Mockito.never()).logAuditMessage(ArgumentMatchers.anyString());
    }

    @Test
    public void testMultipleDebugCommandsLoggedAtDebug() {
        DefaultAuditLogger logger = makeSpyLogger(Level.DEBUG, Optional.of(Arrays.asList(TestAuditLogAtDebug.DUMMY_COMMAND_1, TestAuditLogAtDebug.DUMMY_COMMAND_2)));
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_1);
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_2);
        Mockito.verify(logger, Mockito.times(2)).logAuditMessage(ArgumentMatchers.anyString());
    }

    @Test
    public void testEmptyDebugCommands() {
        DefaultAuditLogger logger = makeSpyLogger(Level.INFO, Optional.empty());
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_1);
        logDummyCommandToAuditLog(logger, TestAuditLogAtDebug.DUMMY_COMMAND_2);
        Mockito.verify(logger, Mockito.times(2)).logAuditMessage(ArgumentMatchers.anyString());
    }
}

