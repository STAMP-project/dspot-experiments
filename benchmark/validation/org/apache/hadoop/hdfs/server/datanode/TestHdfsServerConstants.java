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
package org.apache.hadoop.hdfs.server.datanode;


import RollingUpgradeStartupOption.STARTED;
import StartupOption.CHECKPOINT;
import StartupOption.FORMAT;
import StartupOption.IMPORT;
import StartupOption.INITIALIZESHAREDEDITS;
import StartupOption.REGULAR;
import StartupOption.ROLLBACK;
import StartupOption.ROLLINGUPGRADE;
import StartupOption.UPGRADE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test enumerations in TestHdfsServerConstants.
 */
public class TestHdfsServerConstants {
    /**
     * Test that we can parse a StartupOption string without the optional
     * RollingUpgradeStartupOption.
     */
    @Test
    public void testStartupOptionParsing() {
        TestHdfsServerConstants.verifyStartupOptionResult("FORMAT", FORMAT, null);
        TestHdfsServerConstants.verifyStartupOptionResult("REGULAR", REGULAR, null);
        TestHdfsServerConstants.verifyStartupOptionResult("CHECKPOINT", CHECKPOINT, null);
        TestHdfsServerConstants.verifyStartupOptionResult("UPGRADE", UPGRADE, null);
        TestHdfsServerConstants.verifyStartupOptionResult("ROLLBACK", ROLLBACK, null);
        TestHdfsServerConstants.verifyStartupOptionResult("ROLLINGUPGRADE", ROLLINGUPGRADE, null);
        TestHdfsServerConstants.verifyStartupOptionResult("IMPORT", IMPORT, null);
        TestHdfsServerConstants.verifyStartupOptionResult("INITIALIZESHAREDEDITS", INITIALIZESHAREDEDITS, null);
        try {
            TestHdfsServerConstants.verifyStartupOptionResult("UNKNOWN(UNKNOWNOPTION)", FORMAT, null);
            Assert.fail("Failed to get expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // Expected!
        }
    }

    /**
     * Test that we can parse a StartupOption string with a
     * RollingUpgradeStartupOption.
     */
    @Test
    public void testRollingUpgradeStartupOptionParsing() {
        TestHdfsServerConstants.verifyStartupOptionResult("ROLLINGUPGRADE(ROLLBACK)", ROLLINGUPGRADE, RollingUpgradeStartupOption.ROLLBACK);
        TestHdfsServerConstants.verifyStartupOptionResult("ROLLINGUPGRADE(STARTED)", ROLLINGUPGRADE, STARTED);
        try {
            TestHdfsServerConstants.verifyStartupOptionResult("ROLLINGUPGRADE(UNKNOWNOPTION)", ROLLINGUPGRADE, null);
            Assert.fail("Failed to get expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // Expected!
        }
    }
}

