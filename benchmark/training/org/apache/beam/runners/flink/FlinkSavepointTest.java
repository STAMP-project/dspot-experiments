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
package org.apache.beam.runners.flink;


import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests that Flink's Savepoints work with the Flink Runner.
 */
public class FlinkSavepointTest implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkSavepointTest.class);

    /**
     * Static for synchronization between the pipeline state and the test.
     */
    private static CountDownLatch oneShotLatch;

    @ClassRule
    public static transient TemporaryFolder tempFolder = new TemporaryFolder();

    private static transient MiniCluster flinkCluster;

    @Test(timeout = 60000)
    public void testSavepointRestoreLegacy() throws Exception {
        runSavepointAndRestore(false);
    }

    @Test(timeout = 60000)
    public void testSavepointRestorePortable() throws Exception {
        runSavepointAndRestore(true);
    }
}

